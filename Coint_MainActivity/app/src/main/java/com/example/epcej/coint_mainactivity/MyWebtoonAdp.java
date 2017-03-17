package com.example.epcej.coint_mainactivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by epcej on 2017-03-13.
 */

public class MyWebtoonAdp extends RecyclerView.Adapter<MyWebtoonAdp.ViewHolder> {
    private ArrayList<SearchResult> arrayList = new ArrayList<SearchResult>();
    COINT_SQLiteManager coint_sqLiteManager;
    private View v;
    private Context mContext;
    private int lastPosition = -1;
    private Cursor cursor;
    private RecyclerView recyclerView;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title;
        TextView artist;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.addView);
            title = (TextView) view.findViewById(R.id.mainTitle);
            artist = (TextView) view.findViewById(R.id.mainArtist);
        }
    }

    public MyWebtoonAdp(Context context) {
        this.mContext = context;
        coint_sqLiteManager = COINT_SQLiteManager.getInstance(mContext);

        cursor = coint_sqLiteManager.isMyWebtoon();

        SearchResult resultQuery = new SearchResult(0,"웹툰 추가","","", Float.parseFloat("0.0"));
        arrayList.add(resultQuery);

        while (cursor.moveToNext()) {
            resultQuery = new SearchResult(Integer.parseInt(cursor.getString(0).toString()), cursor.getString(1).toString(), cursor.getString(2).toString(), cursor.getString(3).toString(), Float.parseFloat(cursor.getString(4).toString()));
            //차례대로 id, title, artist, thumburl, starscore
            /*Log.i("result",resultQuery.title);*/
            arrayList.add(resultQuery);
        }
        Log.i("CURSOR SIZE : ", Integer.toString(cursor.getCount()));
    }

    public MyWebtoonAdp.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_webtoon, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);

        recyclerView = (RecyclerView)v.findViewById(R.id.my_recycler_view);

        return viewHolder;
    }

    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.i("position Number",Integer.toString(position));
        if (position == 0) {
            holder.title.setText("웹툰 추가");
            holder.artist.setText("");
            holder.imageView.setImageResource(R.drawable.addmark);
        } else {
            holder.title.setText(arrayList.get(position - 1).title);
            holder.artist.setText(arrayList.get(position - 1).artist);
            Glide.with(mContext).load(arrayList.get(position - 1).thumbUrl).into(holder.imageView);
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                int id = v.getId();
                Intent intent;

                switch (id) {
                    case R.id.cardview:
                        if (holder.title.getText().equals("웹툰 추가")) {
                            intent = new Intent(mContext, IntentTest.class);
                            intent.putExtra("Intent", holder.title.getText());
                            Toast.makeText(mContext, "????", Toast.LENGTH_SHORT).show();
                            mContext.startActivity(intent);
                        } else {
                            intent = new Intent(mContext, IntentTest.class);
                            intent.putExtra("Intent", holder.title.getText());
                            mContext.startActivity(intent);
                            System.out.println(position);
                        }
                        break;
                }
            }
        };
        v.findViewById(R.id.cardview).setOnClickListener(onClickListener);

        setAnimation(holder.imageView, position);
    }

    public int getItemCount() {
        return arrayList.size() + 1;
    }

    public void addRemoveItem(Cursor c) {
        arrayList.clear();
        //여기 position0에 들어가는 웹툰 따로 넣고 돌리기

        System.out.println(c.getCount());
        while (c.moveToNext()) {
            SearchResult resultQuery = new SearchResult(Integer.parseInt(c.getString(0).toString()), c.getString(1).toString(), c.getString(2).toString(), c.getString(3).toString(), Float.parseFloat(c.getString(4).toString()));
            //차례대로 id, title, artist, thumburl, starscore
            /*Log.i("result", resultQuery.title);*/
            arrayList.add(resultQuery);
        }
        getItemCount();
        notifyItemRangeChanged(0,getItemCount());
    }

    private void setAnimation(View viewToAnimate, int position) {
        // 새로 보여지는 뷰라면 애니메이션을 해줍니다
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
