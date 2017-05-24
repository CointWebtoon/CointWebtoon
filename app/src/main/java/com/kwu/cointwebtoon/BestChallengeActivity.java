package com.kwu.cointwebtoon;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BestChallengeActivity extends TypeKitActivity {

    private WebView webView;
    private CointProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.best_challenge_activity);
        dialog = new CointProgressDialog(this);
        webView = (WebView) findViewById(R.id.challengeVIew);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                dialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.i("coint", url);
                if (url.contains("genre.nhn") | url.contains("list.nhn")) {
                    Log.i("coint", "genre, list page finished");
                    webView.loadUrl("javascript:document.getElementsByTagName('header')[0].style.display='none';" +
                            "document.getElementsByClassName('bx_tab')[0].style.display='none';" +
                            "document.getElementsByTagName('footer')[0].style.display='none';" +
                            "document.getElementsByClassName('info_bottom')[0].style.display='none';void(0);");
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {
                    }
                } else if (url.contains("detail.nhn")) {
                    webView.loadUrl("javascript:document.getElementsByTagName('footer')[0].style.display='none';" +
                            "document.getElementsByClassName('info_bottom')[0].style.display='none';void(0);");
                    Log.i("coint", "detail page finished");
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {
                    }
                } else if (url.contains("comment.nhn")) {
                    Log.i("coint", "comment page finished");
                    webView.loadUrl("javascript:document.getElementsByClassName('u_ts')[0].style.display='none';" +
                            "document.getElementsByClassName('info_bottom')[0].style.display='none';void(0);");
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {
                    }
                }
            }
        });
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
