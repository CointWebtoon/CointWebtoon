package com.kwu.cointwebtoon;

import android.content.Context;
import android.content.Intent;
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
import com.kwu.cointwebtoon.DataStructure.Webtoon;
import com.kwu.cointwebtoon.DataStructure.Weekday;
import com.kwu.cointwebtoon.DataStructure.Weekday_ListItem;

import java.util.ArrayList;

public class Main_MyToonAdapter extends RecyclerView.Adapter<Main_MyToonAdapter.ViewHolder> {
    private ArrayList<Webtoon> arrayList = new ArrayList<>();
    private ArrayList<Webtoon> weekdayList = new ArrayList<>();
    private Weekday_ListItem weekday_listItem;
    COINT_SQLiteManager coint_sqLiteManager;
    private View v;
    private Context mContext;
    private int lastPosition = -1;
    private Main_MyToonAdapter adapter;
    private int day;                // day는 0:완결, 1- 7 까지 차례로 요일.

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

    public Main_MyToonAdapter(Context context, int num) {
        this.mContext = context;
        this.adapter = this;
        this.day = num;
        ArrayList<Webtoon> mList = new ArrayList<>();
        coint_sqLiteManager = COINT_SQLiteManager.getInstance(mContext);
        Cursor cursor = coint_sqLiteManager.getMyWebtoons();
        while (cursor.moveToNext()) {
            mList.add(new Webtoon(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getFloat(3),
                    cursor.getInt(4), cursor.getString(5), cursor.getInt(6), cursor.getString(7).charAt(0), cursor.getInt(8)==1?true:false,
                    cursor.getInt(9)==1?true:false, cursor.getInt(10)==1?true:false, cursor.getInt(11)));
        }
        addRemoveItem(mList);
        weekday_listItem = new Weekday_ListItem(mContext, num);
        weekdayList = weekday_listItem.getList();
        Log.i("weekday list size : ", Integer.toString(weekdayList.size()));
    }

    public Main_MyToonAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_mywebtoon_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (position == 0) {
            holder.title.setText("웹툰 추가");
            holder.artist.setText("");
            holder.imageView.setImageResource(R.drawable.main_addmark);
        } else {
            holder.title.setText(arrayList.get(position).getTitle());
            holder.artist.setText(arrayList.get(position).getArtist());
            Glide.with(mContext).load(arrayList.get(position).getThumbURL()).into(holder.imageView);
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                int id = v.getId();
                Intent intent;
                switch (id) {
                    case R.id.cardview:
                        if (holder.title.getText().equals("웹툰 추가")) {
                            /**
                             * Activity 연결부
                             */
                            intent = new Intent(mContext,WeekdayActivity.class);
                            mContext.startActivity(intent);
                        } else {
                            Webtoon target = arrayList.get(position);
                            intent = new Intent(mContext, EpisodeActivity.class);
                            intent.putExtra("id", target.getId());
                            intent.putExtra("toontype", target.getToonType());
                            mContext.startActivity(intent);
                        }
                        break;
                }
            }
        };
        v.findViewById(R.id.cardview).setOnClickListener(onClickListener);

        setAnimation(holder.imageView, position);
    }

    public int getItemCount() {
        return arrayList.size();
    }

    public void addRemoveItem(final ArrayList<Webtoon> list) {

        ArrayList<Webtoon> temparr = new ArrayList<>();
        temparr.clear();
        arrayList.clear();
        //여기 position0에 들어가는 웹툰 따로 넣고 돌리기
        Webtoon resultQuery = new Webtoon();
        resultQuery.setId(0);
        resultQuery.setTitle("웹툰 추가");
        resultQuery.setArtist("");
        resultQuery.setThumbURL("");
        resultQuery.setStarScore(0.0f);
        arrayList.add(resultQuery);

        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < weekdayList.size(); j++) {
                if(list.get(i).getId()==weekdayList.get(j).getId()){
                    arrayList.add(list.get(i));
                    break;
                }
            }
        }
        notifyDataSetChanged();
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
