package com.kwu.cointwebtoon;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

public class CointProgressDialog extends Dialog {
    public CointProgressDialog(Context mContext){
        super(mContext);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.coint_progressbar_dialog);
    }
}
