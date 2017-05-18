package com.kwu.cointwebtoon;

import android.os.Bundle;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by shees on 2017-05-18.
 */
public class ViewerCommentActivity extends TypeKitActivity implements Observer {

    @Override
    public void update(Observable observable, Object o) {

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewer_comment);
    }
}
