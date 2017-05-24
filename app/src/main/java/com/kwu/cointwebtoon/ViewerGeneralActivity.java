package com.kwu.cointwebtoon;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
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

public class ViewerGeneralActivity extends TypeKitActivity implements Observer{
    /**
     * UI Components
     */
    private ImageView scrollbar;
    private RelativeLayout scrollSection;
    private Toolbar GeneralToonTopToolbar, GeneralToonBottomToolbar;
    private TextView episodeTitleTextView, episodeIdTextView, goodCount;
    private ImageButton good, runButton, autoScrollButton;
    private RecyclerView recycler;
    private ViewerGeneralAdapter adapter;
    private LinearLayoutManager layoutManager;
    private CointProgressDialog dialog;

    /**
     * Data
     */
    private Application_UserInfo userInfo;
    private COINT_SQLiteManager manager;
    private Webtoon webtoonInstance;
    private Episode episodeInstance;
    private GetServerData getServerData;
    private ArrayList<String> images = new ArrayList<>();
    private SharedPreferences likePreference;
    private float myStar = -1;

    /**
     * Members
     */
    public static final int REQUEST_CODE_RATING = 1001;
    private int toonId, episodeId;
    private int yDelta, ys = 0, maxTopMargin = 0;                          //스크롤바 좌표 계산
    private boolean scrollBySwipe = true;
    private boolean runMode = false;
    private boolean runModeOnce = true;
    private boolean toolbarShowing = true;
    private int count = 0; // 하트 좋아요
    private int toolbarHeight;
    private int scrollWidth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewer_general_activity);
        initData();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getServerData.registerObserver(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getServerData.removeObserver(this);
    }

    @Override
    protected void onDestroy() {
        getServerData.removeObserver(this);
        super.onDestroy();
    }

    /**
     * init UI Components
     */
    private void initView() {
        //Recycler
        recycler = (RecyclerView) findViewById(R.id.viewer_general_recycler);
        layoutManager = new CustomLinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);
        adapter = new ViewerGeneralAdapter(this);
        recycler.setAdapter(adapter);
        recycler.setOnScrollListener(new RecyclerOnScroll());
        recycler.setOnTouchListener(new RecyclerViewClickListener());

        //툴바
        GeneralToonTopToolbar = (Toolbar) findViewById(R.id.GeneralToontoptoolbar);
        GeneralToonBottomToolbar = (Toolbar) findViewById(R.id.GeneralToontbottomtoolbar);
        GeneralToonTopToolbar.setVisibility(View.VISIBLE);
        GeneralToonBottomToolbar.setVisibility(View.VISIBLE);
        setSupportActionBar(GeneralToonTopToolbar);
        toolbarHeight = dpToPixel(42);

        //툴바 자식
        episodeTitleTextView = (TextView) findViewById(R.id.GeneralToontEpisodeTitle);
        episodeTitleTextView.setSelected(true);
        episodeIdTextView = (TextView) findViewById(R.id.GeneralToont_current_pos);
        goodCount = (TextView) findViewById(R.id.GeneralToont_count_txt);
        good = (ImageButton) findViewById(R.id.GeneralToontgood);
        runButton = (ImageButton)findViewById(R.id.general_run);
        autoScrollButton = (ImageButton)findViewById(R.id.general_auto_scroll);

        //오른쪽 스크롤바
        scrollSection = (RelativeLayout) findViewById(R.id.scrollSection);
        scrollbar = (ImageView) findViewById(R.id.scrollbar);
        scrollbar.setOnTouchListener(new ScrollBarOnTouchListener());
        scrollWidth = dpToPixel(25);
    }

    /**
     * init Data
     */
    private void initData() {
        Intent intent = getIntent();
        toonId = intent.getIntExtra("id", -1);
        episodeId = intent.getIntExtra("ep_id", -1);

        if (toonId == -1 | episodeId == -1) {
            Toast.makeText(this, "존재하지 않는 에피소드입니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
        likePreference = getSharedPreferences("episode_like", MODE_PRIVATE);
        userInfo = (Application_UserInfo) getApplication();
        manager = COINT_SQLiteManager.getInstance(this);

        dialog = new CointProgressDialog(this);
        dialog.show();

        getServerData = new GetServerData(this);
        getServerData.registerObserver(this);
        getServerData.getImagesFromServer(toonId, episodeId);
        new GetInstances().execute();
    }

    @Override
    public void update(Observable observable, Object data) {
        dialog.dismiss();
        maxTopMargin = scrollSection.getHeight() - scrollbar.getHeight();
        if (data != null) {
            runModeOnce = true;
            images.clear();
            images.addAll((ArrayList<String>) data);
            adapter.changeData(images);
        }
    }

    private void autoScroll() {
        layoutManager.smoothScrollToPosition(recycler, new RecyclerView.State() , images.size());
    }

    public void showToolbars(boolean show){
        if(show){
            toolbarShowing = true;
            GeneralToonTopToolbar.animate().translationY(0).withLayer();
            GeneralToonBottomToolbar.animate().translationY(0).withLayer();
            scrollSection.animate().translationX(0).withLayer();
            scrollbar.animate().translationX(0).withLayer();
        }else{
            toolbarShowing = false;
            GeneralToonTopToolbar.animate().translationY((-1) * toolbarHeight).withLayer();
            GeneralToonBottomToolbar.animate().translationY(toolbarHeight).withLayer();
            scrollSection.animate().translationX(scrollWidth).withLayer();
            scrollbar.animate().translationX(scrollWidth).withLayer();
        }
    }

    public int dpToPixel(int dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    //Getter
    public Webtoon getWebtoonInstance() {
        return webtoonInstance;
    }

    public Episode getEpisodeInstance() {
        return episodeInstance;
    }

    public float getMyStar() {
        return myStar;
    }

    //---onClick Listener--//
    public void givingStarBtnClick(View v) {
        try {
            if(!userInfo.isLogin()){
                new AlertDialog.Builder(this)
                        .setTitle("로그인")
                        .setMessage("로그인이 필요한 서비스 입니다. 로그인 하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(ViewerGeneralActivity.this, LoginActivity.class));
                            }
                        }).setNegativeButton("아니요", null).show();
                return;
            }
            Intent starIntent = new Intent(this, ViewerStarScoreActivity.class);
            starIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivityForResult(starIntent, REQUEST_CODE_RATING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RATING) {
            if (resultCode == RESULT_OK) {
                try {
                    float SCORE = data.getExtras().getFloat("SCORE");
                    ViewerGeneralAdapter.ViewHolder_TYPE_RATING holder = (ViewerGeneralAdapter.ViewHolder_TYPE_RATING) adapter.rating_holder_public;
                    if (holder != null) {
                        holder.starTv.setText(String.valueOf(SCORE));
                        holder.ratingBar.setMax(10);
                        holder.ratingBar.setRating(SCORE / 2);
                        holder.givingStar.setEnabled(false);
                        manager.updateMyStarScore(toonId, episodeId, SCORE);
                        myStar = SCORE;
                    }
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void general_onClick(View v) {
        switch (v.getId()) {
            case R.id.GeneralToontgotoepi:
            case R.id.GeneralToontEpisodeTitle: {
                //백버튼 행동
                this.finish();
                break;
            }
            case R.id.general_run: {
                //정주행 모드
                if(runMode){
                    runMode = false;
                    runButton.setImageDrawable(getDrawable(R.drawable.run_inactive));
                }else{
                    runMode = true;
                    runButton.setImageDrawable(getDrawable(R.drawable.run_active));
                }
                break;
            }
            case R.id.general_auto_scroll: {
                //오토스크롤
                showToolbars(false);
                autoScroll();
                break;
            }
            case R.id.GeneralToont_count_txt:
            case R.id.GeneralToontgood: {
                //하트
                if(!userInfo.isLogin()){
                    new AlertDialog.Builder(this)
                            .setTitle("로그인")
                            .setMessage("로그인이 필요한 서비스 입니다. 로그인 하시겠습니까?")
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(ViewerGeneralActivity.this, LoginActivity.class));
                                }
                            }).setNegativeButton("아니요", null).show();
                    return;
                }
                SharedPreferences.Editor editor = likePreference.edit();
                count++;
                if(count % 2 != 0) {
                    getServerData.likeWebtoon(toonId, "plus");
                    editor.putBoolean(String.valueOf(toonId), true);
                    good.setImageDrawable(getDrawable(R.drawable.episode_heart_active));
                    if(webtoonInstance != null)
                        goodCount.setText(String.valueOf(webtoonInstance.getLikes() + 1));
                }
                else if(count % 2 == 0 && likePreference.getBoolean(String.valueOf(toonId), false)){
                    getServerData.likeWebtoon(toonId, "minus");
                    editor.putBoolean(String.valueOf(toonId), false);
                    good.setImageDrawable(getDrawable(R.drawable.episode_heart_inactive));
                    if(webtoonInstance != null)
                        goodCount.setText(String.valueOf(webtoonInstance.getLikes()));
                }
                editor.commit();
                break;
            }
            case R.id.GeneralToont_datgule:
            case R.id.GeneralToont_count_txt2: {
                //댓글
                Intent comment_intent = new Intent(this, ViewerCommentActivity.class);
                comment_intent.putExtra("id", toonId);
                comment_intent.putExtra("ep_id", episodeId);
                comment_intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(comment_intent);
                break;
            }
            case R.id.GeneralToont_before: {
                //이전화
                if(episodeId > 1 ){
                    episodeId -= 1;
                    dialog.show();
                    adapter.clearImage();
                    manager.updateEpisodeRead(toonId, episodeId);
                    new GetInstances().execute();
                    getServerData.getImagesFromServer(toonId, episodeId);
                }
                break;
            }
            case R.id.GeneralToont_next: {
                //다음화
                if(episodeId > 0 && (episodeId < manager.maxEpisodeId(toonId))){
                    episodeId += 1;
                    dialog.show();
                    adapter.clearImage();
                    manager.updateEpisodeRead(toonId, episodeId);
                    new GetInstances().execute();
                    getServerData.getImagesFromServer(toonId, episodeId);
                }
                break;
            }
        }
    }

    private class RecyclerViewClickListener implements View.OnTouchListener{
        private float x, y;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    x = event.getX();
                    y = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    float gapX = Math.abs(x - event.getX());
                    float gapY = Math.abs(y - event.getY());
                    if(gapX < 15 && gapY < 15){
                        showToolbars(!toolbarShowing);
                        return true;
                    }
                    break;
            }
            return false;
        }
    }
    //---onClick Listener--//

    //----onScrollListener-----//
    private class ScrollBarOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (images == null)
                return false;
            ys = (int) event.getRawY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    yDelta = ys - lParams.topMargin;
                    break;
                case MotionEvent.ACTION_UP:
                    scrollBySwipe = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    if (scrollSection.getHeight() - (ys - yDelta) > view.getHeight()) {
                        layoutParams.topMargin = ys - yDelta;
                        if (ys - yDelta < 0) {
                            layoutParams.topMargin = 0;
                        }
                    } else {
                        layoutParams.topMargin = maxTopMargin;
                    }
                    scrollBySwipe = false;
                    try {
                        int position = images.size() * layoutParams.topMargin / maxTopMargin;
                        recycler.scrollToPosition(position);
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

    private class RecyclerOnScroll extends RecyclerView.OnScrollListener {
        private boolean scrollFirst = true;
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (images.size() == 0)
                return;
            if(scrollFirst){
                scrollFirst = false;
                return;
            }
            int currentItemPosition = layoutManager.findLastVisibleItemPosition();
            if (scrollBySwipe) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) scrollbar.getLayoutParams();
                int newMargin = currentItemPosition * maxTopMargin / (images.size() + 1);
                if (currentItemPosition == images.size()) {
                    newMargin = maxTopMargin;
                }
                params.topMargin = newMargin;
                scrollbar.setLayoutParams(params);
                scrollSection.invalidate();
            }
            if (runMode && runModeOnce) {
                if (currentItemPosition == images.size()) {
                    runModeOnce = false;
                    if(episodeId > 0 && (episodeId < manager.maxEpisodeId(toonId))){
                        episodeId += 1;
                        dialog.show();
                        recycler.scrollToPosition(0);
                        manager.updateEpisodeRead(toonId, episodeId);
                        new GetInstances().execute();
                        getServerData.getImagesFromServer(toonId, episodeId);
                    }
                }
            }
            super.onScrolled(recyclerView, dx, dy);
        }
    }
    //----onScrollListener-----//

    /**
     * Database 에서 데이터 가져오는 Task
     */
    private class GetInstances extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            episodeInstance = manager.getEpisodeInstance(toonId, episodeId);
            webtoonInstance = manager.getWebtoonInstance(toonId);
            myStar = manager.getMyStar(toonId, episodeId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            episodeTitleTextView.setText(episodeInstance.getEpisode_title());
            episodeTitleTextView.setSelected(true);
            episodeIdTextView.setText(String.valueOf(episodeInstance.getEpisode_id()));
            goodCount.setText(String.valueOf(webtoonInstance.getLikes()));
            if(likePreference.getBoolean(String.valueOf(toonId), false)){
                good.setImageDrawable(getDrawable(R.drawable.episode_heart_active));
            }else{
                good.setImageDrawable(getDrawable(R.drawable.episode_heart_inactive));
            }
        }
    }

    private  class CustomLinearLayoutManager extends LinearLayoutManager{
        public CustomLinearLayoutManager(Context context) {
            super(context);
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
            final LinearSmoothScroller linearSmoothScroller =
                    new LinearSmoothScroller(recyclerView.getContext()) {
                        private static final float MILLISECONDS_PER_INCH = 1000f;

                        @Override
                        public PointF computeScrollVectorForPosition(int targetPosition) {
                            return CustomLinearLayoutManager.this
                                    .computeScrollVectorForPosition(targetPosition);
                        }

                        @Override
                        protected float calculateSpeedPerPixel
                                (DisplayMetrics displayMetrics) {
                            return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
                        }
                    };
            linearSmoothScroller.setTargetPosition(position);
            startSmoothScroll(linearSmoothScroller);
        }
    }
}
