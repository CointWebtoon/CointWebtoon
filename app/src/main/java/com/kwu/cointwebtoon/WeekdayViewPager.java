package com.kwu.cointwebtoon;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by JWP on 2017-05-18.
 */

public class WeekdayViewPager extends ViewPager{
    public boolean enabled;

    public WeekdayViewPager(Context context) {
        super(context);
    }

    public WeekdayViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(enabled){
            return super.onInterceptTouchEvent(ev);
        }
        else{
            if(MotionEventCompat.getActionMasked(ev) == MotionEvent.ACTION_MOVE){

            }
            else {
                if(super.onInterceptTouchEvent(ev)){
                    super.onTouchEvent(ev);
                }
            }
            return  false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(enabled){
            return super.onTouchEvent(ev);
        }
        else{
            return MotionEventCompat.getActionMasked(ev) != MotionEvent.ACTION_MOVE && super.onTouchEvent(ev);
        }
    }

    public void setPagingEnabled(boolean enabled){
        this.enabled = enabled;
    }

}
