package com.kwu.cointwebtoon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kwu.cointwebtoon.Views.Smart_Cut_ImageView;

import java.util.ArrayList;

public class ViewerSmartCustomAdapter {
    private static final int TOTAL_VIEW_COUNT = 3;

    /**
     * UI Components
     */
    private ViewFlipper targetView;
    private ArrayList<View> imageViewContainers = new ArrayList<>();
    private LayoutInflater inflater;
    /**
     * Data
     */
    private ArrayList<String> imageURLs = new ArrayList<>();
    private int position = 0;

    /**
     * Members
     */
    private Context mContext;

    /**
     * Constructor
     * @param mContext : Current Context of Application(Activity)
     * @param targetView : ViewFlipper To Manage
     */
    public ViewerSmartCustomAdapter(Context mContext, ViewFlipper targetView){
        this.mContext = mContext;
        this.targetView = targetView;
        this.inflater = LayoutInflater.from(mContext);
        createViews();
    }

    private void createViews(){
        for(int i = 0 ; i < TOTAL_VIEW_COUNT; i++){
            View view = inflater.inflate(R.layout.viewer_smart_image_item, null);
            ImageViewHolder holder = new ImageViewHolder();
            holder.imageView = (Smart_Cut_ImageView)view.findViewById(R.id.viewer_smart_image);
            view.setTag(holder);
            imageViewContainers.add(view);
            targetView.addView(view);
        }
    }

    private void onBindView(){
        ImageViewHolder holder1 = (ImageViewHolder)imageViewContainers.get(0).getTag(),
                holder2 = (ImageViewHolder)imageViewContainers.get(1).getTag(),
                holder3 = (ImageViewHolder)imageViewContainers.get(2).getTag();

        if(position - 1 >= 0){
            Glide.with(mContext)
                    .load(imageURLs.get(position - 1))
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .placeholder(R.drawable.view_placeholder_testing)
                    .into(holder1.imageView);
        }else{
            holder1.imageView.setImageDrawable(null);
        }

        if(position >= 0 && position < imageURLs.size()){
            Glide.with(mContext)
                    .load(imageURLs.get(position))
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .placeholder(R.drawable.view_placeholder_testing)
                    .into(holder2.imageView);
        }else{
            holder2.imageView.setImageDrawable(null);
        }

        if(position + 1 < imageURLs.size()){
            Glide.with(mContext)
                    .load(imageURLs.get(position + 1))
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .placeholder(R.drawable.view_placeholder_testing)
                    .into(holder3.imageView);
        }else{
            holder3.imageView.setImageDrawable(null);
        }
    }

    /**
     * 회차가 변경될 때 Image URL 데이터를 리셋하는 메소드
     * @param images
     */
    public void changeImageData(ArrayList<String> images){
        if(images != null){
            position = 0;
            this.imageURLs.clear();
            this.imageURLs.addAll(images);
            onBindView();
            targetView.setDisplayedChild(1);
        }
    }

    /**
     * Flipper 의 다음 View 를 보여주는 메소드
     */
    public boolean showNext(){
        if(position < imageURLs.size() - 1){
            imageViewContainers.add(imageViewContainers.get(0));
            imageViewContainers.remove(0);
            position++;
            onBindView();
            targetView.showNext();
            return true;
        }
        return false;
    }

    /**
     * Flipper 의 이전 View 를 보여주는 메소드
     */
    public boolean showPrevious(){
        if(position > 0){
            imageViewContainers.add(0, imageViewContainers.get(2));
            imageViewContainers.remove(3);
            position--;
            onBindView();
            targetView.showPrevious();
            return true;
        }
        return false;
    }

    public boolean showChild(int position){
        int tempPosition = this.position;
        if(position >= 0 && position < imageURLs.size()){
            this.position = position;
            onBindView();
            return true;
        }else{
            this.position = tempPosition;
            return false;
        }
    }

    /**
     * Getters
     */
    public int getItemCount(){
        return imageURLs.size();
    }
    public int getPosition(){
        return position;
    }

    /**
     * ViewHolder
     */
    private class ImageViewHolder{
        public Smart_Cut_ImageView imageView;
    }
}
