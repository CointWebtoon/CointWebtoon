package com.example.epcej.coint_mainactivity;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import java.util.ArrayList;

/**
 * Created by epcej on 2017-03-13.
 */

public class MyWebtoonAdp extends RecyclerView.Adapter<MyWebtoonAdp.ViewHolder>{
  private ArrayList<SearchResult> arrayList;
    private Context mContext;
    private int lastPosition = -1;
    private Cursor cursor;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView title;
        TextView artist;


        public ViewHolder(View view){
            super(view);
            imageView = (ImageView)view.findViewById(R.id.addView);
            title = (TextView)view.findViewById(R.id.mainTitle);
            artist = (TextView)view.findViewById(R.id.mainArtist);
        }
    }
    public MyWebtoonAdp(Cursor c, Context context){
        this.mContext = context;
        this.cursor = c;
        Log.i("CURSOR SIZE : ", Integer.toString(c.getCount()));
    }

    public MyWebtoonAdp.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_webtoon,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    public void onBindViewHolder(ViewHolder holder, int position){
        if(position==0){
            holder.title.setText("웹툰 추가");
            holder.artist.setText("");
            holder.imageView.setImageResource(R.drawable.addmark);
        }else{
            cursor.moveToPosition(position-1);
            holder.title.setText(cursor.getString(1).toString());
            holder.artist.setText(cursor.getString(2).toString());
            Glide.with(mContext).load(cursor.getString(3).toString()).into(holder.imageView);
        }


        setAnimation(holder.imageView, position);
    }

    public int getItemCount(){
            return cursor.getCount()+1;
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // 새로 보여지는 뷰라면 애니메이션을 해줍니다
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
