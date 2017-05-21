package com.kwu.cointwebtoon;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.kwu.cointwebtoon.DataStructure.Episode;
import com.kwu.cointwebtoon.Views.Smart_Cut_ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

public class ViewerSmartActivity extends AppCompatActivity implements Observer {
    private ViewFlipper imageFlipper;
    private int toonId, episodeId;
    private ArrayList<String> imageURLs;
    private int imageIndex = 0;
    private HashMap<Integer, Integer> animations = new HashMap<>();
    private Random rand = new Random();
    private Context mContext;
    private Toolbar topToolbar, bottomToolbar;
    private GetServerData serverData;
    private SeekBar progressSeekBar;
    private TextView progressTextView, episodeTitleTextView, episodeIdTextView;
    private boolean runMode = false;
    private COINT_SQLiteManager manager;
    private int sleepTime = -1;
    private Thread autoThread;
    private LayoutInflater inflater = null;
    private Episode episode_instance;
    private RatingBar ratingbar;
    private TextView mention, starTV;
    private Button givingStar;
    public static final int REQUEST_CODE_RATING = 1001;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewer_smart_activity);
        initView();
        initData();
    }

    /**
     * UI Components
     */
    private void initView(){
        topToolbar = (Toolbar) findViewById(R.id.topToolbar_smart);
        bottomToolbar = (Toolbar) findViewById(R.id.bottomToolbar_smart);
        setSupportActionBar(topToolbar);    //현재 Activity의 ActionBar를 Toolbar로 설정
        getWindow().setStatusBarColor(Color.BLACK);
        //애니메이션 in, out 세트 설정
        animations.put(R.anim.push_down_in, R.anim.push_down_out);
        animations.put(R.anim.push_up_in, R.anim.push_up_out);
        animations.put(R.anim.slide_in_left, R.anim.slide_out_right);
        animations.put(R.anim.slide_in_right, R.anim.slide_out_left);
        imageFlipper = (ViewFlipper) findViewById(R.id.smarttoon_Flipper);
        imageFlipper.setOnLongClickListener(new OnFlipperLongClick());
        progressSeekBar = (SeekBar) findViewById(R.id.cutProgressSeekbar);
        progressSeekBar.setOnSeekBarChangeListener(new OnCutSeekBarChanged());
        progressTextView = (TextView) findViewById(R.id.cutProgressTextView);
        episodeTitleTextView = (TextView) findViewById(R.id.smarttoon_episodeTitle);
        episodeTitleTextView.setSelected(true);
        episodeIdTextView = (TextView)findViewById(R.id.smarttoon_episodeId);

        inflater = LayoutInflater.from(this);
    }

    /**
     * Intent, SQlite etc. Data Initializing
     */
    private void initData(){
        mContext = this;
        manager = COINT_SQLiteManager.getInstance(this);
        Intent getIntent = getIntent();
        toonId = getIntent.getIntExtra("id", -1);
        episodeId = getIntent.getIntExtra("ep_id", -1);
        if(toonId == -1 | episodeId == -1){
            Toast.makeText(this, "존재하지 않는 에피소드입니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
        serverData = new GetServerData(this);
        serverData.registerObserver(this);
        serverData.getImagesFromServer(toonId, episodeId);
    }

    //이미지 로드가 완료되었을 때 수행할 행동
    @Override
    public void update(Observable observable, Object data) {
        View view;
        imageURLs = (ArrayList<String>) data;
        if(imageURLs == null){
            return;
        }
        episodeTitleTextView.setText(manager.getEpisodeTitle(toonId, episodeId));
        episodeIdTextView.setText(String.valueOf(episodeId));
        if(imageURLs.size() == 0) {
            Toast.makeText(this, "이미지 로드 에러", Toast.LENGTH_SHORT).show();
            return;
        }
        imageIndex = 0;
        progressSeekBar.setMax(imageURLs.size());
        progressSeekBar.setProgress(1);
        progressTextView.setText("1 / " + imageURLs.size());
        for(int i = 0 ; i < imageURLs.size(); i++){
            Smart_Cut_ImageView newImageView = new Smart_Cut_ImageView(this);
            if(i==0){
                Glide.with(this)
                        .load(imageURLs.get(i))
                        .asBitmap()
                        .placeholder(R.drawable.viewer_sc_placeholder)
                        .into(newImageView);
            }else{
                System.out.println(i);
                Glide.with(this)
                        .load(imageURLs.get(i))
                        .asBitmap()
                        .skipMemoryCache(true)
                        .placeholder(R.drawable.view_placeholder_testing)
                        .into(newImageView);
            }
            imageFlipper.addView(newImageView);
        }
        view = inflater.inflate(R.layout.viewer_rating_item, null);
        imageFlipper.addView(view);
        ratingbar = (RatingBar) view.findViewById(R.id.cut_rating_bar);
        mention = (TextView) view.findViewById(R.id.cut_mention);
        starTV = (TextView) view.findViewById(R.id.cut_starScore);
        givingStar = (Button) view.findViewById(R.id.cut_giving_star);
        new ViewerSmartActivity.GetCurrentToonInfo().execute();
        serverData.plusHit(toonId);
    }
    public void cut_givingStarBtnClick(View v) {
        try {
            startActivityForResult(new Intent(this, ViewerStarScoreActivity.class), REQUEST_CODE_RATING);
        }catch (Exception e) { e.printStackTrace();}
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_RATING) {
            if(resultCode == RESULT_OK) {
                try {
                    float SCORE = data.getExtras().getFloat("SCORE");
                    Toast.makeText(this, "전달 된 별점은 " + SCORE, Toast.LENGTH_SHORT).show();
                    if(starTV != null && ratingbar != null){
                        starTV.setText(String.valueOf(SCORE));
                        ratingbar.setMax(10);
                        ratingbar.setRating((SCORE)/2);
                        givingStar.setEnabled(false);
                    }
                }catch (NullPointerException ex) {ex.printStackTrace();}
            }
        }
    }
    @Override
    protected void onDestroy() {
        serverData.removeObserver(this);
        super.onDestroy();
    }
    public void BackBtn(View v){
        this.finish();
    }

    private void showToolbars(boolean show) {
        if (show) {
            topToolbar.setVisibility(View.VISIBLE);
            bottomToolbar.setVisibility(View.VISIBLE);
        } else {
            topToolbar.setVisibility(View.GONE);
            bottomToolbar.setVisibility(View.GONE);
        }
    }
    /*
     * imageFlipper 를 클릭했을 때의 행동
     * 1. 현재 툴바가 보이는 상태일 경우 툴바를 보이지 않게 설정하고 return
     * 2. 현재 툴바가 보이지 않는 상태이고,
     */
    public void flipperClick(View v) {
        if(imageURLs == null)
            return;
        if (topToolbar.getVisibility() == View.VISIBLE) {
            showToolbars(false);
            return;
        }
        int animationGenerator = rand.nextInt(4);
        switch (animationGenerator) {
            case 0:
                imageFlipper.setInAnimation(mContext, R.anim.push_down_in);
                imageFlipper.setOutAnimation(mContext, animations.get(R.anim.push_down_in));
                break;
            case 1:
                imageFlipper.setInAnimation(mContext, R.anim.push_up_in);
                imageFlipper.setOutAnimation(mContext, animations.get(R.anim.push_up_in));
                break;
            case 2:
                imageFlipper.setInAnimation(mContext, R.anim.slide_in_left);
                imageFlipper.setOutAnimation(mContext, animations.get(R.anim.slide_in_left));
                break;
            case 3:
                imageFlipper.setInAnimation(mContext, R.anim.slide_in_right);
                imageFlipper.setOutAnimation(mContext, animations.get(R.anim.slide_in_right));
                break;
        }
        if (imageIndex < imageURLs.size()) {
            imageFlipper.showNext();
            imageIndex++;
            progressSeekBar.setProgress(imageIndex + 1);
        } else {
            if (runMode && (episodeId < manager.maxEpisodeId(toonId))) {
                //정주행 모드일 때, 마지막 컷에서 Flipper 를 클릭했을 경우 다음 회차가 존재할 경우에 다음 회차로 넘어감
                imageFlipper.removeAllViews();
                imageURLs.clear();
                episodeId += 1;
                serverData.getImagesFromServer(toonId, episodeId);
                manager.updateEpisodeRead(toonId, episodeId);
            }
        }
    }

    public void previousBtnClick(View v) {
        if(v.getId() == R.id.smart_menuBtn){
            showToolbars(true);
            return;
        }
        if (imageIndex > 0) {
            imageFlipper.setInAnimation(null);
            imageFlipper.setOutAnimation(null);
            imageFlipper.showPrevious();
            imageIndex--;
            progressSeekBar.setProgress(imageIndex + 1);
        }else {
            if(runMode && episodeId > 1){   //정주행 모드 일 때, 첫 컷에서 이전 버튼을 클릭하면 이전 회차로 넘어감
                imageFlipper.removeAllViews();
                imageURLs.clear();
                episodeId -= 1;
                serverData.getImagesFromServer(toonId, episodeId);
                manager.updateEpisodeRead(toonId, episodeId);
            }
        }
    }

    public void commentClick(View v) {
        Intent comment_intent = new Intent(this, ViewerCommentActivity.class);
        comment_intent.putExtra("id", toonId);
        comment_intent.putExtra("ep_id", episodeId);
        startActivity(comment_intent);
    }

    public void runButtonClick(View v){
        if(runMode) {
            runMode = false;
            ImageButton target = (ImageButton)v;
            target.setImageDrawable(getDrawable(R.drawable.run_inactive));
        }
        else {
            runMode = true;
            ImageButton target = (ImageButton)v;
            target.setImageDrawable(getDrawable(R.drawable.run_active));
        }
    }
    public void SmartToonPrevious(View v) {
        if(episodeId > 1) {
            imageFlipper.removeAllViews();
            imageURLs.clear();
            episodeId -= 1;
            serverData.getImagesFromServer(toonId, episodeId);
            manager.updateEpisodeRead(toonId, episodeId);
        }
    }
    public void SmartToonNext(View v){
        if(episodeId > 0){
            imageFlipper.removeAllViews();
            imageURLs.clear();
            episodeId += 1;
            serverData.getImagesFromServer(toonId, episodeId);
            manager.updateEpisodeRead(toonId, episodeId);
        }
    }
    public void timerClick(View v) {
        ImageButton my = (ImageButton) v;
        if(sleepTime == -1) {
            my.setImageDrawable(getDrawable(R.drawable.viewer_2sec));
            sleepTime = 2000;
        }
        else if(sleepTime == 2000) {
            my.setImageDrawable(getDrawable(R.drawable.viewer_3sec));
            sleepTime = 3000;
        }
        else if(sleepTime == 3000) {
            my.setImageDrawable(getDrawable(R.drawable.viewer_4sec));
            sleepTime = 4000;
        }
        else if(sleepTime == 4000) {
            my.setImageDrawable(getDrawable(R.drawable.viewer_defaultset));
            sleepTime = -1;
            try {
                autoThread.interrupt();
            } catch (Exception e) {
            }
            return;
        }
        try {
            autoThread.interrupt();
        } catch (Exception e) {
        }
        autoThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(sleepTime);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                flipperClick(imageFlipper);
                            }
                        });
                    } catch (InterruptedException intex) {
                        break;
                    }
                }
            }
        });
        autoThread.start();
    }

    private class OnFlipperLongClick implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {
            showToolbars(true);
            return true;
        }
    }
    private class GetCurrentToonInfo extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            episode_instance = manager.getEpisodeInstance(toonId, episodeId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ratingbar.setRating(0);
            mention.setText(episode_instance.getMention());
            starTV.setText(String.valueOf(0.0));
        }
    }

    private class OnCutSeekBarChanged implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            try {
                if (fromUser) {
                    if (progress == 0)
                        progress = 1;
                    imageFlipper.setDisplayedChild(progress - 1);
                    imageIndex = progress - 1;
                }
                progressTextView.setText(String.valueOf(progress) + " / " + String.valueOf(imageFlipper.getChildCount()));
            } catch (Exception e) {
            }
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }
}
