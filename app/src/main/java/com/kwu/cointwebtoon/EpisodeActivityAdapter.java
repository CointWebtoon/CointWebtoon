package com.kwu.cointwebtoon;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.kwu.cointwebtoon.DataStructure.Episode;

import java.util.ArrayList;

////////////////////////ADAPTER////////////////////////
public  class EpisodeActivityAdapter extends RecyclerView.Adapter{
    private Context mContext;
    private ArrayList<Episode> episodes;
    private LayoutInflater inflater;

    public EpisodeActivityAdapter(Context mContext, ArrayList<Episode> episodes){
        this.mContext = mContext;
        this.episodes = episodes;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.episode_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder myHolder = (ViewHolder)holder;
        Episode currentItem = episodes.get(position);
        myHolder.background.setBackgroundColor(Color.parseColor("#FFFFFF"));
        if(currentItem.getIs_read() == 1) {
            myHolder.background.setBackgroundColor(Color.parseColor("#708090"));
        }
        Glide.with(mContext)
                .load(currentItem.getEp_thumbURL())
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .fitCenter()
                .placeholder(R.drawable.episode_placeholder)
                .into(myHolder.thumb);
        myHolder.epTitle.setText(currentItem.getEpisode_title());
        myHolder.regDate.setTag(currentItem);
        myHolder.starScore.setText(String.valueOf(currentItem.getEp_starScore()));
        myHolder.regDate.setText(currentItem.getReg_date());
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView thumb;
        public TextView epTitle;
        public TextView regDate;
        public TextView starScore;
        public CardView cardView;
        public RelativeLayout background;
        public View view;
        public ViewHolder(View view){
            super(view);
            this.view = view;
            background = (RelativeLayout)view.findViewById(R.id.backgroundRelative) ;
            cardView = (CardView)view.findViewById(R.id.cardView);
            thumb = (ImageView)view.findViewById(R.id.thumb);
            epTitle = (TextView)view.findViewById(R.id.episode_Title);
            regDate = (TextView)view.findViewById(R.id.reg_date);
            starScore = (TextView)view.findViewById(R.id.episode_Starscore);

            epTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            epTitle.setSingleLine(true);
            epTitle.setMarqueeRepeatLimit(5);
            epTitle.setSelected(true);
        }
    }
}