package com.kwu.cointwebtoon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class ViewerGerneralActivity extends TypeKitActivity implements Observer {
    private ArrayList<String> imageUrls; // 웹툰 한 화에 있는 이미지 url을 순서대로 담을 ArraytList
    private ListView viewerListView; // url들을 통해 이미지들이 놓일 ListView
    private AppCompatActivity mContext; // Adapter View 에 넘겨줄 Context
    private RatingBar ratingBar;
    private String artist;
    private String buffer;
    private Thread myTread;
    private float x, y;
    private int count = 0;
    private LinearLayout linearLayout;
    private RelativeLayout relativeLayout;
    private GetServerData serverData;
    private Toolbar GeneralToonTopToolbar, GeneralToonBottomToolbar;
    private TextView episodeTitleTextView, episodeIdTextView, starScore;
    private COINT_SQLiteManager manager;
    private Button good;

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
        relativeLayout = (RelativeLayout)findViewById(R.id.coint_layout);
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        serverData = new GetServerData(this);
        serverData.registerObserver(this);
        starScore = (TextView) findViewById(R.id.textview_starScore);
        episodeTitleTextView = (TextView)findViewById(R.id.GeneralToontEpisodeTitle);
        episodeIdTextView = (TextView)findViewById(R.id.GeneralToont_current_pos);
        GeneralToonTopToolbar = (Toolbar) findViewById(R.id.GeneralToontoptoolbar);
        GeneralToonBottomToolbar = (Toolbar) findViewById(R.id.GeneralToontbottomtoolbar);
        good = (Button)findViewById(R.id.GeneralToontgood);
        setSupportActionBar(GeneralToonTopToolbar);
        manager = COINT_SQLiteManager.getInstance(this);
        GeneralToonTopToolbar.setVisibility(View.VISIBLE);
        GeneralToonBottomToolbar.setVisibility(View.VISIBLE);
        initializeThread();
        viewerListView = (ListView) findViewById(R.id.list_view);
        viewerListView.setDivider(null);
        viewerListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(imageUrls==null){return true;}
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = motionEvent.getX();
                        y = motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        float xGap = x - motionEvent.getX();
                        float yGap = y - motionEvent.getY();
                        if ((xGap < 10 && xGap > -10) && (yGap < 10 && yGap > -10)) {

                            if(GeneralToonTopToolbar.getVisibility() == View.VISIBLE) {
                                showToolbars(false);
                                try {
                                    myTread.interrupt();
                                } catch (Exception e) {}
                            }
                            else if(GeneralToonTopToolbar.getVisibility() == View.GONE) {
                                showToolbars(true);
                            }
                            initializeThread();
                        }
                        break;
                }
                return false;
            }
        });
        Intent getIntent = getIntent();
        int id = getIntent.getIntExtra("id", -1);
        int ep_id = getIntent.getIntExtra("ep_id", -1);
       // float star_score = getIntent.getFloatExtra("starScore",-1f);
       // if(star_score == -1f){
        //    Toast.makeText(this, "전달된 별점이 없습니다", Toast.LENGTH_SHORT).show();
        //    finish();
       // }
        if(id == -1 | ep_id == -1){
            Toast.makeText(this, "존재하지 않는 에피소드입니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
        //buffer = String.valueOf(star_score);
        serverData.getImagesFromServer(id, ep_id);
        episodeTitleTextView.setText(manager.getEpisodeTitle(id, ep_id));
        episodeIdTextView.setText(String.valueOf(ep_id));
       // starScore.setText(buffer);
        //ratingBar.setRating(star_score);
    }

    private void initializeThread() {
        try {
            myTread.interrupt();
        } catch (Exception e) { }
        myTread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("i'm here1");
                            if(GeneralToonTopToolbar.getVisibility() == View.VISIBLE) {
                                System.out.println("i'm here2");
                                showToolbars(false);
                            }
                            else{System.out.println("i'm here3");}

                        }
                    });
                } catch (InterruptedException intex) {
                    Log.i("thread", "Interrupted");
                }
            }
        });
        myTread.start();
    }
    private void showToolbars(boolean show){
        if(show){
            GeneralToonTopToolbar.setVisibility(View.VISIBLE);
            GeneralToonBottomToolbar.setVisibility(View.VISIBLE);
            GeneralToonTopToolbar.animate().translationY(0).withLayer();
            GeneralToonBottomToolbar.animate().translationY(0).withLayer();
        }else{
            GeneralToonTopToolbar.setVisibility(View.GONE);
            GeneralToonBottomToolbar.setVisibility(View.GONE);
            GeneralToonTopToolbar.animate().translationY(-60).withLayer();
            GeneralToonBottomToolbar.animate().translationY(60).withLayer();
        }
    }
    public void BackBtn(View v) {
        Toast.makeText(this, "뒤로가기 버튼을 클릭했습니다.", Toast.LENGTH_SHORT).show();
    }
    public void HeartBtn(View v){
        Toast.makeText(this, "좋아요 버튼을 클릭했습니다.", Toast.LENGTH_SHORT).show();
        count++;
        if(count % 2 != 0) {
            good.setBackgroundResource(R.drawable.view_heartcolor);
        }
        else{
            good.setBackgroundResource(R.drawable.view_heartempty);
        }
    }

    public void Dat(View v){
        Toast.makeText(this, "댓글 버튼을 클릭했습니다.", Toast.LENGTH_SHORT).show();
    }
    public void Previous(View v) { Toast.makeText(this, "이전화 보기 버튼을 클릭했습니다.", Toast.LENGTH_SHORT).show();}
    public void Current(View v) {Toast.makeText(this, "현재회차 버튼을 클릭했습니다.", Toast.LENGTH_SHORT).show();}
    public void Next(View v) {Toast.makeText(this, "다음화 보기 버튼을 클릭했습니다.", Toast.LENGTH_SHORT).show();}
    public void givingStarBtnClick(View v) { startActivity(new Intent(v.getContext(), ViewerStarScoreActivity.class));}

    @Override
    protected void onDestroy() {
        serverData.removeObserver(this);
        super.onDestroy();
    }
}