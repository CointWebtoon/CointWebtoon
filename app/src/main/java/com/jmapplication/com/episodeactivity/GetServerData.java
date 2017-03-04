package com.jmapplication.com.episodeactivity;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by epcej on 2017-02-28.
 */

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

    private void updateObserver(){
        for(Observer observer : observers){
            observer.update(this, null);
        }
    }
    //--------------Observer Pattern-----------end---//

    /*
     * 메인 액티비티에서 스플래시 액티비티를 띄우기 전에 실행해놓아야 할 웹툰 목록 업데이트 메소드
     * 액티비티를 Observer 를 구현한 클래스로 만들고 update 메소드에 목록 업데이트가 완료되면 할 행동을 지정해주면 되겠다!
     */
    public void getWebtoonFromServer(){
        GetWebtoon webtoon =  new GetWebtoon();
        webtoon.execute();
    }

    /*
     * 서버에서 SQLite 로 회차정보 가져오는 메소드
     * Input Parameter : 회차정보를 얻어올 웹툰의 고유 ID
     * Episode Activity 에서 밖에 사용할 일이 없음!!
     */
    public void getEpisodesFromServer(int toonId){
        GetEpisode episode = new GetEpisode();
        episode.execute(toonId);
    }

    //ToonList_Client.jsp
    private class GetWebtoon extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... urls) {
            Log.i("PROGRESS","GETWEBTOON STARTED");

            ArrayList<Webtoon> webtoons = new ArrayList<>();
            StringBuilder jsonHtml = new StringBuilder();

            try {
                URL url = new URL("http://10.0.2.2:8080/ToonList_Client.jsp"); //연결 url 설정 --> 추후에 서버 IP로 URL 변경하여야 함
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
                        throw new Exception("CONNECTION ERR");
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
                                isAdult = Boolean.parseBoolean(jsonObject.getString("Is_adult"));
                                isCharged = Boolean.parseBoolean(jsonObject.getString("Is_charged"));
                                isUpdated = Integer.parseInt(jsonObject.getString("Is_updated"));

                                webtoons.add(new Webtoon(id, title, artist, starscore, thumburl, likes, hits, toonType, isCharged, isAdult, isUpdated));
                            }
                            coint_sqLiteManager.insertWebtoon(webtoons);
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
                URL url = new URL("http://10.0.2.2:8080/Episode_Client.jsp?id=" + toonIds[0].toString()); //연결 url 설정
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
                    throw new Exception("GET EPISODE ERR");
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
                        episodes.add(new Episode(idE, epId, epTitle, epStarscore, epThumburl, regDate, mention, likesE));
                    }
                    coint_sqLiteManager.insertEpsode(episodes);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            updateObserver();
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
                URL url = new URL("http://10.0.2.2:8080/Weekday.jsp"); //연결 url 설정
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
                URL url = new URL("http://10.0.2.2:8080/Genre.jsp"); //연결 url 설정
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
            updateObserver();
            super.onPostExecute(aVoid);
        }
    }
}
