package com.jmapplication.com.episodeactivity;

import android.app.Application;

import java.util.Map;

/**
 * Created by jm on 2017-03-08.
 */

public class COINT_Application extends Application {
    private Map<String, String> cookie = null;

    public void setCookie(Map<String,String> cookie){
        this.cookie = cookie;
    }

    public Map<String, String> getCookie(){
        return cookie;
    }
}
