package com.kwu.cointwebtoon.DataStructure;

import android.content.Context;
import android.database.Cursor;

import com.kwu.cointwebtoon.COINT_SQLiteManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Weekday_ListItem {
    COINT_SQLiteManager manager = null;
    private ArrayList<Webtoon> list;    //기존에 여러 개의 리스트를 사용하여 불편하게 접근하던 것을 웹툰형 리스트로 변경
    Cursor cursor = null;
    private int day;

    public ArrayList<Webtoon> getList() {
        return list;
    }

    public Weekday_ListItem(Context context, int day) {
        list = new ArrayList<>();
        manager = COINT_SQLiteManager.getInstance(context);
        this.day = day;
        this.updateList();
    }

    public void updateList() {
        list.clear();
        cursor = null;
        cursor = manager.getListItem(day);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(new Webtoon(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getFloat(3),
                        cursor.getInt(4), cursor.getString(5), cursor.getInt(6), cursor.getString(7).charAt(0), cursor.getInt(8) == 1 ? true : false,
                        cursor.getInt(9) == 1 ? true : false, cursor.getInt(10) == 1 ? true : false, cursor.getInt(11)));
            }
            cursor.close();
        }
    }

    public void orderByHits() {
        //조회순 정렬
        Collections.sort(this.list, new Comparator<Webtoon>() {
            @Override
            public int compare(Webtoon lhs, Webtoon rhs) {
                if (lhs.getHits() < rhs.getHits()) {
                    return 1;
                } else if (lhs.getHits() > rhs.getHits()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
    }
}
