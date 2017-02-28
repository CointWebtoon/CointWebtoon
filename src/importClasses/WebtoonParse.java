package importClasses;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;

public class WebtoonParse {
    private static final int ERR_CODE = -1;
    private static final float ERR_FLOAT_CODE = -1;
    private static final String ERR_STRING = "ERR";
    private static final int
            mon = 1,
            tue = 2,
            wed = 3,
            thu = 4,
            fri = 5,
            sat = 6,
            sun = 7;

    private ArrayList<String> genreType;//장르의 종류가 들어갈 ArrayList
    private ArrayList<Webtoon> webtoons; //웹툰의 목록이 들어갈 ArrayList

    //DB에 사용될 변수들
    private Connection con = null;
    private PreparedStatement pst = null;
    private String webtoonInsertSQL = "INSERT INTO WEBTOON(Id, Title, Artist, StarScore, Thumburl, Toontype, Is_charged, Is_updated) " +
            "VALUES(?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE Title=?, Artist=?, StarScore=?, Thumburl=?, Toontype=?, Is_charged=?, Is_updated=?";
    //웹툰에 대해 해당 키가 이미 존재하면 update문을 실행하고 존재하지 않으면 insert문을 수행한다.
    private String weekdayInsertSQL = "INSERT IGNORE INTO WEEKDAY(Id_W, Weekday) VALUES(?,?)";
    private String genreInsertSQL = "INSERT IGNORE INTO GENRE(Id_G, Genre) VALUES(?,?)";

    //Constructor
    public WebtoonParse() {
        webtoons = new ArrayList<>();
        genreType = new ArrayList<>();
        genreType.add("daily");
        genreType.add("comic");
        genreType.add("fantasy");
        genreType.add("action");
        genreType.add("drama");
        genreType.add("pure");
        genreType.add("sensibility");
        genreType.add("thrill");
        genreType.add("historical");
        genreType.add("sports");
    }

    /*
  웹툰의 고유 ID를 얻어오는 메소드
  Input Parameter : Jsoup으로 얻어온 웹툰 정보가 들어있는 HTML 한 블럭(Element type)
  Output : 웹툰의 고유 ID
 */
    private int parseId(Element element) {
        /*
        <dd class="desc">
            <a href="#" onclick="return artistAction.viewArtist('679519', this)">자까</a>
        onclick 부분을 ' 를 기준으로 잘라 id를 얻어온다.
        얻어오고 싶은 부분은 679519이므로 split 후 index 1을 얻어온다.
         */
        try {
            String onClick = element.select(".desc > a").first().attr("onclick");
            int returnStartIndex = onClick.indexOf("return");   //return 부분을 찾아서 return 전의 String 들은 날려버림
            if (returnStartIndex != 0) {
                onClick = onClick.substring(returnStartIndex, onClick.length() - 1);
            }
            String[] onClickSplit = onClick.split("\'");
            return Integer.parseInt(onClickSplit[1]);
        } catch (NullPointerException npex) {
            return ERR_CODE;
        }
    }

    /*
    웹툰의 제목을 가져오는 메소드
     Input Parameter : Jsoup으로 얻어온 웹툰 정보가 들어있는 HTML 한 블럭(Element type)
     Output : 웹툰 제목
     */
    private String parseTitle(Element element) {
        try {
    /*
        <div class="thumb">
            <a href="/webtoon/list.nhn?titleId=679519&amp;weekday=mon" title="대학일기">
    */
            return element.select(".thumb > a").first().attr("title");
        } catch (NullPointerException exx) {
            return ERR_STRING;
        }
    }

    /*
     웹툰 작가명 가져오는 메소드
     Input Parameter : Jsoup으로 얻어온 웹툰 정보가 들어있는 HTML 한 블럭(Element type)
     Output : 해당 웹툰 작가명
     */
    private String parseArtist(Element element) {
        try {
            /*
            <dd class="desc">
                <a href="#" onclick="return artistAction.viewArtist('679519', this)">자까</a>
             */
            return element.select(".desc > a").first().text();
        } catch (NullPointerException exx) {
            return ERR_STRING;
        }
    }

