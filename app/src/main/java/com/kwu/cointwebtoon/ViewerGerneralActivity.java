package com.kwu.cointwebtoon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kwu.cointwebtoon.DataStructure.Episode;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class ViewerGerneralActivity extends TypeKitActivity implements Observer {
    private ArrayList<String> imageUrls; // 웹툰 한 화에 있는 이미지 url을 순서대로 담을 ArraytList
    private ListView viewerListView; // url들을 통해 이미지들이 놓일 ListView
    private ViewerGeneralAdapter adapter;
    public static final int REQUEST_CODE_RATING = 1001;
    private Thread myTread;
    private float x, y;
    private int count = 0;
    private String general_artist, general_mention;
    private ImageView scrollbar;
    private LinearLayout linearLayout;
    private RelativeLayout relativeLayout;
    private RelativeLayout scrollSection;
    private GetServerData serverData;
    private Toolbar GeneralToonTopToolbar, GeneralToonBottomToolbar;
    private TextView episodeTitleTextView, episodeIdTextView;
    private COINT_SQLiteManager manager;
    private Button good;
    private boolean runMode;
    private boolean isFirst = true;             //읽은 화까지 스크롤할 때 사용
    private int yDelta, ys= 0;                          //스크롤바 좌표 계산
    private int maxTopMargin = 0;               //스크롤바 좌표 계산
    private boolean scrollManually = true;      //스크롤바로 스크롤했는지, 제스처로 스크롤했는지
    private int id, ep_id;

    @Override
    public void update(Observable observable, Object o) {
        this.imageUrls = (ArrayList<String>) o;
        adapter = new ViewerGeneralAdapter(this, imageUrls);
        viewerListView.setAdapter(adapter);
        if (isFirst) {
            int readNumber;
            for (readNumber = 0; readNumber < imageUrls.size(); readNumber++) {
                if (imageUrls.get(readNumber).isEmpty())
                    break;
            }
            if (readNumber > 0 && readNumber < imageUrls.size())
                readNumber--;
            else if (readNumber >= imageUrls.size())
                readNumber = 0;
            viewerListView.smoothScrollToPosition(readNumber);
            isFirst = false;
        }
        maxTopMargin = scrollSection.getHeight() - scrollbar.getHeight();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewer_general_activity);
        relativeLayout = (RelativeLayout)findViewById(R.id.coint_layout);
        serverData = new GetServerData(this);
        serverData.registerObserver(this);
        runMode = false;
        episodeTitleTextView = (TextView)findViewById(R.id.GeneralToontEpisodeTitle);
        episodeIdTextView = (TextView)findViewById(R.id.GeneralToont_current_pos);
        GeneralToonTopToolbar = (Toolbar) findViewById(R.id.GeneralToontoptoolbar);
        GeneralToonBottomToolbar = (Toolbar) findViewById(R.id.GeneralToontbottomtoolbar);
        good = (Button)findViewById(R.id.GeneralToontgood);
        scrollSection = (RelativeLayout) findViewById(R.id.scrollSection);
        scrollbar = (ImageView) findViewById(R.id.scrollbar);
        scrollbar.setOnTouchListener(new ScrollBarOnTouchListener());
        setSupportActionBar(GeneralToonTopToolbar);
        manager = COINT_SQLiteManager.getInstance(this);
        GeneralToonTopToolbar.setVisibility(View.VISIBLE);
        GeneralToonBottomToolbar.setVisibility(View.VISIBLE);
        initializeThread();
        viewerListView = (ListView) findViewById(R.id.list_view);
        viewerListView.setDivider(null);
        viewerListView.setVerticalScrollBarEnabled(false);
        viewerListView.setOnScrollListener(new ScrollBarOnScrollListener());
        viewerListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if( imageUrls == null ){ return true; }
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
                                showUIs(false);
                                try {
                                    myTread.interrupt();
                                } catch (Exception e) {}
                            }
                            else if(GeneralToonTopToolbar.getVisibility() == View.GONE) {
                                showToolbars(true);
                                showUIs(true);
                            }
                            initializeThread();
                        }
                        break;
                }
                return false;
            }
        });
        Intent getIntent = getIntent();
        id = getIntent.getIntExtra("id", -1);
        ep_id = getIntent.getIntExtra("ep_id", -1);
        general_mention = getIntent.getStringExtra("mention");
        if(id == -1 | ep_id == -1){
            Toast.makeText(this, "존재하지 않는 에피소드입니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
        serverData.getImagesFromServer(id, ep_id);
        episodeTitleTextView.setText(manager.getEpisodeTitle(id, ep_id));
        episodeIdTextView.setText(String.valueOf(ep_id));
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
                            if(GeneralToonTopToolbar.getVisibility() == View.VISIBLE) {
                                showToolbars(false);
                                showUIs(false);
                            }
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
        this.finish();
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
        startActivity(new Intent(this, ViewerCommentActivity.class));
        Toast.makeText(this, "댓글 버튼을 클릭했습니다.", Toast.LENGTH_SHORT).show();
    }
    public void Previous(View v) {
        if(ep_id > 1 ){
            ep_id -= 1;
            serverData.getImagesFromServer(id, ep_id);
            manager.updateEpisodeRead(id, ep_id);
            episodeTitleTextView.setText(manager.getEpisodeTitle(id, ep_id));
            episodeIdTextView.setText(String.valueOf(ep_id));
            System.out.println("작가의말" + general_mention);
        }
    }
    public void Next(View v) {
        if(ep_id > 0 && (ep_id < manager.maxEpisodeId(id))){
            ep_id += 1;
            serverData.getImagesFromServer(id, ep_id);
            manager.updateEpisodeRead(id, ep_id);
            episodeTitleTextView.setText(manager.getEpisodeTitle(id, ep_id));
            episodeIdTextView.setText(String.valueOf(ep_id));

        }
    }
    public void givingStarBtnClick(View v) {
        try {
            startActivityForResult(new Intent(this, ViewerStarScoreActivity.class), REQUEST_CODE_RATING);
        }catch (Exception e) { e.printStackTrace();}
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_RATING) {
            if(resultCode == RESULT_OK) {
                try {
                    float SCORE = data.getExtras().getFloat("SCORE");
                    Toast.makeText(this, "전달 된 별점은 " + SCORE, Toast.LENGTH_SHORT).show();
                    if(adapter.starTV != null && adapter.ratingbar != null){
                        adapter.starTV.setText(String.valueOf((manager.getEpisodeInstance(id,ep_id).getEp_starScore() + SCORE)));
                        adapter.ratingbar.setMax(10);
                        adapter.ratingbar.setRating(((manager.getEpisodeInstance(id,ep_id).getEp_starScore() + SCORE)/2));
                        adapter.givingstar.setEnabled(false);
                    }
                }catch (NullPointerException ex) {ex.printStackTrace();}
            }
        }
    }

    private class ScrollBarOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if(imageUrls.size() == 0)
                return false;
            ys = (int) event.getRawY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    yDelta = ys - lParams.topMargin;
                    break;
                case MotionEvent.ACTION_UP:
                    scrollManually = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    if (scrollSection.getHeight() - (ys - yDelta) > view.getHeight()) {
                        layoutParams.topMargin = ys - yDelta;
                        if (ys - yDelta < 0) {
                            layoutParams.topMargin = 0;
                        }
                    } else {
                        layoutParams.topMargin = maxTopMargin;
                    }
                    scrollManually = false;
                    try{
                        viewerListView.smoothScrollToPosition((imageUrls.size() - 1) * layoutParams.topMargin / maxTopMargin);
                    }catch(ArithmeticException e){
                        //Divided By Zero Excpetion 처리 --> 아직 아이템들이 로드되지 않았을 때 스크롤을 하면 이렇게 됨
                        return true;
                    }
                    view.setLayoutParams(layoutParams);
                    break;
            }
            scrollSection.invalidate();
            return true;
        }
    }
    private class ScrollBarOnScrollListener implements AbsListView.OnScrollListener {
        private int mLastFirstVisibleItem = 0;
        private boolean lastItemVisibleFlag = false;
        @Override
        public void onScrollStateChanged(AbsListView view, int event) {
            if(event == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag) {
                if (runMode && (ep_id < manager.maxEpisodeId(id))) {
                    //정주행 모드일 때, List View의 바닥에 닿으면 다음 회차가 존재할 경우에 다음 회차로 넘어감
                    imageUrls.clear();
                    ep_id += 1;
                    serverData.getImagesFromServer(id, ep_id);
                    manager.updateEpisodeRead(id, ep_id);
                    episodeTitleTextView.setText(manager.getEpisodeTitle(id, ep_id));
                    episodeIdTextView.setText(String.valueOf(ep_id));
                }
                else{
                    if(adapter != null){
                        adapter.ratingbar.setRating((manager.getEpisodeInstance(id,ep_id).getEp_starScore())/2);
                        adapter.starTV.setText(String.valueOf(manager.getEpisodeInstance(id,ep_id).getEp_starScore()));
                        adapter.mention.setText(manager.getEpisodeInstance(id, ep_id).getMention());
                    }
                    showToolbars(true);
                }
            }
        }
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if(imageUrls == null){ return; }
            lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
            final int currentFirstVisibleItem = viewerListView.getFirstVisiblePosition();
            if (currentFirstVisibleItem < this.mLastFirstVisibleItem && GeneralToonTopToolbar.getVisibility() == View.GONE) {
                showUIs(false);
            } else if (currentFirstVisibleItem > this.mLastFirstVisibleItem && GeneralToonTopToolbar.getVisibility() == View.VISIBLE) {
                if (scrollManually)
                    showUIs(false);
            }
            initializeThread();
            if (scrollManually) {
                try {
                    //스크롤바를 통한 스크롤이 아닐 때
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) scrollbar.getLayoutParams();
                    layoutParams.topMargin = maxTopMargin * currentFirstVisibleItem / (imageUrls.size() - 1);
                    scrollbar.setLayoutParams(layoutParams);
                    scrollSection.invalidate();
                }catch (NullPointerException ex) { ex.printStackTrace();}
            }
            this.mLastFirstVisibleItem = currentFirstVisibleItem;
        }
    }
    private void showUIs(boolean show) {
        if (show) {
            scrollbar.setVisibility(View.VISIBLE);
            scrollbar.animate().translationX(0).withLayer();
            scrollSection.setVisibility(View.VISIBLE);
        } else {
            scrollbar.setVisibility(View.GONE);
            scrollbar.animate().translationX(20).withLayer();
            scrollSection.setVisibility(View.GONE);
        }
    }
    public void runButtonClick(View v){
        if(runMode) {
            runMode = false;
            ImageButton target = (ImageButton)v;
            target.setImageDrawable(getDrawable(R.drawable.run_inactive));
        }
        else {
            runMode = true;
            ImageButton target = (ImageButton)v;
            target.setImageDrawable(getDrawable(R.drawable.run_active));
        }
    }
    @Override
    protected void onDestroy() {
        serverData.removeObserver(this);
        super.onDestroy();
    }
}