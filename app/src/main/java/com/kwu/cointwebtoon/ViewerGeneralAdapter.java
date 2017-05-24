package com.kwu.cointwebtoon;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.kwu.cointwebtoon.DataStructure.Episode;
import com.kwu.cointwebtoon.DataStructure.Webtoon;
import com.kwu.cointwebtoon.Views.GeneralToonImageView;

import java.util.ArrayList;

public class ViewerGeneralAdapter extends RecyclerView.Adapter{
    private static final int VIEW_TYPE_IMAGE = 0;
    private static final int VIEW_TYPE_RATING = 1;

    private ArrayList<String> images = new ArrayList<>();
    private LayoutInflater inflater;
    private ViewerGeneralActivity mContext;
    private int width;
    public RecyclerView.ViewHolder rating_holder_public;

    public ViewerGeneralAdapter(ViewerGeneralActivity mContext){
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
        width = mContext.getWindowManager().getDefaultDisplay().getWidth();
    }

    public void changeData(ArrayList<String> imageList){
        if(imageList != null){
            images.clear();
            images.addAll(imageList);
            notifyDataSetChanged();
        }
    }

    public void clearImage(){
        images.clear();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        switch (viewType){
            case VIEW_TYPE_IMAGE:{
                View view = inflater.inflate(R.layout.viewer_general_image_item, null);
                holder = new ViewHolder_TYPE_IMAGE(view);
                return holder;
            }
            case VIEW_TYPE_RATING:{
                View view = inflater.inflate(R.layout.viewer_general_rating_item, null);
                holder = new ViewHolder_TYPE_RATING(view);
                rating_holder_public = holder;
                return holder;
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try{
            if(holder instanceof ViewHolder_TYPE_IMAGE){
                ViewHolder_TYPE_IMAGE imageHolder = (ViewHolder_TYPE_IMAGE)holder;
                if(position < images.size()){
                    imageHolder.imageItem.setClickable(false);
                    Glide.with(mContext)
                            .load(images.get(position))
                            .asBitmap()
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .override(width, Target.SIZE_ORIGINAL)
                            .placeholder(R.drawable.view_placeholder)
                            .into(imageHolder.imageItem);   //이미지 세팅
                }
            }else if(holder instanceof ViewHolder_TYPE_RATING) {
                ViewHolder_TYPE_RATING ratingHolder = (ViewHolder_TYPE_RATING) holder;
                if (position == images.size()) {
                    float rating = mContext.getMyStar();
                    Webtoon webtoon = mContext.getWebtoonInstance();
                    Episode episode = mContext.getEpisodeInstance();

                    if (episode != null) {
                        //에피소드를 통해 초기화
                        ratingHolder.mention.setText(episode.getMention()); //작가의 말 세팅
                    }
                    if (webtoon != null) {
                        //웹툰 객체로 초기화
                        ratingHolder.artistTv.setText("작가의 말 (" + webtoon.getArtist() + ")");   //작가명 세팅
                    }

                    //-----별점 셋팅----//
                    if (rating != -1) {
                        //사용자가 준 별점 값이 있을 때
                        ratingHolder.ratingBar.setMax(10);
                        ratingHolder.ratingBar.setRating(rating / 2);
                        ratingHolder.starTv.setText(String.valueOf(rating));
                        ratingHolder.givingStar.setEnabled(false);
                    } else {
                        //myStar가 -1일 때
                        ratingHolder.ratingBar.setRating(0);
                        ratingHolder.starTv.setText("0.0");
                        ratingHolder.givingStar.setEnabled(true);
                    }
                    //-----별점 셋팅----//
                }
            }
        }catch (Exception e){return;}
    }

    @Override
    public int getItemCount() {
        try{return images.size() + 1;}catch (Exception e){return 1;}
    }

    @Override
    public int getItemViewType(int position) {
        if(position == images.size()){
            return VIEW_TYPE_RATING;
        }else{
            return VIEW_TYPE_IMAGE;
        }
    }

    public class ViewHolder_TYPE_IMAGE extends RecyclerView.ViewHolder{
        public GeneralToonImageView imageItem;
        public ViewHolder_TYPE_IMAGE(View itemView) {
            super(itemView);
            imageItem = (GeneralToonImageView)itemView.findViewById(R.id.GeneralToonImageView);
        }
    }

    public class ViewHolder_TYPE_RATING extends RecyclerView.ViewHolder{
        public TextView starTv, artistTv, mention;
        public RatingBar ratingBar;
        public Button givingStar;

        public ViewHolder_TYPE_RATING(View itemView) {
            super(itemView);
            starTv = (TextView) itemView.findViewById(R.id.textview_starScore);
            artistTv = (TextView) itemView.findViewById(R.id.artist);
            mention = (TextView) itemView.findViewById(R.id.mention);
            ratingBar = (RatingBar) itemView.findViewById(R.id.rating_bar);
            givingStar = (Button) itemView.findViewById(R.id.giving_star);
        }
    }
}
