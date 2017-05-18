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
        getServerData.registerObserver(this);
        getServerData.getWebtoonFromServer();
    }

    @Override
    public void update(Observable observable, Object data) {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        SplashActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        getServerData.removeObserver(this);
        super.onDestroy();
    }
}
