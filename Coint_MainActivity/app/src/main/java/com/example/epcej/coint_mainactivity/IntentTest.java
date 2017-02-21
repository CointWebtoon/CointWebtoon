package com.example.epcej.coint_mainactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by epcej on 2017-02-21.
 */

public class IntentTest extends Activity {

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intent_test);

        TextView textView = (TextView)findViewById(R.id.intentText);

        Intent intent = getIntent();
        String something = intent.getStringExtra("Intent");
        textView.setText(something);
    }
}
