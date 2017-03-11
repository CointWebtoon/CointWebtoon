package com.jmapplication.com.episodeactivity;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

/**
 * Created by jm on 2017-03-09.
 */

public class CustomProgressDialog extends Dialog {
    public CustomProgressDialog(Context mContext){
        super(mContext);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.customdialog);
    }
}
