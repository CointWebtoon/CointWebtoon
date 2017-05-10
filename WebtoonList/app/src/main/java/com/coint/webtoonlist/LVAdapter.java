package com.coint.webtoonlist;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.coint.webtoonlist.databinding.FragmentDayBinding;
import com.coint.webtoonlist.databinding.ViewListItemBinding;

import java.util.List;
import java.util.Objects;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.coint.webtoonlist.R.id.btn_addMy;
import static com.coint.webtoonlist.R.id.listView;
import static java.security.AccessController.getContext;

/**
 * Created by JWP on 2017-05-10.
 */

public class LVAdapter extends BaseAdapter{

    private Context mContext;
    private ListItem mListitem;

    private ListView mListView;
    private ImageView ivThumbnail;
    private ImageView ivUpIcon;

    private TextView tvTitle ;
    private TextView tvStarPoint;
    private TextView tvArtist;
    private ToggleButton btnAddMy;

    public LVAdapter(ListView listView,ListItem listItem, Context context) {
        mListitem = listItem;
        mContext = context;
        mListView = listView;
    }

    @Override
    public int getCount() {
        return mListitem.getListTotalCount();
    }
    @Override
    public Object getItem(int position) {
        String wID = mListitem.getWebtoonID().get(position).toString();
        return  wID;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            CircularView m = (CircularView) LayoutInflater.from(mContext).inflate(R.layout.view_list_item, null);
            m.setParentHeight(mListView.getHeight());
            convertView = m;
        }
        btnAddMy = (ToggleButton) convertView.findViewById(R.id.btn_addMy);
        ivThumbnail = (ImageView) convertView.findViewById(R.id.iv_thumbnail);
        tvTitle = (TextView)convertView.findViewById(R.id.tv_title);
        tvStarPoint = (TextView)convertView.findViewById(R.id.tv_starPoint);
        tvArtist = (TextView)convertView.findViewById(R.id.tv_artist);
        ivUpIcon = (ImageView)convertView.findViewById(R.id.iv_upIcon);
        btnAddMy.setTag(position);
        btnAddMy.setOnClickListener(new btnAddMyOnClick());
        btnAddMy.setOnCheckedChangeListener(new btnAddMyOnCheckedChange());

        Glide.with(mContext)
                .load(mListitem.getThumbUrls().get(position).toString())
                .centerCrop()
                .bitmapTransform(new CropCircleTransformation(new CustomBitmapPool()))
                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .into(ivThumbnail);
        tvTitle.setText(mListitem.getTitles().get(position).toString());
        tvStarPoint.setText("★" + mListitem.getStarPoints().get(position).toString());
        if(true) //TODO - 최신화 조건에 맞는 up Icon 표시 작업
            ivUpIcon.setImageResource(R.drawable.icon_up);
        tvArtist.setText(mListitem.getArtists().get(position).toString());

        return convertView;
    }
    class btnAddMyOnClick implements ToggleButton.OnClickListener{
        @Override
        public void onClick(View v) {
            //TODO - 해당 웹툰 MY웹툰으로 설정/해제
            Toast.makeText(mContext, "Tag : " + v.getTag().toString(), Toast.LENGTH_SHORT).show();

        }
    }
    class btnAddMyOnCheckedChange implements ToggleButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked)
                buttonView.setBackgroundResource(R.drawable.icon_highlight_star);
            else
                buttonView.setBackgroundResource(R.drawable.icon_star);

        }
    }
}
