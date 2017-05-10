package com.coint.webtoonlist;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JWP on 2017-04-24.
 */

public class ListItem {
    COINT_SQLiteManager manager = null;
    private  Context mContext;

    private List webtoonID;
    private List titles;
    private List starPoints;
    private List thumbUrls;
    private List artists;
    private int listTotalCount;
    Cursor cursor = null;

    public List getWebtoonID() {
        return webtoonID;
    }

    public List getTitles() {
        return titles;
    }

    public List getStarPoints() {
        return starPoints;
    }

    public List getThumbUrls() {
        return thumbUrls;
    }

    public List getArtists() {
        return artists;
    }
    public int getListTotalCount() {
        return listTotalCount;
    }

    public ListItem(Context context){
        this.mContext = context;
        webtoonID = new ArrayList();
        titles = new ArrayList();
        starPoints = new ArrayList();
        artists = new ArrayList();
        thumbUrls = new ArrayList();
    }

    public void generateList(int day){
        manager = COINT_SQLiteManager.getInstance(mContext);
        cursor = manager.getListItem(day);

        if(cursor != null){
            listTotalCount = cursor.getCount();
            cursor.moveToFirst();
            do{
                webtoonID.add(cursor.getString(0));
                titles.add(cursor.getString(1));
                artists.add(cursor.getString(2));
                starPoints.add(cursor.getFloat(3));
                thumbUrls.add(cursor.getString(5));
            }while(cursor.moveToNext());

            cursor.close();
        }
    }

}
