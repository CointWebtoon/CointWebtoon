package com.kwu.cointwebtoon;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

public class AppInfoDialog extends Dialog {
    public AppInfoDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_info_dialog);
    }
}
