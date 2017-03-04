package com.jmapplication.com.episodeactivity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

public class EpisodeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Observer {

    ListView listView;
    ArrayList<Episode> episodes = new ArrayList<>();
    COINT_SQLiteManager manager;
    GetServerData getServerData;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {//hello
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        manager = COINT_SQLiteManager.getInstance(this);
        getServerData =  new GetServerData(this);
        getServerData.registerObserver(this);
        listView = (ListView)findViewById(R.id.episodeListView);
        listView.setDivider(null);
        progressBar = (ProgressBar)findViewById(R.id.loadingbar) ;
        progressBar.setIndeterminate(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(){
            public void run(){
                Cursor episodeCursor = manager.getEpisodes(20853);
                if(episodeCursor.getCount() < 1){
                    //프로그레스바 동작
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    });
                    getServerData.getEpisodesFromServer(20853);
                }else {
                    //일단 Cursor에 있는 데이터를 띄움
                    updateCursorFromSQLite(episodeCursor);
                    getServerData.getEpisodesFromServer(20853);
                }
            }
        }.start();
    }

    private void updateCursorFromSQLite(Cursor episodeCursor){
        int id_E, episode_id, likes_E;
        String episode_title, ep_thumburl, reg_date, mention;
        float ep_starscore;
        Episode episode;
        while(episodeCursor.moveToNext()){
            id_E = episodeCursor.getInt(0);
            episode_id = episodeCursor.getInt(1);
            episode_title = episodeCursor.getString(2);
            ep_starscore = episodeCursor.getFloat(3);
            ep_thumburl = episodeCursor.getString(4);
            reg_date = episodeCursor.getString(5);
            mention = episodeCursor.getString(6);
            likes_E = episodeCursor.getInt(7);

            //Log.i("episodeData", String.valueOf(id_E) + " " +  String.valueOf(episode_id) + " " + episode_title + " " + String.valueOf(ep_starscore) + " " + ep_thumburl + " " + reg_date + " " + mention);

            episode = new Episode(id_E, episode_id, episode_title, ep_starscore, ep_thumburl, reg_date, mention, likes_E);
            episodes.add(episode);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(new EpisodeAdapter(EpisodeActivity.this, makeData(episodes), episodes.size()));
            }
        });
    }

    /*
     * 현재 각 리스트 뷰 아이템 안에는 Episode 데이터가 7개씩 들어가야 함. 따라서 만들어진 ArrayList 를
     * Episode[7]를 묶은 형태의 ArrayList<Episode[]> 형태로 변경할 필요성을 느낌
     * Input Parameter : Server, SQLite 에서 가져온 Cursor 를 ArrayList<Episode> 형으로 만든 데이터
     * Output : 에피소드가 7개씩 묶인 형태의 ArrayList
     */
    private ArrayList<Episode[]> makeData(ArrayList<Episode> episodes){
        ArrayList<Episode[]> data = new ArrayList<>();
        Episode[] episodeArray = new Episode[7];

        for(int i = 0 ; i < episodes.size(); i++){
            episodeArray[i%7] = episodes.get(i);
            if((i != 0)&&((((i + 1) % 7) == 0) || i == episodes.size() -1)){
                data.add(episodeArray);
                episodeArray = new Episode[7];
            }
        }
        return data;
    }

    @Override
    public void update(Observable observable, Object data) {
        Cursor episodeCursor = manager.getEpisodes(20853);
        updateCursorFromSQLite(episodeCursor);
        progressBar.setVisibility(View.GONE);
        Log.i("ProgressReport", "Cursor Updated , " + String.valueOf(episodeCursor.getCount()) + "개의 Row");
    }

    @Override
    protected void onDestroy() {
        getServerData.removeObserver(this);
        super.onDestroy();
    }

    public void onImageViewItemClick(View v){
        Integer tag = (Integer)v.getTag();
        if(tag != null){
            Toast.makeText(this, String.valueOf(tag), Toast.LENGTH_SHORT).show();
        }
    }

    //----------------------DRAWER METHODS---------------------a//

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.episode, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
