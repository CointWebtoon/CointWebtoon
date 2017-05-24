package com.kwu.cointwebtoon;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import java.util.Observable;
import java.util.Observer;

public class SplashActivity extends AppCompatActivity implements Observer {
    GetServerData getServerData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_activity);
        getServerData = new GetServerData(this);
        if (getSharedPreferences("isFirstLaunch", MODE_PRIVATE).getBoolean("first", true)) {
            //처음 실행
            getServerData.registerObserver(this);
            getServerData.getWebtoonFromServer();
        } else {
            //두 번째 이상 실행
            getServerData.getWebtoonFromServer();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                /* 메뉴액티비티를 실행하고 로딩화면을 죽인다.*/
                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    SplashActivity.this.finish();
                }
            }, 2000);
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        SplashActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        try {
            getServerData.removeObserver(this);
        } catch (Exception e) {
        }
        super.onDestroy();
    }
}
