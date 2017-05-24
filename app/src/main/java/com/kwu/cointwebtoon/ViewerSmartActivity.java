package com.kwu.cointwebtoon;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.kwu.cointwebtoon.DataStructure.Episode;
import com.kwu.cointwebtoon.DataStructure.Webtoon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

public class ViewerSmartActivity extends TypeKitActivity implements Observer {
    public static final int REQUEST_CODE_RATING = 1001;

    /**
     * UI Components
     */
    private ViewFlipper targetView;
    private ViewerSmartCustomAdapter adapter;
    private Toolbar topToolbar, bottomToolbar;
    private TextView progressTextView, starTV, artistTV, mentionTV, episodeTitleTextView, episodeIdTextView;
    private SeekBar progressSeekBar;
    private ImageButton runButton, autoScrollButton;
    private View ratingView;
    private RatingBar ratingBar;
    private Button givingStar;
    private CointProgressDialog dialog;
    private RelativeLayout parentLayout;

    /**
     * Data
     */
    private int toonId, episodeId;
    private COINT_SQLiteManager manager;
    private Application_UserInfo userInfo;
    private GetServerData getServerData;
    private ArrayList<String> imageURLs = new ArrayList<>();
    private Webtoon webtoon_instance;
    private Episode episode_instance;
    private float myStar = -1;

