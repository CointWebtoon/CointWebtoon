package com.kwu.cointwebtoon;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.nhn.android.naverlogin.OAuthLogin;

public class Application_UserInfo extends Application {
    /**
     *  Login API Client Info
     */
    public static String API_CLIENT_ID = "6pk7u9S1BMLuEF0jnJr4";
    public static String API_CLIENT_SECRET = "FYcZMaLCKp";
    public static String API_CLIENT_NAME = "COINT 웹툰";

    private static OAuthLogin loginInstance;

    /**
     * User Info ( Save Login Data )
     */
    private boolean login = false;
    private String userID = null;
    private String userName = null;
    private boolean userAdult = false;
    private char userGender = 'N';   //설정 안된 상태 : N, 남자 : M, 여자 : F

    /**
     * Getter Methods
     */
    public String getUserName(){return userName;}
    public boolean isUserAdult(){return userAdult;}
    public char getUserGender(){return userGender;}
    public OAuthLogin getLoginInstance(){return loginInstance;}
    public String getUserID(){return userID;}
    public boolean isLogin(){return login;}

    public void initUserInfo(){
        userName = null;
        userAdult = false;
        userGender = 'N';
    }

    @Override
    public void onCreate() {
        loginInstance = OAuthLogin.getInstance();
        loginInstance.init(getApplicationContext(), Application_UserInfo.API_CLIENT_ID, Application_UserInfo.API_CLIENT_SECRET, Application_UserInfo.API_CLIENT_NAME);
        LoginRequestApiTask apiTask = new LoginRequestApiTask(getApplicationContext(), this);
        apiTask.execute();
        super.onCreate();
    }

    public void onLogIn(String ID, String name, boolean adult, char gender){
        login = true;
        userID = ID;
        userName = name;
        userAdult = adult;
        userGender = gender;
        Log.i("coint", "onLogIn");
    }

    public void onLogOut(Context mContext){
        login = false;
        userID = null;
        userName = null;
        userAdult = false;
        userGender = 'N';
        loginInstance.logoutAndDeleteToken(mContext);
    }
}