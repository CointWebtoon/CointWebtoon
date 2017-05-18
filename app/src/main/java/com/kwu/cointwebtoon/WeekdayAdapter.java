package com.kwu.cointwebtoon;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.kwu.cointwebtoon.DataStructure.CustomBitmapPool;
import com.kwu.cointwebtoon.DataStructure.Webtoon;
import com.kwu.cointwebtoon.DataStructure.Weekday_ListItem;
import com.kwu.cointwebtoon.Views.CircularView;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class WeekdayAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Webtoon> webtoons;

    private ListView mListView;
    private ImageView ivThumbnail;
    private TextView tvUpdateIcon;
    private TextView tvToontypeIcon;

    private TextView tvTitle ;
    private TextView tvStarPoint;
    private TextView tvArtist;

    private CircularView frameMy;

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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            CircularView m = (CircularView) LayoutInflater.from(mContext).inflate(R.layout.weekday_item, null);
            m.setParentHeight(mListView.getHeight());
            convertView = m;
        }
        //btnAddMy = (ToggleButton) convertView.findViewById(R.id.btn_addMy);
        ivThumbnail = (ImageView) convertView.findViewById(R.id.iv_thumbnail);
        tvTitle = (TextView)convertView.findViewById(R.id.tv_title);
        tvStarPoint = (TextView)convertView.findViewById(R.id.tv_starPoint);
        tvArtist = (TextView)convertView.findViewById(R.id.tv_artist);
        tvUpdateIcon = (TextView)convertView.findViewById(R.id.tv_update_icon);
        tvToontypeIcon = (TextView)convertView.findViewById(R.id.tv_toontype_icon);

        frameMy = (CircularView)convertView.findViewById(R.id.frame_my);
        //iconMy = (TextView)convertView.findViewById(R.id.icon_my);
        //btnAddMy.setTag(position);
        //btnAddMy.setOnClickListener(new btnAddMyOnClick());
        //btnAddMy.setOnCheckedChangeListener(new btnAddMyOnCheckedChange());

        Webtoon currentItem = webtoons.get(position);   //웹툰형으로 변경해서 더 쉽게 데이터 받을 수 있어요
        Glide.with(mContext)
                .load(currentItem.getThumbURL())
                .centerCrop()
                .bitmapTransform(new CropCircleTransformation(new CustomBitmapPool()))
                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .placeholder(R.drawable.weekday_placeholder)
                .into(ivThumbnail);
        tvTitle.setText(currentItem.getTitle());
        tvStarPoint.setText("★" + String.valueOf(currentItem.getStarScore()));
        tvArtist.setText(currentItem.getArtist());

       if (currentItem.isMine()){
           //마이 웹툰 여부에 따라 별 아이콘 다르게 설정
           frameMy.setBackgroundResource(R.drawable.week_background_my);
       }
        else{
           frameMy.setBackgroundResource(0);
       }

        //Update상태 아이콘 표시 TODO - 최신화 조건에 맞는 up Icon 표시 작업 --> 조건 추가 완료했는데 서버에 다 is_updated로 되있는거가틈 서버 수정할게요
        if(currentItem.isUpdated() == 0){
            tvUpdateIcon.setBackgroundResource(0);
            tvUpdateIcon.setText(null);
            tvUpdateIcon.setVisibility(View.GONE);
        }
        else if(currentItem.isUpdated() == 1){
            //Updated
            tvUpdateIcon.setBackgroundResource(R.drawable.week_icon_update);
            tvUpdateIcon.setText("UP");
            tvUpdateIcon.setTextColor(Color.parseColor("#fc6c00"));
        }
        else if(currentItem.isUpdated() == 2){
            //Dormant
            tvUpdateIcon.setBackgroundResource(R.drawable.week_icon_dormant);
            tvUpdateIcon.setText("휴재");
            tvUpdateIcon.setTextColor(Color.parseColor("#ffffff"));

        }

        //Webtoon type 아이콘 표시
        if (currentItem.getToonType() == 'G'){
            tvToontypeIcon.setBackgroundResource(0);
            tvToontypeIcon.setText(null);
            tvToontypeIcon.setVisibility(View.GONE);
        }
        else if (currentItem.getToonType() == 'C'){
            tvToontypeIcon.setBackgroundResource(R.drawable.week_icon_cuttoon);
            tvToontypeIcon.setText("컷툰");
        }
        return convertView;
    }

    public void setItemList(Weekday_ListItem listItem){
        this.webtoons = listItem.getList();
    }

    class btnAddMyOnClick implements ToggleButton.OnClickListener{
        @Override
        public void onClick(View v) {
            //TODO - 해당 웹툰 MY웹툰으로 설정/해제 --> 완료
            int position  = (int)v.getTag();
            Webtoon item = (Webtoon)getItem(position);

            String result = manager.updateMyWebtoon(String.valueOf(item.getId()));
            if(result.equals("마이 웹툰 설정")){
                item.setIs_mine(true);
            }else if(result.equals("마이 웹툰 해제")){
                item.setIs_mine(false);
            }
            Toast.makeText(mContext, item.getTitle() + " " + result , Toast.LENGTH_SHORT).show();
        }
    }
    class btnAddMyOnCheckedChange implements ToggleButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked)
                buttonView.setBackgroundResource(R.drawable.week_icon_highlight_star);
            else
                buttonView.setBackgroundResource(R.drawable.week_icon_star);
        }
    }
}