    /**
     * Members
     */
    private Thread autoThread;
    private HashMap<Integer, Integer> animations = new HashMap<>();
    private boolean isToolbarShowing = true, runMode = false;
    private int toolbarHeight, sleepTime = -1;
    private Random rand = new Random();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewer_smart_activity);
        initView();
        initData();
    }

    /**
     * Init UI Components
     */
    private void initView() {
        //--Toolbar--//
        topToolbar = (Toolbar) findViewById(R.id.topToolbar_smart);
        bottomToolbar = (Toolbar) findViewById(R.id.bottomToolbar_smart);
        setSupportActionBar(topToolbar);    //현재 Activity의 ActionBar를 Toolbar로 설정
        getWindow().setStatusBarColor(Color.parseColor("#333333"));
        toolbarHeight = dpToPixel(42);
        //--Toolbar--//

        //--Flipper--//
        targetView = (ViewFlipper) findViewById(R.id.smarttoon_Flipper);
        targetView.setOnLongClickListener(new OnFlipperLongClick());
        adapter = new ViewerSmartCustomAdapter(this, targetView);

        //--Seek--//
        progressTextView = (TextView) findViewById(R.id.cutProgressTextView);
        progressSeekBar = (SeekBar) findViewById(R.id.cutProgressSeekbar);
        progressSeekBar.setOnSeekBarChangeListener(new OnCutSeekBarChanged());

        //--Buttons--//
        runButton = (ImageButton) findViewById(R.id.smart_run);
        autoScrollButton = (ImageButton) findViewById(R.id.smart_timer);

        //--TextViews--//
        episodeTitleTextView = (TextView) findViewById(R.id.smarttoon_episodeTitle);
        episodeIdTextView = (TextView) findViewById(R.id.smarttoon_episodeId);
        episodeTitleTextView.setSelected(true);

        //--ratingView--//
        parentLayout = (RelativeLayout) findViewById(R.id.smart_relative_layout);
        ratingView = LayoutInflater.from(this).inflate(R.layout.viewer_rating_item, null);
        ratingBar = (RatingBar) ratingView.findViewById(R.id.cut_rating_bar);
        mentionTV = (TextView) ratingView.findViewById(R.id.cut_mention);
        starTV = (TextView) ratingView.findViewById(R.id.cut_starScore);
        givingStar = (Button) ratingView.findViewById(R.id.cut_giving_star);
        artistTV = (TextView) ratingView.findViewById(R.id.cut_artist);
        ratingView.setVisibility(View.GONE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.topMargin = toolbarHeight;
        params.bottomMargin = toolbarHeight;
        ratingView.setLayoutParams(params);
        parentLayout.addView(ratingView);

        //--ProgressDialog--//
        dialog = new CointProgressDialog(this);
    }

    private void initData() {
        //--랜덤 애니메이션 Pair 초기화--//
        animations.put(R.anim.push_down_in, R.anim.push_down_out);
        animations.put(R.anim.push_up_in, R.anim.push_up_out);
        animations.put(R.anim.slide_in_left, R.anim.slide_out_right);
        animations.put(R.anim.slide_in_right, R.anim.slide_out_left);

        manager = COINT_SQLiteManager.getInstance(this);
        Intent getIntent = getIntent();
        toonId = getIntent.getIntExtra("id", -1);
        episodeId = getIntent.getIntExtra("ep_id", -1);

        if (toonId == -1 | episodeId == -1) {
            Toast.makeText(this, "존재하지 않는 에피소드입니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
        dialog.show();
        new GetCurrentToonInfo().execute();
        getServerData = new GetServerData(this);
        getServerData.registerObserver(this);
        getServerData.getImagesFromServer(toonId, episodeId);
        userInfo = (Application_UserInfo) getApplication();
    }

    @Override
    public void update(Observable observable, Object data) {
        ratingView.setVisibility(View.GONE);
        dialog.dismiss();
        if (data != null) {
            imageURLs.clear();
            imageURLs = (ArrayList<String>) data;
            if (imageURLs.size() != 0) {
                adapter.changeImageData(imageURLs);
                progressSeekBar.setMax(adapter.getItemCount());
                progressSeekBar.setProgress(1);
                progressTextView.setText("1 / " + adapter.getItemCount());
            }
        }
    }

    private void autoSetDefault() {
        autoScrollButton.setImageDrawable(getDrawable(R.drawable.viewer_defaultset));
        sleepTime = -1;
        try {
            autoThread.interrupt();
        } catch (Exception e) {
        }
    }

    //----------onClick Listener--------//

    /**
     * imageFlipper 를 클릭했을 때의 행동
     * 1. 현재 툴바가 보이는 상태일 경우 툴바를 보이지 않게 설정하고 return
     * 2. 현재 툴바가 보이지 않는 상태이면 다음 컷으로 넘김
     */
    public void flipperClick(View v) {
        ratingView.setVisibility(View.GONE);
        if (imageURLs == null)
            return;
        if (isToolbarShowing) {
            showToolbars(!isToolbarShowing);
            return;
        }
        int animationGenerator = rand.nextInt(4);
        switch (animationGenerator) {
            case 0:
                targetView.setInAnimation(this, R.anim.push_down_in);
                targetView.setOutAnimation(this, animations.get(R.anim.push_down_in));
                break;
            case 1:
                targetView.setInAnimation(this, R.anim.push_up_in);
                targetView.setOutAnimation(this, animations.get(R.anim.push_up_in));
                break;
            case 2:
                targetView.setInAnimation(this, R.anim.slide_in_left);
                targetView.setOutAnimation(this, animations.get(R.anim.slide_in_left));
                break;
            case 3:
                targetView.setInAnimation(this, R.anim.slide_in_right);
                targetView.setOutAnimation(this, animations.get(R.anim.slide_in_right));
                break;
        }
        if (adapter.showNext()) {
            progressSeekBar.setProgress(adapter.getPosition() + 1);
        } else if (adapter.getPosition() == adapter.getItemCount() - 1 && runMode) {
            //정주행 모드일 때
            if (episodeId > 0 && (episodeId < manager.maxEpisodeId(toonId))) {
                episodeId += 1;
                dialog.show();
                new GetCurrentToonInfo().execute();
                getServerData.getImagesFromServer(toonId, episodeId);
                manager.updateEpisodeRead(toonId, episodeId);
            }
        } else if (adapter.getPosition() == adapter.getItemCount() - 1 && !runMode) {
            //정주행 모드가 아닐 때 --> inflate View Show
            try {
                autoThread.interrupt();
            } catch (Exception e) {
            }
            autoSetDefault();
            ratingView.setVisibility(View.VISIBLE);
            showToolbars(true);
        }
    }

    public void smartButtonClick(View v) {
        ratingView.setVisibility(View.GONE);
        switch (v.getId()) {
            case R.id.smart_previous_cut: {
                //이전 컷
                adapter.showPrevious();
                break;
            }
            case R.id.smart_menuBtn: {
                //툴바 보이게
                showToolbars(true);
                break;
            }
            case R.id.smarttoon_episodeTitle:
            case R.id.smarttoon_gotoepi: {
                //finish
                this.finish();
                break;
            }
            case R.id.smart_run: {
                //정주행
                runMode = !runMode;
                if (runMode) {
                    runButton.setImageDrawable(getDrawable(R.drawable.run_active));
                } else {
                    runButton.setImageDrawable(getDrawable(R.drawable.run_inactive));
                }
                break;
            }
            case R.id.smart_timer: {
                //오토페이징
                ImageButton my = (ImageButton) v;
                if (sleepTime == -1) {
                    my.setImageDrawable(getDrawable(R.drawable.viewer_2sec));
                    sleepTime = 2000;
                } else if (sleepTime == 2000) {
                    my.setImageDrawable(getDrawable(R.drawable.viewer_3sec));
                    sleepTime = 3000;
                } else if (sleepTime == 3000) {
                    my.setImageDrawable(getDrawable(R.drawable.viewer_4sec));
                    sleepTime = 4000;
                } else if (sleepTime == 4000) {
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
                                        targetView.performClick();
                                    }
                                });
                            } catch (InterruptedException intex) {
                                break;
                            }
                        }
                    }
                });
                autoThread.start();
                break;
            }
            case R.id.SmartToon_datgule: {
                //댓글 액티비티 실행
                Intent comment_intent = new Intent(this, ViewerCommentActivity.class);
                comment_intent.putExtra("id", toonId);
                comment_intent.putExtra("ep_id", episodeId);
                comment_intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(comment_intent);
                break;
            }
            case R.id.smart_previous_episode: {
                //이전 화
                if (episodeId > 1) {
                    episodeId -= 1;
                    dialog.show();
                    new GetCurrentToonInfo().execute();
                    getServerData.getImagesFromServer(toonId, episodeId);
                    manager.updateEpisodeRead(toonId, episodeId);
                }
                break;
            }
            case R.id.smart_next_episode: {
                //다음 화
                if (episodeId > 0 && (episodeId < manager.maxEpisodeId(toonId))) {
                    episodeId += 1;
                    dialog.show();
                    new GetCurrentToonInfo().execute();
                    getServerData.getImagesFromServer(toonId, episodeId);
                    manager.updateEpisodeRead(toonId, episodeId);
                }
                break;
            }
        }
    }

    public void cut_givingStarBtnClick(View v) {
        try {
            if (!userInfo.isLogin()) {
                new AlertDialog.Builder(this)
                        .setTitle("로그인")
                        .setMessage("로그인이 필요한 서비스 입니다. 로그인 하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(ViewerSmartActivity.this, LoginActivity.class));
                            }
                        }).setNegativeButton("아니요", null).show();
                return;
            }
            Intent intent = new Intent(this, ViewerStarScoreActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivityForResult(intent, REQUEST_CODE_RATING);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RATING) {
            if (resultCode == RESULT_OK) {
                try {
                    float SCORE = data.getExtras().getFloat("SCORE");
                    Toast.makeText(this, "전달 된 별점은 " + SCORE, Toast.LENGTH_SHORT).show();
                    if (starTV != null && ratingBar != null) {
                        starTV.setText(String.valueOf(SCORE));
                        ratingBar.setMax(10);
                        ratingBar.setRating((SCORE) / 2);
                        givingStar.setEnabled(false);
                        manager.updateMyStarScore(toonId, episodeId, SCORE);
                    }
                } catch (NullPointerException ex) {
                }
            }
        }
    }
    //----------onClick Listener--------//

    /**
     * 툴바를 숨기고 나타내는 메소드
     *
     * @param show true : 나타나게 함
     *             false : 숨김
     */
    private void showToolbars(boolean show) {
        isToolbarShowing = show;
        if (show) {
            topToolbar.animate().translationY(0).withLayer();
            bottomToolbar.animate().translationY(0).withLayer();
        } else {
            if (ratingView.getVisibility() == View.VISIBLE) {
                return;
            }
            topToolbar.animate().translationY((-1) * toolbarHeight).withLayer();
            bottomToolbar.animate().translationY(toolbarHeight).withLayer();
        }
    }

    public int dpToPixel(int dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    @Override
    protected void onDestroy() {
        getServerData.removeObserver(this);
        try {
            autoThread.interrupt();
        } catch (Exception e) {
        }
        super.onDestroy();
    }

    /**
     * -----------------Classes------------------------
     */
    private class OnFlipperLongClick implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {
            showToolbars(!isToolbarShowing);
            return true;
        }
    }

    private class OnCutSeekBarChanged implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            try {
                progressTextView.setText(String.valueOf(adapter.getPosition() + 1) + " / " + String.valueOf(adapter.getItemCount()));
            } catch (Exception e) {
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (ratingView.getVisibility() == View.VISIBLE) {
                ratingView.setVisibility(View.GONE);
            }
            if (seekBar.getProgress() == 0) {
                adapter.showChild(0);
            } else {
                adapter.showChild(seekBar.getProgress() - 1);
            }
            progressTextView.setText(String.valueOf(adapter.getPosition() + 1) + " / " + String.valueOf(adapter.getItemCount()));
        }
    }

    private class GetCurrentToonInfo extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                episode_instance = manager.getEpisodeInstance(toonId, episodeId);
                webtoon_instance = manager.getWebtoonInstance(toonId);
                myStar = manager.getMyStar(toonId, episodeId);
            } catch (NullPointerException ex) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (myStar != -1) {
                    ratingBar.setMax(10);
                    ratingBar.setRating(myStar / 2);
                    givingStar.setEnabled(false);
                    starTV.setText(String.valueOf(myStar));
                } else {
                    givingStar.setEnabled(true);
                    ratingBar.setRating(0);
                    starTV.setText("0.0");
                }
                artistTV.setText("작가의 말 (" + webtoon_instance.getArtist() + ")");
                artistTV.setPaintFlags(artistTV.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
                mentionTV.setText(episode_instance.getMention());
                episodeIdTextView.setText(String.valueOf(episode_instance.getEpisode_id()));
                episodeTitleTextView.setText(episode_instance.getEpisode_title());
            } catch (NullPointerException ex) {
            }
        }
    }
}
