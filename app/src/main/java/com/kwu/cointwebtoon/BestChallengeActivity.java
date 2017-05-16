package com.kwu.cointwebtoon;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class BestChallengeActivity extends TypeKitActivity {

    private WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.best_challenge_activity);
        webView = (WebView) findViewById(R.id.challengeVIew);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://m.comic.naver.com/bestChallenge/genre.nhn");
        //문제가 있음 왜째서 조회순 업데이트순 이런 체크박스 체크는 되지않는가!
    }

    /*
    *  뒤로가기 버튼을 눌렀을 때 첫 화면이면 종료확인을,
    *  방문한 페이지가 있다면 뒤로가기 기능을 하도록 구현
    * */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }

        if ((keyCode == KeyEvent.KEYCODE_BACK) && (webView.canGoBack() == false)) {
            new AlertDialog.Builder(this)
                    .setTitle("베스트도전 종료")
                    .setMessage("페이지를 종료하시겠습니까?")
                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            webView.clearCache(false);
                            finish();
                        }
                    }).setNegativeButton("아니요", null).show();
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        //종료 버튼 추가

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.best_challenge_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {       // 검색 누르면 실행
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.best_challenge_terminate:
                new AlertDialog.Builder(this)
                        .setTitle("베스트도전 종료")
                        .setMessage("페이지를 종료하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                webView.clearCache(false);
                                finish();
                            }
                        }).setNegativeButton("아니요", null).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
