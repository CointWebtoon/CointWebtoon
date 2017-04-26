package com.jmapplication.com.episodeactivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class CommentActivity extends AppCompatActivity {
    private WebView viewer;
    private float xValue;
    private int toonId, episodeId;
    private String URLToLoad;
    private COINT_Application mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment);
        getWindow().setStatusBarColor(Color.BLACK);
        mContext = (COINT_Application)getApplicationContext();
        viewer = (WebView)findViewById(R.id.commentWebView);
        viewer.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(url.contains("nid.naver.com/nidlogin.login")){
                    Log.i("CommentActivity", "자동 로그인");
                    final String js = "javascript:" +
                            "document.getElementById('pw').value = '" + mContext.getNaverPW() + "';"  +
                            "document.getElementById('id').value = '" + mContext.getNaverID() + "';"  +
                            "document.getElementById('frmNIDLogin').submit();";
                    view.loadUrl(js);
                }else if(url.contains("scrolltoon")){
                    view.loadUrl("javascript:document.getElementsByTagName(\"header\")[0].setAttribute(\"style\",\"display:none;\");" +
                            "document.getElementsByClassName(\"info_bottom\")[0].setAttribute(\"style\",\"display:none;\");" +
                            "document.getElementsByClassName(\"navi_area\")[0].setAttribute(\"style\",\"display:none;\");" +
                            "document.getElementsByClassName(\"btn_prev\")[0].setAttribute(\"style\",\"display:none;\");" +
                            "document.getElementsByClassName(\"u_cbox_select\")[0].click();");
                    viewer.setVisibility(View.VISIBLE);
                }
            }
        });
        WebSettings webSettings = viewer.getSettings();
        webSettings.setJavaScriptEnabled(true);
        viewer.setHorizontalScrollBarEnabled(false);
        viewer.setVerticalScrollBarEnabled(false);
        viewer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        xValue = event.getX();
                        break;
                    default :
                        event.setLocation(xValue, event.getY());
                }
                return false;
            }
        });

        Intent getIntent = getIntent();
        toonId = getIntent.getIntExtra("id", -1);
        episodeId = getIntent.getIntExtra("ep_id", -1);
        toonId = -1;
        episodeId = 1241;

        if(toonId == -1 || episodeId == -1) {
            Toast.makeText(this, "댓글 정보 로드를 실패하였습니다.", Toast.LENGTH_SHORT).show();
            //this.finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewer.setVisibility(View.GONE);
        String commentURL = "http://m.comic.naver.com/scrolltoon/comment.nhn?titleId=" + String.valueOf(toonId) + "&no=" + String.valueOf(episodeId);
        URLToLoad = mContext.isLogin() ? "https://nid.naver.com/nidlogin.login?svctype=262144&url=" + commentURL: commentURL;
        LinearLayout layout = (LinearLayout)findViewById(R.id.Comment_nonLogin);
        if(mContext.isLogin()){
            layout.setVisibility(View.GONE);
        }else
            layout.setVisibility(View.VISIBLE);
        viewer.loadUrl(URLToLoad);
    }

    public void loginBtnClick(View v){
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    protected void onDestroy() {
        CookieManager.getInstance().removeAllCookie();
        super.onDestroy();
    }
}
