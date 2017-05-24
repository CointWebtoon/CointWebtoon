package com.kwu.cointwebtoon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

public class ViewerStarScoreActivity extends TypeKitActivity implements View.OnClickListener {
    private Button mConfirm, mCancle, giving;
    private TextView starTextView;
    private float number = 0.0f;
    private RatingBar ratingbar;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(saveInstanceState);
        setContentView(R.layout.viewer_starscore_activity);
        starTextView = (TextView) findViewById(R.id.txtView);
        setContent();
        ratingbar = (RatingBar) findViewById(R.id.dialog_rating);
        ratingbar.setMax(10);
        ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean onTouch) {
                if (rating == 0) {
                    ratingBar.setRating(0.5f);
                    rating = 0.5f;
                }
                starTextView.setText(String.valueOf((int) (rating * 2)));
                number = rating * 2;
                System.out.println(number);
            }
        });
    }

    private void setContent() {
        mConfirm = (Button) findViewById(R.id.btnConfirm);
        mCancle = (Button) findViewById(R.id.btnCancel);
        giving = (Button) findViewById(R.id.giving_star);
        mConfirm.setOnClickListener(this);
        mCancle.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnConfirm:
                System.out.println("현재별점" + number);
                try {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("SCORE", number);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
            case R.id.btnCancel:
                this.finish();
                break;
            default:
                break;
        }
    }

}
