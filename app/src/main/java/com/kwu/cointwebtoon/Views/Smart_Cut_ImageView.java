package com.kwu.cointwebtoon.Views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class Smart_Cut_ImageView extends ImageView {
    Context mContext;
    /*
     *커스텀 이미지 뷰
     * 역할 : 기존 이미지 뷰는 크기가 고정되어 있으며, 이는 모든 컷의 크기가 다른 웹툰에서는 사용할 수 없다.
     * setImageBitmap에 들어오는 비트맵의 크기에 맞추어 Width를 디바이스의 width로 만들고,
     * 그에 맞추어 가로 세로 비율을 통해 이미지뷰의 Height를 조정할 수 있도록 하는 뷰
     */

    public Smart_Cut_ImageView(Context mContext) {
        super(mContext);
        this.mContext = mContext;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        setLayoutParams(params);
    }

    public Smart_Cut_ImageView(Context mContext, AttributeSet attributeSet) {
        super(mContext, attributeSet);
        this.mContext = mContext;
    }

    public Smart_Cut_ImageView(Context mContext, AttributeSet attributeSet, int style) {
        super(mContext, attributeSet, style);
        this.mContext = mContext;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            Drawable resource = getDrawable();
            if (resource == null)
                setMeasuredDimension(0, 0);
            else {
                AppCompatActivity activity = (AppCompatActivity) mContext;
                int outWidth = activity.getWindowManager().getDefaultDisplay().getWidth();
                int height = outWidth * resource.getIntrinsicHeight() / resource.getIntrinsicWidth();
                setMeasuredDimension(outWidth, height);
                if (activity.getWindowManager().getDefaultDisplay().getHeight() < height) {
                    int outHeight = MeasureSpec.getSize(heightMeasureSpec);
                    int width = outHeight * resource.getIntrinsicWidth() / resource.getIntrinsicHeight();
                    setMeasuredDimension(width, outHeight);
                }
            }
        } catch (Exception e) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
