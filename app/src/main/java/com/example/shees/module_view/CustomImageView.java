package com.example.shees.module_view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by 신정민 on 2016-12-31.
 */
public class CustomImageView extends ImageView {

    /*
    *커스텀 이미지 뷰
    * 역할 : 기존 이미지 뷰는 크기가 고정되어 있으며, 이는 모든 컷의 크기가 다른 웹툰에서는 사용할 수 없다.
            * setImageBitmap에 들어오는 비트맵의 크기에 맞추어 Width를 디바이스의 width로 만들고,
            * 그에 맞추어 가로 세로 비율을 통해 이미지뷰의 Height를 조정할 수 있도록 하는 뷰
    */

    public CustomImageView(Context mContext) {
        super(mContext);
    }

    public CustomImageView(Context mContext, AttributeSet attributeSet) {
        super(mContext, attributeSet);
    }

    public CustomImageView(Context mContext, AttributeSet attributeSet, int style) {
        super(mContext, attributeSet, style);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            Drawable resource = getDrawable();
            if (resource == null)
                setMeasuredDimension(0, 0);
            else {
                int outWidth = MeasureSpec.getSize(widthMeasureSpec);
                int height = outWidth * resource.getIntrinsicHeight() / resource.getIntrinsicWidth();
                setMeasuredDimension(outWidth, height);
            }
        } catch (Exception e) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
