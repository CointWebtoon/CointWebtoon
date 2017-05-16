package importClasses;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.*;
import java.util.*;

public class ImageURLParse {
    private static final int threadSleepTime = 500;     //도스 공격이라고 오해받지 않도록 Sleep 할 시간
    private static final int numOfThreads = 8;             //업데이트에 사용할 쓰레드 개수
    private static final int timeout = 60 * 1000;               //Jsoup timeout exception방지

    private Thread[] updateThreads = new Thread[numOfThreads];      //업데이트에 사용할 쓰레드들의 배열
    private ArrayList<ArrayList<ImagesInEpisode>> episodes;             //업데이트 할 회차들이 담길 ArrayList
    private JspWriter out;  //결과를 출력하기 위해 생성자 인자로 받은 JspWriter
    private int[] countsUpdateImage = new int[numOfThreads];     //각 쓰레드 별로 몇 개의 이미지 업데이트문을 작성했는지 모니터하기 위해 만든 배열
    private int[] countsUpdateMention = new int[numOfThreads];  //각 쓰레드 별로 몇 개의 작가의 말 업데이트문을 작성했는지 모니터하기 위해 만든 배열
    private Map<String, String> loginCookie;                                    //성인 웹툰을 받아오기 위해 로그인을 하고 그 쿠키를 저장
    private HashSet<Integer> motionToonList = new HashSet<>();   //모션툰 리스트
    private HashSet<Integer> mobileUnsupportedList = new HashSet<>();   //모바일 미지원 웹툰
    private HashSet<ImagesInEpisode> errorList = new HashSet<>();   //Timeout Exception 발생한 것들 재시도
    private int timeoutExceptionCount = 0;


    //DB에서 사용할 인증 정보, SQL
    private static final String allQuery = "SELECT Id, Episode_id, Toontype FROM WEBTOON, EPISODE WHERE Id=Id_E";//모든 회차 이미지 업데이트하는 질의
    private static final String weekdayQuery = "SELECT Id_E, Episode_id, ToonType" +
            " FROM WEBTOON, EPISODE" +
            " WHERE Id=Id_E AND" +
            " Id_E IN(SELECT DISTINCT Id" +
            " FROM WEBTOON, WEEKDAY" +
            " WHERE Id=Id_W AND Weekday>0)";    //연재 중인 웹툰만 업데이트하는 질의
    private static final String recentTwoEpQuery = "SELECT Id_E, Episode_id, Toontype" +
            " FROM" +
            "(SELECT Id_E, Episode_id" +
            "  FROM" +
            "     (" +
            "      SELECT  @row_num := IF(@prev_value=Id_E,@row_num+1,1) AS RowNumber" +
            "             ,Id_E" +
            "             ,Episode_id" +
            "             ,@prev_value := Id_E" +
            "        FROM EPISODE," +
            "             (SELECT @row_num := 1) x," +
            "             (SELECT @prev_value := '') y" +
            "       ORDER BY Id_E, Episode_id DESC" +
            "     ) subquery" +
            " WHERE RowNumber<=2) AS RECENT, WEBTOON" +
            " WHERE RECENT.Id_E=Id AND RECENT.Id_E  IN (SELECT DISTINCT Id FROM WEBTOON, WEEKDAY WHERE Id=Id_W AND Weekday=?)" +
            "ORDER BY Id_E, Episode_id DESC";   //각 연재 중 요일을 골라 웹툰 최신 회차 2개만 받아오는 질의 --> 빠른 업데이트
    private static final String insertSQL = "INSERT INTO IMAGE(Id_I, Ep_id, Image_id, Image_url)" +
            " VALUES(?,?,?,?) ON DUPLICATE KEY UPDATE Image_url=?"; //업데이트 시 사용할 SQL. 해당 회차가 이미 존재할 경우 INSERT 대신 업데이트
    private static final String updateMentionSQL = "UPDATE EPISODE SET Mention=? WHERE Id_E=? AND Episode_id=?"; //작가의 말을 업데이트 하는 SQL
    private static final String updateBlackListSQL = "UPDATE WEBTOON SET Mobile_unsupported=1 WHERE Id=?";
    private static final String updateMotionToonSQL = "UPDATE WEBTOON SET Toontype=? WHERE Id=?";

