package com.example.epcej.coint_mainactivity;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by epcej on 2017-02-28.
 */

public class GetServerData {

    private ArrayList webtoonInfo = new ArrayList();
    COINT_SQLiteManager coint_sqLiteManager = null;
    private Context mContext;

    GetServerData(Context context){
        this.mContext = context;

        coint_sqLiteManager = COINT_SQLiteManager.getInstance(context);
        getWebtoon getWebtoon = new getWebtoon();
        getEpisode getEpisode = new getEpisode();
        getWeekday getWeekday = new getWeekday();
        getGenre getGenre = new getGenre();

        getWebtoon.execute();
        getEpisode.execute();
        getWeekday.execute();
        getGenre.execute();
    }

    //ToonList_Client.jsp
    private class getWebtoon extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
            try {
                /*URL url = new URL("http://192.168.0.49:8080/ToonList_Client.jsp"); //연결 url 설정*/
                URL url = new URL("http://10.0.2.2:8080/ToonList_Client.jsp"); //연결 url 설정

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
                    }else {
                        throw new Exception("CONNECTION ERR");
                    }
                    connection.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Toast.makeText(mContext , "된당!", Toast.LENGTH_SHORT).show();
            }
            return jsonHtml.toString();
        }

        protected void onPostExecute(String str) {          //JSON 파싱부분
            String title, artist, thumburl;
            float starscore;
            int id, likes, hits, isUpdated;        //is_update : 휴재면 2
            char toonType;
            boolean isCharged, isAdult;

            try {
                JSONObject root = new JSONObject(str);

                if(root.isNull("result") != true){                                          //JSON으로 파싱할 내용이 있는지 검사
                    JSONArray jsonArray = root.getJSONArray("result");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        ArrayList arrayList = new ArrayList();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        id = Integer.parseInt( jsonObject.getString("Id"));
                        title = jsonObject.getString("Title");
                        artist = jsonObject.getString("Artist");
                        starscore = Float.parseFloat(jsonObject.getString("Starscore"));
                        hits = Integer.parseInt(jsonObject.getString("Hits"));
                        thumburl = jsonObject.getString("Thumburl");
                        likes = Integer.parseInt( jsonObject.getString("Likes"));
                        toonType =  jsonObject.getString("Toontype").charAt(0);
                        isAdult = Boolean.parseBoolean(jsonObject.getString("Is_adult"));
                        isCharged = Boolean.parseBoolean(jsonObject.getString("Is_charged"));
                        isUpdated = Integer.parseInt(jsonObject.getString("Is_updated"));

                        Webtoon webtoon = new Webtoon(id, title, artist, starscore, thumburl, likes, hits, toonType, isCharged, isAdult, isUpdated);

                        coint_sqLiteManager.insertWebtoon(webtoon);
                        /*webtoonInfo.add(arrayList);*/
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i("log", "ERR");
            }
        }
    }

    //Episode_Client.jsp
    private class getEpisode extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
            try {
                /*URL url = new URL("http://192.168.0.49:8080/ToonList_Client.jsp"); //연결 url 설정*/
                URL url = new URL("http://10.0.2.2:8080/Episode_Client.jsp?id=20853"); //연결 url 설정
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
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.i("log", "ERR");
            }
            return jsonHtml.toString();
        }

        protected void onPostExecute(String str) {          //JSON 파싱부분
            String epTitle, epThumburl, mention;
            int epId, idE, likesE;
            String regDate;
            float epStarscore;
            try {
                JSONObject root = new JSONObject(str);

                if(root.isNull("result") != true){                                          //JSON으로 파싱할 내용이 있는지 검사
                    JSONArray jsonArray = root.getJSONArray("result");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        ArrayList arrayList = new ArrayList();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        idE = Integer.parseInt(jsonObject.getString("Id_E"));
                        epId = Integer.parseInt(jsonObject.getString("Episode_id"));
                        epTitle = jsonObject.getString("Episode_title");
                        epStarscore = Float.parseFloat(jsonObject.getString("Ep_starscore"));
                        epThumburl = jsonObject.getString("Ep_thumburl");
                        regDate = jsonObject.getString("Reg_date");
                        mention = jsonObject.getString("Mention");
                        likesE = Integer.parseInt(jsonObject.getString("Likes_E"));

/*                        SimpleDateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = null;

                        try{
                            date = toFormat.parse(regDate);
                        }catch (ParseException e){
                            e.printStackTrace();
                            date = new Date();
                        }

                        toFormat.format(date);*/

                        Episode episode = new Episode(idE, epId, epTitle, epStarscore, epThumburl, regDate, mention, likesE);
                        coint_sqLiteManager.insertEpsode(episode);

                        Log.i("log", epTitle);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //Weekday.jsp
    private class getWeekday extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
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
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return jsonHtml.toString();
        }

        protected void onPostExecute(String str) {          //JSON 파싱부분
            int idW, weekday;
            try {
                JSONObject root = new JSONObject(str);

                if(root.isNull("result") != true){                                          //JSON으로 파싱할 내용이 있는지 검사
                    JSONArray jsonArray = root.getJSONArray("result");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        ArrayList arrayList = new ArrayList();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        idW = Integer.parseInt(jsonObject.getString("id"));
                        weekday = Integer.parseInt(jsonObject.getString("weekday"));

                        arrayList.add(idW);
                        arrayList.add(weekday);

                        Weekday weekday1 = new Weekday(idW, weekday);
                        coint_sqLiteManager.insertWeekday(weekday1);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //Genre.jsp
    private class getGenre extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
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
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return jsonHtml.toString();
        }

        protected void onPostExecute(String str) {          //JSON 파싱부분
            int idG;
            String genre;
            try {
                JSONObject root = new JSONObject(str);

                if(root.isNull("result") != true){                                          //JSON으로 파싱할 내용이 있는지 검사
                    JSONArray jsonArray = root.getJSONArray("result");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        ArrayList arrayList = new ArrayList();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        idG = Integer.parseInt(jsonObject.getString("id"));
                        genre = jsonObject.getString("genre");

                        Genre genre1 = new Genre(idG, genre);
                        coint_sqLiteManager.insertGenre(genre1);
                        /*webtoonInfo.add(arrayList);*/
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
