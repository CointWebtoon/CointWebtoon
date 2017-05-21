package com.kwu.cointwebtoon;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kwu.cointwebtoon.DataStructure.Episode;

public class ViewerMotionActivity extends TypeKitActivity implements View.OnTouchListener{
    private WebView viewer;
    public static final int REQUEST_CODE_RATING = 1001;
    private int id, ep_id;
    private String url;
    private Episode episode_instance;
    private boolean loadComplete = true;
    private Toolbar MotionToonTopToolbar, MotionToonBottomToolbar;
    private Thread toolbarHideThread;
    private float x, y;
    private GetServerData serverData;
    private static final float clickCriteria = 10;
    private boolean runMode = false;
    private TextView episodeTitleTextView, episodeIdTextView, goodCount;
    private Button good;
    private int count = 0;
    private ImageView scrollbar;
    private RelativeLayout scrollSection;
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
        serverData = new GetServerData(this);
        url = "http://m.comic.naver.com/webtoon/detail.nhn?titleId=" + id + "&no=" + ep_id;
        episodeTitleTextView = (TextView)findViewById(R.id.MotionToontEpisodeTitle);
        episodeTitleTextView.setSelected(true);
        episodeIdTextView = (TextView)findViewById(R.id.MotionToont_current_pos);
        good = (Button)findViewById(R.id.MotionToontgood);
        viewer = (WebView) findViewById(R.id.motion_viewer_webView);
        manager = COINT_SQLiteManager.getInstance(this);
        goodCount = (TextView) findViewById(R.id.MotionToont_count_txt);
        scrollSection = (RelativeLayout) findViewById(R.id.scrollSection);
        scrollbar = (ImageView) findViewById(R.id.scrollbar);
        MotionToonTopToolbar = (Toolbar) findViewById(R.id.MotionToontoptoolbar);
        MotionToonBottomToolbar = (Toolbar) findViewById(R.id.MotionToontbottomtoolbar);
        viewer.getSettings().setJavaScriptEnabled(true);
        viewer.setOnTouchListener(this);
        viewer.setVerticalScrollBarEnabled(false);
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
        viewer.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(loadComplete){
                    view.loadUrl("javascript:document.getElementById(\'cbox_module\').style.display=\'none\';" +
                            "document.getElementsByClassName(\'end_sub_cont\')[0].style.display=\'none\';" +
                            "document.getElementsByClassName(\'info_bottom\')[0].style.display=\'none\';" +
                            "document.getElementsByClassName(\'relate_item\')[0].style.display=\'none\';" +
                            "document.getElementsByClassName(\'toon_view_lst\')[0].style.display=\'none\';" +
                            "document.getElementsByClassName(\'item_area\')[0].style.display=\'none\';" +
                            "document.getElementById(\'adPostArea\').style.display=\'none\';void(0);");
                    loadComplete = false;
                }
            }
        });
        viewer.loadUrl(url);
        viewer.getContentHeight();
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
                            if(MotionToonTopToolbar.getVisibility() == View.VISIBLE) {
                                showToolbars(false);
                            }
                        }
                    });
                } catch (InterruptedException intex) {
                    Log.i("thread", "Interrupted");
                }
            }
        });
        toolbarHideThread.start();
    }
    private void showToolbars(boolean show){
        if(show){
            MotionToonTopToolbar.setVisibility(View.VISIBLE);
            MotionToonBottomToolbar.setVisibility(View.VISIBLE);
            MotionToonTopToolbar.animate().translationY(0).withLayer();
            MotionToonBottomToolbar.animate().translationY(0).withLayer();
        }else{
            MotionToonTopToolbar.animate().translationY(-60).withLayer();
            MotionToonBottomToolbar.animate().translationY(60).withLayer();
            MotionToonTopToolbar.setVisibility(View.GONE);
            MotionToonBottomToolbar.setVisibility(View.GONE);
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
                    if(MotionToonTopToolbar.getVisibility() == View.VISIBLE){
                        showToolbars(false);
                    }else if(MotionToonTopToolbar.getVisibility() == View.GONE){
                        showToolbars(true);
                        initializeThread();
                    }
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
        SharedPreferences.Editor editor = likePreference.edit();
        count++;
        if(count % 2 != 0) {
            serverData.likeWebtoon(id, "plus");
            editor.putBoolean(String.valueOf(id), true);
            good.setBackgroundResource(R.drawable.view_heartcolor);
        }
        else if(count % 2 ==0 && likePreference.getBoolean(String.valueOf(id), false)){
            serverData.likeWebtoon(id, "minus");
            editor.putBoolean(String.valueOf(id), false);
            good.setBackgroundResource(R.drawable.view_heartempty);
        }
        editor.commit();
    }
    public void Dat(View v){
        Toast.makeText(this, "댓글 버튼을 클릭했습니다.", Toast.LENGTH_SHORT).show();
    }
    public void Previous(View v) {
        if(ep_id > 1 ){
            ep_id -= 1;
            url = "http://m.comic.naver.com/webtoon/detail.nhn?titleId=" + id + "&no=" + ep_id;
            myUpdate(url);
            manager.updateEpisodeRead(id, ep_id);
            episodeTitleTextView.setText(manager.getEpisodeTitle(id, ep_id));
            episodeIdTextView.setText(String.valueOf(ep_id));
        }
    }
    public void Next(View v) {
        url = "http://m.comic.naver.com/webtoon/detail.nhn?titleId=" + id + "&no=" + ep_id;
        if(ep_id > 0 && (ep_id < manager.maxEpisodeId(id))){
            ep_id += 1;
            url = "http://m.comic.naver.com/webtoon/detail.nhn?titleId=" + id + "&no=" + ep_id;
            myUpdate(url);
            manager.updateEpisodeRead(id, ep_id);
            episodeTitleTextView.setText(manager.getEpisodeTitle(id, ep_id));
            episodeIdTextView.setText(String.valueOf(ep_id));
        }
    }
    public void givingStarBtnClick(View v) {
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

                }catch (NullPointerException ex) {ex.printStackTrace();}
            }
        }
    }
    private class GetCurrentToonInfo extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            episode_instance = manager.getEpisodeInstance(id, ep_id);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (!userInfo.isLogin()) {
                    likePreference.edit().putBoolean(String.valueOf(id), false).commit();
                }
                if (likePreference.getBoolean(String.valueOf(id), false)) {
                    good.setBackgroundResource(R.drawable.view_heartcolor);
                }
            }catch (NullPointerException ex) {ex.printStackTrace();}
        }
    }
}
