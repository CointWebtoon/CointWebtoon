package com.example.epcej.coint_mainactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

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

        if(something.equals("weekday")){
            Date date = new Date();
            String s = date.toString();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E");          //왜 미국시간으로 나오는지는 모르겠으나... 일단 나오긴 함
            textView.setText(simpleDateFormat.format(date));
        }
        else{
            textView.setText(something);
        }
    }
}
