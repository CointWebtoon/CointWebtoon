package importClasses;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;

public class EpisodeParse {
    //Parse Method에서 반환할 오류 코드들
    private static final int ERR_CODE = -1;
    private static final float ERR_FLOAT_CODE = -1;
    private static final String ERR_STRING = "ERR";
    private static final int timeout = 60 * 1000;               //Jsoup timeout exception방지
    private boolean isUpdateAll = false;    //모든 회차 업데이트시 23시~1시에도 OnePage 말고 모든 회차 업데이트 되도록 한다
    private static final int
            finished = 0,
            mon = 1,
            tue = 2,
            wed = 3,
            thu = 4,
            fri = 5,
            sat = 6,
            sun = 7;

    private static final int numOfThreads = 8;          //업데이트 할 때 사용할 쓰레드의 개수 -> 도스 공격으로 인식당하지 않게 주의해서 개수 정하자
    private static final int threadSleepTime = 500;     //업데이트 시 짧은 시간에 많은 URL을 요청하면 도스공격으로 인식당할 수 있음 요청할 때 잠깐의 인터벌을 두어야 함
    private int[] counts = new int[numOfThreads];   //각 쓰레드에서 업데이트한 회차 개수
    private Thread[] updateThreads;     //업데이트를 진행할 쓰레드들을 담을 배열
    private ArrayList<ArrayList<Integer>> idLists;    //쓰레드에서 사용할 ArrayList
    private HashMap<Integer, Integer> adultMap = new HashMap<>();
    private HashSet<Integer> errorList = new HashSet<>();
    private int timeoutCount = 0;
    boolean doNotDeleteChargedEpisode = false;

    //DB에서 사용할 인증 정보, SQL
    private static final String query = "SELECT Id FROM WEBTOON WHERE Mobile_unsupported=0";   //모든 웹툰 Id를 가져오는 질의(모바일 지원 웹툰만)
    private static final String weekdayQuery = "SELECT DISTINCT Id FROM WEBTOON, WEEKDAY WHERE Id=Id_W AND Weekday>0 " +
            "AND Mobile_unsupported=0";
    private static final String oneWeekdayQuery = "SELECT Id FROM WEBTOON, WEEKDAY WHERE Id=Id_W AND Weekday=";
    private static final String insertSQL = "INSERT INTO EPISODE(Id_E, Episode_id, Episode_title, Ep_starscore, Ep_thumburl, Reg_date)" +
            "VALUES (?,?,?,?,?,?) ON DUPLICATE KEY UPDATE Episode_title=?, Ep_starscore=?,Ep_thumburl=?,Reg_date=?";
    //해당 회차가 존재하지 않으면 INSERT, 존재하면 UPDATE
    private static final String isAdultUpdate = "UPDATE WEBTOON SET Is_adult=? WHERE Id=?";
    private static final String deleteChargedWebtoon = "DELETE FROM EPISODE WHERE Id_E=? AND Episode_Id > ?";
    private Connection connection = null;   //DB Connection
    private PreparedStatement[] psts;   //쓰레드에서 사용할 PreparedStatement 객체들을 담는 배열
    private PreparedStatement[] deleteStatements; //쓰레드에서 사용할 에피소드 삭제 Statement들을 담는 배열
    private ArrayList<Integer> ids = new ArrayList<>();    //회차를 가져올 ID 리스트

    //Constuctor
    public EpisodeParse() {
    }

    /*
     * 해당 웹툰이 몇 화까지 나왔는지 계산하는 메소드
     * Input Parameter : Elements(각 웹툰 회차정보 첫 페이지의 웹툰들이 들어 있는 Elements)
     * output : 현재 몇 화까지 나왔는지 가장 최근 화의 회차번호
     */
    private int parsePage(Elements elements) {
        try {
            int pageNumber = ERR_CODE;

            for (Element element : elements) {
                if (element.className().length() > 0)   //회차정보 상단 광고 배너 무시
                    continue;
                String onClick = element.select("td > a").first().attr("onclick");
                String[] onClickSplit = onClick.split("\'");
                pageNumber = Integer.parseInt(onClickSplit[5]);
                break;  //가장 최근 회차 번호를 얻어오는 것이므로 첫 회차를 알아냈다면 break;
            }

            if (pageNumber % 10 != 0) {//회차 수가 10으로 딱 떨어지면 10으로 나눈다면 페이지 수가 될 것(1page 10개)
                pageNumber = pageNumber / 10 + 1;
            } else {
                pageNumber = pageNumber / 10;
            }

            return pageNumber;  //모든 작업을 성공적으로 완료하였다면 pageNumber return
        } catch (NullPointerException npex) {
            npex.printStackTrace();
            return ERR_CODE;
        } catch (NumberFormatException nfex) {
            nfex.printStackTrace();
            return ERR_CODE;
        }
    }

