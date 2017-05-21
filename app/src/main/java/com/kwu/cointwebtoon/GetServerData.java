package com.kwu.cointwebtoon;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.kwu.cointwebtoon.DataStructure.Comment;
import com.kwu.cointwebtoon.DataStructure.Episode;
import com.kwu.cointwebtoon.DataStructure.Genre;
import com.kwu.cointwebtoon.DataStructure.Webtoon;
import com.kwu.cointwebtoon.DataStructure.Weekday;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import static android.R.attr.id;

public class GetServerData extends Observable{
    private static ArrayList<Observer> observers = new ArrayList<>();
    private static COINT_SQLiteManager coint_sqLiteManager = null;

    GetServerData(Context context) {
        coint_sqLiteManager = COINT_SQLiteManager.getInstance(context);
    }


    //--------------Observer Pattern-----------------//
    public void registerObserver(Observer observer){
        observers.add(observer);
    }

    public void removeObserver(Observer observer){
        observers.remove(observer);
    }

    private void updateObservers(){
        for(Observer observer : observers){
            observer.update(this, null);
        }
    }

    private void updateObservers(Object objectToSend){
        for(Observer observer : observers){
            observer.update(this, objectToSend);
        }
    }
    //--------------Observer Pattern-----------end---//

    /**
     * 메인 액티비티에서 스플래시 액티비티를 띄우기 전에 실행해놓아야 할 웹툰 목록 업데이트 메소드
     * 액티비티를 Observer 를 구현한 클래스로 만들고 update 메소드에 목록 업데이트가 완료되면 할 행동을 지정해주면 되겠다!
     */
    public void getWebtoonFromServer(){
        GetWebtoon webtoon =  new GetWebtoon();
        webtoon.execute();
    }

    /**
     * 서버에서 SQLite 로 회차정보 가져오는 메소드
     * Input Parameter : 회차정보를 얻어올 웹툰의 고유 ID
     * Episode Activity 에서 밖에 사용할 일이 없음!!
     */
    public void getEpisodesFromServer(int toonId){
        GetEpisode episode = new GetEpisode();
        episode.execute(toonId);
    }

    /**
     * 서버에서 이미지 목록을 가져오는 메소드 --> Viewer 에서 사용
     * @param toonId : 웹툰 고유 ID
     * @param episodeId : 에피소드 ID
     */
    public void getImagesFromServer(int toonId, int episodeId){
        String url = "http://coint.iptime.org:8080/Image_Client.jsp?id="  + String.valueOf(toonId) + "&ep_id=" + String.valueOf(episodeId);
        GetImage getImage = new GetImage();
        getImage.execute(url);
    }

    /**
     * 서버의 웹툰의 조회수를 올려주는 메소드
     * @param id : 웹툰 고유 ID
     */
    public void plusHit(int id){
        String url = "http://coint.iptime.org:8080/Hits.jsp?id=" + id;
        URLRequest request = new URLRequest();
        request.execute(url);
    }

    /**
     * 서버의 웹툰의 좋아요를 올리고 내리는 메소드
     * @param id : 웹툰 고유 ID
     * @param plusMinus : 좋아요를 올리는 행동이면 'plus' 내리는 행동이면 'minus'
     */
    public void likeWebtoon(int id, String plusMinus){
        String url = "http://coint.iptime.org:8080/Likes.jsp?type=webtoon&id=" + id + "&value=" + plusMinus;
        URLRequest request = new URLRequest();
        request.execute(url);
    }

    public void getComments(String url){
        GetComment comment = new GetComment();
        comment.execute(url);
    }

    /**
     * 서버의 댓글의 좋아요를 올리는 메소드
     * @param commentID : 댓글 고유 ID
     */
    public void likeComment(int commentID){
        String url = "http://coint.iptime.org:8080/Comment_Like.jsp?comment_id=" + commentID;
        URLRequest request = new URLRequest();
        request.execute(url);
    }

    /**
     * 서버의 댓글을 삭제하는 메소드
     * @param commentID : 댓글 고유 ID
     */
    public void deleteComment(int commentID){
        String url = "http://coint.iptime.org:8080/Comment_Delete.jsp?comment_id=" + commentID;
        URLRequest request = new URLRequest();
        request.execute(url);
    }

