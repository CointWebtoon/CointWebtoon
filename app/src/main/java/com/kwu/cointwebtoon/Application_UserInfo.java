package com.kwu.cointwebtoon;

import android.app.Application;
import android.content.Context;

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
    private static boolean login = false;
    private static String userName = null;
    private static boolean userAdult = false;
    private static char userGender = 'N';   //설정 안된 상태 : N, 남자 : M, 여자 : F

    /**
     * Getter Methods
     */
    public static String getUserName(){return userName;}
    public static boolean isUserAdult(){return userAdult;}
    public static char getUserGender(){return userGender;}
    public static OAuthLogin getLoginInstance(){return loginInstance;}
    public static boolean isLogin(){return login;}

    public static void initUserInfo(){
        userName = null;
        userAdult = false;
        userGender = 'N';
    }

    @Override
    public void onCreate() {
        loginInstance = OAuthLogin.getInstance();
        loginInstance.init(getApplicationContext(), Application_UserInfo.API_CLIENT_ID, Application_UserInfo.API_CLIENT_SECRET, Application_UserInfo.API_CLIENT_NAME);

        LoginRequestApiTask apiTask = new LoginRequestApiTask(getApplicationContext());
        apiTask.execute();
        super.onCreate();
    }

    public static void onLogIn(String name, boolean adult, char gender){
        login = true;
        userName = name;
        userAdult = adult;
        userGender = gender;
    }

    public static void onLogOut(Context mContext){
        login = false;
        userName = null;
        userAdult = false;
        userGender = 'N';
        loginInstance.logout(mContext);
    }
}