    /*
     * Element 에 담겨져 있는 회차의 ID를 가져오는 메소드
     * Input Parameter : 하나의 회차에 대한 정보들이 담겨있는 Element HTML CODE BLOCK
     * Output : 해당 회차의 고유 ID
     */
    private int parseEpisode_Id(Element element) {
        int num;
        try {
            String onClick = element.select("td > a").first().attr("onclick");
            String[] onClickSplit = onClick.split("\'");

            num = Integer.parseInt(onClickSplit[5]);
        } catch (NullPointerException npex) {
            npex.printStackTrace();
            return ERR_CODE;
        } catch (NumberFormatException nfex) {
            nfex.printStackTrace();
            return ERR_CODE;
        }
        return num;
    }

    /*
     * Element 에 담겨져 있는 회차의 소제목을 가져오는 메소드
     * Input Parameter : 하나의 회차에 대한 정보들이 담겨있는 Element HTML CODE BLOCK
     * Output : 해당 회차의 소제목
     */
    private String parseEpTitle(Element element) {
        try {
            String epTitle = element.select("td > a > img").first().attr("title");
            return epTitle;
        } catch (NullPointerException ex) {
            return ERR_STRING;
        }
    }

    /*
     * 에피소드의 별점을 가져오는 메소드
     * Input Parameter : 하나의 회차에 대한 정보들이 담겨있는 Element HTML CODE BLOCK
     * Output : 해당 회차의 별점
     */
    private float parseEp_StarScore(Element element) {
        try {
            return Float.parseFloat(element.select(".rating_type > strong").first().text());
        } catch (NullPointerException ex) {
            return ERR_FLOAT_CODE;
        }
    }

    /*
     * 에피소드의 썸네일 URL 을 가져오는 메소드
     * Input Parameter : 하나의 회차에 대한 정보들이 담겨있는 Element HTML CODE BLOCK
     * Output : 해당 회차의 썸네일 URL
     */
    private String parseEp_Thumburl(Element element) {
        try {
            String thumbURL = element.select("td > a > img").first().attr("src");
            return thumbURL;
        } catch (NullPointerException ex) {
            return ERR_STRING;
        }
    }

    /*
     * 에피소드의 업데이트 날짜를 가져오는 메소드
     * Input Parameter : 하나의 회차에 대한 정보들이 담겨있는 Element HTML CODE BLOCK
     * Output : 해당 회차의 업데이트 날짜
     */
    private String parseRegDate(Element element) {
        try {
            return element.select(".num").first().text();
        } catch (NullPointerException ex) {
            return ERR_STRING;
        }
    }


