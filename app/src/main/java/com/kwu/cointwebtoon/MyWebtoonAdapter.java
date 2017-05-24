package com.kwu.cointwebtoon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kwu.cointwebtoon.DataStructure.Webtoon;

import java.util.ArrayList;

public class MyWebtoonAdapter extends BaseAdapter {
    COINT_SQLiteManager coint_sqLiteManager;
    Context context = null;
    ArrayList<Webtoon> resultQuery = null;
    LayoutInflater layoutInflater = null;
    private Application_UserInfo userInfo;

    public MyWebtoonAdapter(Context context, ArrayList<Webtoon> arrayList) {
        this.context = context;
        this.resultQuery = arrayList;
        this.layoutInflater = LayoutInflater.from(this.context);
        coint_sqLiteManager = COINT_SQLiteManager.getInstance(this.context);
        userInfo = (Application_UserInfo) context.getApplicationContext();
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
            itemLayout = layoutInflater.inflate(R.layout.top100_item, null);
            viewHolder = new ViewHolder();

            viewHolder.imageView = (ImageView) itemLayout.findViewById(R.id.webtoonImg);
            viewHolder.title = (TextView) itemLayout.findViewById(R.id.webtoonName);
            viewHolder.artist = (TextView) itemLayout.findViewById(R.id.artistName);
            viewHolder.starScore = (TextView) itemLayout.findViewById(R.id.starScore);
            viewHolder.ranking = (TextView) itemLayout.findViewById(R.id.totalRanking);
            viewHolder.add = (ImageView) itemLayout.findViewById(R.id.addWebtoon);
            viewHolder.cuttoon = (TextView) itemLayout.findViewById(R.id.cuttoon);
            viewHolder.update = (TextView) itemLayout.findViewById(R.id.update);
            viewHolder.adult = (TextView) itemLayout.findViewById(R.id.adult);

            viewHolder.title.setSelected(true);

            itemLayout.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) itemLayout.getTag();
        }

        Webtoon currentItem = resultQuery.get(position);
        Glide.with(context).load(currentItem.getThumbURL()).into(viewHolder.imageView);
        viewHolder.title.setText(currentItem.getTitle());
        viewHolder.artist.setText(currentItem.getArtist());
        viewHolder.starScore.setText("★ " + Float.toString(currentItem.getStarScore()));
        viewHolder.ranking.setText(Integer.toString(position + 1));

        if (currentItem.isMine()) {           //마이웹툰일 경우 -로 설정
            viewHolder.add.setImageResource(R.drawable.my_star_active);
        } else {
            viewHolder.add.setImageResource(R.drawable.my_star_unactive);
        }

        if (currentItem.getToonType() == 'C') {      // 컷툰 여부
            viewHolder.cuttoon.setVisibility(itemLayout.VISIBLE);
            viewHolder.cuttoon.setBackgroundResource(R.drawable.week_icon_cuttoon);
            viewHolder.cuttoon.setTextColor(Color.parseColor("#28dcbe"));
            viewHolder.cuttoon.setText("컷툰");
        } else if (currentItem.getToonType() == 'M') {     //모션툰
            viewHolder.cuttoon.setVisibility(View.VISIBLE);
            viewHolder.cuttoon.setBackgroundResource(R.drawable.week_icon_motiontoon);
            viewHolder.cuttoon.setTextColor(Color.parseColor("#6d1daf"));
            viewHolder.cuttoon.setText("모션");
        } else if (currentItem.getToonType() == 'S') {
            viewHolder.cuttoon.setVisibility(View.VISIBLE);
            viewHolder.cuttoon.setBackgroundResource(R.drawable.week_icon_smarttoon);
            viewHolder.cuttoon.setTextColor(Color.parseColor("#0050b4"));
            viewHolder.cuttoon.setText("스마트");
        } else {
            viewHolder.cuttoon.setBackgroundResource(R.drawable.week_icon_cuttoon);
            viewHolder.cuttoon.setText(null);
            viewHolder.cuttoon.setVisibility(itemLayout.GONE);
        }

        if (currentItem.isAdult() == true) {      // 성인툰 여부
            viewHolder.adult.setVisibility(itemLayout.VISIBLE);
            viewHolder.adult.setBackgroundResource(R.drawable.main_icon_adult);
            viewHolder.adult.setText("성인");
        } else {
            viewHolder.adult.setBackgroundResource(R.drawable.main_icon_adult);
            viewHolder.adult.setText(null);
            viewHolder.adult.setVisibility(itemLayout.GONE);
        }

        if (currentItem.isUpdated() == 1) {             //순서대로 연재일, 휴재, 연재일 아님
            viewHolder.update.setVisibility(itemLayout.VISIBLE);
            viewHolder.update.setBackgroundResource(R.drawable.week_icon_update);
            viewHolder.update.setText("UP");
            viewHolder.update.setTextColor(Color.parseColor("#fc6c00"));
        } else if (currentItem.isUpdated() == 2) {
            viewHolder.update.setVisibility(itemLayout.VISIBLE);
            viewHolder.update.setBackgroundResource(R.drawable.week_icon_dormant);
            viewHolder.update.setText("휴재");
            viewHolder.update.setTextColor(Color.parseColor("#ffffff"));
        } else {
            viewHolder.update.setBackgroundResource(R.drawable.week_icon_cuttoon);
            viewHolder.update.setText(null);
            viewHolder.update.setVisibility(itemLayout.GONE);
        }
        //버튼을 누르면 해당 웹툰의 id를 가져옴
        //버튼이나 이미지버튼을 사용 할 경우, 리스트뷰의 focus가 버튼에 가서 onClickListener가 동작하지 않음
        final int getId = resultQuery.get(position).getId();
        final ImageView addWebtoon = (ImageView) itemLayout.findViewById(R.id.addWebtoon);

        addWebtoon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {

                    if (resultQuery.get(position).isAdult()) {
                        if (userInfo.isLogin()) {
                            if (!userInfo.isUserAdult()) {
                                Toast.makeText(context, "만 19세 이상 시청 가능한 컨텐츠입니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } else {
                            new AlertDialog.Builder(context)
                                    .setTitle("로그인")
                                    .setMessage("로그인이 필요한 서비스 입니다. 로그인 하시겠습니까?")
                                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            context.startActivity(new Intent(context, LoginActivity.class));
                                        }
                                    }).setNegativeButton("아니요", null).show();
                            return;
                        }
                    }
                    String result = coint_sqLiteManager.updateMyWebtoon(Integer.toString(getId));
                    if (result.equals("마이 웹툰 설정")) {
                        resultQuery.get(position).setIs_mine(true);
                        addWebtoon.setImageResource(R.drawable.my_star_active);
                    } else {
                        resultQuery.get(position).setIs_mine(false);
                        addWebtoon.setImageResource(R.drawable.my_star_unactive);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        return itemLayout;
    }

    class ViewHolder {
        ImageView imageView;
        TextView title;
        TextView artist;
        TextView starScore;
        TextView ranking;
        ImageView add;
        TextView update;
        TextView cuttoon;
        TextView adult;
        TextView empty;
    }
}
