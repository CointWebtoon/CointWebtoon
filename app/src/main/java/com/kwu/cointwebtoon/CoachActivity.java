package com.kwu.cointwebtoon;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.kwu.cointwebtoon.CoachAdapter;
import com.kwu.cointwebtoon.R;
import com.kwu.cointwebtoon.TypeKitActivity;

/**
 * Created by epcej on 2017-05-25.
 */

public class CoachActivity extends TypeKitActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                 /*안드로이드 데이터베이스에 데이터를 넣음*/
        setContentView(R.layout.coach_pager);

        ViewPager pager = (ViewPager)findViewById(R.id.coach_pager);
        CoachAdapter coach_adapter = new CoachAdapter(this);
        pager.setAdapter(coach_adapter);
    }
}
