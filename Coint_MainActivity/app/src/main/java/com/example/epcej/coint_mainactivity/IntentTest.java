package com.example.epcej.coint_mainactivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by epcej on 2017-02-21.
 */

public class IntentTest extends Activity {

    ArrayList<SearchResult> resultQueries = null;
    ListView listView = null;
    SearchAdapter searchAdapter = null;
    Cursor cursor = null;
    COINT_SQLiteManager coint_sqLiteManager;

    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.intent_test);

        TextView textView = (TextView)findViewById(R.id.intentText);

        Intent intent = getIntent();
        String something = intent.getStringExtra("Intent");
        textView.setText(something);

        coint_sqLiteManager = COINT_SQLiteManager.getInstance(this);
        cursor = coint_sqLiteManager.findquery(something);

        resultQueries = new ArrayList<SearchResult>();

        while(cursor.moveToNext()){
            SearchResult resultQuery = new SearchResult(Integer.parseInt(cursor.getString(0).toString()),cursor.getString(1).toString(),cursor.getString(2).toString(),cursor.getString(3).toString(),Float.parseFloat(cursor.getString(4).toString()));
            /*Log.i("result",resultQuery.title);*/
            resultQueries.add(resultQuery);
        }

        searchAdapter = new SearchAdapter(this, resultQueries);

        listView = (ListView)findViewById(R.id.searchView);
        listView.setAdapter(searchAdapter);
    }
}
