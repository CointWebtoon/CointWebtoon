package com.example.shees.module_view;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class ToonViewerActivity extends AppCompatActivity implements Observer {
    private ArrayList<String> imageUrls; // 웹툰 한 화에 있는 이미지 url을 순서대로 담을 ArraytList
    private ListView viewerListView; // url들을 통해 이미지들이 놓일 ListView
    private AppCompatActivity mContext; // Adapter View 에 넘겨줄 Context
    private String title = "COINT";
    private String artist;
    public int count = 0;
    private Thread myTread;
    private float x, y;
    private boolean lastItemflag = false;
    private LinearLayout linearLayout;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void update(Observable observable, Object o) {
        this.imageUrls = (ArrayList<String>) o;
        ToonViewerAdapter adapter = new ToonViewerAdapter(this, imageUrls);
        viewerListView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toon_viewer);
        linearLayout = (LinearLayout)findViewById(R.id.coint_layout);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        final TextView textView = (TextView) findViewById(R.id.text_view);
        GetServerData serverData = new GetServerData(this);
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
        serverData.getImagesFromServer(654809, 90);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void initializeThread() {
        try {
            myTread.interrupt();
        } catch (Exception e) {
        }
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
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        myTread.interrupt();
        int id = item.getItemId();
        if (id == R.id.menu_button) {
            Toast.makeText(this, "액션버튼 이벤트", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == android.R.id.home) {
            Toast.makeText(this, "홈 아이콘 이벤트", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("ToonViewer Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
