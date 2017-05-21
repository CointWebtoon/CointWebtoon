package com.kwu.cointwebtoon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kwu.cointwebtoon.DataStructure.Webtoon;
import com.nhn.android.naverlogin.OAuthLogin;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends TypeKitActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnKeyListener{

    InputMethodManager imm;
    ViewPager pager;
    RecyclerView recyclerView;
    Main_Top15Adapter top15Adapter;
    RecyclerView.LayoutManager layoutManager;
    Main_MyToonAdapter myToonAdapter, myToonAdapter2, myToonAdapter3, myToonAdapter4,
            myToonAdapter5,myToonAdapter6,myToonAdapter7,myToonAdapter8;
    private COINT_SQLiteManager coint_sqLiteManager;
    private EditText search;
    private int[] dateday={0,0,0,0,0,0,0};
    private Application_UserInfo userInfo;
    Button navHeader;
    TextView navStatus;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                 /*안드로이드 데이터베이스에 데이터를 넣음*/
        setContentView(R.layout.main_activity);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        userInfo = (Application_UserInfo)getApplication();
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

        navStatus = (TextView)headerview.findViewById(R.id.nav_status);
        navHeader = (Button)headerview.findViewById(R.id.nav_login);
        navHeader.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(userInfo.isLogin()){
                    userInfo.onLogOut(MainActivity.this);
                    Toast.makeText(MainActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                    navHeader.setBackgroundResource(R.drawable.login);
                    navStatus.setText("로그인 해주세요");
                }else{
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    drawer.closeDrawer(GravityCompat.START);
                }
            }
        });
        search = (EditText)findViewById(R.id.searchbar);
        search.setOnKeyListener(this);

        pager = (ViewPager)findViewById(R.id.viewpager);                        //뷰페이저에 어댑터를 연결하는 부분
        top15Adapter = new Main_Top15Adapter(this);
        pager.setAdapter(top15Adapter);

        //즐겨찾는 웹툰 추가하는 부분 Id(R.id.my_recycler_view);

        try{
            dateday = getDateDay();
        }catch (Exception e){}

        //1번
        recyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this,1, GridLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);

        myToonAdapter = new Main_MyToonAdapter(this, dateday[0]);
        recyclerView.setAdapter(myToonAdapter);

        //2번
        recyclerView = (RecyclerView)findViewById(R.id.my_recycler_view2);
        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this,1, GridLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);

        myToonAdapter2 = new Main_MyToonAdapter(this, dateday[1]);
        recyclerView.setAdapter(myToonAdapter2);


        //3번
        recyclerView = (RecyclerView)findViewById(R.id.my_recycler_view3);
        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this,1, GridLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);

        myToonAdapter3 = new Main_MyToonAdapter(this, dateday[2]);
        recyclerView.setAdapter(myToonAdapter3);


        //4번
        recyclerView = (RecyclerView)findViewById(R.id.my_recycler_view4);
        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this,1, GridLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);

        myToonAdapter4 = new Main_MyToonAdapter(this, dateday[3]);
        recyclerView.setAdapter(myToonAdapter4);


        //5번
        recyclerView = (RecyclerView)findViewById(R.id.my_recycler_view5);
        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this,1, GridLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);

        myToonAdapter5 = new Main_MyToonAdapter(this, dateday[4]);
        recyclerView.setAdapter(myToonAdapter5);


        //6번
        recyclerView = (RecyclerView)findViewById(R.id.my_recycler_view6);
        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this,1, GridLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);

        myToonAdapter6 = new Main_MyToonAdapter(this, dateday[5]);
        recyclerView.setAdapter(myToonAdapter6);


        //7번
        recyclerView = (RecyclerView)findViewById(R.id.my_recycler_view7);
        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this,1, GridLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);

        myToonAdapter7 = new Main_MyToonAdapter(this, dateday[6]);
        recyclerView.setAdapter(myToonAdapter7);

        //8번
        recyclerView = (RecyclerView)findViewById(R.id.my_recycler_view8);
        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this,1, GridLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);

        myToonAdapter8 = new Main_MyToonAdapter(this, 0);
        recyclerView.setAdapter(myToonAdapter8);
    }

    public static int[] getDateDay() throws Exception {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN);
        String str_date = dateFormat.format(new Date());
        Date nDate = dateFormat.parse(str_date);
        int[] arr = {0,0,0,0,0,0,0};
        Calendar cal = Calendar.getInstance();
        cal.setTime(nDate);

        int dayNum = cal.get(Calendar.DAY_OF_WEEK);

        System.out.println(dayNum);
        //일요일 : 1 , 월- 토 : 2-7 이라서 바꿔서 넘겨줌
        if(dayNum == 1) {//일요일인 경우
            dayNum = 7;
        }else{
            dayNum-=1;
        }

        for(int i=0;i<7;i++){
            if(dayNum%8!=0){
                arr[i]=dayNum%8;/*
                System.out.println(arr[i]);*/
            }else{
                i--;
            }
            dayNum++;
        }
        return arr;
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
        ArrayList<Webtoon> mList = new ArrayList<>();
        Cursor cursor;
        ImageView addWebtoon;
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
                cursor = coint_sqLiteManager.topHits(position);
                cursor.moveToFirst();
                if(cursor.getString(8).equals("1")){
                    if(userInfo.isLogin()){
                        if(!userInfo.isUserAdult()){
                            Toast.makeText(this, "만 19세 이상 시청 가능한 컨텐츠입니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }else{
                        new AlertDialog.Builder(this)
                                .setTitle("로그인")
                                .setMessage("로그인이 필요한 서비스 입니다. 로그인 하시겠습니까?")
                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                    }
                                }).setNegativeButton("아니요", null).show();
                        return;
                    }
                }
                result = coint_sqLiteManager.updateMyWebtoon(cursor.getString(0).toString());
                top15Adapter.notifyDataSetChanged();
                break;

            case R.id.addMidBtn:
                position = (Integer)view.getTag();
                cursor = coint_sqLiteManager.topHits(position);
                cursor.moveToPosition(1);
                if(cursor.getString(8).equals("1")){
                    if(userInfo.isLogin()){
                        if(!userInfo.isUserAdult()){
                            Toast.makeText(this, "만 19세 이상 시청 가능한 컨텐츠입니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }else{
                        new AlertDialog.Builder(this)
                                .setTitle("로그인")
                                .setMessage("로그인이 필요한 서비스 입니다. 로그인 하시겠습니까?")
                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                    }
                                }).setNegativeButton("아니요", null).show();
                        return;
                    }
                }
                result = coint_sqLiteManager.updateMyWebtoon(cursor.getString(0).toString());
                top15Adapter.notifyDataSetChanged();
                break;
            case R.id.addBotBtn:
                position = (Integer)view.getTag();

                cursor = coint_sqLiteManager.topHits(position);
                cursor.moveToLast();

                if(cursor.getString(8).equals("1")){
                    if(userInfo.isLogin()){
                        if(!userInfo.isUserAdult()){
                            Toast.makeText(this, "만 19세 이상 시청 가능한 컨텐츠입니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }else{
                        new AlertDialog.Builder(this)
                                .setTitle("로그인")
                                .setMessage("로그인이 필요한 서비스 입니다. 로그인 하시겠습니까?")
                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                    }
                                }).setNegativeButton("아니요", null).show();
                        return;
                    }
                }

                result = coint_sqLiteManager.updateMyWebtoon(cursor.getString(0).toString());
                top15Adapter.notifyDataSetChanged();
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
                break;
            case R.id.top15More:
                startActivity(new Intent(this, Top100Activity.class));
                break;
            case R.id.myMore:
                startActivity(new Intent(this, MyWebtoonActivity.class));
                break;
        }
        cursor = coint_sqLiteManager.getMyWebtoons();
        while (cursor.moveToNext()) {
            mList.add(new Webtoon(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getFloat(3),
                    cursor.getInt(4), cursor.getString(5), cursor.getInt(6), cursor.getString(7).charAt(0), cursor.getInt(8)==1?true:false,
                    cursor.getInt(9)==1?true:false, cursor.getInt(10)==1?true:false, cursor.getInt(11)));
        }
        myToonAdapter.addRemoveItem(mList);
        myToonAdapter2.addRemoveItem(mList);
        myToonAdapter3.addRemoveItem(mList);
        myToonAdapter4.addRemoveItem(mList);
        myToonAdapter5.addRemoveItem(mList);
        myToonAdapter6.addRemoveItem(mList);
        myToonAdapter7.addRemoveItem(mList);
        myToonAdapter8.addRemoveItem(mList);
    }

    protected void onResume() {
        super.onResume();
        search.clearFocus();

        if(userInfo.isLogin()){
            navHeader.setBackgroundResource(R.drawable.logout);
            navStatus.setText(userInfo.getUserName()+"님");
        }else{
            navHeader.setBackgroundResource(R.drawable.login);
            navStatus.setText("로그인 해주세요");
        }

        ArrayList<Webtoon> mList = new ArrayList<>();
        coint_sqLiteManager = COINT_SQLiteManager.getInstance(this);
        Cursor cursor = coint_sqLiteManager.getMyWebtoons();
        while (cursor.moveToNext()) {
            mList.add(new Webtoon(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getFloat(3),
                    cursor.getInt(4), cursor.getString(5), cursor.getInt(6), cursor.getString(7).charAt(0), cursor.getInt(8)==1?true:false,
                    cursor.getInt(9)==1?true:false, cursor.getInt(10)==1?true:false, cursor.getInt(11)));
        }
        myToonAdapter.addRemoveItem(mList);
        myToonAdapter2.addRemoveItem(mList);
        myToonAdapter3.addRemoveItem(mList);
        myToonAdapter4.addRemoveItem(mList);
        myToonAdapter5.addRemoveItem(mList);
        myToonAdapter6.addRemoveItem(mList);
        myToonAdapter7.addRemoveItem(mList);
        myToonAdapter8.addRemoveItem(mList);
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
