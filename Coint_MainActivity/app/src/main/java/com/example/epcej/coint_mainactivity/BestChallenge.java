package com.example.epcej.coint_mainactivity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by epcej on 2017-02-21.
 */

public class BestChallenge extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.best_challenge);

        WebView webView = (WebView)findViewById(R.id.challengeVIew);
        webView.setWebViewClient(new WebViewClient());

        webView.loadUrl("http://m.comic.naver.com/bestChallenge/genre.nhn");
        //문제가 있음 왜째서 조회순 업데이트순 이런 체크박스 체크는 되지않는가!

    }
}