    /*
    웹툰 별점 가져오는 메소드
     Input Parameter : Jsoup으로 얻어온 웹툰 정보가 들어있는 HTML 한 블럭(Element type)
     Output : 해당 웹툰 전체 평균 평점
     */
    private float parseStarScore(Element element) {
        try {
            /*
            <div class="rating_type">
                <strong>9.95</strong>
             */
            return Float.parseFloat(element.select(".rating_type > strong").first().text());
        } catch (NullPointerException exx) {
            return ERR_FLOAT_CODE;
        }
    }

    /*
    웹툰 썸네일 URL 가져오는 메소드
     Input Parameter : Jsoup으로 얻어온 웹툰 정보가 들어있는 HTML 한 블럭(Element type)
     Output : 웹툰 썸네일 URL
     */
    private String parseThumbURL(Element element) {
        try {
            /*
            <div class="thumb">
                <a href="/webtoon/list.nhn?titleId=679519&amp;weekday=mon" title="대학일기">
                    <img src="http://thumb.comic.naver.net/webtoon/679519/thumbnail/title_thumbnail_20160601180804_t83x90.jpg">
             */
            return element.select(".thumb > a > img").first().attr("src");
        } catch (NullPointerException exx) {
            return ERR_STRING;
        }
    }

    /*
    웹툰이 일반툰인지 컷툰인지 얻어오는 메소드
     Input Parameter : Jsoup으로 얻어온 웹툰 정보가 들어있는 HTML 한 블럭(Element type)
     Output : true - 컷툰, false - 일반툰
     */
    private boolean parseIs_cuttoon(Element element) {
        try {
            //<span class="ico_cut">컷툰</span>
            if (element.select(".ico_cut").first().text().equals("컷툰"))
                return true;
            else
                return false;
        } catch (Exception ex) {
            return false;
        }
    }

    /*
    웹툰이 일반툰인지 스마트툰인지 얻어오는 메소드
    Input Parameter : Jsoup 으로 얻어온 웹툰 정보가 들어있는 HTML 한 블럭(Element type)
    Output : true - 스마트툰, false - 일반툰
 */
    private boolean parseIs_smarttoon(Element element) {
        try {
            //<span class="ico_smart">스마트툰</span>
            if (element.select(".ico_smart").first().text().equals("스마트툰"))
                return true;
            else
                return false;
        } catch (Exception ex) {
            return false;
        }
    }

    /*
    웹툰이 스토어 웹툰(유료 웹툰)인지 얻어오는 메소드
    Input Parameter : Jsoup 으로 얻어온 웹툰 정보가 들어있는 HTML 한 블럭(Element type)
    Output : true - 유료, false - 무료
 */
    private boolean parseIs_charged(Element element) {
        try {
            //<em class="ico_store">스토어</em>
            if (element.select(".ico_store").first().text().equals("스토어"))
                return true;
            else
                return false;
        } catch (Exception ex) {
            return false;
        }
    }

    /*
     *웹툰이 최근에 업데이트 되었는지 확인하는 메소드(UP)
     * Input Parameter : Jsoup 으로 얻어온 웹툰 정보가 들어있는 HTML 한 블럭(Element type)
     * Output : true - UP false - 최근 업데이트 아님
     */
    private boolean parseIs_updated(Element element){
        try {
            //<em class="ico_store">스토어</em>
            if (element.select(".ico_updt").first().text() != null)
                return true;
            else
                return false;
        } catch (Exception ex) {
            return false;
        }
    }

    /*
 *휴재중인 웹툰인지 확인하는 메소드
 * Input Parameter : Jsoup 으로 얻어온 웹툰 정보가 들어있는 HTML 한 블럭(Element type)
 * Output : true - 휴재 false - 정상연재
 */
    private boolean parseIs_break(Element element){
        try {
            if (element.select(".ico_break").first().text() != null)
                return true;
            else
                return false;
        } catch (Exception ex) {
            return false;
        }
    }



