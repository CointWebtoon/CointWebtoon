package com.jmapplication.com.episodeactivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by jm on 2017-02-27.
 */

public class EpisodeAdapter extends BaseAdapter{
    private ArrayList<Episode[]> episodes;
    private Context mContext;
    private LayoutInflater inflater;

    public EpisodeAdapter(Context mContext, ArrayList<Episode[]> episodes){
        this.mContext = mContext;
        this.episodes = episodes;
        inflater = LayoutInflater.from(mContext);
    }

    private class EpisodeViewHolder {
        TextView[] textViews = new TextView[7];
        ImageView[] imageViews = new ImageView[7];
    }

    @Override
    public int getCount() {
        return episodes.size();
    }

    @Override
    public Object getItem(int position) {
        return episodes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        EpisodeViewHolder holder;
        if(view == null){
            view = inflater.inflate(R.layout.ship, null);
            holder = new EpisodeViewHolder();
            holder.textViews[0] = (TextView)view.findViewById(R.id.subtitle1);
            holder.textViews[1] = (TextView)view.findViewById(R.id.subtitle2);
            holder.textViews[2] = (TextView)view.findViewById(R.id.subtitle3);
            holder.textViews[3] = (TextView)view.findViewById(R.id.subtitle4);
            holder.textViews[4] = (TextView)view.findViewById(R.id.subtitle5);
            holder.textViews[5] = (TextView)view.findViewById(R.id.subtitle6);
            holder.textViews[6] = (TextView)view.findViewById(R.id.subtitle7);

            holder.imageViews[0] = (ImageView)view.findViewById(R.id.thumb1);
            holder.imageViews[1] = (ImageView)view.findViewById(R.id.thumb2);
            holder.imageViews[2] = (ImageView)view.findViewById(R.id.thumb3);
            holder.imageViews[3] = (ImageView)view.findViewById(R.id.thumb4);
            holder.imageViews[4] = (ImageView)view.findViewById(R.id.thumb5);
            holder.imageViews[5] = (ImageView)view.findViewById(R.id.thumb6);
            holder.imageViews[6] = (ImageView)view.findViewById(R.id.thumb7);

            view.setTag(holder);
        }else{
            holder = (EpisodeViewHolder)view.getTag();
        }

        Episode[] episodeArray = episodes.get(position);

        for(int i = 0 ; i < episodeArray.length; i++){
            if(episodeArray[i] != null){
                holder.textViews[i].setText(episodeArray[i].subtitle);
                Glide.with(mContext).load(episodeArray[i].thumbURL).into(holder.imageViews[i]);
            }
        }
        return view;
    }
}