    /*
     * 파싱 소드들을 이용하여 웹툰 고유 Id를 통해 해당 웹툰의 회차들을 얻어오는 메소드
     * Input Parameter : 가져오고 싶은 웹툰의 고유 ID
     * Output : 해당 웹툰의 회차들의 정보가 담긴 ArrayList
     */
    private ArrayList<Episode> getEpisodes(Integer Id_e) {
        ArrayList<Episode> episodes = new ArrayList<>();
        String id_e = Id_e.toString();
        String url = "http://comic.naver.com/webtoon/list.nhn?titleId=" + id_e + "&page=";
        int pageNum;

        //가져와야 할 요소들
        String ep_Title;
        int ep_id;
        float starScore;
        String thumbURL;
        String regDate;

        try {
            try {
                Thread.sleep(threadSleepTime);
            } catch (InterruptedException intex) {
                return null;
            }
            Document doc = Jsoup.connect(url + "1").timeout(timeout).get();  //1페이지의 정보를 가져옴
            Elements page = doc.select(".viewList > tbody > tr");

            String adultKeyword = "var isAdultWebtoon = '";
            String content = doc.html();
            char isAdult = content.charAt(content.indexOf(adultKeyword) + adultKeyword.length());
            synchronized (this) {
                switch (isAdult) {
                    case 'Y': {
                        adultMap.put(Id_e, 1);
                        break;
                    }
                    case 'N': {
                        adultMap.put(Id_e, 0);
                        break;
                    }
                    default:
                        adultMap.put(Id_e, 3);
                        break;
                }
            }

            if (page == null) {
                System.out.println("JSOUP PARSE ERROR");
                return null;
            }

            pageNum = parsePage(page);

            for (int i = 1; i <= pageNum; i++) {
                try {
                    Thread.sleep(threadSleepTime);
                } catch (InterruptedException intex) {
                    return null;
                }
                doc = Jsoup.connect(url + String.valueOf(i)).timeout(timeout).get();
                page = doc.select(".viewList > tbody > tr");

                for (Element element : page) {
                    if (element.className().length() > 0)
                        continue;   //회차정보 상단 광고 배너 무시
                    ep_Title = parseEpTitle(element);
                    ep_id = parseEpisode_Id(element);
                    starScore = parseEp_StarScore(element);
                    thumbURL = parseEp_Thumburl(element);
                    regDate = parseRegDate(element);

                    Episode insertItem = new Episode(Id_e, ep_id, ep_Title, starScore, thumbURL, regDate);
                    episodes.add(insertItem);
                }
            }
        } catch (SocketTimeoutException toutex) {
            System.out.println("Timeout Exception Occurred - id : " + Id_e);
            timeoutCount++;
            errorList.add(Id_e);
            return null;
        } catch (IOException ioex) {
            System.out.println("IOException Occurred - id : " + Id_e);
            errorList.add(Id_e);
            return null;
        }
        return episodes;
    }

    /*
     * 파싱 소드들을 이용하여 웹툰 고유 Id를 통해 해당 웹툰의 회차들을 얻어오는 메소드(하나의 페이지씩만)
     * Input Parameter : 가져오고 싶은 웹툰의 고유 ID
     * Output : 해당 웹툰의 회차들의 정보가 담긴 ArrayList        --> 11시 ~ 1시 빠른 업데이트를 위한 메소드
     */
    private ArrayList<Episode> getEpisodesOnePage(Integer Id_e) {
        ArrayList<Episode> episodes = new ArrayList<>();
        String id_e = Id_e.toString();
        String url = "http://comic.naver.com/webtoon/list.nhn?titleId=" + id_e + "&page=";

        //가져와야 할 요소들
        String ep_Title;
        int ep_id;
        float starScore;
        String thumbURL;
        String regDate;

        try {
            try {
                Thread.sleep(threadSleepTime);
            } catch (InterruptedException intex) {
                return null;
            }
            Document doc = Jsoup.connect(url + "1").timeout(timeout).get();  //1페이지의 정보를 가져옴
            Elements page = doc.select(".viewList > tbody > tr");

            String adultKeyword = "var isAdultWebtoon = '";
            String content = doc.html();
            char isAdult = content.charAt(content.indexOf(adultKeyword) + adultKeyword.length());

            try {
                Thread.sleep(threadSleepTime);
            } catch (InterruptedException intex) {
                return null;
            }

            for (Element element : page) {
                if (element.className().length() > 0)
                    continue;   //회차정보 상단 광고 배너 무시
                ep_Title = parseEpTitle(element);
                ep_id = parseEpisode_Id(element);
                starScore = parseEp_StarScore(element);
                thumbURL = parseEp_Thumburl(element);
                regDate = parseRegDate(element);

                Episode insertItem = new Episode(Id_e, ep_id, ep_Title, starScore, thumbURL, regDate);
                episodes.add(insertItem);
            }

            synchronized (this) {
                switch (isAdult) {
                    case 'Y': {
                        adultMap.put(Id_e, 1);
                        break;
                    }
                    case 'N': {
                        adultMap.put(Id_e, 0);
                        break;
                    }
                    default:
                        adultMap.put(Id_e, 3);
                        break;
                }
            }

            if (page == null) {
                System.out.println("JSOUP PARSE ERROR");
                return null;
            }
        } catch (SocketTimeoutException toutex) {
            System.out.println("Timeout Exception Occurred - id : " + Id_e);
            errorList.add(Id_e);
            timeoutCount++;
            return null;
        } catch (IOException ioex) {
            System.out.println("IOException Occurred - id : " + Id_e);
            errorList.add(Id_e);
            return null;
        }
        return episodes;
    }

