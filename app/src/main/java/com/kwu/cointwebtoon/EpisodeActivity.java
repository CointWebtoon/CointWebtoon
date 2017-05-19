package com.kwu.cointwebtoon;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kwu.cointwebtoon.DataStructure.Episode;
import com.kwu.cointwebtoon.DataStructure.Webtoon;

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
    private EpisodeActivityAdapter recyclerAdapter;
    private RelativeLayout scrollSection;
    private ImageView scrollbar, likeImageButton, myImageButton;
    private TextView title;
    private Toolbar toolbar;
    private FloatingActionButton main, top, first, home;
    private CointProgressDialog progressDialog;

    /**
     * Data
     */
    private ArrayList<Episode> episodes = new ArrayList<>();
    private COINT_SQLiteManager manager;
    private GetServerData getServerData;
    private int currentToonId;
    private char currentToonType;
    private Webtoon currentWebtoon;
    private SharedPreferences likePreference;
    Application_UserInfo userInfo;

    /**
     * Members
     */
    private boolean isFirst = true;             //읽은 화까지 스크롤할 때 사용
    private int yDelta, y = 0;                          //스크롤바 좌표 계산
    private int maxTopMargin = 0;               //스크롤바 좌표 계산
    private boolean scrollManually = true;      //스크롤바로 스크롤했는지, 제스처로 스크롤했는지
    private boolean isFloatingShown = false;
    private Thread timeOutThread;

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
        recyclerAdapter = new EpisodeActivityAdapter(this, episodes);
        recycler = (RecyclerView) findViewById(R.id.episode_recycler);
        recycler.setVerticalScrollBarEnabled(false);
        recycler.setOnScrollListener(new ActionbarShowHideListener());
        recycler.setAdapter(recyclerAdapter);
        title = (TextView) findViewById(R.id.episodeActivity_Title);
        toolbar = (Toolbar) findViewById(R.id.episode_toolbar);
        toolbar.bringToFront();
        setSupportActionBar(toolbar);
        title.setSelected(true);
        main = (FloatingActionButton) findViewById(R.id.episode_floating_more);
        top = (FloatingActionButton) findViewById(R.id.episode_floating_top);
        first = (FloatingActionButton) findViewById(R.id.episode_floating_first);
        home = (FloatingActionButton) findViewById(R.id.episode_floating_home);
        likeImageButton = (ImageButton) findViewById(R.id.episode_like);
        myImageButton = (ImageButton) findViewById(R.id.episode_my);
        progressDialog = new CointProgressDialog(this);
        progressDialog.show();
    }

    private void initData() {
        userInfo = (Application_UserInfo)getApplication();
        likePreference = getSharedPreferences("episode_like", MODE_PRIVATE);
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
        new GetCurrentToonInfo().execute();
        getServerData = new GetServerData(this);
        getServerData.registerObserver(this);
        timeOutThread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000 * 15);
                } catch (InterruptedException e) {
                    Log.i("coint", "No Time Out");
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("coint", "Time Out");
                        Toast.makeText(EpisodeActivity.this, "에피소드 목록을 가져오지 못했습니다.", Toast.LENGTH_LONG).show();
                        EpisodeActivity.this.finish();
                    }
                });
            }
        };
        timeOutThread.start();
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
        try {
            timeOutThread.interrupt();
        } catch (Exception e) {
        }
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
                hideFloatingButtons();
                recyclerAdapter.addEpisodes(episodes);
                progressDialog.dismiss();
            }
        });
    }


    /**
     * 서버에서 데이터 로드가 완료되었을 때 호출되는 메소드
     *
     * @param observable
     * @param data
     */
    @Override
    public void update(Observable observable, Object data) {
        new Thread(){
            public void run(){
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
                    isFirst = false;
                    final int readNumCopy = readNumber;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recycler.getLayoutManager().scrollToPosition(readNumCopy);
                            getSupportActionBar().show();
                        }
                    });
                }
                Log.i("ProgressReport", "Cursor Updated From Server 총 , " + String.valueOf(episodeCursor.getCount()) + "개의 Row");
                maxTopMargin = scrollSection.getHeight() - scrollbar.getHeight();
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
        }
        getServerData.removeObserver(this);
        super.onDestroy();
    }

    public void onRecyclerViewItemClick(View v) {
        Episode target = (Episode) v.findViewById(R.id.reg_date).getTag();
        if(currentWebtoon.isAdult()){
            Log.i("coint", "로그인 상태 : " + String.valueOf(userInfo.isLogin()));
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
                                startActivity(new Intent(EpisodeActivity.this, LoginActivity.class));
                            }
                        }).setNegativeButton("아니요", null).show();
                return;
            }
        }
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
            case 'M': {//모션툰
                manager.updateEpisodeRead(target.getId(), target.getEpisode_id());
                Intent motionIntent = new Intent(this, ViewerMotionActivity.class);
                motionIntent.putExtra("id", target.getId());
                motionIntent.putExtra("ep_id", target.getEpisode_id());
                startActivity(motionIntent);
                break;
            }
        }
    }

    private class ScrollBarOnTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (episodes.size() == 0)
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
                    try {
                        recycler.scrollToPosition((episodes.size() - 1) * layoutParams.topMargin / maxTopMargin);
                    } catch (ArithmeticException e) {
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
     * 스크롤 시 스크롤바를 보이도록, 안보이도록 한다.
     *
     * @param show - true : 보임 false : 안보임
     */
    private void showUIs(boolean show) {
        if (show) {
            /**
             * 상단바 고정인게 좋을 것 같다는 의견 반영해서 주석!
             */
            //getSupportActionBar().show();
            scrollbar.setVisibility(View.VISIBLE);
            scrollSection.setVisibility(View.VISIBLE);
        } else {
            //getSupportActionBar().hide();
            scrollbar.setVisibility(View.GONE);
            scrollSection.setVisibility(View.GONE);
        }
    }

    public void episodeFloatingClick(View v) {
        switch (v.getId()) {
            case R.id.episode_floating_more:
                if (isFloatingShown) {
                    hideFloatingButtons();
                } else {
                    showFloatingButtons();
                }
                break;
            case R.id.episode_floating_first:
                if(currentWebtoon.isAdult()){
                    Log.i("coint", "로그인 상태 : " + String.valueOf(userInfo.isLogin()));
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
                                        startActivity(new Intent(EpisodeActivity.this, LoginActivity.class));
                                    }
                                }).setNegativeButton("아니요", null).show();
                        return;
                    }
                }
                recycler.scrollToPosition(recycler.getChildCount() - 1);
                switch (currentToonType) {
                    case 'G': {//일반툰
                        manager.updateEpisodeRead(currentToonId, 1);
                        Intent generalIntent = new Intent(this, ViewerGerneralActivity.class);
                        generalIntent.putExtra("id", currentToonId);
                        generalIntent.putExtra("ep_id", 1);
                        startActivity(generalIntent);
                        break;
                    }
                    case 'C': {//컷툰
                        manager.updateEpisodeRead(currentToonId, 1);
                        Intent cutIntent = new Intent(this, ViewerCutActivity.class);
                        cutIntent.putExtra("id", currentToonId);
                        cutIntent.putExtra("ep_id", 1);
                        startActivity(cutIntent);
                        break;
                    }
                    case 'S': {//스마트툰
                        manager.updateEpisodeRead(currentToonId, 1);
                        Intent smartIntent = new Intent(this, ViewerSmartActivity.class);
                        smartIntent.putExtra("id", currentToonId);
                        smartIntent.putExtra("ep_id", 1);
                        startActivity(smartIntent);
                        break;
                    }
                    case 'M': {//모션툰
                        manager.updateEpisodeRead(currentToonId, 1);
                        Intent motionIntent = new Intent(this, ViewerMotionActivity.class);
                        motionIntent.putExtra("id", currentToonId);
                        motionIntent.putExtra("ep_id", 1);
                        startActivity(motionIntent);
                        break;
                    }
                }
                break;
            case R.id.episode_floating_top:
                try {
                    recycler.scrollToPosition(0);
                } catch (Exception e) {
                    //index outofbound exception 방지
                }
                break;
            case R.id.episode_floating_home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finishAffinity();
                break;
        }
    }

    public void episodeToolbarClick(View v) {
        switch (v.getId()) {
            case R.id.episode_like:
            case R.id.episode_like_text:
                SharedPreferences.Editor editor = likePreference.edit();
                if (likePreference.getBoolean(String.valueOf(currentToonId), false)) {//좋아요 취소
                    editor.putBoolean(String.valueOf(currentToonId), false);
                    likeImageButton.setImageDrawable(getDrawable(R.drawable.episode_heart_inactive));
                } else {//좋아요
                    editor.putBoolean(String.valueOf(currentToonId), true);
                    likeImageButton.setImageDrawable(getDrawable(R.drawable.episode_heart_active));
                }
                editor.commit();
                break;
            case R.id.episode_my:
            case R.id.episode_my_text:
                //마이웹툰작업
                String result = manager.updateMyWebtoon(String.valueOf(currentWebtoon.getId()));
                if (result.equals("마이 웹툰 설정")) {
                    currentWebtoon.setIs_mine(true);
                    myImageButton.setImageDrawable(getDrawable(R.drawable.my_set));
                } else if (result.equals("마이 웹툰 해제")) {
                    currentWebtoon.setIs_mine(false);
                    myImageButton.setImageDrawable(getDrawable(R.drawable.my_release));
                }
                Toast.makeText(this, currentWebtoon.getTitle() + " " + result, Toast.LENGTH_SHORT).show();
                break;
            case R.id.episodeActivity_Title:
            case R.id.episode_finish:
                finish();
                break;
        }
    }

    /**
     * 메인 플로팅 버튼 클릭시 다른 플로팅 버튼 띄우는 함수
     */
    private void showFloatingButtons() {
        isFloatingShown = true;
        main.setImageDrawable(getDrawable(R.drawable.floating_close));
        first.animate().translationY(0).setInterpolator(new DecelerateInterpolator(1)).start();
        top.animate().translationY(0).setInterpolator(new DecelerateInterpolator(1)).start();
        home.animate().translationY(0).setInterpolator(new DecelerateInterpolator(1)).start();
    }

    /**
     * 메인 플로팅 버튼 클릭시 다른 플로팅 버튼 없애는 함수
     */
    private void hideFloatingButtons() {
        isFloatingShown = false;
        main.setImageDrawable(getDrawable(R.drawable.floating_more));
        first.animate().translationY(first.getHeight() + 19).setInterpolator(new AccelerateInterpolator(1)).start();
        top.animate().translationY((top.getHeight() + 19) * 2).setInterpolator(new AccelerateInterpolator(1)).start();
        home.animate().translationY((home.getHeight() + 19) * 3).setInterpolator(new AccelerateInterpolator(1)).start();
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
            if (currentFirstVisibleItem < this.mLastFirstVisibleItem && !scrollSection.isShown()) {
                showUIs(true);
            } else if (currentFirstVisibleItem > this.mLastFirstVisibleItem && scrollSection.isShown()) {
                if (scrollManually) {
                    showUIs(false);
                }
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

    private class GetCurrentToonInfo extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            currentWebtoon = manager.getWebtoonInstance(currentToonId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            title.setText(currentWebtoon.getTitle());
            if (currentWebtoon.isMine()) {
                myImageButton.setImageDrawable(getDrawable(R.drawable.my_set));
            }
            if (likePreference.getBoolean(String.valueOf(currentToonId), false)) {
                likeImageButton.setImageDrawable(getDrawable(R.drawable.episode_heart_active));
            }
        }
    }
}