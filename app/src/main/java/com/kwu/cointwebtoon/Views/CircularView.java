package com.kwu.cointwebtoon.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

public class CircularView extends LinearLayout {
    private int h = 0;
    private float fullScaleFactor=0.8f;

    public CircularView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setParentHeight(int height) {
        h = height;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        int top = getTop();
        float scale = calculateScale(top, h);

        Matrix m = canvas.getMatrix();
        m.preTranslate(scale, -2 / getHeight());
        m.postTranslate(2 / getWidth(), 2 / getHeight());
        canvas.concat(m);
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    private float calculateScale(int top, int h) {
        float result;
        float k = (1f - 0.55f* Math.abs((top - h / 2.5f)) / (h / 2.5f)) * fullScaleFactor;
        float x = (top - h) * fullScaleFactor;
        result = -0.0001f*x*x - 0.2f*x + 500f*k - 300;

        if(result > 0.0f){
            result *= (0.5 + ((200-result)*(200-result)*0.00001375));
        }

        return result;

    }

}