    private Connection connection;  //DB 업데이트에 사용할 Connection 객체
    private PreparedStatement[] psts = new PreparedStatement[numOfThreads]; //각 쓰레드에서 사용할 이미지 업데이트 PreparedStatement 객체 -> 동기화 문제때문에 배열 사용
    private PreparedStatement[] mentionPsts = new PreparedStatement[numOfThreads];//각 쓰레드에서 사용할 작가의 말 업데이트 PreparedStatement 객체 -> 동기화 문제때문에 배열 사용

    //Constructor
    public ImageURLParse(JspWriter out) {
        episodes = new ArrayList<>();
        for (int i = 0; i < numOfThreads; i++)
            episodes.add(new ArrayList<>());
        this.out = out;
        System.out.println("네이버 로그인 중...");
        try {
            NaverLogin login = new NaverLogin(NaverAuthentication.id, NaverAuthentication.pw, false);
            if (login.isLogin()) {
                out.println("네이버 로그인 성공!");
                System.out.println("네이버 로그인 성공");
                loginCookie = login.getCookies();
            }

            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(DBAuthentication.url, DBAuthentication.id, DBAuthentication.password);//드라이버 로드
            connection.setAutoCommit(false);    //모두 업데이트 된 후 Commit 을 하기 위해 autoCommit 을 끈다.
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("초기화 실패... 시스템을 종료합니다....");
            System.exit(-1);
        }
    }

    /*
     * 이미지 업데이트를 실행하는 메소드 - 클라이언트에서 호출 가능한 유일한 메소드
     * Input Parameter  : boolean true-완결 웹툰까지 모두 업데이트 false - 연재 중인 웹툰만 업데이트
     * Output : 실행 결과 true - 성공 false - 실패
     */
    public boolean updateImages(String request) {
        long startTime = System.currentTimeMillis();        //모두 업데이트하는 데에 얼마나 걸렸는지 계산
        try {
            if (!getEpisodeLists(request))    //모든 웹툰의 회차정보 리스트를 받아온다. 에러 시 return
                return false;
            initializeThreads();    //전역 변수 쓰레드 배열에 있는 Thread 들을 초기화하고 행동 지정

            for (int i = 0; i < numOfThreads; i++) {
                countsUpdateImage[i] = 0;
                countsUpdateMention[i] = 0;         //각 쓰레드 별로 몇 개의 업데이트문을 작성했는지 모니터하기 위해 만든 배열 초기화
                psts[i] = connection.prepareStatement(insertSQL);
                mentionPsts[i] = connection.prepareStatement(updateMentionSQL);  //PreparedStatement 객체에 용도에 맞는 SQL 문 작성
                updateThreads[i].start();
            }
            for (int i = 0; i < numOfThreads; i++) {
                while (updateThreads[i].isAlive()) ;
                System.out.println("Thread" + String.valueOf(i+1) + " 기다리는중.......");
            }                                                                           //쓰레드 시작 후 모든 쓰레드가 끝날 때까지 기다린다.
            System.out.println("================쓰레드 행동 종료.... ERROR HANDLING STARTED=======");
            retryErrorList();   //모든 쓰레드 종료 후 에러 리스트 핸들링 처리
            int countImage = 0, countMention = 0;
            for (int i = 0; i < numOfThreads; i++) {
                countImage += countsUpdateImage[i];             //각 쓰레드에서 업데이트 한 개수 모두 더함
                countMention += countsUpdateMention[i];
            }
            //쓰레드들에서 업데이트 한 SQL 개수 출력
            System.out.println(countImage + "개의 이미지 업데이트문 작성 완료");
            System.out.println(countMention + "개의 작가의 말 업데이트문 작성 완료");
            System.out.println("DB 업데이트 시작");
            try {
                out.println(countImage + "개의 이미지 업데이트문 작성 완료");
                out.println(countMention + "개의 작가의 말 업데이트문 작성 완료");
                out.println("DB 업데이트 시작");
            } catch (IOException ioex) {}
            for (int i = 0; i < numOfThreads; i++) {
                psts[i].executeBatch();
            }
            for (int i = 0; i < numOfThreads; i++) {
                mentionPsts[i].executeBatch();
            }                                                                                           //Thread에서 담았던 PreparedStatement 객체들에 있는 Batch를 실행
            PreparedStatement insertBlackListStatement = connection.prepareStatement(updateBlackListSQL);
            for (Integer blackListId : mobileUnsupportedList) {
                insertBlackListStatement.setInt(1, blackListId);
                insertBlackListStatement.addBatch();
                insertBlackListStatement.clearParameters();
            }
            PreparedStatement motionToonTypeStatement = connection.prepareStatement(updateMotionToonSQL);
            for (Integer motionToonId : motionToonList) {
                motionToonTypeStatement.setString(1,  "M");
                motionToonTypeStatement.setInt(2, motionToonId);
                motionToonTypeStatement.addBatch();
                motionToonTypeStatement.clearParameters();
            }
            motionToonTypeStatement.executeBatch();
            insertBlackListStatement.executeBatch();
            connection.commit();                                        //모두 실행 후 Commit
            System.out.println("DB 업데이트 완료");
            try {
                out.println("DB 업데이트 완료");
            } catch (IOException ioex) {
            }

            System.out.println("총 " + timeoutExceptionCount + "번의 TIME OUT 발생");
            System.out.println("최종 ERR LIST : " + errorList);
            System.out.println("모바일 미지원 웹툰 목록 : " + mobileUnsupportedList);
            System.out.println("모션툰 목록 : " + motionToonList);
        } catch (SQLException sqlex) {  //에러시 에러 출력 후 false return
            sqlex.printStackTrace();
            return false;
        } finally {
            for (int i = 0; i < numOfThreads; i++) {
                try {
                    psts[i].close();
                } catch (Exception e) {
                }
                try {
                    connection.close();
                } catch (Exception e) {
                }//리소스 해제
            }

            //메소드 동작 완료에 몇 초가 걸렸는지 출력
            System.out.println("모든 동작 종료... 총" + String.valueOf((System.currentTimeMillis() - startTime) / 1000) + "초 경과");
            try {
                out.println("모든 동작 종료... 총" + String.valueOf((System.currentTimeMillis() - startTime) / 1000) + "초 경과");
            } catch (IOException ioex) {
            }
        }

        return true;        //모든 동작을 완료하였으므로 true
    }

