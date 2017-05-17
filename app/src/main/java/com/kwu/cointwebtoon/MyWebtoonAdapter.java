package com.kwu.cointwebtoon;

import android.content.Context;
import android.media.Image;
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

    public MyWebtoonAdapter(Context context, ArrayList<Webtoon> arrayList) {
        this.context = context;
        this.resultQuery = arrayList;
        this.layoutInflater = LayoutInflater.from(this.context);
        coint_sqLiteManager = COINT_SQLiteManager.getInstance(this.context);
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

    public View getView(int position, final View convertView, ViewGroup parent) {

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
            viewHolder.add = (ImageView)itemLayout.findViewById(R.id.addWebtoon);

            viewHolder.title.setSelected(true);

            itemLayout.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) itemLayout.getTag();
        }

        Webtoon currentItem = resultQuery.get(position);
        Glide.with(context).load(currentItem.getThumbURL()).into(viewHolder.imageView);
        viewHolder.title.setText(currentItem.getTitle());
        viewHolder.artist.setText(currentItem.getArtist());
        viewHolder.starScore.setText(Float.toString(currentItem.getStarScore()));
        viewHolder.ranking.setText(Integer.toString(position + 1));

        if(currentItem.isMine()){
            viewHolder.add.setImageResource(R.drawable.main_minus_button_state);
        }else{
            viewHolder.add.setImageResource(R.drawable.main_add_button_state);
        }

        //버튼을 누르면 해당 웹툰의 id를 가져옴
        //버튼이나 이미지버튼을 사용 할 경우, 리스트뷰의 focus가 버튼에 가서 onClickListener가 동작하지 않음
        final int getId = resultQuery.get(position).getId();
        final ImageView addWebtoon = (ImageView) itemLayout.findViewById(R.id.addWebtoon);

        addWebtoon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    String result = coint_sqLiteManager.updateMyWebtoon(Integer.toString(getId));
                    Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
                    if(result.equals("마이 웹툰 설정")){
                        addWebtoon.setImageResource(R.drawable.main_minus_button_state);
                    }else{
                        addWebtoon.setImageResource(R.drawable.main_add_button_state);
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
    }
}
