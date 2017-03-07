package com.example.epcej.coint_mainactivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by epcej on 2017-03-06.
 */

public class SearchList extends Activity {

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
        cursor = coint_sqLiteManager.searchquery(something);

        resultQueries = new ArrayList<SearchResult>();

        while(cursor.moveToNext()){
            SearchResult resultQuery = new SearchResult(Integer.parseInt(cursor.getString(0).toString()),cursor.getString(1).toString(),cursor.getString(2).toString(),cursor.getString(3).toString(),Float.parseFloat(cursor.getString(4).toString()));
            //차례대로 id, title, artist, thumburl, starscore
            /*Log.i("result",resultQuery.title);*/
            resultQueries.add(resultQuery);
        }

        searchAdapter = new SearchAdapter(this, resultQueries);

        listView = (ListView)findViewById(R.id.searchView);
        listView.setAdapter(searchAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {             //리스트뷰 클릭 리스너로 id를 보내줌
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SearchList.this,resultQueries.get(position).title.toString(),Toast.LENGTH_SHORT).show();
                // Intent로 id를 넘겨주면 회차정보 액티비티가 뜨면 됨.
            }
        });
    }
}
