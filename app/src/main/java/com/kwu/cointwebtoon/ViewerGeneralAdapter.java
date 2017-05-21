package com.kwu.cointwebtoon;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kwu.cointwebtoon.Views.GeneralToonImageView;

import java.util.ArrayList;

public class ViewerGeneralAdapter extends BaseAdapter{
    ArrayList<String> bitmaps;
    AppCompatActivity mContext;
    LayoutInflater inflater = null;
    public TextView starTV = null;
    public TextView artist = null;
    public TextView mention = null;
    public RatingBar ratingbar = null;
    public Button givingstar = null;

    int width;
    private class ViewHolder {
        ImageView oneImage;
    }
    public ViewerGeneralAdapter(AppCompatActivity mContext, ArrayList<String> bitmaps) {
        this.bitmaps = bitmaps;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
        width = mContext.getWindowManager().getDefaultDisplay().getWidth();
    }
    @Override
    public int getCount() {
        try{
            return bitmaps.size() + 1;
        }catch (Exception e){ return 0;}
    }
    @Override
    public Object getItem(int position) {
        if(position < bitmaps.size())
            return bitmaps.get(position);
        return null;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if(position == bitmaps.size()){
            view = inflater.inflate(R.layout.viewer_general_rating_item, null);
            starTV = (TextView)view.findViewById(R.id.textview_starScore);
            artist = (TextView)view.findViewById(R.id.artist);
            mention = (TextView)view.findViewById(R.id.mention);
            ratingbar = (RatingBar)view.findViewById(R.id.rating_bar);
            givingstar = (Button) view.findViewById(R.id.giving_star);
        }else {
            view = convertView;
            ViewHolder holder = null;
            if(view == null){ //재활용 뷰가 아니라면 생성해줘야 하므로
                view = inflater.inflate(R.layout.viewer_general_image_item, null);//view를 inflate하고
                holder = new ViewHolder();//뷰 홀더를 통해 최적화
                holder.oneImage = (GeneralToonImageView) view.findViewById(R.id.GeneralToonImageView);
                view.setTag(holder);
            }else//재활용 뷰라면 해당 이미지뷰를 그냥 받아옴
                holder = (ViewHolder)view.getTag();

            //이미지 뷰에 Glide를 사용해서 url 이미지 뿌리는 부분
            try{
                if(holder.oneImage != null){
                    Glide.with(mContext)
                            .load(bitmaps.get(position))
                            .asBitmap()
                            .override(width, com.bumptech.glide.request.target.Target.SIZE_ORIGINAL)
                            .placeholder(R.drawable.view_placeholder)
                            .into(holder.oneImage);
                }
            }catch (IndexOutOfBoundsException iobex){
                Log.i("coint","Episode Changing..");
            }
        }
        return view;
    }
}
