package com.kwu.cointwebtoon;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kwu.cointwebtoon.DataStructure.Episode;
import com.kwu.cointwebtoon.DataStructure.Webtoon;

public class ViewerMotionActivity extends TypeKitActivity implements View.OnTouchListener{
    private WebView viewer;
    public static final int REQUEST_CODE_RATING = 1001;
    private int id, ep_id;
    private String url;
    private Webtoon webtoon_instance;
    private Episode episode_instance;
    private boolean loadComplete = true;
    private Toolbar MotionToonTopToolbar, MotionToonBottomToolbar;
    private Thread toolbarHideThread;
    private float x, y;
    private GetServerData serverData;
    private static final float clickCriteria = 15;
    private boolean runMode = false;
    private int toolbarheight = 0;
    private TextView episodeTitleTextView, episodeIdTextView, goodCount;
    private ImageButton good;
    private  boolean showtoolbar;
    private int count = 0;
    private int motion_like = 0;
    private COINT_SQLiteManager manager;
    private SharedPreferences likePreference;
    private Application_UserInfo userInfo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewer_motion_activity);
        Intent getIntent = getIntent();
        id = getIntent.getIntExtra("id", -1);
        ep_id = getIntent.getIntExtra("ep_id", -1);
        if(id == -1 | ep_id == -1){
            Toast.makeText(this, "존재하지 않는 에피소드입니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
        likePreference = getSharedPreferences("episode_like", MODE_PRIVATE);
        serverData = new GetServerData(this);
        url = "http://m.comic.naver.com/webtoon/detail.nhn?titleId=" + id + "&no=" + ep_id;
        episodeTitleTextView = (TextView)findViewById(R.id.MotionToontEpisodeTitle);
        episodeTitleTextView.setSelected(true);
        episodeIdTextView = (TextView)findViewById(R.id.MotionToont_current_pos);
        good = (ImageButton)findViewById(R.id.MotionToontgood);
        viewer = (WebView) findViewById(R.id.motion_viewer_webView);
        manager = COINT_SQLiteManager.getInstance(this);
        showtoolbar = true;
        goodCount = (TextView) findViewById(R.id.MotionToont_count_txt);
        MotionToonTopToolbar = (Toolbar) findViewById(R.id.MotionToontoptoolbar);
        MotionToonBottomToolbar = (Toolbar) findViewById(R.id.MotionToontbottomtoolbar);
        viewer.getSettings().setJavaScriptEnabled(true);
        viewer.setOnTouchListener(this);
        viewer.setVerticalScrollBarEnabled(false);
        toolbarheight = dpToPixel(42);
        myUpdate(url);
        initializeThread();
        episode_instance = manager.getEpisodeInstance(id,ep_id);
        new ViewerMotionActivity.GetCurrentToonInfo().execute();
        userInfo = (Application_UserInfo)getApplication();
        episodeTitleTextView.setText(manager.getEpisodeTitle(id, ep_id));
        episodeIdTextView.setText(String.valueOf(ep_id));
        goodCount.setText(String.valueOf(manager.getEpisodeInstance(id, ep_id).getLikes_E()));
    }
    private void myUpdate(String url){
        try {
            viewer.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    if (loadComplete) {
                        view.loadUrl("javascript:document.getElementById(\'cbox_module\').style.display=\'none\';" +
                                "document.getElementsByClassName(\'info_bottom\')[0].style.display=\'none\';" +
                                "document.getElementsByClassName(\'share_area\')[0].style.display=\'none\';" +
                                "document.getElementsByClassName(\'btn_open\')[0].style.display=\'none\';" +
                                "document.getElementsByClassName(\'navi_area\')[0].style.display=\'none\';" +
                                "document.getElementsByClassName(\'relate_item\')[0].style.display=\'none\';" +
                                "document.getElementsByClassName(\'toon_view_lst\')[0].style.display=\'none\';" +
                                "document.getElementsByClassName(\'item_area\')[0].style.display=\'none\';" +
                                "document.getElementById(\'starUser\').style.display=\'none\';" +
                                "document.getElementById(\'adPostArea\').style.display=\'none\';void(0);");
                        loadComplete = false;
                    }
                }
            });
            viewer.loadUrl(url);
            viewer.getContentHeight();
        }catch (Exception ex){}
    }
    private void initializeThread() {
        try {
            toolbarHideThread.interrupt();
        } catch (Exception e) { }
        toolbarHideThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(showtoolbar){
                                showToolbars(!showtoolbar);
                            }
                        }
                    });
                } catch (InterruptedException intex) {
                }
            }
        });
        toolbarHideThread.start();
    }
    public int dpToPixel(int dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private void showToolbars(boolean show) {
        showtoolbar = show;
        if (show) {
            MotionToonTopToolbar.animate().translationY(0).withLayer();
            MotionToonBottomToolbar.animate().translationY(0).withLayer();
        } else {
            MotionToonTopToolbar.animate().translationY(-1 * toolbarheight).withLayer();
            MotionToonBottomToolbar.animate().translationY(toolbarheight).withLayer();
        }
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                if(Math.abs(event.getX() - x) < clickCriteria && Math.abs(event.getY() - y) < clickCriteria){
                    showToolbars(!showtoolbar);
                    return true;
                }
                break;
        }
        return false;
    }
    @Override
    protected void onDestroy() {
        try{
            toolbarHideThread.interrupt();
        }catch (Exception e){}
        super.onDestroy();
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
    public void BackBtn(View v) {
        this.finish();
    }
    public void HeartBtn(View v){
        if(!userInfo.isLogin()){
            new AlertDialog.Builder(this)
                    .setTitle("로그인")
                    .setMessage("로그인이 필요한 서비스 입니다. 로그인 하시겠습니까?")
                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(ViewerMotionActivity.this, LoginActivity.class));
                        }
                    }).setNegativeButton("아니요", null).show();
            return;
        }
        try {
            SharedPreferences.Editor editor = likePreference.edit();
            count++;
            if (count % 2 != 0) {
                serverData.likeWebtoon(id, "plus");
                editor.putBoolean(String.valueOf(id), true);
                good.setImageDrawable(getDrawable(R.drawable.episode_heart_active));
                goodCount.setText(String.valueOf(motion_like + 1));

            } else if (count % 2 == 0 && likePreference.getBoolean(String.valueOf(id), false)) {
                serverData.likeWebtoon(id, "minus");
                editor.putBoolean(String.valueOf(id), false);
                good.setImageDrawable(getDrawable(R.drawable.episode_heart_inactive));
                if (motion_like >= 0) {
                    goodCount.setText(String.valueOf(motion_like));
                }
            }
            editor.commit();
        }catch (NullPointerException ex){}
    }
    public void Dat(View v){
        try {
            Intent comment_intent = new Intent(this, ViewerCommentActivity.class);
            comment_intent.putExtra("id", id);
            comment_intent.putExtra("ep_id", ep_id);
            comment_intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(comment_intent);
        }catch (NullPointerException ex){}
    }
    public void Previous(View v) {
        try {
            if (ep_id > 1) {
                ep_id -= 1;
                url = "http://m.comic.naver.com/webtoon/detail.nhn?titleId=" + id + "&no=" + ep_id;
                myUpdate(url);
                manager.updateEpisodeRead(id, ep_id);
                episodeTitleTextView.setText(manager.getEpisodeTitle(id, ep_id));
                episodeIdTextView.setText(String.valueOf(ep_id));
            }
        }catch (NullPointerException ex){}
    }
    public void Next(View v) {
        try {
            url = "http://m.comic.naver.com/webtoon/detail.nhn?titleId=" + id + "&no=" + ep_id;
            if (ep_id > 0 && (ep_id < manager.maxEpisodeId(id))) {
                ep_id += 1;
                url = "http://m.comic.naver.com/webtoon/detail.nhn?titleId=" + id + "&no=" + ep_id;
                myUpdate(url);
                manager.updateEpisodeRead(id, ep_id);
                episodeTitleTextView.setText(manager.getEpisodeTitle(id, ep_id));
                episodeIdTextView.setText(String.valueOf(ep_id));
            }
        }catch (NullPointerException ex){}
    }
    public void givingStarBtnClick(View v) {
        try {
            Intent intent = new Intent(this, ViewerStarScoreActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivityForResult(intent, REQUEST_CODE_RATING);
        }catch (Exception e) {}
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_RATING) {
            if(resultCode == RESULT_OK) {
                try {
                    float SCORE = data.getExtras().getFloat("SCORE");
                    Toast.makeText(this, "전달 된 별점은 " + SCORE, Toast.LENGTH_SHORT).show();

                }catch (NullPointerException ex) {ex.printStackTrace();}
            }
        }
    }
    private class GetCurrentToonInfo extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            episode_instance = manager.getEpisodeInstance(id, ep_id);
            webtoon_instance = manager.getWebtoonInstance(id);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            motion_like = webtoon_instance.getLikes();
            goodCount.setText(String.valueOf(motion_like));
            try {
                if (!userInfo.isLogin()) {
                    likePreference.edit().putBoolean(String.valueOf(id), false).commit();
                }
                if (likePreference.getBoolean(String.valueOf(id), false)) {
                    good.setBackgroundResource(R.drawable.episode_heart_active);
                }
            }catch (NullPointerException ex) {ex.printStackTrace();}
        }
    }
}