    /*
     * 회차정보 페이지들을 돌며 수집한 성인 웹툰 정보를 업데이트하는 메소드
     */
    private void updateIsAdult() throws SQLException {
        int booleanValue;
        PreparedStatement adultStatement = connection.prepareStatement(isAdultUpdate);
        for (Integer toonId : ids) {
            booleanValue = adultMap.get(toonId);
            adultStatement.setInt(1, booleanValue);
            adultStatement.setInt(2, toonId);
            adultStatement.addBatch();
            adultStatement.clearParameters();
            if (booleanValue == 1)
                System.out.print(toonId + " ");
        }
        adultStatement.executeBatch();
    }

    /*
     * 웹툰 테이블에서 웹툰의 ID 들을 가져와 ArrayList 로 반환하는 메소드
     * Output : 실행 결과 true - 성공 false - 실패
     */
    private boolean getIds(String weekday) {
        Statement statement = null;
        ResultSet resultSet = null;
        int index, rowCount;

        String idQuery = "";

        if (weekday.equals("all")) {
            idQuery = query;
            isUpdateAll = true;
        } else if (weekday.equals("weekday")) {
            idQuery = weekdayQuery;
        } else if (weekday.equals("mon")) {
            idQuery = oneWeekdayQuery + String.valueOf(mon);
        } else if (weekday.equals("tue")) {
            idQuery = oneWeekdayQuery + String.valueOf(tue);
        } else if (weekday.equals("wed")) {
            idQuery = oneWeekdayQuery + String.valueOf(wed);
        } else if (weekday.equals("thu")) {
            idQuery = oneWeekdayQuery + String.valueOf(thu);
        } else if (weekday.equals("fri")) {
            idQuery = oneWeekdayQuery + String.valueOf(fri);
        } else if (weekday.equals("sat")) {
            idQuery = oneWeekdayQuery + String.valueOf(sat);
        } else if (weekday.equals("sun")) {
            idQuery = oneWeekdayQuery + String.valueOf(sun);
        } else if (weekday.equals("fin")) {
            idQuery = oneWeekdayQuery + String.valueOf(finished);
        }

        idQuery += " AND Mobile_unsupported=0";

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(idQuery);
            resultSet.last();   //총 몇 개의 row 가 반환되었는지 알아내기 위해
            rowCount = resultSet.getRow();
            resultSet.beforeFirst();    //resultSet 을 사용하기 위해 커서를 다시 원위치

            idLists = new ArrayList<>();

            for (index = 0; index < numOfThreads; index++) {
                idLists.add(new ArrayList<>());
            }

            for (int i = 0; i < rowCount; i++) {
                resultSet.next();
                idLists.get(i % numOfThreads).add(resultSet.getInt(1));
                ids.add(resultSet.getInt(1));
            }

            int numOfWebtoons = 0;
            for (ArrayList<Integer> ids : idLists)
                numOfWebtoons += ids.size();
            System.out.println("총 " + numOfWebtoons + "개의 웹툰 회차 정보 업데이트......");
        } catch (Exception e) {
            System.out.println("GET WEBTOON IDs ERR");
            e.printStackTrace();
            return false;
        } finally {
            try {
                resultSet.close();
            } catch (Exception e) {
            }
            try {
                statement.close();
            } catch (Exception e) {
            }
        }

