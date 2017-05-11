package com.kwu.cointwebtoon.DataStructure;

import android.content.Context;
import android.database.Cursor;

import com.kwu.cointwebtoon.COINT_SQLiteManager;

import java.util.ArrayList;

public class Weekday_ListItem {
    COINT_SQLiteManager manager = null;
    private Context mContext;
    private ArrayList<Webtoon> list;    //기존에 여러 개의 리스트를 사용하여 불편하게 접근하던 것을 웹툰형 리스트로 변경
    Cursor cursor = null;

    public ArrayList<Webtoon> getList(){return list;}

    public Weekday_ListItem(Context context){
        this.mContext = context;
        list = new ArrayList<>();
    }

    public void generateList(int day){
        manager = COINT_SQLiteManager.getInstance(mContext);
        cursor = manager.getListItem(day);
        if(cursor != null){
            while(cursor.moveToNext()){
                list.add(new Webtoon(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getFloat(3),
                        cursor.getInt(4), cursor.getString(5), cursor.getInt(6), cursor.getString(7).charAt(0), cursor.getInt(8)==1?true:false,
                        cursor.getInt(9)==1?true:false, cursor.getInt(10)==1?true:false, cursor.getInt(11)));
            }
            cursor.close();
        }
    }
}
