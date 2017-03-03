package com.example.epcej.coint_mainactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ViewPager pager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);            // 액션바에서 앱 이름 보이지 않게 함
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        pager = (ViewPager)findViewById(R.id.pager);                        //뷰페이저에 어댑터를 연결하는 부분
        CustomAdapter adapter = new CustomAdapter(this);
        pager.setAdapter(adapter);

        GetServerData getServerData = new GetServerData(this);

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {       // 검색 누르면 실행
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        EditText search = (EditText)findViewById(R.id.searchbar);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Toast.makeText(this,search.getText().toString(),Toast.LENGTH_SHORT).show();     //search를 누르면 해당 텍스트로 디비에 접근해서 결과를 새로운 창에 뿌리기
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {        // navigation drawer에 있는 메뉴 선택시 실행
        // Handle navigation view item clicks here.
        int id = item.getItemId();

/*        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onClick(View view) {        // 베스트도전은 웹뷰로 띄우고, 나머지는 액티비티
        int id = view.getId();
        Intent intent;
        switch (id){
            case R.id.genreBtn:
                intent = new Intent(MainActivity.this, IntentTest.class);
                intent.putExtra("Intent","genre");
                startActivity(intent);
                break;
            case R.id.artistBtn:
                intent = new Intent(MainActivity.this, IntentTest.class);
                intent.putExtra("Intent","artist");
                startActivity(intent);
                break;
            case R.id.weekdayBtn:
                intent = new Intent(MainActivity.this, IntentTest.class);
                intent.putExtra("Intent","weekday");
                startActivity(intent);
                break;
            case R.id.bestBtn:
                //webview로 띄울 예정
                intent = new Intent(MainActivity.this, BestChallenge.class);
                intent.putExtra("Best","bestchallenge");
                startActivity(intent);
                break;
            case R.id.settingBtn:
                intent = new Intent(MainActivity.this, IntentTest.class);
                intent.putExtra("Intent","setting");
                startActivity(intent);
                break;
            case R.id.addItemBtn:
                intent = new Intent(MainActivity.this, IntentTest.class);
                intent.putExtra("Intent","weekday");
                startActivity(intent);
                break;
        }
    }

    public void bestClick(View view) {
        int id = view.getId();
        Intent intent;
        switch (id){
            case R.id.top:
                Toast.makeText(this, "toptop", Toast.LENGTH_SHORT).show();
                break;
            case R.id.middle:
                break;
            case R.id.bottom:
                break;
        }
    }
}
