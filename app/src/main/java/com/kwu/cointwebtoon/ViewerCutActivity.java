package com.kwu.cointwebtoon;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.kwu.cointwebtoon.Views.Smart_Cut_ImageView;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class ViewerCutActivity extends TypeKitActivity implements Observer{
    public static enum Action{
        PREVIOUS,
        NEXT,
        None // when no action was detected
    }
    private ArrayList<String> imageURLs;//imageURL 담을 ArrayList
    private ViewFlipper flipper;//이미지를 넘기면서 볼 수 있는 뷰
    private int imageIndex = 0;//현재 보고 있는 컷이 몇 번째 컷인가?
    private int count = 0; // 좋아요 기능을 위한 변수
    private GetServerData getServerData;
    private int toonId, episodeId;
    private Button good;
    private Toolbar topToolbar, bottomToolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewer_cut_activity);
        good = (Button)findViewById(R.id.good);
        topToolbar = (Toolbar)findViewById(R.id.toptoolbar);
        bottomToolbar = (Toolbar)findViewById(R.id.bottomtoolbar);
        setSupportActionBar(topToolbar);
        flipper = (ViewFlipper)findViewById(R.id.viewflipper);

        ///Flipper 리스너 추가 + SwipeDetector Class를 추가하여 Swipe과 touch 동작을 구분
        final SwipeDetector swipeDetector = new SwipeDetector();
        flipper.setOnTouchListener(swipeDetector);

        flipper.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(swipeDetector.swipeDetected()){
                    switch (swipeDetector.getAction()){
                        case PREVIOUS:
                            MovewPreviousView(); break;
                        case NEXT:
                            MoveNextView(); break;
                    }
                }
                else{
                    //클릭 처리
                    if(topToolbar.getVisibility() == View.VISIBLE){
                        System.out.println("toolbar is visible, than unvisible");
                        showToolbars(false);
                    }
                    else if(topToolbar.getVisibility() == View.GONE) {
                        System.out.println("toolbar is unvisible, than visible");
                        showToolbars(true);
                    }
                }
            }

        });

        flipper.setBackgroundColor(Color.WHITE);

        Intent getIntent = getIntent();
        toonId = getIntent.getIntExtra("id", -1);
        episodeId = getIntent.getIntExtra("ep_id", -1);

        if(toonId == -1 || episodeId == -1){
            Toast.makeText(this, "이미지 로드를 실패하였습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
        getServerData = new GetServerData(this);
        getServerData.registerObserver(this);
        getServerData.getImagesFromServer(toonId, episodeId);
    }
    @Override
    public void update(Observable observable, Object o) {
        Log.i("update", "되냐?");
        this.imageURLs = (ArrayList<String>) o;
        if(imageURLs != null){
            for(String imageURL : imageURLs){
                Smart_Cut_ImageView newImageView = new Smart_Cut_ImageView(this);
                Glide.with(this)
                        .load(imageURL)
                        .asBitmap()
                        .placeholder(R.drawable.view_placeholder)
                        .into(newImageView);
                flipper.addView(newImageView);
            }
            System.out.println(imageURLs.size());
        }
    }
    private void MoveNextView()
   {
       if(imageIndex == imageURLs.size() - 1){System.out.println("final");} //여기에 starScore를 inflate하고싶다.
       if (imageIndex < imageURLs.size() - 1 ) {
           flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.appear_from_right));
           flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.disappear_to_left));
           flipper.showNext();
           imageIndex++;
       }
   }
    private void MovewPreviousView()
    {
        if (imageIndex > 0) {
            flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.appear_from_left));
            flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.disappear_to_right));
            flipper.showPrevious();
            imageIndex--;
        }
    }
    private void showToolbars(boolean show){
        if(show){
            topToolbar.setVisibility(View.VISIBLE);
            bottomToolbar.setVisibility(View.VISIBLE);
            topToolbar.animate().translationY(0).withLayer();
            bottomToolbar.animate().translationY(0).withLayer();
        }else{
            topToolbar.setVisibility(View.GONE);
            bottomToolbar.setVisibility(View.GONE);
            topToolbar.animate().translationY(-60).withLayer();
            bottomToolbar.animate().translationY(60).withLayer();
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

    private class SwipeDetector implements View.OnTouchListener {
        public final int HORIZONTAL_MIN_DISTANCE = 40;
        private float downX, upX;
        private Action mSwipeDetected = Action.None;

        public boolean swipeDetected() {
            return mSwipeDetected != Action.None;
        }

        public Action getAction(){
            return mSwipeDetected;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    downX = event.getX();
                    mSwipeDetected = Action.None;
                    return false;
                case MotionEvent.ACTION_MOVE:
                    upX = event.getX();
                    float deltaX = downX - upX;
                    if (Math.abs(deltaX) > HORIZONTAL_MIN_DISTANCE){
                        if (deltaX < 0) {
                            mSwipeDetected = Action.PREVIOUS;
                            return true;
                        }
                        if(deltaX > 0){
                            mSwipeDetected = Action.NEXT;
                            return true;
                        }
                    }
            }
            return false;
        }
    }
    @Override
    protected void onDestroy() {
        getServerData.removeObserver(this);
        super.onDestroy();
    }
}
