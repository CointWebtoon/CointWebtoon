package com.kwu.cointwebtoon;

import android.app.Application;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.kwu.cointwebtoon.DataStructure.CustomBitmapPool;
import com.kwu.cointwebtoon.DataStructure.Webtoon;
import com.kwu.cointwebtoon.DataStructure.Weekday_ListItem;
import com.kwu.cointwebtoon.Views.FastScrollRecyclerViewInterface;

import java.util.ArrayList;
import java.util.HashMap;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by JWP on 2017-05-21.
 */

public class ArtistActivityAdapter extends RecyclerView.Adapter<ArtistActivityAdapter.ViewHolder>implements FastScrollRecyclerViewInterface {
    private ArrayList<Webtoon> dataset;
    private Context mContext;
    private HashMap<String, Integer> mapIndex;


    public  static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvArtist;
        public TextView tvTitle;
        public TextView tvStarScore;
        public ImageView ivThumbnail;
        public Button btnMy;

        public ViewHolder(View itemView) {
            super(itemView);
            tvArtist = (TextView)itemView.findViewById(R.id.tv_artist);
            tvTitle = (TextView)itemView.findViewById(R.id.tv_title);
            tvStarScore = (TextView)itemView.findViewById(R.id.tv_starScore);
            ivThumbnail = (ImageView)itemView.findViewById(R.id.iv_thumbnail);
            btnMy = (Button)itemView.findViewById(R.id.btn_my);
        }
    }

    public ArtistActivityAdapter(ArrayList<Webtoon> dataset,  HashMap<String, Integer> mapIndex) {
        this.dataset = dataset;
        this.mapIndex = mapIndex;
    }

    @Override
    public ArtistActivityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        mContext = parent.getContext();
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Titlebar 설정
        holder.tvArtist.setText(dataset.get(position).getArtist());
        if(position == 0)
            holder.tvArtist.setVisibility(View.VISIBLE);
        else {
            if (dataset.get(position-1).getArtist().compareTo(dataset.get(position).getArtist()) == 0)
                holder.tvArtist.setVisibility(View.GONE);
            else
                holder.tvArtist.setVisibility(View.VISIBLE);
        }

        //List Item 설정
        holder.tvTitle.setText(dataset.get(position).getTitle());
        holder.tvStarScore.setText("★" + String.valueOf(dataset.get(position).getStarScore()));
        Glide.with(mContext)
                .load(dataset.get(position).getThumbURL())
                .centerCrop()
                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .placeholder(R.drawable.week_placeholder)
                .into(holder.ivThumbnail);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    @Override
    public HashMap<String, Integer> getMapIndex() {
        return this.mapIndex;
    }
}
