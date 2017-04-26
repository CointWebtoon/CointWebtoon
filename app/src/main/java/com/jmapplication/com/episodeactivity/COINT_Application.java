package com.jmapplication.com.episodeactivity;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.webkit.CookieManager;
import android.webkit.WebView;

import java.util.Map;

public class COINT_Application extends Application {
    private String naverID = null;
    private String naverPW = null;
    private boolean autoLoginEnabled;
    private boolean login;
    private boolean isAdult;
    private SharedPreferences autoLogin;
    private SharedPreferences.Editor autoLoginEditor;

    @Override
    public void onCreate() {
        super.onCreate();
        autoLogin = getSharedPreferences("AUTO_LOGIN", Context.MODE_PRIVATE);
        autoLoginEditor = autoLogin.edit();
        autoLoginEnabled = autoLogin.getBoolean("AUTO_LOGIN_ENABLED", false);
        if(autoLoginEnabled){
            naverID = autoLogin.getString("id", "ERR");
            naverPW = autoLogin.getString("pw", "ERR");
            isAdult = autoLogin.getBoolean("adult", false);
            login = true;
        }
    }

    public void setLogin(String naverID, String naverPW, boolean isAdult){
        this.naverID = naverID;
        this.naverPW = naverPW;
        this.isAdult = isAdult;
        login = true;
    }

    public void setAutoLoginEnabled(boolean autoLoginEnabled){
        this.autoLoginEnabled = autoLoginEnabled;
    }

    public String getNaverID(){
        return naverID;
    }

    public String getNaverPW(){
        return naverPW;
    }

    public boolean isAutoLoginEnabled(){
        return autoLoginEnabled;
    }

    public boolean isLogin(){
        return login;
    }

    public boolean isAdult(){
        return isAdult;
    }

    public void saveLoginToSharedPreference(){
        autoLoginEditor.putString("id", naverID);
        autoLoginEditor.putString("pw", naverPW);
        autoLoginEditor.putBoolean("adult", isAdult);
        autoLoginEditor.apply();
    }

    public void logOut(){
        naverID = naverPW = null;
        isAdult = false;
        login = false;
        autoLoginEditor.putBoolean("AUTO_LOGIN_ENABLED", false);
        autoLoginEditor.remove("id");
        autoLoginEditor.remove("pw");
        autoLoginEditor.apply();
    }
}
