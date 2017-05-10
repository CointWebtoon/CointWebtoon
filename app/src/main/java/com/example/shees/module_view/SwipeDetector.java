package com.example.shees.module_view;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by shees on 2017-04-28.
 */

public class SwipeDetector implements View.OnTouchListener {
    public final int HORIZONTAL_MIN_DISTANCE = 40;
    public static enum Action{
        PREVIOUS,
        NEXT,
        None // when no action was detected
    }
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
