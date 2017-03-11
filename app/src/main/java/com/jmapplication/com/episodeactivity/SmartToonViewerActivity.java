package com.jmapplication.com.episodeactivity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

/**
 * Created by jm on 2017-03-10.
 */

public class SmartToonViewerActivity extends AppCompatActivity implements Observer {
    private ViewFlipper imageFlipper;
    private int toonId, episodeId;
    private ArrayList<String> imageURLs;
    private int imageIndex = 0;
    private HashMap<Integer, Integer> animations = new HashMap<>();
    private Random rand = new Random();
    private Context mContext;
    private Toolbar topToolbar, bottomToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smarttoon_viewer);

        topToolbar = (Toolbar) findViewById(R.id.topToolbar_smart);
        bottomToolbar = (Toolbar) findViewById(R.id.bottomToolbar_smart);
        //setSupportActionBar(topToolbar);    //현재 Activity의 ActionBar를 Toolbar로 설정

        mContext = this;

        //애니메이션 in, out 세트 설정
        animations.put(R.anim.push_down_in, R.anim.push_down_out);
        animations.put(R.anim.push_up_in, R.anim.push_up_out);
        animations.put(R.anim.slide_in_left, R.anim.slide_out_right);
        animations.put(R.anim.slide_in_right, R.anim.slide_out_left);

        imageFlipper = (ViewFlipper) findViewById(R.id.smarttoon_Flipper);
        imageFlipper.setOnLongClickListener(new OnFlipperLongClick());

        toonId = 617882;
        episodeId = 2;

        GetServerData serverData = new GetServerData(this);
        serverData.registerObserver(this);
        serverData.getImagesFromServer(toonId, episodeId);
    }

    //이미지 로드가 완료되었을 때 수행할 행동
    @Override
    public void update(Observable observable, Object data) {
        imageURLs = (ArrayList<String>) data;
        for (String imageURL : imageURLs) {
            CustomImageView smartCut = new CustomImageView(SmartToonViewerActivity.this);
            smartCut.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Glide.with(SmartToonViewerActivity.this)
                    .load(imageURL)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .fitCenter()
                    .into(smartCut);
            imageFlipper.addView(smartCut);
        }
    }

    private void showToolbars(boolean show){
        if(show){
            topToolbar.setVisibility(View.VISIBLE);
            bottomToolbar.setVisibility(View.VISIBLE);
        }else{
            topToolbar.setVisibility(View.GONE);
            bottomToolbar.setVisibility(View.GONE);
        }
    }

    public void flipperClick(View v){
        if(topToolbar.getVisibility() == View.VISIBLE){
            showToolbars(false);
            return;
        }
        int animationGenerator = rand.nextInt(4);
        switch (animationGenerator) {
            case 0:
                imageFlipper.setInAnimation(mContext, R.anim.push_down_in);
                imageFlipper.setOutAnimation(mContext, animations.get(R.anim.push_down_in));
                break;
            case 1:
                imageFlipper.setInAnimation(mContext, R.anim.push_up_in);
                imageFlipper.setOutAnimation(mContext, animations.get(R.anim.push_up_in));
                break;
            case 2:
                imageFlipper.setInAnimation(mContext, R.anim.slide_in_left);
                imageFlipper.setOutAnimation(mContext, animations.get(R.anim.slide_in_left));
                break;
            case 3:
                imageFlipper.setInAnimation(mContext, R.anim.slide_in_right);
                imageFlipper.setOutAnimation(mContext, animations.get(R.anim.slide_in_right));
                break;
        }
        if (imageIndex < imageURLs.size() - 1) {
            imageFlipper.showNext();
            imageIndex++;
        }
    }

    public void previousBtnClick(View v){
        if (imageIndex > 0) {
            imageFlipper.setInAnimation(null);
            imageFlipper.setOutAnimation(null);
            imageFlipper.showPrevious();
            imageIndex--;
        }
    }

    private class OnFlipperLongClick implements View.OnLongClickListener{
        @Override
        public boolean onLongClick(View v) {
            showToolbars(true);
            return true;
        }
    }
}