    /*
    위 파싱 메소드들을 이용하여 ArrayList에 집어넣는 코드
     Input Parameter : 가져올 요일의 정보(int --> 선언부 윗부분 final 변수 참고)
     Output : boolean --> 성공적으로 가져왔는지 확인 후 성공하면 DB에 넣자
     */
    public boolean getWebtoonArrayList(final int weekdayNumber) {
        String baseURL = weekdayNumber > 0 ? "http://comic.naver.com/webtoon/weekdayList.nhn?week=" : "http://comic.naver.com/webtoon/finish.nhn";
        String weekdayString = "", URL;
        //가져올 데이터들
        int id;                                         //웹툰 고유 ID
        String title;                               //웹툰 제목
        String artist;                             //작가명
        float starScore;                   //평균 평점
        String thumbURL;                    //썸네일 URL
        boolean is_cuttoon;            //컷툰, 일반툰 구분
        boolean is_smarttoon;       //스마트툰,
        boolean is_charged;         //유료
        int is_updated;
        String genre;                       //장르

        if (weekdayNumber > 0) {
            switch (weekdayNumber) {
                case mon:
                    weekdayString = "mon";
                    break;
                case tue:
                    weekdayString = "tue";
                    break;
                case wed:
                    weekdayString = "wed";
                    break;
                case thu:
                    weekdayString = "thu";
                    break;
                case fri:
                    weekdayString = "fri";
                    break;
                case sat:
                    weekdayString = "sat";
                    break;
                case sun:
                    weekdayString = "sun";
                    break;
                default://finished, .....
                    weekdayString = "";
                    break;
            }//가져올 요일에 따라 String 설정
        }
        URL = baseURL + weekdayString;
        try {
            Document doc = Jsoup.connect(URL).timeout(30 * 1000).get();
            Elements toons = doc.select(".img_list > li");
            Webtoon insertElement;
            for (Element toon : toons) {//id, title, artist, starscore, thumbURL, is_cuttoon, is_smarttoon
                //각 메소드들을 나누어서 하는 이유는 메소드마다 try-catch문을 삽입하여 각각 예외를 처리하기 위해
                //나누지 않으면 코드가 길어질 수 있음
                id = parseId(toon);
                title = parseTitle(toon);
                artist = parseArtist(toon);
                starScore = parseStarScore(toon);
                thumbURL = parseThumbURL(toon);
                is_cuttoon = parseIs_cuttoon(toon);
                is_smarttoon = parseIs_smarttoon(toon);
                is_charged = parseIs_charged(toon);
                if(parseIs_break(toon)){
                    is_updated = 2;
                }else if(parseIs_updated(toon)){
                    is_updated = 1;
                }else{
                    is_updated = 0;
                }

                insertElement = new Webtoon(id, title, artist, starScore, thumbURL, is_cuttoon, is_smarttoon,is_charged, is_updated ,weekdayNumber);
                if (webtoons.contains(insertElement)) {//만약 ArrayList 안에 해당 id의 웹툰이 있다면 요일만 업데이트한다. --> 요일은 HashSet으로 구현되어 중복된 값이 들어가지 않으므로 문제X
                    webtoons.get(webtoons.indexOf(insertElement)).getWeekday().add(Integer.valueOf(weekdayNumber));
                } else//ArrayList에 현재 toon id를 가진 웹툰이 없다면 add
                    webtoons.add(insertElement);
            }

            ////////////////////////////////////////////////////////////////////////////장르업데이트부분///////////////////////////////////////////////////////////////////////////////
            baseURL = "http://comic.naver.com/webtoon/genre.nhn?genre=";

            Webtoon redaundancyCheckInstance = new Webtoon(-1, null, null, -1, null, false, false, false ,-1,-1);
            //id 중복 확인을 위해 만들어진 임의의 객체
            for(String genreString : genreType){
                doc = Jsoup.connect(baseURL + genreString).timeout(30 * 1000).get();
                toons = doc.select(".img_list > li");
                for(Element toon : toons){
                    id = parseId(toon);
                    genre = genreString.toUpperCase();
                    redaundancyCheckInstance.setId(id);//id를 현재 id로 설정하고 겹치는 아이디가 있는지 확인(equals)
                    if (webtoons.contains(redaundancyCheckInstance)) {//만약 ArrayList 안에 해당 id의 웹툰이 있다면 장르만 업데이트한다. --> 장르는 HashSet으로 구현되어 중복된 값이 들어가지 않으므로 문제X
                        webtoons.get(webtoons.indexOf(redaundancyCheckInstance)).getGenre().add(genre);
                    }
                }
            }
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        } catch (Exception e) {
            e.printStackTrace();
            return false; //예외가 발생하면 false return
        }

        return true;    //모든 작업을 예외, 오류 없이 완료 했다면 true를 return
    }

