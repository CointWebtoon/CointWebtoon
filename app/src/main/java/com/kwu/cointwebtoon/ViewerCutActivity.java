package com.kwu.cointwebtoon;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.kwu.cointwebtoon.DataStructure.Episode;
import com.kwu.cointwebtoon.Views.Smart_Cut_ImageView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Handler;

public class ViewerCutActivity extends TypeKitActivity implements Observer {
    public static enum Action {
        PREVIOUS,
        NEXT,
        None // when no action was detected
    }

    private ArrayList<String> imageURLs;//imageURL 담을 ArrayList
    private Episode episode_instance;
    private ViewFlipper flipper;//이미지를 넘기면서 볼 수 있는 뷰
    private int imageIndex = 0;//현재 보고 있는 컷이 몇 번째 컷인가?
    private int count = 0; // 좋아요 기능을 위한 변수
    private GetServerData getServerData;
    private int toonId, episodeId;
    private Button good;
    private Toolbar topToolbar, bottomToolbar;
    private COINT_SQLiteManager manager;
    private TextView episodeTitleTextView, episodeIdTextView;
    private boolean runMode, autoMode;
    private Thread autoThread;
    private int sleepTime = -1;
    private RatingBar ratingbar;
    private TextView mention, starTV;
    private LayoutInflater inflater = null;
    private Button givingStar;
    public static final int REQUEST_CODE_RATING = 1001;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewer_cut_activity);
        good = (Button) findViewById(R.id.good);
        topToolbar = (Toolbar) findViewById(R.id.toptoolbar);
        bottomToolbar = (Toolbar) findViewById(R.id.bottomtoolbar);
        episodeTitleTextView = (TextView) findViewById(R.id.CutToonTitle);
        episodeIdTextView = (TextView) findViewById(R.id.current_pos);
        manager = COINT_SQLiteManager.getInstance(this);
        runMode = false;
        autoMode = false;
        setSupportActionBar(topToolbar);
        flipper = (ViewFlipper) findViewById(R.id.viewflipper);
        inflater = LayoutInflater.from(this);

        ///Flipper 리스너 추가 + SwipeDetector Class를 추가하여 Swipe과 touch 동작을 구분
        final SwipeDetector swipeDetector = new SwipeDetector();
        flipper.setOnTouchListener(swipeDetector);

        flipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swipeDetector.swipeDetected()) {
                    switch (swipeDetector.getAction()) {
                        case PREVIOUS:
                            MovewPreviousView();
                            break;
                        case NEXT:
                            MoveNextView();
                            break;
                    }
                } else {
                    //클릭 처리
                    if (topToolbar.getVisibility() == View.VISIBLE) {
                        showToolbars(false);
                    } else if (topToolbar.getVisibility() == View.GONE) {
                        showToolbars(true);
                    }
                }
            }

        });

        flipper.setBackgroundColor(Color.WHITE);
        Intent getIntent = getIntent();
        toonId = getIntent.getIntExtra("id", -1);
        episodeId = getIntent.getIntExtra("ep_id", -1);

        if (toonId == -1 || episodeId == -1) {
            Toast.makeText(this, "이미지 로드를 실패하였습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
        getServerData = new GetServerData(this);
        getServerData.registerObserver(this);
        getServerData.getImagesFromServer(toonId, episodeId);
    }

    @Override
    public void update(Observable observable, Object o) {
        View view;
        Log.i("update", "되냐?");
        episodeTitleTextView.setText(manager.getEpisodeTitle(toonId, episodeId));
        episodeIdTextView.setText(String.valueOf(episodeId));
        this.imageURLs = (ArrayList<String>) o;
        imageIndex = 0;
        if (imageURLs != null) {
            for (String imageURL : imageURLs) {
                Smart_Cut_ImageView newImageView = new Smart_Cut_ImageView(this);
                Glide.with(this)
                        .load(imageURL)
                        .asBitmap()
                        .placeholder(R.drawable.view_placeholder)
                        .into(newImageView);
                flipper.addView(newImageView);
            }
            view = inflater.inflate(R.layout.viewer_rating_item, null);
            flipper.addView(view);
            ratingbar = (RatingBar) view.findViewById(R.id.cut_rating_bar);
            mention = (TextView) view.findViewById(R.id.cut_mention);
            starTV = (TextView) view.findViewById(R.id.cut_starScore);
            givingStar = (Button) view.findViewById(R.id.cut_giving_star);
            new ViewerCutActivity.GetCurrentToonInfo().execute();
        }
        getServerData.plusHit(toonId);
    }

    private void MoveNextView() {
        if (imageIndex < imageURLs.size()) {
            flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.appear_from_right));
            flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.disappear_to_left));
            flipper.showNext();
            imageIndex++;
        } else {
            if (runMode && (episodeId < manager.maxEpisodeId(toonId))) {
                //정주행 모드일 때, 마지막 컷에서 Flipper 를 클릭했을 경우 다음 회차가 존재할 경우에 다음 회차로 넘어감
                flipper.removeAllViews();
                imageURLs.clear();
                episodeId += 1;
                getServerData.getImagesFromServer(toonId, episodeId);
                manager.updateEpisodeRead(toonId, episodeId);
            }
        }
    }

    private void MovewPreviousView() {
        if (imageIndex > 0) {
            flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.appear_from_left));
            flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.disappear_to_right));
            flipper.showPrevious();
            imageIndex--;
        } else {
            if (runMode && episodeId > 1) {   //정주행 모드 일 때, 첫 컷에서 이전 버튼을 클릭하면 이전 회차로 넘어감
                flipper.removeAllViews();
                imageURLs.clear();
                episodeId -= 1;
                getServerData.getImagesFromServer(toonId, episodeId);
                manager.updateEpisodeRead(toonId, episodeId);
            }
        }
    }

    private void showToolbars(boolean show) {
        if (show) {
            topToolbar.setVisibility(View.VISIBLE);
            bottomToolbar.setVisibility(View.VISIBLE);
            topToolbar.animate().translationY(0).withLayer();
            bottomToolbar.animate().translationY(0).withLayer();
        } else {
            topToolbar.setVisibility(View.GONE);
            bottomToolbar.setVisibility(View.GONE);
            topToolbar.animate().translationY(-60).withLayer();
            bottomToolbar.animate().translationY(60).withLayer();
        }
    }

    public void runButtonClick(View v) {
        if (runMode) {
            runMode = false;
            ImageButton target = (ImageButton) v;
            target.setImageDrawable(getDrawable(R.drawable.run_inactive));
        } else {
            runMode = true;
            ImageButton target = (ImageButton) v;
            target.setImageDrawable(getDrawable(R.drawable.run_active));
        }
    }

    public void BackBtn(View v) {
        this.finish();
    }

    public void HeartBtn(View v) {
        Toast.makeText(this, "좋아요 버튼을 클릭했습니다.", Toast.LENGTH_SHORT).show();
        count++;
        if (count % 2 != 0) {
            good.setBackgroundResource(R.drawable.view_heartcolor);
        } else {
            good.setBackgroundResource(R.drawable.view_heartempty);
        }
    }

    public void Dat(View v) {
        Toast.makeText(this, "댓글 버튼을 클릭했습니다.", Toast.LENGTH_SHORT).show();
    }

    public void Previous(View v) {
        if(episodeId > 1){
            flipper.removeAllViews();
            imageURLs.clear();
            episodeId -= 1;
            getServerData.getImagesFromServer(toonId, episodeId);
            manager.updateEpisodeRead(toonId, episodeId);
        }
    }

    public void Next(View v) {
        if(episodeId > 0 ){
            flipper.removeAllViews();
            imageURLs.clear();
            episodeId += 1;
            getServerData.getImagesFromServer(toonId, episodeId);
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
                                MoveNextView();
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
    private class SwipeDetector implements View.OnTouchListener {
        public final int HORIZONTAL_MIN_DISTANCE = 40;
        private float downX, upX;
        private Action mSwipeDetected = Action.None;

        public boolean swipeDetected() {
            return mSwipeDetected != Action.None;
        }

        public Action getAction() {
            return mSwipeDetected;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (imageURLs == null) {
                return true;
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = event.getX();
                    mSwipeDetected = Action.None;
                    return false;
                case MotionEvent.ACTION_MOVE:
                    upX = event.getX();
                    float deltaX = downX - upX;
                    if (Math.abs(deltaX) > HORIZONTAL_MIN_DISTANCE) {
                        if (deltaX < 0) {
                            mSwipeDetected = Action.PREVIOUS;
                            return true;
                        }
                        if (deltaX > 0) {
                            mSwipeDetected = Action.NEXT;
                            return true;
                        }
                    }
            }
            return false;
        }
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
                        starTV.setText(String.valueOf(episode_instance.getEp_starScore() + SCORE));
                        ratingbar.setMax(10);
                        ratingbar.setRating((episode_instance.getEp_starScore() + SCORE)/2);
                        givingStar.setEnabled(false);
                    }
                }catch (NullPointerException ex) {ex.printStackTrace();}
            }
        }
    }
    @Override
    protected void onDestroy() {
        getServerData.removeObserver(this);
        super.onDestroy();
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
            ratingbar.setRating(episode_instance.getEp_starScore()/2);
            mention.setText(episode_instance.getMention());
            starTV.setText(String.valueOf(episode_instance.getEp_starScore()));
        }
    }
}
