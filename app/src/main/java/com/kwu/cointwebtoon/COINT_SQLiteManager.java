package com.kwu.cointwebtoon;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.kwu.cointwebtoon.DataStructure.Episode;
import com.kwu.cointwebtoon.DataStructure.Genre;
import com.kwu.cointwebtoon.DataStructure.Webtoon;
import com.kwu.cointwebtoon.DataStructure.Weekday;

import java.util.ArrayList;

public class COINT_SQLiteManager {
    private static final String DBNAME = "COINT.db";
    private static final int DB_VERSION = 1;

    Context mContext = null;

    private static COINT_SQLiteManager manager = null;
    private static SQLiteDatabase db = null;

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
                "Hits INTEGER," +
                "Thumburl TEXT," +
                "Likes INTEGER," +
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
                "Is_saved INTEGER DEFAULT 0," +
                "Is_read INTEGER DEFAULT 0," +
                "Location INTEGER DEFAULT 1," +
                "My_star REAL DEFAULT -1," +
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

    /*
     * 웹툰 목록을 ArrayList 에 모두 받아 한번에 업데이트 하는 메소드
     * Input Parameter : Server 에서 가져온 웹툰 목록 데이터가 담긴 ArrayList
     */
    public void insertWebtoon(ArrayList<Webtoon> webtoons) {
        int id;                                        //웹툰 고유 ID
        String title;                               //웹툰 제목
        String artist;                             //작가명
        float starScore;                        //평균 평점
        String thumbURL;                    //썸네일 URL
        int likes;                                      //좋아요
        int hits;                                       //조회수
        char toonType;                         //일반툰 : G 컷툰 : C 스마트툰 : S
        int is_charged;               //유료 웹툰 구분(스토어)
        int is_adult;                   //성인웹툰
        int is_updated;                         //업데이트

        try {
            db.beginTransaction();
            for (Webtoon webtoon : webtoons) {
                id = webtoon.getId();
                title = webtoon.getTitle();
                artist = webtoon.getArtist();
                starScore = webtoon.getStarScore();
                thumbURL = webtoon.getThumbURL();
                toonType = webtoon.getToonType();
                is_charged = webtoon.isCharged() ? 1 : 0;
                is_adult = webtoon.isAdult() ? 1 : 0;
                is_updated = webtoon.isUpdated();
                likes = webtoon.getLikes();
                hits = webtoon.getHits();

                db.execSQL("INSERT OR IGNORE INTO WEBTOON(Id)" +
                        " VALUES(" + id + ");");
                db.execSQL("UPDATE WEBTOON SET Title='" + title + "', Artist='" + artist + "', Starscore=" + starScore
                        + ", Thumburl='" + thumbURL + "', Toontype='" + toonType + "', Is_charged=" + is_charged
                        + ", Is_adult=" + is_adult + ", Is_updated=" + is_updated + ", Likes=" + likes + ", Hits=" + hits
                        + " WHERE Id=" + id + ";");
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /*
 * 웹툰 하나의 모든 회차 목록을 ArrayList 에 모두 받아 한번에 업데이트 하는 메소드
 * Input Parameter : Server 에서 가져온 해당 웹툰의 회차 정보 데이터가 담긴 ArrayList
 */
    public void insertEpsode(ArrayList<Episode> episodes) {
        int id_E;                                        //웹툰 고유 ID
        int episode_id;                       //에피소드 ID
        String episode_title;                    //에피소드 제목
        float ep_starScore;                      //평균 평점
        String ep_thumbURL;                 //썸네일 URL
        String reg_date;                               //등록일
        String mention;                                //작가의말
        int likes_E;                                //좋아요

        db.beginTransaction();
        try {
            for (Episode episode : episodes) {
                id_E = episode.getId();
                episode_id = episode.getEpisode_id();
                episode_title = episode.getEpisode_title().replace("\'", "\''").replace("\"", "\"");
                ep_starScore = episode.getEp_starScore();
                ep_thumbURL = episode.getEp_thumbURL();
                reg_date = episode.getReg_date();
                mention = episode.getMention().replace("\'", "\''").replace("\"", "\"");
                likes_E = episode.getLikes_E();

                db.execSQL("INSERT OR IGNORE INTO EPISODE(Id_E, Episode_id)" +
                        " VALUES (" + id_E + ", " + episode_id + ");");
                db.execSQL("UPDATE EPISODE SET Episode_title='" + episode_title + "' , Ep_starscore="
                        + ep_starScore + " , Ep_thumburl='" + ep_thumbURL + "', Reg_date='" + reg_date + "', Mention='" + mention + "', Likes_E=" + likes_E
                        + " WHERE Id_E=" + id_E + " AND Episode_id=" + episode_id + ";");
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /*
     * 서버에서 가져온 요일 데이터를 SQLite 에 업데이트하는 메소드
     * Input Parameter : 모든 웹툰의 요일 정보가 담긴 Weekday 형 ArrayList
     */
    public void insertWeekday(ArrayList<Weekday> weekdays) {
        int id;                                        //웹툰 고유 ID
        int weekdayValue;                               //웹툰 요일

        db.beginTransaction();
        try {
            for (Weekday weekday : weekdays) {
                id = weekday.getId();
                weekdayValue = weekday.getWeekday();
                db.execSQL("INSERT OR IGNORE INTO WEEKDAY(Id_W, Weekday) VALUES(" + id + ", " + weekdayValue + ");");
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /*
 * 서버에서 가져온 장르를 SQLite 에 업데이트하는 메소드
 * Input Parameter : 모든 웹툰의 요일 정보가 담긴 Weekday 형 ArrayList
 */
    public void insertGenre(ArrayList<Genre> genres) {
        int id;                                        //웹툰 고유 ID
        String genreValue;                               //웹툰 장르

        db.beginTransaction();
        try {
            for (Genre genre : genres) {
                id = genre.getId();
                genreValue = genre.getGenre();
                db.execSQL("INSERT OR IGNORE INTO GENRE(Id_G, Genre) VALUES(" + id + ", '" + genreValue + "');");
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /*
     * SQLite 내에 현재 들어있는 해당 웹툰의 회차를 모두 가져오는 메소드
     * Input Parameter : 회차 정보를 얻어올 웹툰의 고유 ID
     * Output : 쿼리 결과가 담긴 Cursor (해당 웹툰의 모든 회차 데이터)
     */
    public Cursor getEpisodes(int toonId) {
        //Episode Constructor
        //int id_E, int episode_id, String episode_title, float ep_starScore, String ep_thumbURL, String reg_date, String mention, int likes_E, Is_read
        return db.rawQuery("SELECT Id_E, Episode_id, Episode_title, Ep_starscore, Ep_thumburl, Reg_date, Mention, Likes_E, Is_read " +
                "FROM EPISODE " +
                "WHERE Id_E=" + String.valueOf(toonId) +
                " ORDER BY Episode_id DESC;", null);
    }

    //ViewerActivity 를 통해 사용자가 본 웹툰을 EpisodeActivity 에서 사용자가 읽은 웹툰이라고 표시하기 위해 SQLite 를 업데이트 하는 메소드
    public void updateEpisodeRead(int toonId, int episodeId) {
        db.execSQL("UPDATE EPISODE SET Is_read=1 WHERE Id_E=" + String.valueOf(toonId) + " AND Episode_id=" + String.valueOf(episodeId) + "");
    }

    //ViewerActivity 에서 다음 화가 존재하는지 확인하기 위해서 해당 웹툰의 가장 최신 회차의 ID를 알아오는 메소드
    //에러 시 -1 RETURN
    public int maxEpisodeId(int toonId) {
        Cursor queryResult = db.rawQuery("SELECT Episode_id FROM EPISODE WHERE Id_E=" + String.valueOf(toonId) + " ORDER BY Episode_id DESC LIMIT 1;", null);
        if (queryResult.moveToNext()) {
            return queryResult.getInt(0);
        } else {
            return -1;
        }
    }

    //웹툰 고유 ID와 회차 ID를 이용해 해당 회차의 제목을 SQLite 에서 가져오는 메소드
    //에러 시 NULL RETURN
    public String getEpisodeTitle(int toonId, int episodeId) {
        Cursor queryResult = db.rawQuery("SELECT Episode_title FROM EPISODE WHERE Id_E=" + String.valueOf(toonId) + " AND Episode_id=" + String.valueOf(episodeId) + ";", null);
        if (queryResult.moveToNext()) {
            return queryResult.getString(0);
        } else {
            return null;
        }
    }

    public Episode getEpisodeInstance(int id, int ep_id) {
        //Episode 생성자 :
        Cursor cursor = db.rawQuery("SELECT Episode_title, Ep_starscore, Ep_thumbURL, Reg_date, Mention, Likes_E FROM EPISODE WHERE Id_E=" + id + " AND Episode_id=" + ep_id, null);
        if (cursor.moveToNext())
            return new Episode(id, ep_id, cursor.getString(0), cursor.getFloat(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getInt(5), 1);
        else
            return null;
    }


    //테스트용 --> 모든 Episode를 안읽은 상태로 만드는 메소드
    public void initializeEpisodeRead() {
        db.execSQL("UPDATE EPISODE SET Is_read=0");
    }


    /**
     * 1차 통합 은주 부분 추가 메소드
     */
    public Cursor topHits(int position) {           // TODO - 웹툰의 모든 속성 가져오는 걸로 변경했음! --> 더 다양한 데이터 표시
        position *= 3;
        return db.rawQuery("SELECT * FROM WEBTOON ORDER BY Hits DESC LIMIT 3 OFFSET " + Integer.toString(position), null);
        //position으로부터 내림차순 정리된 것 중 세개의 정보를 리턴함.
    }

    public Cursor favorite(int position, String genre) {           // TODO - 웹툰의 모든 속성 가져오는 걸로 변경했음! --> 더 다양한 데이터 표시
        position *= 3;
        return db.rawQuery("SELECT * FROM WEBTOON WHERE Is_mine = 0 AND " +
                "Id IN ( SELECT Id_G FROM GENRE WHERE Genre = '" + genre + "') " +
                "ORDER BY Hits DESC LIMIT 3 OFFSET " + Integer.toString(position), null);
        //position으로부터 내림차순 정리된 것 중 세개의 정보를 리턴함.
    }

    public String updateMyWebtoon(String id) {
        Cursor c;
        // db에 my webtoon 업데이트
        db.execSQL("UPDATE WEBTOON SET Is_mine = " +
                "CASE WHEN Is_mine = 0 THEN 1 " +
                "ELSE 0 END " +
                "WHERE Id = " + id);

        //My Webtoon에 추가 여부 판단
        c = db.rawQuery("SELECT Is_mine FROM WEBTOON WHERE Id = " + id, null);
        c.moveToFirst();
        if (c.getInt(0) == 0) {
            Log.i("MY WEBTOON", c.getString(0).toString());
            return "마이 웹툰 해제";
        } else {
            Log.i("MY WEBTOON", c.getString(0).toString());
            return "마이 웹툰 설정";
        }
    }

    public Cursor getMyWebtoons() {
        return db.rawQuery("SELECT * " +
                "FROM WEBTOON " +
                "WHERE Is_mine=1"
                + " ORDER BY title ASC", null);
    }

    public Cursor searchquery(String s) {
        return db.rawQuery("SELECT * " +
                "FROM WEBTOON WHERE Title Like \"%" + s + "%\" OR Artist Like \"%" + s + "%\"", null);
    }

    public Cursor Top100Ranking() {
        return db.rawQuery("SELECT * FROM WEBTOON ORDER BY Hits DESC LIMIT 100", null);
    }

    public ArrayList<Genre> getGenreAll() {
        Cursor cursor = db.rawQuery("SELECT * FROM Genre", null);
        ArrayList<Genre> genreArrayList = new ArrayList<>();
        while (cursor.moveToNext()) {
            genreArrayList.add(new Genre(cursor.getInt(0), cursor.getString(1)));
        }
        return genreArrayList;
    }

    public Episode getLatestEpisode(int toonId) {
        Episode episode = null;
        Cursor cursor = db.rawQuery("SELECT * " +
                "FROM EPISODE " +
                "WHERE Is_read = 1 AND Id_E =" + Integer.toString(toonId) +
                " ORDER BY Episode_id DESC LIMIT 1", null);
        if (cursor.moveToNext()) {
            return new Episode(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getFloat(3), cursor.getString(4),
                    cursor.getString(5), cursor.getString(6), cursor.getInt(7), cursor.getInt(8));
        }
        return episode;

    }

    public void removeAllMyWebtoon() {
        db.execSQL("UPDATE WEBTOON SET Is_mine=0");
    }
    /**
     * 1차 통합 은주 부분 추가 메소드 end
     */

    /**
     * 1차 통합 원형 부분 추가 메소드
     */
    /* 요일별 웹툰 아이템요소 데이터 리턴 쿼리문 */
    public Cursor getListItem(int weekday) {
        return db.rawQuery("SELECT  * " +
                        "FROM WEBTOON " +
                        "WHERE ID IN (SELECT ID_W FROM WEEKDAY WHERE Weekday=" + String.valueOf(weekday) + ")" +
                        "ORDER BY Hits DESC"
                , null);
    }

    /**
     * 1차 통합 원형 부분 추가 메소드 end
     */

    public Webtoon getWebtoonInstance(int id) {
        Cursor cursor = db.rawQuery("SELECT * FROM WEBTOON WHERE ID=" + id, null);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            return new Webtoon(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getFloat(3),
                    cursor.getInt(4), cursor.getString(5), cursor.getInt(6), cursor.getString(7).charAt(0), cursor.getInt(8) == 1 ? true : false,
                    cursor.getInt(9) == 1 ? true : false, cursor.getInt(10) == 1 ? true : false, cursor.getInt(11));
        } else {
            return null;
        }
    }

    public ArrayList<Webtoon> getWebtoonsByGenre(String genre) {
        ArrayList<Webtoon> returnList = new ArrayList<>();
        if (genre.equals("BRANDETC")) {
            Cursor cursor = db.rawQuery("SELECT * FROM WEBTOON WHERE Id NOT IN(SELECT DISTINCT Id_G FROM GENRE) ORDER BY Id DESC", null);
            while (cursor.moveToNext()) {
                returnList.add(new Webtoon(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getFloat(3),
                        cursor.getInt(4), cursor.getString(5), cursor.getInt(6), cursor.getString(7).charAt(0), cursor.getInt(8) == 1 ? true : false,
                        cursor.getInt(9) == 1 ? true : false, cursor.getInt(10) == 1 ? true : false, cursor.getInt(11)));
            }
            cursor.close();
            cursor = db.rawQuery("SELECT * FROM WEBTOON, GENRE WHERE Id=Id_G AND Genre='BRAND' ORDER BY Id DESC", null);
            while (cursor.moveToNext()) {
                returnList.add(new Webtoon(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getFloat(3),
                        cursor.getInt(4), cursor.getString(5), cursor.getInt(6), cursor.getString(7).charAt(0), cursor.getInt(8) == 1 ? true : false,
                        cursor.getInt(9) == 1 ? true : false, cursor.getInt(10) == 1 ? true : false, cursor.getInt(11)));
            }
            cursor.close();
        } else {
            Cursor cursor = db.rawQuery("SELECT * FROM WEBTOON, GENRE WHERE Id=Id_G AND Genre='" + genre + "' ORDER BY Id DESC", null);
            while (cursor.moveToNext()) {
                returnList.add(new Webtoon(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getFloat(3),
                        cursor.getInt(4), cursor.getString(5), cursor.getInt(6), cursor.getString(7).charAt(0), cursor.getInt(8) == 1 ? true : false,
                        cursor.getInt(9) == 1 ? true : false, cursor.getInt(10) == 1 ? true : false, cursor.getInt(11)));
            }
            cursor.close();
        }
        return returnList;
    }

    /**
     * 에피소드 내 별점 업데이트
     *
     * @param id
     * @param ep_id
     * @param score
     * @return true : 성공 false : 실패
     */
    public boolean updateMyStarScore(int id, int ep_id, float score) {
        try {
            db.execSQL("UPDATE EPISODE SET My_star=" + score + " WHERE Id_E=" + id + " AND Episode_id=" + ep_id + ";");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 로그아웃 시 내 별점 초기화
     */
    public void initMyStar() {
        db.execSQL("UPDATE EPISODE SET My_star=-1;");
    }

    public float getMyStar(int id, int ep_id) {
        Cursor c = db.rawQuery("SELECT My_star FROM EPISODE WHERE Id_E=" + id + " AND Episode_id=" + ep_id, null);
        if (c.getCount() > 0) {
            c.moveToNext();
            return c.getFloat(0);
        } else {
            return -1;
        }
    }
}

