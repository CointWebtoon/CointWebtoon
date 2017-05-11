package com.kwu.cointwebtoon;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BestChallengeActivity extends TypeKitActivity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.best_challenge_activity);
        WebView webView = (WebView)findViewById(R.id.challengeVIew);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://m.comic.naver.com/bestChallenge/genre.nhn");
        //문제가 있음 왜째서 조회순 업데이트순 이런 체크박스 체크는 되지않는가!
    }
}