    /*
     * DB 에서 요일별 웹툰 업데이트인지 모든 웹툰 업데이트인지를 인자로 받아 요청에 따른 이미지 업데이트할 회차 리스트를 만듦(episodes)
     * Input Parameter : boolean true - 모든 웹툰 업데이트 false - 요일별 웹툰만 업데이트
     * Output : 실행 결과 true - 성공 false - 실패, 쓰레드별로 회차가 균등하게 분배된 episode ArrayList가 만들어진다.
     */
    private boolean getEpisodeLists(String request) {
        PreparedStatement pst = null;   //쿼리를 할 PreparedStatement 객체
        ResultSet rs = null;    //쿼리 결과를 담을 ResultSet 객체
        try {
            if (request.equals("all"))  //모든 웹툰을 업데이트 하라는 요청인지 요일별 요청인지에 따라 다른 SQL 을 사용하여 PreparedStatement 객체 생성
                pst = connection.prepareStatement(allQuery);
            else if (request.equals("weekday"))
                pst = connection.prepareStatement(weekdayQuery);
            else if (request.equals("recent")) {
                pst = connection.prepareStatement(recentTwoEpQuery);
                Calendar now = Calendar.getInstance();
                int weekday = -1;
                switch (now.get(Calendar.DAY_OF_WEEK)) {
                    case 1: //일
                        if (now.get(Calendar.HOUR_OF_DAY) == 23)
                            weekday = 1;
                        else
                            weekday = 7;
                        break;
                    case 2://월
                        if (now.get(Calendar.HOUR_OF_DAY) == 23)
                            weekday = 2;
                        else
                            weekday = 1;
                        break;
                    case 3://화
                        if (now.get(Calendar.HOUR_OF_DAY) == 23)
                            weekday = 3;
                        else
                            weekday = 2;
                        break;
                    case 4://수
                        if (now.get(Calendar.HOUR_OF_DAY) == 23)
                            weekday = 4;
                        else
                            weekday = 3;
                        break;
                    case 5://목
                        if (now.get(Calendar.HOUR_OF_DAY) == 23)
                            weekday = 5;
                        else
                            weekday = 4;
                        break;
                    case 6://금
                        if (now.get(Calendar.HOUR_OF_DAY) == 23)
                            weekday = 6;
                        else
                            weekday = 5;
                        break;
                    case 7://토
                        if (now.get(Calendar.HOUR_OF_DAY) == 23)
                            weekday = 7;
                        else
                            weekday = 6;
                        break;
                }
                pst.setInt(1, weekday);
            }else if(request.equals("retry")){
                int index = 0;
                for(ImagesInEpisode instance : errorList){
                    episodes.get(index % numOfThreads).add(instance);  //episodes 안에 있는 ArrayList 들에 돌아가면서 요소 추가
                    index++;
                }
                return true;
            }
            rs = pst.executeQuery();    //쿼리 실행

            rs.last();
            int numOfEpisodes = rs.getRow();
            rs.beforeFirst();   //총 개수를 계산하기 위한 행동. 다시 원위치로 돌려놓는다

            System.out.println("====총 " + numOfEpisodes + "개의 회차의 이미지 업데이트 시작====");

            try {
                out.println("====총 " + numOfEpisodes + "개의 회차의 이미지 업데이트 시작====");
            } catch (IOException ioex) {
                ioex.printStackTrace();
            }

            while (rs.next()) {
                int webtoonId = rs.getInt(1);
                int ep_id = rs.getInt(2);
                char toonType = rs.getString(3).charAt(0);
                episodes.get(rs.getRow() % numOfThreads).add(new ImagesInEpisode(webtoonId, ep_id, toonType));  //episodes 안에 있는 ArrayList 들에 돌아가면서 요소 추가
            }

            System.out.println("====총 " + numOfEpisodes + "개의 회차 ArrayList 업데이트 완료====");

            try {
                int count = 0;
                for (ArrayList<ImagesInEpisode> list : episodes)
                    count += list.size();
                out.println("====총 " + count + "개의 회차 ArrayList 에 업데이트 완료====");
            } catch (IOException ioex) {
                ioex.printStackTrace();
            }

            rs.close();
            pst.close();
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            System.out.println("SQLException OCCURRED IN getEpisode FUNCTION. PROGRAM EXIT WITH CODE -1....");
            System.exit(-1);
        } finally {
            try {
                pst.close();
            } catch (Exception e) {
            }
            try {
                rs.close();
            } catch (Exception e) {
            }
        }
        return true;    //모든 행동을 마쳤다면 true return
    }

