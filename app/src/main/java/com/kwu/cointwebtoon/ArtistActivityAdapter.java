package com.kwu.cointwebtoon;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.kwu.cointwebtoon.DataStructure.CustomBitmapPool;
import com.kwu.cointwebtoon.DataStructure.Webtoon;
import com.kwu.cointwebtoon.DataStructure.Weekday_ListItem;
import com.kwu.cointwebtoon.Views.FastScrollRecyclerViewInterface;
import com.kwu.cointwebtoon.databinding.ArtistItemBinding;

import java.util.ArrayList;
import java.util.HashMap;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by JWP on 2017-05-21.
 */

public class ArtistActivityAdapter extends RecyclerView.Adapter<ArtistActivityAdapter.ViewHolder>implements FastScrollRecyclerViewInterface {
    private ArrayList<Webtoon> dataSet;
    private Context mContext;
    private HashMap<String, Integer> mapIndex;
    private COINT_SQLiteManager manager;

    public  static class ViewHolder extends RecyclerView.ViewHolder{
        private final ArtistItemBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);

        }
        public ArtistItemBinding getBinding(){
            return binding;
        }
    }

    public ArtistActivityAdapter(ArrayList<Webtoon> dataset,  HashMap<String, Integer> mapIndex) {
        this.dataSet = dataset;
        this.mapIndex = mapIndex;

    }
    public void setDataSet(ArrayList<Webtoon> dataSet){
        this.dataSet = dataSet;
    }

    @Override
    public ArtistActivityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        manager = COINT_SQLiteManager.getInstance(mContext);

        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ArtistItemBinding binding = holder.getBinding();

        //Title bar 설정
        binding.tvArtist.setText(dataSet.get(position).getArtist());
        if(position == 0)
            binding.tvArtist.setVisibility(View.VISIBLE);
        else {
            if (dataSet.get(position-1).getArtist().compareTo(dataSet.get(position).getArtist()) == 0)
                binding.tvArtist.setVisibility(View.GONE);
            else
                binding.tvArtist.setVisibility(View.VISIBLE);
        }

        //List Item 설정
        binding.tvTitle.setText(dataSet.get(position).getTitle());
        binding.tvTitle.setSelected(true);
        binding.tvStarScore.setText("★" + String.valueOf(dataSet.get(position).getStarScore()));
        Glide.with(mContext)
                .load(dataSet.get(position).getThumbURL())
                .centerCrop()
                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .placeholder(R.drawable.artist_placeholder)
                .into(binding.ivThumbnail);

        //My button 상태 설정
        if (dataSet.get(position).isMine()){
            //마이 웹툰 여부에 따라 별 아이콘 다르게 설정
            binding.btnMy.setBackgroundResource(R.drawable.my_star_active);
        }
        else{
            binding.btnMy.setBackgroundResource(R.drawable.my_star_unactive);
        }

        //Item onClick설정
        binding.itemWebtoon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //웹툰연결
                Webtoon target = dataSet.get(position);
                Toast.makeText(mContext,"웹툰 ID : " + target.getTitle(), Toast.LENGTH_SHORT).show();
                Intent episodeIntent = new Intent(mContext, EpisodeActivity.class);
                episodeIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                episodeIntent.putExtra("id", target.getId());
                episodeIntent.putExtra("toontype", target.getToonType());
                mContext.startActivity(episodeIntent);
            }
        });

        //My button onClick 설정
        binding.btnMy.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //해당 웹툰 MY웹툰으로 설정/해제
                Webtoon item = dataSet.get(position);
                String result = manager.updateMyWebtoon(String.valueOf(item.getId()));

                if(result.equals("마이 웹툰 설정")){
                    item.setIs_mine(true);
                    YoYo.with(Techniques.Flash).duration(500).delay(100).playOn(v);
                    v.setBackgroundResource(R.drawable.my_star_active);
                }else if(result.equals("마이 웹툰 해제")){
                    item.setIs_mine(false);
                    YoYo.with(Techniques.Wobble).duration(500).delay(100).playOn(v);
                    v.setBackgroundResource(R.drawable.my_star_unactive);
                }

                Toast.makeText(mContext, item.getTitle() + " " + result , Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @Override
    public HashMap<String, Integer> getMapIndex() {
        return this.mapIndex;
    }
}
