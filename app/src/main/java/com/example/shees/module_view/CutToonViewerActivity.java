package com.example.shees.module_view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterViewFlipper;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by shees on 2017-02-13.
 */

public class CutToonViewerActivity extends AppCompatActivity implements Observer{
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
        setContentView(R.layout.cut_toon_viewer);
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
        toonId = getIntent().getIntExtra("id", -1);
        episodeId = getIntent().getIntExtra("ep_id", -1);

        //나중에 지울 테스트용 코드//
        toonId = 679519;
        episodeId = 1;
        ///
        if(toonId == -1 || episodeId == -1){
            Toast.makeText(this, "이미지 로드를 실패하였습니다.", Toast.LENGTH_SHORT).show();
        }
        getServerData = new GetServerData(this);
        getServerData.registerObserver(this);
        getServerData.getImagesFromServer(toonId, episodeId);
    }
    @Override
    public void update(Observable observable, Object o) {
        Log.i("update", "되냐?");
        this.imageURLs = (ArrayList<String>) o;
        for(String imageURL : imageURLs){
            Smart_Cut_Toon_ImageView newImageView = new Smart_Cut_Toon_ImageView(this);
            Glide.with(this)
                    .load(imageURL)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .placeholder(R.drawable.placeholder)
                    .fitCenter()
                    .into(newImageView);
            flipper.addView(newImageView);
        }
        System.out.println(imageURLs.size());
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
            good.setBackgroundResource(R.drawable.heartcolor);
        }
        else{
            good.setBackgroundResource(R.drawable.heartempty);
        }
    }
    public void Dat(View v){
        Toast.makeText(this, "댓글 버튼을 클릭했습니다.", Toast.LENGTH_SHORT).show();
    }
    public void Previous(View v) { Toast.makeText(this, "이전화 보기 버튼을 클릭했습니다.", Toast.LENGTH_SHORT).show();}
    public void Current(View v) {Toast.makeText(this, "현재회차 버튼을 클릭했습니다.", Toast.LENGTH_SHORT).show();}
    public void Next(View v) {Toast.makeText(this, "다음화 보기 버튼을 클릭했습니다.", Toast.LENGTH_SHORT).show();}
}
