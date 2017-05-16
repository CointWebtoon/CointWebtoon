package com.kwu.cointwebtoon;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ViewerMotionActivity extends TypeKitActivity implements View.OnTouchListener{
    private WebView viewer;
    private boolean loadComplete = true;
    private Toolbar MotionToonTopToolbar, MotionToonBottomToolbar;
    private Thread toolbarHideThread;
    private float x, y;
    private static final float clickCriteria = 10;
    private boolean runMode = false;
    private TextView episodeTitleTextView, episodeIdTextView, starScore;
    private Button good;
    private int count = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewer_motion_activity);
        String url = "http://m.comic.naver.com/webtoon/detail.nhn?titleId=655277&no=22";
        episodeTitleTextView = (TextView)findViewById(R.id.MotionToontEpisodeTitle);
        episodeIdTextView = (TextView)findViewById(R.id.MotionToont_current_pos);
        good = (Button)findViewById(R.id.MotionToontgood);
        viewer = (WebView) findViewById(R.id.motion_viewer_webView);
        MotionToonTopToolbar = (Toolbar) findViewById(R.id.MotionToontoptoolbar);
        MotionToonBottomToolbar = (Toolbar) findViewById(R.id.MotionToontbottomtoolbar);
        viewer.getSettings().setJavaScriptEnabled(true);
        viewer.setOnTouchListener(this);
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
        initializeThread();
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
        Toast.makeText(this, "좋아요 버튼을 클릭했습니다.", Toast.LENGTH_SHORT).show();
        count++;
        if(count % 2 != 0) {
            good.setBackgroundResource(R.drawable.view_heartcolor);
        }
        else{
            good.setBackgroundResource(R.drawable.view_heartempty);
        }
    }
    public void Dat(View v){
        Toast.makeText(this, "댓글 버튼을 클릭했습니다.", Toast.LENGTH_SHORT).show();
    }
    public void Previous(View v) { Toast.makeText(this, "이전화 보기 버튼을 클릭했습니다.", Toast.LENGTH_SHORT).show();}
    public void Current(View v) {Toast.makeText(this, "현재회차 버튼을 클릭했습니다.", Toast.LENGTH_SHORT).show();}
    public void Next(View v) {Toast.makeText(this, "다음화 보기 버튼을 클릭했습니다.", Toast.LENGTH_SHORT).show();}
    public void givingStarBtnClick(View v) { startActivity(new Intent(v.getContext(), ViewerStarScoreActivity.class));}
}
