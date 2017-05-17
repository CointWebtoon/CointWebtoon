package com.kwu.cointwebtoon;

import android.content.Context;
import android.util.Log;
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

public class SearchAdapter extends BaseAdapter {
    COINT_SQLiteManager coint_sqLiteManager;
    Context context = null;
    ArrayList<Webtoon> resultQuery = null;
    LayoutInflater layoutInflater = null;

    public SearchAdapter(Context context, ArrayList<Webtoon> arrayList){
        this.context = context;
        this.resultQuery = arrayList;
        this.layoutInflater = LayoutInflater.from(this.context);
        coint_sqLiteManager = COINT_SQLiteManager.getInstance(this.context);
    }

    public int getCount(){
        return resultQuery.size();
    }

    public long getItemId(int position){
        return position;
    }

    public Webtoon getItem(int position){
        return resultQuery.get(position);
    }

    public View getView(int position, final View convertView, ViewGroup parent){

        View itemLayout = convertView;
        ViewHolder viewHolder = null;

        if(itemLayout ==null){
            itemLayout = layoutInflater.inflate(R.layout.search_item, null);

            viewHolder = new ViewHolder();

            viewHolder.imageView = (ImageView) itemLayout.findViewById(R.id.webtoonImg);
            viewHolder.title = (TextView) itemLayout.findViewById(R.id.webtoonName);
            viewHolder.artist = (TextView) itemLayout.findViewById(R.id.artistName);
            viewHolder. starScore = (TextView) itemLayout.findViewById(R.id.starScore);

            viewHolder.title.setSelected(true);

            itemLayout.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)itemLayout.getTag();
        }
            Glide.with(context).load(resultQuery.get(position).getThumbURL()).into(viewHolder.imageView);
            viewHolder.title.setText(resultQuery.get(position).getTitle());
            viewHolder.artist.setText(resultQuery.get(position).getArtist());
            viewHolder.starScore.setText(Float.toString(resultQuery.get(position).getStarScore()));

        //버튼을 누르면 해당 웹툰의 id를 가져옴
        //버튼이나 이미지버튼을 사용 할 경우, 리스트뷰의 focus가 버튼에 가서 onClickListener가 동작하지 않음
        final int getId = resultQuery.get(position).getId();
        ImageView addWebtoon = (ImageView)itemLayout.findViewById(R.id.addWebtoon);

        addWebtoon.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                try {
                    String result = coint_sqLiteManager.updateMyWebtoon(Integer.toString(getId));
                    Toast.makeText(context,result,Toast.LENGTH_SHORT).show();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        });
        return itemLayout;
    }

    class ViewHolder{
        ImageView imageView;
        TextView title;
        TextView artist;
        TextView starScore;
    }

}
