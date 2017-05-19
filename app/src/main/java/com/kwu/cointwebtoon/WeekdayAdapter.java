package com.kwu.cointwebtoon;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.kwu.cointwebtoon.DataStructure.CustomBitmapPool;
import com.kwu.cointwebtoon.DataStructure.Webtoon;
import com.kwu.cointwebtoon.DataStructure.Weekday_ListItem;
import com.kwu.cointwebtoon.Views.CircularView;
import com.kwu.cointwebtoon.databinding.WeekdayItemBinding;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class WeekdayAdapter extends BaseSwipeAdapter {
    private Context mContext;
    private ArrayList<Webtoon> webtoons;


    private ListView mListView;
    private ImageView ivThumbnail;
    private TextView tvUpdateIcon;
    private TextView tvToontypeIcon;

    private TextView tvTitle ;
    private TextView tvStarPoint;
    private TextView tvArtist;
    private ImageButton btnMy;
    private SwipeLayout frameMy;
    private LinearLayout weekMy;
    private COINT_SQLiteManager manager;

    public WeekdayAdapter(ListView listView, Weekday_ListItem listItem, Context context) {
        webtoons = listItem.getList();
        mContext = context;
        mListView = listView;
        manager = COINT_SQLiteManager.getInstance(mContext);
    }

    @Override
    public int getCount() {
        return webtoons.size();
    }

    @Override
    public Object getItem(int position) {
        return webtoons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }
    @Override
    public View generateView(final int position, final ViewGroup parent) {
        CircularView v = (CircularView) LayoutInflater.from(mContext).inflate(R.layout.weekday_item, null);
        v.setParentHeight(mListView.getHeight());
        final SwipeLayout swipeLayout = (SwipeLayout)v.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(800).delay(100).playOn(layout.findViewById(R.id.btn_my));
            }
        });

        v.findViewById(R.id.btn_my).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //해당 웹툰 MY웹툰으로 설정/해제
                int position  = (int)v.getTag();
                Webtoon item = (Webtoon)getItem(position);

                String result = manager.updateMyWebtoon(String.valueOf(item.getId()));

                if(result.equals("마이 웹툰 설정")){
                    item.setIs_mine(true);
                    YoYo.with(Techniques.Flash).duration(800).delay(100).playOn(v);
                    v.setBackgroundResource(R.drawable.my_set);
                    swipeLayout.setBackgroundResource(R.drawable.week_background_my);
                }else if(result.equals("마이 웹툰 해제")){
                    item.setIs_mine(false);
                    YoYo.with(Techniques.Wobble).duration(800).delay(100).playOn(v);
                    v.setBackgroundResource(R.drawable.my_release);
                    swipeLayout.setBackgroundResource(0);
                }

                Toast.makeText(mContext, item.getTitle() + " " + result , Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }
    @Override
    public void fillValues(final int position, View convertView) {
        ivThumbnail = (ImageView) convertView.findViewById(R.id.iv_thumbnail);
        tvTitle = (TextView)convertView.findViewById(R.id.tv_title);
        tvStarPoint = (TextView)convertView.findViewById(R.id.tv_starPoint);
        tvArtist = (TextView)convertView.findViewById(R.id.tv_artist);
        tvUpdateIcon = (TextView)convertView.findViewById(R.id.tv_update_icon);
        tvToontypeIcon = (TextView)convertView.findViewById(R.id.tv_toontype_icon);
        frameMy = (SwipeLayout)convertView.findViewById(R.id.swipe);
        btnMy = (ImageButton)convertView.findViewById(R.id.btn_my);
        weekMy = (LinearLayout)convertView.findViewById(R.id.week_my);

        btnMy.setTag(position);
        convertView.findViewById(R.id.week_item).setOnClickListener(new View.OnClickListener(){
            //웹툰연결
            @Override
            public void onClick(View v) {
                // get item
                Webtoon target = webtoons.get(position);
                Toast.makeText(mContext, "웹툰 ID : " + target.getTitle(), Toast.LENGTH_SHORT).show();
                Intent episodeIntent = new Intent(mContext, EpisodeActivity.class);
                episodeIntent.putExtra("id", target.getId());
                episodeIntent.putExtra("toontype", target.getToonType());
                mContext.startActivity(episodeIntent);
            }
        });

        Webtoon currentItem = webtoons.get(position);
        Glide.with(mContext)
                .load(currentItem.getThumbURL())
                .centerCrop()
                .bitmapTransform(new CropCircleTransformation(new CustomBitmapPool()))
                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .placeholder(R.drawable.week_placeholder)
                .into(ivThumbnail);
        tvTitle.setText(currentItem.getTitle());
        tvStarPoint.setText("★" + String.valueOf(currentItem.getStarScore()));
        tvArtist.setText(currentItem.getArtist());

        if (currentItem.isMine()){
            //마이 웹툰 여부에 따라 별 아이콘 다르게 설정
            btnMy.setBackgroundResource(R.drawable.my_set);
            frameMy.setBackgroundResource(R.drawable.week_background_my);
        }
        else{
            btnMy.setBackgroundResource(R.drawable.my_release);
            frameMy.setBackgroundResource(0);
        }

        //Update상태 아이콘 표시
        if(currentItem.isUpdated() == 0){
            tvUpdateIcon.setVisibility(View.GONE);
        }
        else if(currentItem.isUpdated() == 1){
            //Updated
            tvUpdateIcon.setVisibility(View.VISIBLE);
            tvUpdateIcon.setBackgroundResource(R.drawable.week_icon_update);
            tvUpdateIcon.setText("UP");
            tvUpdateIcon.setTextColor(Color.parseColor("#fc6c00"));
        }
        else if(currentItem.isUpdated() == 2){
            //Dormant
            tvUpdateIcon.setVisibility(View.VISIBLE);
            tvUpdateIcon.setBackgroundResource(R.drawable.week_icon_dormant);
            tvUpdateIcon.setText("휴재");
            tvUpdateIcon.setTextColor(Color.parseColor("#ffffff"));
        }

        //Webtoon type 아이콘 표시
        if (currentItem.getToonType() == 'G'){
            tvToontypeIcon.setVisibility(View.GONE);
        }
        else if (currentItem.getToonType() == 'C'){
            tvToontypeIcon.setVisibility(View.VISIBLE);
            tvToontypeIcon.setBackgroundResource(R.drawable.week_icon_cuttoon);
            tvToontypeIcon.setText("컷툰");
        }
    }



    public void setItemList(Weekday_ListItem listItem){
        this.webtoons = listItem.getList();
    }

}
