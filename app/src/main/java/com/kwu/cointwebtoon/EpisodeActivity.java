package com.kwu.cointwebtoon;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kwu.cointwebtoon.DataStructure.Episode;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * 회차 정보 Activity
 */
public class EpisodeActivity extends TypeKitActivity implements Observer {
    private static final int ERR_CODE = -1;
    /**
     * Views
     */
    private RecyclerView recycler;
    private RelativeLayout scrollSection;
    private ImageView scrollbar;
    private TextView title;
    private Toolbar toolbar;

    /**
     * Data
     */
    private ArrayList<Episode> episodes = new ArrayList<>();
    private COINT_SQLiteManager manager;
    private GetServerData getServerData;
    private int currentToonId;
    private char currentToonType;

    /**
     * Members
     */
    private boolean isFirst = true;             //읽은 화까지 스크롤할 때 사용
    private int yDelta, y = 0;                          //스크롤바 좌표 계산
    private int maxTopMargin = 0;               //스크롤바 좌표 계산
    private boolean scrollManually = true;      //스크롤바로 스크롤했는지, 제스처로 스크롤했는지

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.episode_activity);
        initData();
        initView();
    }

    private void initView() {
        scrollSection = (RelativeLayout) findViewById(R.id.scrollSection);
        scrollbar = (ImageView) findViewById(R.id.scrollbar);
        scrollbar.setOnTouchListener(new ScrollBarOnTouchListener());
        recycler = (RecyclerView) findViewById(R.id.episode_recycler);
        recycler.setVerticalScrollBarEnabled(false);
        recycler.setOnScrollListener(new ActionbarShowHideListener());
        title = (TextView) findViewById(R.id.episodeActivity_Title);
        toolbar = (Toolbar) findViewById(R.id.episode_toolbar);
        toolbar.bringToFront();
        setSupportActionBar(toolbar);
        title.setText(manager.getWebtoonTitleById(currentToonId));
    }

    private void initData() {
        Intent getIntent = getIntent();
        currentToonId = getIntent.getIntExtra("id", ERR_CODE);
        if (currentToonId == ERR_CODE) {
            Toast.makeText(this, "존재하지 않는 타이틀입니다.", Toast.LENGTH_LONG);
            this.finish();
        }
        currentToonType = getIntent.getCharExtra("toontype", 'U');
        if (currentToonType == 'U') {
            Toast.makeText(this, "존재하지 않는 타이틀입니다.", Toast.LENGTH_LONG);
            this.finish();
        }
        manager = COINT_SQLiteManager.getInstance(this);
        getServerData = new GetServerData(this);
        getServerData.registerObserver(this);
        new Thread() {
            public void run() {
                Cursor episodeCursor = manager.getEpisodes(currentToonId);
                if (episodeCursor.getCount() < 1) {
                    getServerData.getEpisodesFromServer(currentToonId);
                } else {
                    //일단 Cursor에 있는 데이터를 띄움
                    updateCursorFromSQLite(episodeCursor);
                    getServerData.getEpisodesFromServer(currentToonId);
                }
            }
        }.start();
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
                recycler.setAdapter(new EpisodeActivityAdapter(EpisodeActivity.this, episodes));
            }
        });
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

    public void onRecyclerViewItemClick(View v) {
        Episode target = (Episode) v.findViewById(R.id.reg_date).getTag();
        switch (currentToonType) {
            case 'G': {//일반툰
                manager.updateEpisodeRead(target.getId(), target.getEpisode_id());
                Intent generalIntent = new Intent(this, ViewerGerneralActivity.class);
                generalIntent.putExtra("id", target.getId());
                generalIntent.putExtra("ep_id", target.getEpisode_id());
                startActivity(generalIntent);
                break;
            }
            case 'C': {//컷툰
                manager.updateEpisodeRead(target.getId(), target.getEpisode_id());
                Intent cutIntent = new Intent(this, ViewerCutActivity.class);
                cutIntent.putExtra("id", target.getId());
                cutIntent.putExtra("ep_id", target.getEpisode_id());
                startActivity(cutIntent);
                break;
            }
            case 'S': {//스마트툰
                manager.updateEpisodeRead(target.getId(), target.getEpisode_id());
                Intent smartIntent = new Intent(this, ViewerSmartActivity.class);
                smartIntent.putExtra("id", target.getId());
                smartIntent.putExtra("ep_id", target.getEpisode_id());
                startActivity(smartIntent);
                break;
            }
        }
    }

    private class ScrollBarOnTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if(episodes.size() == 0)
                return false;
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
                    try{
                        recycler.scrollToPosition((episodes.size() - 1) * layoutParams.topMargin / maxTopMargin);
                    }catch(ArithmeticException e){
                        //Divided By Zero Excpetion 처리 --> 아직 아이템들이 로드되지 않았을 때 스크롤을 하면 이렇게 됨
                        return true;
                    }
                    view.setLayoutParams(layoutParams);

                    break;
            }
            scrollSection.invalidate();
            return true;
        }
    }

    /**
     * 스크롤 시 상단바와 스크롤바를 보이도록, 안보이도록 한다.
     *
     * @param show - true : 보임 false : 안보임
     */
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

    /**
     * 액티비티의 리사이클러뷰 스크롤 시 스크롤 리스너(상단바, 스크롤바 숨김 등)
     */
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
            if (currentFirstVisibleItem < this.mLastFirstVisibleItem && toolbar.getVisibility() == View.GONE) {
                showUIs(true);
            } else if (currentFirstVisibleItem > this.mLastFirstVisibleItem && toolbar.getVisibility() == View.VISIBLE) {
                if (scrollManually)
                    showUIs(false);
            }
            if (scrollManually) {
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