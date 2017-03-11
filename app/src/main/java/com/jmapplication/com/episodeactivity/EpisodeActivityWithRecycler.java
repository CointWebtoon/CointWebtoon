package com.jmapplication.com.episodeactivity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by jm on 2017-03-05.
 */

public class EpisodeActivityWithRecycler extends AppCompatActivity implements Observer {
    private RecyclerView recycler;
    private ArrayList<Episode> episodes = new ArrayList<>();
    private COINT_SQLiteManager manager;
    private GetServerData getServerData;
    private int currentToonId = 626907;
    private boolean isFirst = true;
    private RelativeLayout scrollSection;
    private ImageView scrollbar;
    private int yDelta, y = 0;
    private int maxTopMargin = 0;
    private boolean scrollManually = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("마음의 소리");
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo);
        setContentView(R.layout.recycleractivity);
        scrollSection = (RelativeLayout) findViewById(R.id.scrollSection);
        scrollbar = (ImageView) findViewById(R.id.scrollbar);
        scrollbar.setOnTouchListener(new ScrollBarOnTouchListener());
        recycler = (RecyclerView) findViewById(R.id.episode_recycler);
        recycler.setVerticalScrollBarEnabled(false);
        recycler.setOnScrollListener(new ActionbarShowHideListener());
        manager = COINT_SQLiteManager.getInstance(this);
        getServerData = new GetServerData(this);
        getServerData.registerObserver(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        get();
    }

    private void updateCursorFromSQLite(Cursor episodeCursor) {
        episodes.clear();
        int id_E, episode_id, likes_E, is_read;
        String episode_title, ep_thumburl, reg_date, mention;
        float ep_starscore;
        Episode episode;
        while (episodeCursor.moveToNext()) {
            id_E = episodeCursor.getInt(0);
            episode_id = episodeCursor.getInt(1);
            episode_title = episodeCursor.getString(2);
            ep_starscore = episodeCursor.getFloat(3);
            ep_thumburl = episodeCursor.getString(4);
            reg_date = episodeCursor.getString(5);
            mention = episodeCursor.getString(6);
            likes_E = episodeCursor.getInt(7);
            is_read = episodeCursor.getInt(8);

            //Log.i("episodeData", String.valueOf(id_E) + " " +  String.valueOf(episode_id) + " " + episode_title + " " + String.valueOf(ep_starscore) + " " + ep_thumburl + " " + reg_date + " " + mention + " " + is_read);

            episode = new Episode(id_E, episode_id, episode_title, ep_starscore, ep_thumburl, reg_date, mention, likes_E, is_read);
            episodes.add(episode);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recycler.setAdapter(new RecyclerAdapter(EpisodeActivityWithRecycler.this, episodes));
            }
        });
    }

    private void get() {
        new Thread() {
            public void run() {
                Cursor episodeCursor = manager.getEpisodes(currentToonId);
                if (episodeCursor.getCount() < 1) {
                    //프로그레스바 동작하게 하자
                    getServerData.getEpisodesFromServer(currentToonId);
                } else {
                    //일단 Cursor에 있는 데이터를 띄움
                    updateCursorFromSQLite(episodeCursor);
                    getServerData.getEpisodesFromServer(currentToonId);
                }
            }
        }.start();
    }

    @Override
    public void update(Observable observable, Object data) {
        Cursor episodeCursor = manager.getEpisodes(currentToonId);
        updateCursorFromSQLite(episodeCursor);
        if (isFirst) {
            int readNumber;
            for (readNumber = 0; readNumber < episodes.size(); readNumber++) {
                if (episodes.get(readNumber).getIs_read() == 1)
                    break;
            }
            if (readNumber > 0 && readNumber < episodes.size())
                readNumber--;
            else if (readNumber >= episodes.size())
                readNumber = 0;
            recycler.getLayoutManager().scrollToPosition(readNumber);
            isFirst = false;
            getSupportActionBar().show();
        }
        Log.i("ProgressReport", "Cursor Updated From Server 총 , " + String.valueOf(episodeCursor.getCount()) + "개의 Row");
        maxTopMargin = scrollSection.getHeight() - scrollbar.getHeight();
    }

    @Override
    protected void onDestroy() {
        getServerData.removeObserver(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.episode_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        this.finish();
        return super.onOptionsItemSelected(item);
    }

    public void onRecyclerViewItemClick(View v) {
        Episode tag = (Episode) v.findViewById(R.id.reg_date).getTag();
        if (tag != null) {
            Toast.makeText(this, String.valueOf(tag.getEpisode_id()) + " " + String.valueOf(tag.getIs_read()), Toast.LENGTH_SHORT).show();
        }
        manager.updateEpisodeRead(tag.getId(), tag.getEpisode_id());
        updateCursorFromSQLite(manager.getEpisodes(currentToonId));
    }

    private void showUIs(boolean show) {
        if (show) {
            getSupportActionBar().show();
            scrollbar.setVisibility(View.VISIBLE);
            scrollSection.setVisibility(View.VISIBLE);
        } else {
            getSupportActionBar().hide();
            scrollbar.setVisibility(View.GONE);
            scrollSection.setVisibility(View.GONE);
        }
    }

    private class ScrollBarOnTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            y = (int) event.getRawY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    yDelta = y - lParams.topMargin;
                    break;

                case MotionEvent.ACTION_UP:
                    scrollManually = true;
                    break;

                case MotionEvent.ACTION_MOVE:
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    if (scrollSection.getHeight() - (y - yDelta) > view.getHeight()) {
                        layoutParams.topMargin = y - yDelta;
                        if (y - yDelta < 0) {
                            layoutParams.topMargin = 0;
                        }
                    } else {
                        layoutParams.topMargin = maxTopMargin;
                    }
                    scrollManually = false;
                    recycler.scrollToPosition((episodes.size() - 1) * layoutParams.topMargin / maxTopMargin);
                    view.setLayoutParams(layoutParams);

                    break;
            }
            scrollSection.invalidate();
            return true;
        }
    }

    private class ActionbarShowHideListener extends RecyclerView.OnScrollListener {
        private int mLastFirstVisibleItem = 0;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            final int currentFirstVisibleItem = layoutManager.findFirstVisibleItemPosition();
            if (currentFirstVisibleItem < this.mLastFirstVisibleItem) {
                showUIs(true);
            } else if (currentFirstVisibleItem > this.mLastFirstVisibleItem) {
                if (scrollManually)
                    showUIs(false);
            }
            if(scrollManually){
                //스크롤바를 통한 스크롤이 아닐 때
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) scrollbar.getLayoutParams();
                layoutParams.topMargin = maxTopMargin * currentFirstVisibleItem / (episodes.size() - 1);
                scrollbar.setLayoutParams(layoutParams);
                scrollSection.invalidate();
            }
            this.mLastFirstVisibleItem = currentFirstVisibleItem;
        }
    }
}