    /*
     * 쓰레드들의 동작을 지정, 초기화하는 메소드
     */
    private void initializeThreads() {
        for (int i = 0; i < numOfThreads; i++) {
            final int currentIndex = i;                //쓰레드 내에서 사용할 현재 쓰레드의 인덱스
            updateThreads[i] = new Thread() {            //배열의 각 인덱스에 할당함으로써  쓰레드 제어권을 잃지 않는다.
                public void run() {
                    ArrayList<ImagesInEpisode> episodeInstance = episodes.get(currentIndex);
                    int progress = 1;           //쓰레드의 현재 진행 상황을 모니터할 변수
                    int blockID = -1;           //이미지를 받아올 수 없는 웹툰을 건너뛰기 위한 변수
                    for (ImagesInEpisode instance : episodeInstance) {
                        if (instance.getId_i() == blockID) {
                            progress++;
                            continue;//이미지를 받아올 수 없는 웹툰이라면 하나만 진행하고, 나머지는 진행하지 않는다.
                        }

                        if (instance.getToonType() == 'G' || instance.getToonType() == 'M') {  //일반툰, 모션툰
                            instance.setImages(getImages_GeneralToon(instance.getId_i(), instance.getEp_id(), instance));
                        } else if (instance.getToonType() == 'C') {    //컷툰
                            instance.setImages(getImages_CutToon(instance.getId_i(), instance.getEp_id(), instance));
                        } else if (instance.getToonType() == 'S') {    //스마트툰
                            instance.setImages(getImages_SmartToon(instance.getId_i(), instance.getEp_id(), instance));
                        }

                        //진행 상황 모니터, 현재 받아오고 있는 정보들을 출력한다
                        if (instance.getImages() == null) {
                            System.out.println("====================" + instance.getId_i() + " " + instance.getEp_id() + "회 " + String.valueOf(progress++) + "/"
                                    + episodeInstance.size() + " NULL 개 업데이트  Thread[" + String.valueOf(currentIndex + 1) + "]====================");
                            blockID = instance.getId_i();
                            System.out.println("Thread[" + String.valueOf(currentIndex + 1) + "] ID " + instance.getId_i() + " BLOCKED");
                        } else {
                            synchronized (ImageURLParse.class) {
                                try {
                                    System.out.println("===================="
                                            + instance.getId_i() + " " + instance.getEp_id() + "회 " + String.valueOf(progress++) + "/" + episodeInstance.size() +
                                            " " + instance.getImages().size() + "개 업데이트  Thread[" + String.valueOf(currentIndex + 1) + "]====================");
                                    System.out.println(instance.getImages().get(0));
                                    System.out.println(instance.getMention());
                                } catch (IndexOutOfBoundsException ioubex) {
                                    System.out.println(ioubex.getMessage());
                                } catch (NullPointerException npex) {
                                    System.out.println(npex.getMessage());
                                }
                            }
                        }
                    }

                    /*
                     * ArrayList episodeInstance 에 모든 정보 입력 완료!!
                     * DB SQL문 작성, PreparedStatement addBatch 시작
                     */
                    try {
                        for (ImagesInEpisode instance : episodeInstance) {  //ArrayList의 각 항목마다 들어있는 정보를 PreparedStatement 에 Parameter 로 추가하고, addBatch --> updateImages 메소드에서 execute
                            if (instance.getImages() == null) {   //이미지 PreparedStatement 작성
                                countsUpdateImage[currentIndex]++;
                                continue;
                            }
                            //null이 아닐 때에만 update
                            for (int imageId = 1; imageId <= instance.getImages().size(); imageId++) {
                                //System.out.println("[DB]" + instance.getId_i() + " " + instance.getEp_id() + "회 " + String.valueOf(imageId) + "/" + instance.getImages().size() + "  Thread[" + String.valueOf(currentIndex + 1) + "]");
                                psts[currentIndex].setInt(1, instance.getId_i());
                                psts[currentIndex].setInt(2, instance.getEp_id());
                                psts[currentIndex].setInt(3, imageId);
                                psts[currentIndex].setString(4, instance.getImages().get(imageId - 1));
                                psts[currentIndex].setString(5, instance.getImages().get(imageId - 1));

                                psts[currentIndex].addBatch();
                                psts[currentIndex].clearParameters();
                                countsUpdateImage[currentIndex]++;
                            }

                            //null이 아닐 때에 update
                            if (instance.getMention() != null) {                                  //작가의 말 PreparedStatement 작성
                                mentionPsts[currentIndex].setString(1, instance.getMention());
                                mentionPsts[currentIndex].setInt(2, instance.getId_i());
                                mentionPsts[currentIndex].setInt(3, instance.getEp_id());

                                mentionPsts[currentIndex].addBatch();
                                mentionPsts[currentIndex].clearParameters();
                                countsUpdateMention[currentIndex]++;
                            }
                        }
                    } catch (SQLException sqlex) {
                        sqlex.printStackTrace();
                        System.out.println("SQLException OCCURRED IN getEpisode FUNCTION. PROGRAM EXIT WITH CODE -1....");
                        System.exit(-1);
                    }
                    //모든 행동 종료
                    System.out.println("========================Thread[" + String.valueOf(currentIndex + 1) + "] FINISHED====================================");
                }//run
            };//thread initialize
        }//for
    }//function

