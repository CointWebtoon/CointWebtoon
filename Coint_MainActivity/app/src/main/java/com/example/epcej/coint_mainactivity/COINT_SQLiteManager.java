package com.example.epcej.coint_mainactivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

/**
 * Created by epcej on 2017-02-28.
 */

/**
 * Created by jm on 2017-02-27.
 */

public class COINT_SQLiteManager {
    private static final String DBNAME = "COINT.db";
    private static final int DB_VERSION = 1;

    Context mContext = null;

    private static COINT_SQLiteManager manager = null;
    private SQLiteDatabase db = null;

    public static COINT_SQLiteManager getInstance(Context mContext) {
        if (manager == null)
            manager = new COINT_SQLiteManager(mContext);

        return manager;
    }

    private COINT_SQLiteManager(Context mContext) {
        this.mContext = mContext;

        db = mContext.openOrCreateDatabase(DBNAME, Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS WEBTOON(" +
                "Id INTEGER PRIMARY KEY NOT NULL UNIQUE," +
                "Title TEXT," +
                "Artist TEXT," +
                "Starscore REAL," +
                "Hits INTEGER DEFAULT 0," +
                "Thumburl TEXT," +
                "Likes INTEGER DEFAULT 0," +
                "Toontype TEXT," +
                "Is_adult INTEGER," +
                "Is_charged INTEGER," +
                "Is_mine INTEGER DEFAULT 0," +
                "Is_updated INTEGER DEFAULT 0);");

        db.execSQL("CREATE TABLE IF NOT EXISTS EPISODE(" +
                "Id_E INTEGER NOT NULL," +
                "Episode_id INTEGER NOT NULL," +
                "Episode_title TEXT," +
                "Ep_starscore REAL," +
                "Ep_thumburl TEXT," +
                "Reg_date DATE," +
                "Mention TEXT," +
                "Likes_E INTEGER," +
                "Is_saved INTEGER," +
                "Is_read INTEGER," +
                "Location INTEGER DEFAULT 0," +
                "PRIMARY KEY(Id_E, Episode_id)," +
                "FOREIGN KEY(Id_E) REFERENCES WEBTOON(Id) ON DELETE CASCADE ON UPDATE CASCADE);");

        db.execSQL("CREATE TABLE IF NOT EXISTS WEEKDAY(" +
                "Id_W INTEGER NOT NULL," +
                "Weekday INTEGER," +
                "PRIMARY KEY(Id_W, Weekday)," +
                "FOREIGN KEY(Id_W) REFERENCES WEBTOON(Id) ON DELETE CASCADE ON UPDATE CASCADE);");

        db.execSQL("CREATE TABLE IF NOT EXISTS GENRE(" +
                "Id_G INTEGER NOT NULL," +
                "Genre TEXT," +
                "PRIMARY KEY(Id_G, Genre)," +
                "FOREIGN KEY(Id_G) REFERENCES WEBTOON(Id) ON DELETE CASCADE ON UPDATE CASCADE);");

        db.execSQL("CREATE TABLE IF NOT EXISTS TEMPORARYTOON(" +
                "Id_T INTEGER NOT NULL," +
                "Episode_id INTEGER NOT NULL," +
                "Saved_date DATE," +
                "Image_path TEXT," +
                "PRIMARY KEY(Id_T, Episode_id)," +
                "FOREIGN KEY(Id_T) REFERENCES EPISODE(Id_E) ON DELETE CASCADE ON UPDATE CASCADE," +
                "FOREIGN KEY(Episode_id) REFERENCES EPISODE(Episode_id) ON DELETE CASCADE ON UPDATE CASCADE);");
    }

    public Cursor  topHits(int position){
        Cursor c= null;
        position*=3;
        return c = db.rawQuery("SELECT Id, Title, Artist, Thumburl, Starscore FROM WEBTOON ORDER BY Hits DESC LIMIT 3 OFFSET "+Integer.toString(position), null);
    }

    public long insertWebtoon(Webtoon webtoon){
        int id;                                        //웹툰 고유 ID
        String title;                               //웹툰 제목
        String artist;                             //작가명
        float starScore;                        //평균 평점
        String thumbURL;                    //썸네일 URL
        int likes;
        int hits;
        char toonType;                         //일반툰 : G 컷툰 : C 스마트툰 : S
        int is_charged;               //유료 웹툰 구분(스토어)
        int is_adult;                   //성인웹툰
        int is_updated;                         //업데이트

        id = webtoon.getId();
        title = webtoon.getTitle();
        artist = webtoon.getArtist();
        starScore = webtoon.getStarScore();
        thumbURL = webtoon.getThumbURL();
        toonType = webtoon.getToonType();
        is_charged = webtoon.isCharged()? 1 : 0;
        is_adult = webtoon.isAdult()? 1 : 0;
        is_updated = webtoon.isUpdated();
        likes = webtoon.getLikes();
        hits = webtoon.getHits();

        ContentValues values = new ContentValues();
        values.put("Id", id);
        values.put("Title", title);
        values.put("Artist", artist);
        values.put("Starscore", starScore);
        values.put("Thumburl", thumbURL);
        values.put("Toontype", String.valueOf(toonType));
        values.put("Is_charged", is_charged);
        values.put("Is_adult", is_adult);
        values.put("Is_updated", is_updated);
        values.put("Likes", likes);
        values.put("Hits", hits);
        return db.insert("WEBTOON", null,values);
    }

    public long insertEpsode(Episode episode){
         int id_E;                                        //웹툰 고유 ID
         int episode_id;                       //에피소드 ID
         String episode_title;                    //에피소드 제목
         float ep_starScore;                      //평균 평점
         String ep_thumbURL;                 //썸네일 URL
         String reg_date;                               //등록일
         String mention;                                //작가의말
         int likes_E;                                //좋아요
         int is_saved;                      //임시저장 여부
         int is_read;                        //읽음 여부
         int location;                               //책갈피한 이미지 url 위치

        id_E = episode.getId();
        episode_id = episode.getEpisode_id();
        episode_title = episode.getEpisode_title();
        ep_starScore = episode.getEp_starScore();
        ep_thumbURL = episode.getEp_thumbURL();
        reg_date = episode.getReg_date();
        mention = episode.getMention();
        likes_E = episode.getLikes_E();
/*        is_saved = episode.getIs_saved()? 1 : 0;
        is_read = episode.getIs_read()? 1 : 0;
        location = episode.getLocation();*/

        ContentValues values = new ContentValues();
        values.put("Id_E", id_E);
        values.put("Episode_id", episode_id);
        values.put("Episode_title", episode_title);
        values.put("Ep_starscore", ep_starScore);
        values.put("Ep_thumburl", ep_thumbURL);
        values.put("Reg_date", String.valueOf(reg_date));
        values.put("Mention", mention);
        values.put("Likes_E", likes_E);
        values.put("Is_saved", 0);
        values.put("Is_read", 0);
        values.put("Location", 0);

        return db.insert("EPISODE", null,values);
    }

    public long insertWeekday(Weekday weekday){
        int id;                                        //웹툰 고유 ID
        int weekday1;                               //웹툰 요일

        id = weekday.getId();
        weekday1 = weekday.getWeekday();

        ContentValues values = new ContentValues();
        values.put("Id_W", id);
        values.put("Weekday", weekday1);
        return db.insert("WEEKDAY", null,values);
    }

    public long insertGenre(Genre genre){
        int id;                                        //웹툰 고유 ID
        String genre1;                               //웹툰 장르

        id = genre.getId();
        genre1 = genre.getGenre();

        ContentValues values = new ContentValues();
        values.put("Id_G", id);
        values.put("Genre", genre1);
        return db.insert("GENRE", null,values);
    }

    public Cursor findquery(String s) {
        Cursor c;
        return c=db.rawQuery("SELECT DISTINCT Id, Title, Artist, Thumburl, Starscore "+
                                                "FROM WEBTOON WHERE Title Like \"%"+s+"%\" OR Artist Like \"%"+s+"%\"",null);
    }
}

