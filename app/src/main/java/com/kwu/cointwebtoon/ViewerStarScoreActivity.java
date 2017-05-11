package com.kwu.cointwebtoon;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

public class ViewerStarScoreActivity extends Activity implements View.OnClickListener {
    private Button mConfirm, mCancle;
    private TextView starTextView;
    @Override
    protected  void onCreate(Bundle saveInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(saveInstanceState);
        setContentView(R.layout.viewer_starscore_activity);
        starTextView = (TextView)findViewById(R.id.txtView);
        setContent();
        RatingBar ratingBar = (RatingBar)findViewById(R.id.dialog_rating);
        ratingBar.setMax(10);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean onTouch) {
                if(rating == 0){
                    ratingBar.setRating(0.5f);
                    rating = 0.5f;
                }
                starTextView.setText(String.valueOf((int)(rating * 2)));
                float number = rating * 2;
                System.out.println(number);
            }
        });
    }
    private void setContent() {
        mConfirm = (Button) findViewById(R.id.btnConfirm);
        mCancle = (Button) findViewById(R.id.btnCancel);
        mConfirm.setOnClickListener(this);
        mCancle.setOnClickListener(this);
    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnConfirm:
                this.finish();
                break;
            case R.id.btnCancel:
                this.finish();
                break;
            default:
                break;
        }
    }

}
