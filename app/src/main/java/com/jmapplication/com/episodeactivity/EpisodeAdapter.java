package com.jmapplication.com.episodeactivity;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class EpisodeAdapter extends BaseAdapter implements View.OnTouchListener{
    private ArrayList<Episode[]> episodes;
    private Context mContext;
    private LayoutInflater inflater;
    private int maxNumber;

    public EpisodeAdapter(Context mContext, ArrayList<Episode[]> episodes, int maxNumber) {
        this.mContext = mContext;
        this.episodes = episodes;
        inflater = LayoutInflater.from(mContext);
        this.maxNumber = maxNumber;
    }

    private class EpisodeViewHolder {
        TextView[] textViews = new TextView[7];
        ImageView[] imageViews = new ImageView[7];
        LinearLayout[] items = new LinearLayout[7];
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
        if (view == null) {
            view = inflater.inflate(R.layout.ship, null);
            holder = new EpisodeViewHolder();
            holder.textViews[0] = (TextView) view.findViewById(R.id.subtitle1);
            holder.textViews[1] = (TextView) view.findViewById(R.id.subtitle2);
            holder.textViews[2] = (TextView) view.findViewById(R.id.subtitle3);
            holder.textViews[3] = (TextView) view.findViewById(R.id.subtitle4);
            holder.textViews[4] = (TextView) view.findViewById(R.id.subtitle5);
            holder.textViews[5] = (TextView) view.findViewById(R.id.subtitle6);
            holder.textViews[6] = (TextView) view.findViewById(R.id.subtitle7);

            holder.imageViews[0] = (ImageView) view.findViewById(R.id.thumb1);
            holder.imageViews[1] = (ImageView) view.findViewById(R.id.thumb2);
            holder.imageViews[2] = (ImageView) view.findViewById(R.id.thumb3);
            holder.imageViews[3] = (ImageView) view.findViewById(R.id.thumb4);
            holder.imageViews[4] = (ImageView) view.findViewById(R.id.thumb5);
            holder.imageViews[5] = (ImageView) view.findViewById(R.id.thumb6);
            holder.imageViews[6] = (ImageView) view.findViewById(R.id.thumb7);

            holder.items[0] = (LinearLayout)view.findViewById(R.id.item1);
            holder.items[1] = (LinearLayout)view.findViewById(R.id.item2);
            holder.items[2] = (LinearLayout)view.findViewById(R.id.item3);
            holder.items[3] = (LinearLayout)view.findViewById(R.id.item4);
            holder.items[4] = (LinearLayout)view.findViewById(R.id.item5);
            holder.items[5] = (LinearLayout)view.findViewById(R.id.item6);
            holder.items[6] = (LinearLayout)view.findViewById(R.id.item7);

            for(int i = 0 ; i < 7; i++){
                holder.items[i].setOnTouchListener(this);
            }

            view.setTag(holder);
        } else {
            holder = (EpisodeViewHolder) view.getTag();
        }

        Episode[] episodeArray = episodes.get(position);
        Integer absoluteNumber;
        for (int i = 0; i < episodeArray.length; i++) {
            absoluteNumber = 7 * position + i;
            if (episodeArray[i] != null) {
                holder.textViews[i].setText(episodeArray[i].getEpisode_title());
                holder.items[i].setTag(maxNumber - absoluteNumber);
                Glide.with(mContext)
                        .load(episodeArray[i].getEp_thumbURL())
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .override(700, 370)
                        .fitCenter()
                        .into(holder.imageViews[i]);
            } else {
                holder.textViews[i].setText("");
                holder.items[i].setTag(null);
                holder.imageViews[i].setImageBitmap(null);
            }
        }
        return view;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event .getAction() == MotionEvent.ACTION_DOWN)
            v.setBackgroundColor(Color.LTGRAY);
        else if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
            v.setBackground(null);

        return false;
    }
}