    /*
     * 일반 웹툰 회차 하나의 작가의 말을 가져오고, 해당 회차의 이미지들을 가져오는 메소드
     * InputParameter id_i : 웹툰 고유 번호 listNum : 회차 번호 instance : 회차의 정보들을 담는 ImagesInEpisode 객체(작가의 말 업데이트 용)
     * Output : ArrayList<String> 이미지 URL 들이 담긴 ArrayList
     */
    private ArrayList<String> getImages_GeneralToon(int id_i, int listNum, ImagesInEpisode instance) {
        ArrayList<String> imageURLs = new ArrayList<>();
        try {
            String url = "http://m.comic.naver.com/webtoon/detail.nhn?titleId=" + id_i + "&no=" + listNum;
            Thread.sleep(threadSleepTime);   //도스 공격처럼 보이지 않게 하기 위한 Sleep
            Document doc = Jsoup.connect(url)
                    .timeout(timeout).cookies(loginCookie).get();
            String html = doc.html();
            if(html.contains("모바일로 제공하지 않는 웹툰입니다.")){
                System.out.println("ID : " + id_i + " Mobile_Unsupported");
                mobileUnsupportedList.add(id_i);
                return null;
            }else if(html.contains("motiontoon")){
                System.out.println("ID : " + id_i + " 모션툰");
                motionToonList.add(id_i);
                instance.setMention(doc.select(".desc").first().text());   //작가의말 설정
                imageURLs.add("Motion");
                imageURLs.add(url);
                return imageURLs;
            }
            instance.setMention(doc.select(".desc").first().text());   //작가의말 설정
            Elements imgs = doc.select(".toon_view_lst > ul > li > p > img");
            for (int i = 1; i <= imgs.size(); i++) {
                Element image = imgs.get(i - 1);
/*                if(i == 1 || i == imgs.size()){                     //첫 번째 이미지와 마지막 이미지는 src attribute 에, 나머지는 data-lazy-src attribute 에 담겨 있음
                    url = image.attr("src");
                }else {
                    url = image.attr("data-original");
                }*/
                url = image.attr("data-original");
                imageURLs.add(url);
            }
        } catch (SocketTimeoutException toutex) {
            System.out.println("TIMEOUT EXCEPTION OCCURRED IN ID : " + id_i + " EP : " + listNum);
            errorList.add(instance);
            timeoutExceptionCount++;
            return null;
        } catch (Exception e) {
            System.out.println("ERROR OCCURRED IN ID : " + id_i + " EP : " + listNum + " --- " + e.getMessage());
            e.printStackTrace();
            return null;
        }
        return imageURLs;
    }