    public void addComment(Comment comment){
        String url = "http://coint.iptime.org:8080/Comment_Add.jsp?id=" + comment.getId() + "&ep_id=" + comment.getEp_id() +
                "&writer=" + comment.getWriter() + "&nickname=" + URLEncoder.encode(comment.getNickname()) + "&content=" + URLEncoder.encode(comment.getContent());
        URLRequest request = new URLRequest();
        request.execute(url);
    }

    /**
        GetWebtoon 클래스, GetWeekday 클래스, GetGenre 클래스는 연쇄적으로 호출되도록 구성하였다.
        GetWebtoon의 AsyncTask를 execute하게 되면 연쇄적으로 GetWeekday, GetGenre가 자동으로 호출되므로
        따로 호출해줄 필요 없다! 이 과정은 getWebtoonFromServer라는 메소드를 통해서만 진행하도록 하자.
     */
    //ToonList_Client.jsp
    private class GetWebtoon extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... urls) {
            Log.i("PROGRESS","GETWEBTOON STARTED");

            ArrayList<Webtoon> webtoons = new ArrayList<>();
            StringBuilder jsonHtml = new StringBuilder();

            try {
                URL url = new URL("http://coint.iptime.org:8080/ToonList_Client.jsp"); //연결 url 설정 --> 추후에 서버 IP로 URL 변경하여야 함
                HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //커넥션 객체 생성
                if (connection != null) {
                    connection.setConnectTimeout(10000);
                    connection.setUseCaches(false);
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "EUC-KR"));
                        for (; ; ) {
                            //웹상에 보여지는 텍스트를 라인단위로 읽어 저장
                            String line = bufferedReader.readLine();
                            if (line == null)
                                break;
                            jsonHtml.append(line + "\n");
                        }
                        bufferedReader.close();
                    } else {
                        throw new Exception("WEBTOON CONNECTION ERR");
                    }
                    connection.disconnect();

                    String title, artist, thumburl;
                    float starscore;
                    int id, likes, hits, isUpdated;        //is_update : 휴재면 2
                    char toonType;
                    boolean isCharged, isAdult;
                        JSONObject root = new JSONObject(jsonHtml.toString());
                        if (root.isNull("result") != true) {                                          //JSON으로 파싱할 내용이 있는지 검사
                            JSONArray jsonArray = root.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                id = Integer.parseInt(jsonObject.getString("Id"));
                                title = jsonObject.getString("Title");
                                artist = jsonObject.getString("Artist");
                                starscore = Float.parseFloat(jsonObject.getString("Starscore"));
                                hits = Integer.parseInt(jsonObject.getString("Hits"));
                                thumburl = jsonObject.getString("Thumburl");
                                likes = Integer.parseInt(jsonObject.getString("Likes"));
                                toonType = jsonObject.getString("Toontype").charAt(0);
                                isAdult = Integer.parseInt(jsonObject.getString("Is_adult")) == 1 ? true : false;
                                isCharged = Integer.parseInt(jsonObject.getString("Is_charged")) == 1 ? true : false;
                                isUpdated = Integer.parseInt(jsonObject.getString("Is_updated"));

                                webtoons.add(new Webtoon(id, title, artist, starscore, thumburl, likes, hits, toonType, isCharged, isAdult, isUpdated));
                            }
                            coint_sqLiteManager.insertWebtoon(webtoons);//서버에서 웹툰 목록 받아서 SQLite 업데이트
                        }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            GetWeekday weekday = new GetWeekday();
            weekday.execute();
            super.onPostExecute(aVoid);
        }
    }
    //Weekday.jsp
    private class GetWeekday extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... urls) {
            Log.i("PROGRESS","GETWEEKDAY STARTED");

            int idW, weekdayValue;
            StringBuilder jsonHtml = new StringBuilder();
            ArrayList<Weekday> weekdays = new ArrayList<>();

            try {
                /*URL url = new URL("http://192.168.0.49:8080/ToonList_Client.jsp"); //연결 url 설정*/
                URL url = new URL("http://coint.iptime.org:8080/Weekday.jsp"); //연결 url 설정
                HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //커넥션 객체 생성
                if (connection != null) {
                    connection.setConnectTimeout(10000);
                    connection.setUseCaches(false);

                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "EUC-KR"));
                        for (; ; ) {
                            //웹상에 보여지는 텍스트를 라인단위로 읽어 저장
                            String line = bufferedReader.readLine();
                            if (line == null)
                                break;
                            jsonHtml.append(line + "\n");
                        }
                        bufferedReader.close();
                    }
                    connection.disconnect();
                }else{
                    throw new Exception("WEEKDAY CONNECTION ERR");
                }
                JSONObject root = new JSONObject(jsonHtml.toString());

                if (root.isNull("result") != true) {                                          //JSON으로 파싱할 내용이 있는지 검사
                    JSONArray jsonArray = root.getJSONArray("result");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        ArrayList arrayList = new ArrayList();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        idW = Integer.parseInt(jsonObject.getString("id"));
                        weekdayValue = Integer.parseInt(jsonObject.getString("weekday"));

                        arrayList.add(idW);
                        arrayList.add(weekdayValue);

                        weekdays.add(new Weekday(idW, weekdayValue));
                    }
                    coint_sqLiteManager.insertWeekday(weekdays);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            GetGenre genre = new GetGenre();
            genre.execute();
            super.onPostExecute(aVoid);
        }
    }
    //Genre.jsp
    private class GetGenre extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... aVoid) {
            Log.i("PROGRESS","GETGENRE STARTED");

            int id_G;
            String genreValue;
            StringBuilder jsonHtml = new StringBuilder();
            ArrayList<Genre> genres = new ArrayList<>();

            try {
                /*URL url = new URL("http://192.168.0.49:8080/ToonList_Client.jsp"); //연결 url 설정*/
                URL url = new URL("http://coint.iptime.org:8080/Genre.jsp"); //연결 url 설정
                HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //커넥션 객체 생성
                if (connection != null) {
                    connection.setConnectTimeout(10000);
                    connection.setUseCaches(false);

                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "EUC-KR"));
                        for (; ; ) {
                            //웹상에 보여지는 텍스트를 라인단위로 읽어 저장
                            String line = bufferedReader.readLine();
                            if (line == null)
                                break;
                            jsonHtml.append(line + "\n");
                        }
                        bufferedReader.close();
                    }
                    connection.disconnect();
                }else{
                    throw new Exception("GENRE CONNECTION ERR");
                }
                JSONObject root = new JSONObject(jsonHtml.toString());

                if (root.isNull("result") != true) {                                          //JSON으로 파싱할 내용이 있는지 검사
                    JSONArray jsonArray = root.getJSONArray("result");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        id_G = Integer.parseInt(jsonObject.getString("id"));
                        genreValue = jsonObject.getString("genre");

                        genres.add(new Genre(id_G, genreValue));
                    }

                    coint_sqLiteManager.insertGenre(genres);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.i("coint","서버에서 데이터 가져오기 완료");
            updateObservers();
            super.onPostExecute(aVoid);
        }
    }

    //Episode_Client.jsp
    private class GetEpisode extends AsyncTask<Integer, Void, Void>{
        @Override
        protected Void doInBackground(Integer... toonIds) {

            String epTitle, epThumburl, mention;
            int epId, idE, likesE;
            String regDate;
            float epStarscore;
            StringBuilder jsonHtml = new StringBuilder();
            ArrayList<Episode> episodes = new ArrayList<>();

            try {
                URL url = new URL("http://coint.iptime.org:8080/Episode_Client.jsp?id=" + toonIds[0].toString()); //연결 url 설정
                HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //커넥션 객체 생성
                if (connection != null) {
                    connection.setConnectTimeout(10000);
                    connection.setUseCaches(false);

                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "EUC-KR"));
                        for (; ; ) {
                            //웹상에 보여지는 텍스트를 라인단위로 읽어 저장
                            String line = bufferedReader.readLine();
                            if (line == null)
                                break;
                            jsonHtml.append(line + "\n");
                        }
                        bufferedReader.close();
                    }
                    connection.disconnect();
                }else{
                    throw new Exception("EPISODE CONNECTION ERR");
                }
                JSONObject root = new JSONObject(jsonHtml.toString());

                if (root.isNull("result") != true) {                                          //JSON으로 파싱할 내용이 있는지 검사
                    JSONArray jsonArray = root.getJSONArray("result");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        idE = Integer.parseInt(jsonObject.getString("Id_E"));
                        epId = Integer.parseInt(jsonObject.getString("Episode_id"));
                        epTitle = jsonObject.getString("Episode_title");
                        epStarscore = Float.parseFloat(jsonObject.getString("Ep_starscore"));
                        epThumburl = jsonObject.getString("Ep_thumburl");
                        regDate = jsonObject.getString("Reg_date");
                        mention = jsonObject.getString("Mention");
                        likesE = Integer.parseInt(jsonObject.getString("Likes_E"));
                        episodes.add(new Episode(idE, epId, epTitle, epStarscore, epThumburl, regDate, mention, likesE, -1));
                    }
                    coint_sqLiteManager.insertEpsode(episodes); //받아온 회차 정보 SQLite에 업데이트
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            updateObservers();
            super.onPostExecute(aVoid);
        }
    }

    //Image_Client.jsp
    private class GetImage extends AsyncTask<String, Void, Void>{
        private ArrayList<String> imageUrls = new ArrayList<>();
        protected Void doInBackground(String... urls){
            StringBuilder jsonHtml = new StringBuilder();
            String image_url;
            try{
                URL url = new URL(urls[0]); //연결 url 설정
                HttpURLConnection connection = (HttpURLConnection)url.openConnection(); //커넥션 객체 생성
                if(connection!=null){
                    connection.setConnectTimeout(10000);
                    connection.setUseCaches(false);
                    if(connection.getResponseCode()==HttpURLConnection.HTTP_OK){
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"EUC-KR"));
                        for(;;){
                            //웹상에 보여지는 텍스트를 라인단위로 읽어 저장
                            String line = bufferedReader.readLine();
                            if (line == null)
                                break;
                            jsonHtml.append(line+"\n");
                        }
                        bufferedReader.close();
                    }
                    connection.disconnect();
                    JSONObject root = new JSONObject(jsonHtml.toString());
                    if(root.isNull("result") != true){
                        JSONArray jsonArray = root.getJSONArray("result");
                        for(int i=0; i<jsonArray.length();i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            image_url = jsonObject.getString("image_url");
                            imageUrls.add(image_url);
                        }
                    }
                }else {
                    throw new Exception("IMAGE CONNECTION ERROR");
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateObservers(imageUrls);
        }
    }

    /**
     * 조회수를 올린다던지, 좋아요를 올리고 내리는 등 페이지에 요청만 하고
     * 데이터를 따로 받아올 필요가 없는 것들을 처리
     */
    private class URLRequest extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... params) {
            try{
                URL url = new URL(params[0]); //연결 url 설정
                HttpURLConnection connection = (HttpURLConnection)url.openConnection(); //커넥션 객체 생성
                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    Log.i("coint", "접속 성공");
                    connection.disconnect();
                }else{
                    throw new Exception(params[0] + " 접속 중 Exception 발생");
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return null;
        }
    }

    /**
     * 댓글을 받아오는 Task
     */
    private class GetComment extends AsyncTask<String, Void, Void>{
        private ArrayList<Comment> comments = new ArrayList<>();
        protected Void doInBackground(String... urls){
            StringBuilder jsonHtml = new StringBuilder();
            String commentid, id, ep_id, writer, nickname, content, like, time, cutnumber;
            try{
                URL url = new URL(urls[0]); //연결 url 설정
                HttpURLConnection connection = (HttpURLConnection)url.openConnection(); //커넥션 객체 생성
                if(connection!=null){
                    connection.setConnectTimeout(10000);
                    connection.setUseCaches(false);
                    if(connection.getResponseCode()==HttpURLConnection.HTTP_OK){
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"EUC-KR"));
                        for(;;){
                            //웹상에 보여지는 텍스트를 라인단위로 읽어 저장
                            String line = bufferedReader.readLine();
                            if (line == null)
                                break;
                            jsonHtml.append(line+"\n");
                        }
                        bufferedReader.close();
                    }
                    connection.disconnect();
                    JSONObject root = new JSONObject(jsonHtml.toString());
                    if(root.isNull("result") != true){
                        JSONArray jsonArray = root.getJSONArray("result");
                        for(int i=0; i<jsonArray.length();i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            commentid = jsonObject.getString("Comment_id");
                            id = jsonObject.getString("Id_C");
                            ep_id = jsonObject.getString("Ep_id");
                            writer = jsonObject.getString("Writer");
                            nickname = jsonObject.getString("Nickname");
                            content = jsonObject.getString("Content");
                            like = jsonObject.getString("Likes_C");
                            time = jsonObject.getString("Time");
                            cutnumber = jsonObject.getString("Cutnumber");
                            comments.add(new Comment(Integer.parseInt(commentid), Integer.parseInt(id),
                                    Integer.parseInt(ep_id), writer, nickname, content, Integer.parseInt(like), time, Integer.parseInt(cutnumber), false));
                        }
                    }
                }else {
                    throw new Exception("IMAGE CONNECTION ERROR");
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateObservers(comments);
        }
    }
}