    /*
    getWebtoonArrayList 메소드를 이용하여 생성된 ArrayList를 이용해
    DB에 데이터를 삽입, 업데이트 하는 메소드
    Input Parameter : 생성된 Webtoon 형 ArrayList
    Output : 수행 결과 true-성공 false-실패
     */
    public boolean insertToDB() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(DBAuthentication.url, DBAuthentication.id, DBAuthentication.password);//드라이버 로드

            con.setAutoCommit(false);               //쿼리들을 모았다가 한번에 수행하기 위해서 수동 커밋 --> Transaction All or Nothing
            pst = con.prepareStatement(webtoonInsertSQL);
            PreparedStatement finishedPst = con.prepareStatement("DELETE FROM WEEKDAY WHERE Weekday>0 AND Id_W=?");
            PreparedStatement weekdayPst = con.prepareStatement("DELETE FROM WEEKDAY WHERE Weekday=0 AND Id_W=?");

            for (Webtoon webtoon : webtoons) {
                //현재 values 확인
                //System.out.println(webtoon.getId() + " " + webtoon.getTitle() + " " + webtoon.getArtist() + " " + webtoon.getStarScore() + " " + webtoon.getThumbURL() + " " + webtoon.isCuttoon());

                //파라미터 설정
                pst.setInt(1, webtoon.getId());
                pst.setString(2, webtoon.getTitle());
                pst.setString(3, webtoon.getArtist());
                pst.setFloat(4, webtoon.getStarScore());
                pst.setString(5, webtoon.getThumbURL());
                if(webtoon.isCuttoon())
                    pst.setString(6,"C");
                else if(webtoon.isSmarttoon())
                    pst.setString(6, "S");
                else
                    pst.setString(6, "G");
                if(webtoon.isCharged())
                    pst.setInt(7, 1);
                else
                    pst.setInt(7, 0);
                pst.setInt(8, webtoon.isUpdated());
                pst.setString(9, webtoon.getTitle());
                pst.setString(10, webtoon.getArtist());
                pst.setFloat(11, webtoon.getStarScore());
                pst.setString(12, webtoon.getThumbURL());
                if(webtoon.isCuttoon())
                    pst.setString(13,"C");
                else if(webtoon.isSmarttoon())
                    pst.setString(13, "S");
                else
                    pst.setString(13, "G");
                if(webtoon.isCharged())
                    pst.setInt(14, 1);
                else
                    pst.setInt(14, 0);
                pst.setInt(15, webtoon.isUpdated());
                pst.addBatch(); //SQL을 트랜잭션에 추가
                pst.clearParameters();  //pst 객체를 재사용하기 위해서 꼭 호출해 주어야 하는 메소드
            }

            pst.executeBatch(); //완성된 트랜잭션을 수행 --> 기대 : 이렇게 한다면 중간에 오류가 나도 아무것도 INSERT, UPDATE 되지 않을것이다.(Atomicity)
            con.commit();   //트랜잭션 수행 완료 후 수동으로 Commit
            pst.close();    //하나의 statement를 완료하였으므로 close

            pst = con.prepareStatement(weekdayInsertSQL);

            for(Webtoon webtoon : webtoons){  //모든 웹툰에 대해
                HashSet<Integer> weekdays = webtoon.getWeekday();
                for(Integer weekdayValue : weekdays)    //요일이 여러 개인 경우를 처리
                {
                    if(weekdayValue == 0){
                        finishedPst.setInt(1, webtoon.getId());
                        finishedPst.addBatch();
                        finishedPst.clearParameters();
                    }//완결웹툰이면 다른 요일 삭제
                    else{
                        weekdayPst.setInt(1, webtoon.getId());
                        weekdayPst.addBatch();
                        weekdayPst.clearParameters();
                    }
                    pst.setInt(1, webtoon.getId());
                    pst.setInt(2, weekdayValue);
                    pst.addBatch();
                    pst.clearParameters();
                }
            }

