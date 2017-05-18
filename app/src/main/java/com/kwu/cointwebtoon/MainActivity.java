package com.kwu.cointwebtoon;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends TypeKitActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnKeyListener{
    ViewPager pager;
    RecyclerView recyclerView;
    Main_Top15Adapter top15Adapter;
    RecyclerView.LayoutManager layoutManager;
    Main_MyToonAdapter myToonAdapter;
    private COINT_SQLiteManager coint_sqLiteManager;
    private EditText search;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                 /*안드로이드 데이터베이스에 데이터를 넣음*/
        setContentView(R.layout.main_activity);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);            // 액션바에서 앱 이름 보이지 않게 함
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        final DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerview = navigationView.getHeaderView(0);
        Button navHeader = (Button)headerview.findViewById(R.id.nav_login);
        navHeader.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        search = (EditText)findViewById(R.id.searchbar);
        search.setOnKeyListener(this);

        pager = (ViewPager)findViewById(R.id.viewpager);                        //뷰페이저에 어댑터를 연결하는 부분
        top15Adapter = new Main_Top15Adapter(this);
        pager.setAdapter(top15Adapter);

        //즐겨찾는 웹툰 추가하는 부분 Id(R.id.my_recycler_view);

        //1번
        recyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this,1, GridLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);

        myToonAdapter = new Main_MyToonAdapter(this);
        recyclerView.setAdapter(myToonAdapter);


        //2번
        recyclerView = (RecyclerView)findViewById(R.id.my_recycler_view2);
        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this,1, GridLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);

        myToonAdapter = new Main_MyToonAdapter(this);
        recyclerView.setAdapter(myToonAdapter);


        //3번
        recyclerView = (RecyclerView)findViewById(R.id.my_recycler_view3);
        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this,1, GridLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);

        myToonAdapter = new Main_MyToonAdapter(this);
        recyclerView.setAdapter(myToonAdapter);


        //4번
        recyclerView = (RecyclerView)findViewById(R.id.my_recycler_view4);
        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this,1, GridLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);

        myToonAdapter = new Main_MyToonAdapter(this);
        recyclerView.setAdapter(myToonAdapter);


        //5번
        recyclerView = (RecyclerView)findViewById(R.id.my_recycler_view5);
        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this,1, GridLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);

        myToonAdapter = new Main_MyToonAdapter(this);
        recyclerView.setAdapter(myToonAdapter);


        //6번
        recyclerView = (RecyclerView)findViewById(R.id.my_recycler_view6);
        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this,1, GridLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);

        myToonAdapter = new Main_MyToonAdapter(this);
        recyclerView.setAdapter(myToonAdapter);


        //7번
        recyclerView = (RecyclerView)findViewById(R.id.my_recycler_view7);
        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this,1, GridLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);

        myToonAdapter = new Main_MyToonAdapter(this);
        recyclerView.setAdapter(myToonAdapter);

        //8번
        recyclerView = (RecyclerView)findViewById(R.id.my_recycler_view8);
        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this,1, GridLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);

        myToonAdapter = new Main_MyToonAdapter(this);
        recyclerView.setAdapter(myToonAdapter);
    }
    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));       //폰트 바꿈
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {     // 검색 메뉴 만들어주는 부분
        getMenuInflater().inflate(R.menu.main_search_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {       // 검색 누르면 실행
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;
        String searchString = search.getText().toString();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            if(searchString.equals("")){
                Toast.makeText(this,"검색어를 입력하세요",Toast.LENGTH_SHORT).show();
            }else{
                intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("Intent",search.getText().toString());
                startActivity(intent);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {        // navigation drawer에 있는 메뉴 선택시 실행
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch(id){
            case R.id.webtoonRanking:
                startActivity(new Intent(MainActivity.this, Top100Activity.class));
            break;
            case R.id.moreMyWebtoon:
                startActivity(new Intent(MainActivity.this, MyWebtoonActivity.class));
                break;
            case R.id.personalFavorite:
                startActivity(new Intent(MainActivity.this, FavoriteChartAcivity.class));
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onClick(View view) {        // 베스트도전은 웹뷰로 띄우고, 나머지는 액티비티
        int id = view.getId();
        String result;
        Intent intent;
        int position;
        Cursor c;
        switch (id){
            case R.id.genreBtn:
                startActivity(new Intent(this, GenreActivity.class));
                break;
            case R.id.artistBtn:
                /**
                 * 작가별 웹툰 Activity 연결부
                 */
                break;
            case R.id.weekdayBtn:
                /**
                 * 요일별 웹툰 Activity 연결부
                 */
                startActivity(new Intent(this, WeekdayActivity.class));
                break;
            case R.id.bestBtn:
                //webview로 띄울 예정
                intent = new Intent(MainActivity.this, BestChallengeActivity.class);
                intent.putExtra("Best","bestchallenge");
                startActivity(intent);
                break;
            case R.id.addTopBtn:
                position = (Integer)view.getTag();
                c = coint_sqLiteManager.topHits(position);
                c.moveToFirst();
                result = coint_sqLiteManager.updateMyWebtoon(c.getString(0).toString());
                c = coint_sqLiteManager.getMyWebtoons();
                myToonAdapter.addRemoveItem(c);
                break;

            case R.id.addMidBtn:
                position = (Integer)view.getTag();
                c = coint_sqLiteManager.topHits(position);
                c.moveToPosition(1);
                result = coint_sqLiteManager.updateMyWebtoon(c.getString(0).toString());
                c = coint_sqLiteManager.getMyWebtoons();
                myToonAdapter.addRemoveItem(c);
                break;
            case R.id.addBotBtn:
                position = (Integer)view.getTag();

                c = coint_sqLiteManager.topHits(position);
                c.moveToLast();

                result = coint_sqLiteManager.updateMyWebtoon(c.getString(0).toString());
                c = coint_sqLiteManager.getMyWebtoons();
                myToonAdapter.addRemoveItem(c);
                break;

            case R.id.action_search:
                String searchString = search.getText().toString();
                if(searchString.equals("")){
                    Toast.makeText(this,"검색어를 입력하세요",Toast.LENGTH_SHORT).show();
                }else{
                    intent = new Intent(MainActivity.this, SearchActivity.class);
                    intent.putExtra("Intent",search.getText().toString());
                    startActivity(intent);
                }
        }
    }

    protected void onResume() {
        super.onResume();
        coint_sqLiteManager = COINT_SQLiteManager.getInstance(this);
        Cursor c = coint_sqLiteManager.getMyWebtoons();
        myToonAdapter.addRemoveItem(c);
        search.setText("");
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
            Log.i("epcej", "ENTER");
            String searchString = search.getText().toString();
            if(searchString.equals("")){
                Toast.makeText(this,"검색어를 입력하세요",Toast.LENGTH_SHORT).show();
            }else{
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("Intent",search.getText().toString());
                startActivity(intent);
            }
            return true;
        }
        return false;
    }
}
