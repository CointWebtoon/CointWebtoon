package com.kwu.cointwebtoon;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kwu.cointwebtoon.DataStructure.Webtoon;

import java.util.ArrayList;

public class SearchAdapter extends BaseAdapter {
    COINT_SQLiteManager coint_sqLiteManager;
    Context context = null;
    ArrayList<Webtoon> resultQuery = null;
    LayoutInflater layoutInflater = null;
    public int height = -1;

    public SearchAdapter(Context context, ArrayList<Webtoon> arrayList) {
        this.context = context;
        this.resultQuery = arrayList;
        this.layoutInflater = LayoutInflater.from(this.context);
        coint_sqLiteManager = COINT_SQLiteManager.getInstance(this.context);
    }

    public void changeItems(ArrayList<Webtoon> list) {
        resultQuery.clear();
        resultQuery.addAll(list);
        notifyDataSetChanged();
    }

    public int getCount() {
        return resultQuery.size();
    }

    public long getItemId(int position) {
        return position;
    }

    public Webtoon getItem(int position) {
        return resultQuery.get(position);
    }

    public View getView(final int position, final View convertView, ViewGroup parent) {
        View itemLayout = convertView;
        ViewHolder viewHolder = null;

        if (itemLayout == null) {
            itemLayout = layoutInflater.inflate(R.layout.search_item, null);

            viewHolder = new ViewHolder();

            viewHolder.imageView = (ImageView) itemLayout.findViewById(R.id.webtoonImg);
            viewHolder.title = (TextView) itemLayout.findViewById(R.id.webtoonName);
            viewHolder.artist = (TextView) itemLayout.findViewById(R.id.artistName);
            viewHolder.starScore = (TextView) itemLayout.findViewById(R.id.starScore);
            viewHolder.up = (TextView) itemLayout.findViewById(R.id.search_update);
            viewHolder.cuttoon = (TextView) itemLayout.findViewById(R.id.search_cuttoon);
            viewHolder.addItemButton = (ImageView) itemLayout.findViewById(R.id.addWebtoon);
            viewHolder.adult = (TextView) itemLayout.findViewById(R.id.search_adult);

            viewHolder.title.setSelected(true);

            itemLayout.setTag(viewHolder);
            height = itemLayout.getHeight();
        } else {
            viewHolder = (ViewHolder) itemLayout.getTag();
        }
        Glide.with(context).load(resultQuery.get(position).getThumbURL()).into(viewHolder.imageView);
        viewHolder.title.setText(resultQuery.get(position).getTitle());
        viewHolder.artist.setText(resultQuery.get(position).getArtist());
        viewHolder.starScore.setText("★ " + Float.toString(resultQuery.get(position).getStarScore()));

        if (resultQuery.get(position).isMine()) {           //마이웹툰일 경우 -로 설정
            viewHolder.addItemButton.setImageResource(R.drawable.my_star_active);
        } else {
            viewHolder.addItemButton.setImageResource(R.drawable.my_star_unactive);
        }

        if (resultQuery.get(position).getToonType() == 'C') {      // 컷툰 여부
            viewHolder.cuttoon.setVisibility(View.VISIBLE);
            viewHolder.cuttoon.setBackgroundResource(R.drawable.week_icon_cuttoon);
            viewHolder.cuttoon.setTextColor(Color.parseColor("#28dcbe"));
            viewHolder.cuttoon.setText("컷툰");
        } else if (resultQuery.get(position).getToonType() == 'M') {     //모션툰
            viewHolder.cuttoon.setVisibility(View.VISIBLE);
            viewHolder.cuttoon.setBackgroundResource(R.drawable.week_icon_motiontoon);
            viewHolder.cuttoon.setTextColor(Color.parseColor("#6d1daf"));
            viewHolder.cuttoon.setText("모션");
        } else if (resultQuery.get(position).getToonType() == 'S') {
            viewHolder.cuttoon.setVisibility(View.VISIBLE);
            viewHolder.cuttoon.setBackgroundResource(R.drawable.week_icon_smarttoon);
            viewHolder.cuttoon.setTextColor(Color.parseColor("#0050b4"));
            viewHolder.cuttoon.setText("스마트");
        } else {
            viewHolder.cuttoon.setBackgroundResource(R.drawable.week_icon_cuttoon);
            viewHolder.cuttoon.setText(null);
            viewHolder.cuttoon.setVisibility(itemLayout.GONE);
        }

        if (resultQuery.get(position).isAdult() == true) {      // 성인툰 여부
            viewHolder.adult.setVisibility(View.VISIBLE);
            viewHolder.adult.setBackgroundResource(R.drawable.main_icon_adult);
            viewHolder.adult.setText("성인");
        } else {
            viewHolder.adult.setBackgroundResource(R.drawable.main_icon_adult);
            viewHolder.adult.setText(null);
            viewHolder.adult.setVisibility(itemLayout.GONE);
        }

        if (resultQuery.get(position).isUpdated() == 1) {             //순서대로 연재일, 휴재, 연재일 아님
            viewHolder.up.setVisibility(View.VISIBLE);
            viewHolder.up.setBackgroundResource(R.drawable.week_icon_update);
            viewHolder.up.setText("UP");
        } else if (resultQuery.get(position).isUpdated() == 2) {
            viewHolder.up.setVisibility(View.VISIBLE);
            viewHolder.up.setBackgroundResource(R.drawable.week_icon_dormant);
            viewHolder.up.setText("휴재");
            viewHolder.up.setTextColor(Color.parseColor("#5F5F5F"));
        } else {
            viewHolder.up.setBackgroundResource(R.drawable.week_icon_cuttoon);
            viewHolder.up.setText(null);
            viewHolder.up.setVisibility(itemLayout.GONE);
        }

        //버튼을 누르면 해당 웹툰의 id를 가져옴
        //버튼이나 이미지버튼을 사용 할 경우, 리스트뷰의 focus가 버튼에 가서 onClickListener가 동작하지 않음
        final int getId = resultQuery.get(position).getId();
        final ImageView addWebtoon = (ImageView) itemLayout.findViewById(R.id.addWebtoon);
        addWebtoon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String result = null;
                Log.i("addWebtoon", Integer.toString(getId));
                try {
                    result = coint_sqLiteManager.updateMyWebtoon(Integer.toString(getId));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (result.equals("마이 웹툰 설정")) {
                    resultQuery.get(position).setIs_mine(true);
                    addWebtoon.setImageResource(R.drawable.my_star_active);
                } else {
                    resultQuery.get(position).setIs_mine(false);
                    addWebtoon.setImageResource(R.drawable.my_star_unactive);
                }
            }
        });
        return itemLayout;
    }

    class ViewHolder {
        ImageView imageView;
        ImageView addItemButton;
        TextView title;
        TextView artist;
        TextView starScore;
        TextView up;
        TextView cuttoon;
        TextView adult;
    }

}