            weekdayPst.executeBatch();//요일 웹툰 시 완결 delete 문
            finishedPst.executeBatch();  //완결 웹툰 시 요일 delete 문
            pst.executeBatch(); //완성된 트랜잭션을 수행 --> 기대 : 이렇게 한다면 중간에 오류가 나도 아무것도 INSERT, UPDATE 되지 않을것이다.(Atomicity)
            con.commit();   //트랜잭션 수행 완료 후 수동으로 Commit
            pst.close();    //하나의 statement를 완료하였으므로 close

            pst = con.prepareStatement(genreInsertSQL);

            for(Webtoon webtoon : webtoons){  //모든 웹툰에 대해
                HashSet<String> genres = webtoon.getGenre();
                for(String genre : genres)    //장르가 여러 개인 경우를 처리
                {
                    pst.setInt(1, webtoon.getId());
                    pst.setString(2, genre);

                    pst.addBatch();
                    pst.clearParameters();
                }
            }

            pst.executeBatch(); //완성된 트랜잭션을 수행 --> 기대 : 이렇게 한다면 중간에 오류가 나도 아무것도 INSERT, UPDATE 되지 않을것이다.(Atomicity)
            con.commit();   //트랜잭션 수행 완료 후 수동으로 Commit
            pst.close();    //하나의 statement를 완료하였으므로 close

            con.close();    //모두 끝나면 연결 종료
        } catch (SQLException sqex) {//예외들이 발생하면 false(실패)를 return 하고 오류메시지 출력
            System.out.println("SQLException\n" + sqex.getMessage());
            return false;
        } catch (ClassNotFoundException cnfex) {
            System.out.println("ClassNotFoundException\n" + cnfex.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("EXCEPTION\n" + e.getMessage());
            return false;
        } finally {//꼭 해주어야 하는 작업(리소스 관리)
            try {
                pst.close();
            } catch (Exception e) {//이미 해제 된 경우
            }
            try {
                con.close();
            } catch (Exception ex) {//이미 해제 된 경우
            }
        }
        return true; //모든 작업을 성공적으로 수행하였으므로 true return
    }

    /*
    ArrayList에 ERR_STRING, ERR_FLOAT_CODE, ERR_STRING이 들어갔는지 확인하는 메소드 --> 오류검사
    InputParameter : 오류검사 할 자료형(ArrayList<Webtoon>)
    Output : 검사 결과 true - ERR, false - 오류 없음
     */
    public boolean IsErrorOccurred() {
        boolean result = false;
        for (Webtoon webtoon : webtoons) {
            if (webtoon.getId() == ERR_CODE
                    || webtoon.getWeekday().contains(Integer.valueOf(ERR_CODE))
                    || webtoon.getThumbURL().equals(ERR_STRING)
                    || webtoon.getArtist().equals(ERR_STRING)
                    || webtoon.getStarScore() == ERR_FLOAT_CODE
                    || webtoon.getTitle().equals(ERR_STRING)
                    ) {  //에러 값이 들어갔다면 에러가 난 부분을 출력하고 true return
                webtoon.printToon();
                result = true;
            }
        }

        ArrayList<Integer> checkList = new ArrayList<>();

        try{
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(DBAuthentication.url, DBAuthentication.id, DBAuthentication.password);//드라이버 로드
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select Id from webtoon");
            while(rs.next()){
                checkList.add(rs.getInt(1));
            }
            for(Webtoon webtoon : webtoons){
                checkList.remove(Integer.valueOf(webtoon.getId()));
            }
            if(checkList.size() > 0){
                System.out.println(checkList);
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            try{con.close();}catch (Exception e){}
        }
        return result;
    }

    //ArrayList getter Method
    public ArrayList<Webtoon> getWebtoons() {
        return webtoons;
    }
}