    /*
     * 컷툰 회차 하나의 작가의 말을 가져오고, 해당 회차의 이미지들을 가져오는 메소드
     * InputParameter id_i : 웹툰 고유 번호 listNum : 회차 번호 instance : 회차의 정보들을 담는 ImagesInEpisode 객체(작가의 말 업데이트 용)
     * Output : ArrayList<String> 이미지 URL 들이 담긴 ArrayList
     */
    private ArrayList<String> getImages_CutToon(int id_i, int listNum, ImagesInEpisode instance) {
        ArrayList<String> cutImages = new ArrayList<String>();   //이미지 URL들을 담을 ArrayList
        String baseURL = "http://m.comic.naver.com/webtoon/detail.nhn?titleId=" + id_i + "&no=" + listNum;  //어떤 웹툰인지, 몇 화인지를 받아서 해당 화의 URL 작성
        try {
            Thread.sleep(threadSleepTime);      //도스 공격처럼 보이지 않게 하기 위한 Sleep
            Document doc = Jsoup.connect(baseURL).timeout(timeout).cookies(loginCookie).get();
            instance.setMention(doc.select(".desc").first().text());
            Elements images = doc.select(".swiper-lazy");
            for (Element image : images) {
                cutImages.add(image.attr("data-src"));
            }
            //이전 코드(네이버 웹페이지 HTML 수정 전
/*
           //javascript 부분을 파싱하는 라이브러리가 없는 것 같음... 파싱하는 모듈
   String content = doc.html();

        String searchImageString = "var aImageList = [";//image url 들이 aImageList 라는 var 내에 들어 있다.
            int startIndex = content.indexOf(searchImageString);
            content = content.substring(startIndex + searchImageString.length());
            //var aImageList = [의 위치를 찾아서 그 전 부분들은 모두 없애버림

            int endIndex = content.indexOf(";");
            content = content.substring(0, endIndex - 1).trim();
            //aImageList의 선언이 끝나는 세미콜론 부분을 찾아서 세미콜론 뒤도 다 날려버림

            String[] imagesSplit = content.split("[{}]");

            for (int i = 0; i < imagesSplit.length; i++) {
                if (imagesSplit.length > 0) {
                    String[] split = imagesSplit[i].split("\'");
                    try {
                        cutImages.add(split[1]);
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        *//*  split은 빈 문자열도 String[] split에 넣기 때문에
                             빈 문자열.split을 할 경우 예외가 남
                             비워두어 그냥 넘어가도록 하자           *//*
                    }
                }
            }*/
        } catch (SocketTimeoutException toutex) {
            System.out.println("TIMEOUT EXCEPTION OCCURRED IN ID : " + id_i + " EP : " + listNum);
            errorList.add(instance);
            timeoutExceptionCount++;
            return null;
        } catch (Exception e) {
            System.out.println("ERROR OCCURRED IN ID : " + id_i + " EP : " + listNum + " --- " + e.getMessage());
            return null;
        }
        if (cutImages.size()==0){
            return null;
        }
        return cutImages;   //작업 완료 후 생성된 imageURL 들의 목록을 return 한다.
    }

