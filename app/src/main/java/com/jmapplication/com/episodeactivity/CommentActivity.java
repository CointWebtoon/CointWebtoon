package com.jmapplication.com.episodeactivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by jm on 2017-03-05.
 */

public class CommentActivity extends AppCompatActivity{
    WebView viewer;
    float xValue;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment);
        viewer = (WebView)findViewById(R.id.commentWebView);
        viewer.setWebViewClient(new WebViewClient());
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
        viewer.loadUrl("http://comic.naver.com/comment/comment.nhn?titleId=20853&no=1080");
    }
}
