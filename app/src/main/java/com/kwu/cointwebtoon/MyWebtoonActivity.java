package com.kwu.cointwebtoon;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.kwu.cointwebtoon.DataStructure.Webtoon;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;

public class MyWebtoonActivity extends TypeKitActivity {
    ArrayList<Webtoon> resultQueries = null;
    ListView listView = null;
    MyWebtoonAdapter allMyWebtoonAdp = null;
    Cursor cursor = null;
    COINT_SQLiteManager coint_sqLiteManager;

    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.mywebtoon_activity);

        coint_sqLiteManager = COINT_SQLiteManager.getInstance(this);
        cursor = coint_sqLiteManager.getMyWebtoons();

        resultQueries = new ArrayList<>();

        while(cursor.moveToNext()){
            resultQueries.add(new Webtoon(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getFloat(3),
                    cursor.getInt(4), cursor.getString(5), cursor.getInt(6), cursor.getString(7).charAt(0), cursor.getInt(8)==1?true:false,
                    cursor.getInt(9)==1?true:false, cursor.getInt(10)==1?true:false, cursor.getInt(11)));
        }

        allMyWebtoonAdp = new MyWebtoonAdapter(this, resultQueries);

        listView = (ListView)findViewById(R.id.searchView);
        listView.setAdapter(allMyWebtoonAdp);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {             //리스트뷰 클릭 리스너로 id를 보내줌
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {// Intent로 id를 넘겨주면 회차정보 액티비티가 뜨면 됨.
                Webtoon target = allMyWebtoonAdp.getItem(position);
                Toast.makeText(MyWebtoonActivity.this, target.getTitle() ,Toast.LENGTH_SHORT).show();
                Intent episodeIntent = new Intent(MyWebtoonActivity.this, EpisodeActivity.class);
                episodeIntent.putExtra("id", target.getId());
                episodeIntent.putExtra("toontype", target.getToonType());
                startActivity(episodeIntent);
            }
        });
    }
    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}
