package com.example.epcej.coint_mainactivity;

import android.content.Context;
import android.content.SearchRecentSuggestionsProvider;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by epcej on 2017-03-04.
 */

public class SearchAdapter extends BaseAdapter {
    Context context = null;
    ArrayList<SearchResult> resultQuery = null;
    LayoutInflater layoutInflater = null;

    public SearchAdapter(Context context, ArrayList<SearchResult> arrayList){
        this.context = context;
        this.resultQuery = arrayList;
        this.layoutInflater = LayoutInflater.from(this.context);
    }

    public int getCount(){
        return resultQuery.size();
    }

    public long getItemId(int position){
        return position;
    }

    public SearchResult getItem(int position){
        return resultQuery.get(position);
    }

    public View getView(int position, View convertView, ViewGroup parent){

        View itemLayout = convertView;
        ViewHolder viewHolder = null;

        if(itemLayout ==null){
            itemLayout = layoutInflater.inflate(R.layout.search_view, null);

            viewHolder = new ViewHolder();

            viewHolder.imageView = (ImageView) itemLayout.findViewById(R.id.webtoonImg);
            viewHolder.title = (TextView) itemLayout.findViewById(R.id.webtoonName);
            viewHolder.artist = (TextView) itemLayout.findViewById(R.id.artistName);
            viewHolder. starScore = (TextView) itemLayout.findViewById(R.id.starScore);

            itemLayout.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)itemLayout.getTag();
        }
            Glide.with(context).load(resultQuery.get(position).thumbUrl).into(viewHolder.imageView);
            viewHolder.title.setText(resultQuery.get(position).title);
            viewHolder.artist.setText(resultQuery.get(position).artist);
            viewHolder.starScore.setText(Float.toString(resultQuery.get(position).starScore));
        return itemLayout;
    }

    class ViewHolder{
        ImageView imageView;
        TextView title;
        TextView artist;
        TextView starScore;
    }
}
