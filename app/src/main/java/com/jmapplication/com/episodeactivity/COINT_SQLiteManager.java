package com.jmapplication.com.episodeactivity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by jm on 2017-02-27.
 */

public class COINT_SQLiteManager {
    private static final String DBNAME = "COINT.db";
    private static final int DB_VERSION = 1;

    Context mContext = null;

    private static COINT_SQLiteManager manager = null;
    private SQLiteDatabase db = null;

    public static COINT_SQLiteManager getInstance(Context mContext){
        if(manager == null)
            manager = new COINT_SQLiteManager(mContext);

        return manager;
    }

    private COINT_SQLiteManager(Context mContext){
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
                "Is_update INTEGER DEFAULT 0);");

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
                "Location INTEGER," +
                "Web_url TEXT," +
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
}