        return true;
    }

    /*
     * 각 Thread 의 행동을 지정해주는 메소드 --> 실제 Background 에서 실행될 코드
     */
    private void initializeThreads() {
        updateThreads = new Thread[numOfThreads];
        for (int i = 0; i < numOfThreads; i++) {
            final int currentIndex = i;
            updateThreads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    counts[currentIndex] = 0;
                    ArrayList<Integer> ids = idLists.get(currentIndex);
                    ArrayList<Episode> episodes;
                    int count = 1;
                    try {
                        psts[currentIndex] = connection.prepareStatement(insertSQL);
                        deleteStatements[currentIndex] = connection.prepareStatement(deleteChargedWebtoon);
                        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                        for (Integer id : ids) {
                            if ((hour == 23 || hour == 0) && !isUpdateAll) {
                                doNotDeleteChargedEpisode = true;
                                if ((episodes = getEpisodesOnePage(id)) == null) {
                                    System.out.println("GET EPISODE ERR IN ID[" + id + "]");
                                    continue;
                                }
                            } else {
                                if ((episodes = getEpisodes(id)) == null) {
                                    System.out.println("GET EPISODE ERR IN ID[" + id + "]");
                                    continue;
                                }
                            }

                            counts[currentIndex] += episodes.size();
                            //현재 진행상황 출력
                            System.out.println("id : " + id + " " + count++ + "/" + ids.size() + " " + episodes.size() + "개 업데이트 -- Thread[" + String.valueOf(currentIndex + 1) + "]");

                            for (Episode episode : episodes) {
                                psts[currentIndex].setInt(1, episode.getId_e());
                                psts[currentIndex].setInt(2, episode.getEpisode_id());
                                psts[currentIndex].setString(3, episode.getEpisode_title());
                                psts[currentIndex].setFloat(4, episode.getEp_starscore());
                                psts[currentIndex].setString(5, episode.getEp_thumburl());
                                psts[currentIndex].setString(6, episode.getReg_date());
                                psts[currentIndex].setString(7, episode.getEpisode_title());
                                psts[currentIndex].setFloat(8, episode.getEp_starscore());
                                psts[currentIndex].setString(9, episode.getEp_thumburl());
                                psts[currentIndex].setString(10, episode.getReg_date());

                                psts[currentIndex].addBatch();
                                psts[currentIndex].clearParameters();
                            }

                            //유료화 된 웹툰은 유료화 된 회차를 삭제해줘야 함!! --> 마지막에 DB에 현재 가져온 회차보다 회차id가 큰 회차가 있으면 삭제
                            deleteStatements[currentIndex].setInt(1, id);
                            deleteStatements[currentIndex].setInt(2, episodes.size());
                            deleteStatements[currentIndex].addBatch();
                            deleteStatements[currentIndex].clearParameters();
                        }
                    } catch (SQLException sqlex) {
                        sqlex.printStackTrace();
                        System.exit(-1);
                    }
                    System.out.println("========Thread[" + String.valueOf(currentIndex + 1) + "] FINISHED========");
                }
            });
        }
    }

    /*
     * 사용했던 Connection 객체, PreparedStatement 객체를 해제하는 메소드
     */
    private void closeDBResources() {
        for (int i = 0; i < numOfThreads; i++) {
            try {
                psts[i].close();
            } catch (Exception e) {
            }
        }
        try {
            connection.close();
        } catch (Exception e) {
        }
    }

    /*
     * getEpisodes 메소드를 통해 가져와서 DB에 존재하는 모든 웹툰에 대해 업데이트 하는 메소드
     * Output : 실행 결과 true - 성공, false - 실패
     */
    public boolean updateEpisodes(String weekday) {
        try {
            long startTime = System.currentTimeMillis();
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(DBAuthentication.url, DBAuthentication.id, DBAuthentication.password);//드라이버 로드
            connection.setAutoCommit(false);
            psts = new PreparedStatement[numOfThreads];
            deleteStatements = new PreparedStatement[numOfThreads];

            if (!getIds(weekday))   //웹툰 테이블에서 Id 가져와서 각 쓰레드에서 사용할 ArrayList<Integer>에 넣음
                return false;
            initializeThreads();    //쓰레드들 초기화
            for (int i = 0; i < numOfThreads; i++) {    //쓰레드 실행
                updateThreads[i].start();
            }
            for (int i = 0; i < numOfThreads; i++) {    // 모든 쓰레드가 끝날 때까지 대기
                while (updateThreads[i].isAlive()) ;
                System.out.println("Thread" + String.valueOf(i + 1) + " 기다리는중.......");
            }
            System.out.println("=======================모든 쓰레드 행동 종료.. ERROR LIST HANDLING 시작===========================");
            retryErrorList();
            for (int i = 0; i < numOfThreads; i++) {
                psts[i].executeBatch();
                if(!doNotDeleteChargedEpisode)
                    deleteStatements[i].executeBatch();
            }
            System.out.println("총 " + timeoutCount + "번의 TIME OUT 발생");
            System.out.println("최종 ERR LIST : " + errorList + "\n============성인웹툰 리스트============\n");
            updateIsAdult();
            System.out.println("\n=========================모든 Batch 실행 완료.... COMMIT 시작===============================");
            connection.commit();
            System.out.println("=========================COMMIT 완료... 총" + updateCount() + "개의 SQL문이 DB에 업데이트 되었습니다================");
            closeDBResources();
            long timeInMinutes = System.currentTimeMillis() - startTime;
            System.out.println("걸린 시간 : " + timeInMinutes / 1000 + "초");
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            closeDBResources();
            return false;
        } catch (ClassNotFoundException cnfex) {
            cnfex.printStackTrace();
            closeDBResources();
            return false;
        }
        return true;
    }

    /*
     * 업데이트 된 회차의 갯수를 return하는 메소드
     */
    public int updateCount() {
        int count = 0;
        for (int i = 0; i < numOfThreads; i++) {
            count += counts[i];
        }

        return count;
    }

    /*
        에러가 났던 ID 들의 리스트를 모아 에러가 났던 것들만 다시 받아오는 메소드.
     */
    private void retryErrorList() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int retryCount = 0;
        HashSet<Integer> errorListInstance = new HashSet<>();
        try {
            PreparedStatement errorInsertStatement = connection.prepareStatement(insertSQL);
            PreparedStatement errorDeleteStatement = connection.prepareStatement(deleteChargedWebtoon);
            ArrayList<Episode> episodes;
            while (!errorList.isEmpty() && retryCount < 5) {    //error List가 비면 error handling 종료.. 재시도 횟수가 5번이 되면 handling 종료
                System.out.println("재시도 횟수 : " + String.valueOf(retryCount + 1) + "/5 현재 ERR LIST : " + errorList);
                errorListInstance.clear();
                errorListInstance.addAll(errorList);
                for (Integer id : errorListInstance) {
                    errorList.remove(id);
                    if (hour == 23 || hour == 0 && !isUpdateAll) {
                        doNotDeleteChargedEpisode = true;
                        if ((episodes = getEpisodesOnePage(id)) == null) {    //23~1시까지 빠른 업데이트 --> 한 페이지(최신화)만 받아옴
                            System.out.println("ERROR OCCURRED AGAIN IN ID " + id);
                            errorList.add(id);
                            continue;
                        }
                    } else {
                        if ((episodes = getEpisodes(id)) == null) {
                            System.out.println("ERROR OCCURRED AGAIN IN ID " + id);
                            errorList.add(id);
                            continue;
                        }
                    }

                    counts[0] += episodes.size();
                    System.out.println("id : " + id + " " + episodes.size() + "개 업데이트");

                    for (Episode episode : episodes) {
                        errorInsertStatement.setInt(1, episode.getId_e());
                        errorInsertStatement.setInt(2, episode.getEpisode_id());
                        errorInsertStatement.setString(3, episode.getEpisode_title());
                        errorInsertStatement.setFloat(4, episode.getEp_starscore());
                        errorInsertStatement.setString(5, episode.getEp_thumburl());
                        errorInsertStatement.setString(6, episode.getReg_date());
                        errorInsertStatement.setString(7, episode.getEpisode_title());
                        errorInsertStatement.setFloat(8, episode.getEp_starscore());
                        errorInsertStatement.setString(9, episode.getEp_thumburl());
                        errorInsertStatement.setString(10, episode.getReg_date());

                        errorInsertStatement.addBatch();
                        errorInsertStatement.clearParameters();
                    }

                    //유료화 된 웹툰은 유료화 된 회차를 삭제해줘야 함!! --> 마지막에 DB에 현재 가져온 회차보다 회차id가 큰 회차가 있으면 삭제
                    errorDeleteStatement.setInt(1, id);
                    errorDeleteStatement.setInt(2, episodes.size());
                    errorDeleteStatement.addBatch();
                    errorDeleteStatement.clearParameters();
                }
                retryCount++;
            }//while
            System.out.println("=========================SQL Batch 작성 완료.... DB Update를 시작합니다===============================");
            errorInsertStatement.executeBatch();
            if(!doNotDeleteChargedEpisode)
                errorDeleteStatement.executeBatch();
        } catch (SQLException sqlex) {
            System.out.println("SQLException OCCURRED..... EXIT PROGRAM....");
            System.exit(-1);
        }
    }//function
}//class
