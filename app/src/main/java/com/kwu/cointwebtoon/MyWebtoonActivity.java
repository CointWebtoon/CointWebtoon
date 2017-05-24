package com.kwu.cointwebtoon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.kwu.cointwebtoon.DataStructure.Webtoon;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;

public class MyWebtoonActivity extends TypeKitActivity {
    ArrayList<Webtoon> resultQueries = null;
    ListView listView = null;
    MyWebtoonAdapter allMyWebtoonAdp = null;
    Cursor cursor = null;
    COINT_SQLiteManager coint_sqLiteManager;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.mywebtoon_activity);

        coint_sqLiteManager = COINT_SQLiteManager.getInstance(this);
        cursor = coint_sqLiteManager.getMyWebtoons();
        TextView textView = (TextView) findViewById(R.id.emptyMy);
        if (cursor.getCount() == 0) {
            textView.setVisibility(View.VISIBLE);
            textView.setText("추가한 웹툰이\n" + "존재하지 않습니다.");
            textView.setTextSize(30.0f);
        } else {
            textView.setVisibility(View.GONE);
        }
        resultQueries = new ArrayList<>();

        while (cursor.moveToNext()) {
            resultQueries.add(new Webtoon(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getFloat(3),
                    cursor.getInt(4), cursor.getString(5), cursor.getInt(6), cursor.getString(7).charAt(0), cursor.getInt(8) == 1 ? true : false,
                    cursor.getInt(9) == 1 ? true : false, cursor.getInt(10) == 1 ? true : false, cursor.getInt(11)));
        }

        allMyWebtoonAdp = new MyWebtoonAdapter(this, resultQueries);

        listView = (ListView) findViewById(R.id.searchView);
        listView.setAdapter(allMyWebtoonAdp);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {             //리스트뷰 클릭 리스너로 id를 보내줌
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {// Intent로 id를 넘겨주면 회차정보 액티비티가 뜨면 됨.
                Webtoon target = allMyWebtoonAdp.getItem(position);
                Intent episodeIntent = new Intent(MyWebtoonActivity.this, EpisodeActivity.class);
                episodeIntent.putExtra("id", target.getId());
                episodeIntent.putExtra("toontype", target.getToonType());
                episodeIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(episodeIntent);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        //종료 버튼 추가

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mywebtoon_activity_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {       // 검색 누르면 실행
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.all_my_webtoon_remove:
                new AlertDialog.Builder(this)
                        .setTitle("My Webtoon 전체삭제")
                        .setMessage("추가한 모든 웹툰을 삭제하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                coint_sqLiteManager.removeAllMyWebtoon();
                                Intent intent = new Intent(MyWebtoonActivity.this, MyWebtoonActivity.class);
                                finish();
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                            }
                        }).setNegativeButton("아니요", null).show();


                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
