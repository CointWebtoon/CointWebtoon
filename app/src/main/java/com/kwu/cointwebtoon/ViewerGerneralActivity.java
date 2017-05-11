package com.kwu.cointwebtoon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class ViewerGerneralActivity extends AppCompatActivity implements Observer {
    private ArrayList<String> imageUrls; // 웹툰 한 화에 있는 이미지 url을 순서대로 담을 ArraytList
    private ListView viewerListView; // url들을 통해 이미지들이 놓일 ListView
    private AppCompatActivity mContext; // Adapter View 에 넘겨줄 Context
    private String title = "COINT";
    private String artist;
    public int count = 0;
    private Thread myTread;
    private float x, y;
    private LinearLayout linearLayout;
    private GetServerData serverData;

    @Override
    public void update(Observable observable, Object o) {
        this.imageUrls = (ArrayList<String>) o;
        ViewerGeneralAdapter adapter = new ViewerGeneralAdapter(this, imageUrls);
        viewerListView.setAdapter(adapter);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewer_general_activity);
        linearLayout = (LinearLayout)findViewById(R.id.coint_layout);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        final TextView textView = (TextView) findViewById(R.id.text_view);
        serverData = new GetServerData(this);
        serverData.registerObserver(this);
        getSupportActionBar().show();
        initializeThread();
        viewerListView = (ListView) findViewById(R.id.list_view);
        viewerListView.setDivider(null);
        viewerListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = motionEvent.getX();
                        y = motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        float xGap = x - motionEvent.getX();
                        float yGap = y - motionEvent.getY();
                        if ((xGap < 10 && xGap > -10) && (yGap < 10 && yGap > -10)) {
                            count++;
                            if (count % 2 != 0) {
                                getSupportActionBar().hide();
                                try {
                                    myTread.interrupt();
                                } catch (Exception e) {
                                }
                            } else {
                                getSupportActionBar().show();
                                initializeThread();
                            }
                        }
                        break;
                }
                return false;
            }
        });
        Intent getIntent = getIntent();
        int id = getIntent.getIntExtra("id", -1);
        int ep_id = getIntent.getIntExtra("ep_id", -1);
        if(id == -1 | ep_id == -1){
            Toast.makeText(this, "존재하지 않는 에피소드입니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
        serverData.getImagesFromServer(id, ep_id);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);
    }

    private void initializeThread() {
        try {
            myTread.interrupt();
        } catch (Exception e) { }
        myTread = new Thread(new Runnable() {
            @Override
            public void run() {
                final ActionBar myActionBar = getSupportActionBar();
                try {
                    Thread.sleep(3000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myActionBar.hide();
                        }
                    });
                } catch (InterruptedException intex) {
                    Log.i("thread", "Interrupted");
                }
            }
        });
        myTread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.viewer_back_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        myTread.interrupt();
        int id = item.getItemId();
        if (id == R.id.back_home) {
            Toast.makeText(this, "뒤로가기", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == android.R.id.home) {
            Toast.makeText(this, "홈 아이콘 이벤트", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        serverData.deleteObserver(this);
        super.onDestroy();
    }
}