    /*
     * 스마트툰 회차 하나의 작가의 말을 가져오고, 해당 회차의 이미지들을 가져오는 메소드
     * InputParameter id_i : 웹툰 고유 번호 listNum : 회차 번호 instance : 회차의 정보들을 담는 ImagesInEpisode 객체(작가의 말 업데이트 용)
     * Output : ArrayList<String> 이미지 URL 들이 담긴 ArrayList
     */
    private ArrayList<String> getImages_SmartToon(int id_i, int listNum, ImagesInEpisode instance) {
        ArrayList<String> smartImages = new ArrayList<>();
        String searchImageString = "var aCutData = [";
        String baseURL = "http://m.comic.naver.com/webtoon/detail.nhn?titleId=" + id_i + "&no=" + listNum;  //어떤 웹툰인지, 몇 화인지를 받아서 해당 화의 URL 작성
        String commentURL = "http://m.comic.naver.com/smarttoon/starComment.nhn?titleId=" + id_i + "&no=" + listNum;
        try {
            Thread.sleep(threadSleepTime);
            Document doc = Jsoup.connect(baseURL).timeout(timeout).cookies(loginCookie).get();
            String content = doc.html();
            int startIndex = content.indexOf(searchImageString);
            content = content.substring(startIndex + searchImageString.length());
            //var aImageList = [의 위치를 찾아서 그 전 부분들은 모두 없애버림
            int endIndex = content.indexOf(";");
            content = content.substring(0, endIndex - 1).trim();
            //aImageList의 선언이 끝나는 세미콜론 부분을 찾아서 세미콜론 뒤도 다 날려버림
            String[] imagesSplit = content.split("[{}]");
            for (int i = 0; i < imagesSplit.length; i++) {
                if (imagesSplit.length > 0) {
                    String[] split = imagesSplit[i].split("\'");
                    try {
                        smartImages.add(split[1]);
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        /*  split은 빈 문자열도 String[] split에 넣기 때문에
                             빈 문자열.split을 할 경우 예외가 남
                             비워두어 그냥 넘어가도록 하자           */
                    }
                }
            }
            Thread.sleep(threadSleepTime);
            doc = Jsoup.connect(commentURL).timeout(timeout).cookies(loginCookie).get();
            instance.setMention(doc.select(".desc").first().text());
        } catch (SocketTimeoutException toutex) {
            System.out.println("TIMEOUT EXCEPTION OCCURRED IN ID : " + id_i + " EP : " + listNum);
            errorList.add(instance);
            timeoutExceptionCount++;
            return null;
        } catch (Exception e) {
            System.out.println("ERROR OCCURRED IN ID : " + id_i + " EP : " + listNum + " --- " + e.getMessage());
            return null;
        }
        if (smartImages.size()==0){
            return null;
        }
        return smartImages;
    }

    /*
        timeout 발생한 웹툰들에 대해서만 다시 업데이트
     */
    private void retryErrorList() {
        if(errorList.isEmpty()){
            System.out.println("TIME OUT이 발생하지 않았습니다.");
            return;
        }
        int retryCount = 0;
        while(!errorList.isEmpty() && retryCount < 5){
            System.out.println("====================" + String.valueOf(retryCount + 1) + "번째 재시도 시작=================\n" +
                    "현재 에러 리스트 : " + errorList);
            for(ArrayList<ImagesInEpisode> list : episodes)
                list.clear();
            getEpisodeLists("retry");
            initializeThreads();
            errorList.clear();
            for(int i = 0 ; i < numOfThreads; i++)
                updateThreads[i].start();
            for (int i = 0; i < numOfThreads; i++) {
                while (updateThreads[i].isAlive()) ;
                System.out.println( String.valueOf(retryCount + 1) + "번째 재시도  -  Thread" + String.valueOf(i+1) + " 기다리는중.......");
            }
            retryCount++;
        }
    }
}
