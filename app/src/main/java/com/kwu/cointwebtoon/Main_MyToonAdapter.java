package com.kwu.cointwebtoon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kwu.cointwebtoon.DataStructure.Episode;
import com.kwu.cointwebtoon.DataStructure.Webtoon;
import com.kwu.cointwebtoon.DataStructure.Weekday_ListItem;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class Main_MyToonAdapter extends RecyclerView.Adapter<Main_MyToonAdapter.ViewHolder> implements Observer {
    private ArrayList<Webtoon> arrayList = new ArrayList<>();
    private ArrayList<Webtoon> weekdayList = new ArrayList<>();
    private Weekday_ListItem weekday_listItem;
    private COINT_SQLiteManager coint_sqLiteManager;
    private View v;
    private Context mContext;
    private int lastPosition = -1;
    private Main_MyToonAdapter adapter;
    private int day;                // day는 0:완결, 1- 7 까지 차례로 요일.
    private Application_UserInfo userInfo;
    private Webtoon updateInstance; //Observer update 함수 에서 사용할 변수
    private CointProgressDialog dialog;
    private GetServerData serverData;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title;
        TextView artist;
        TextView starscore;
        TextView up;
        TextView cuttoon;
        TextView adult;
        Button latest;
        CardView cardView;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.addView);
            title = (TextView) view.findViewById(R.id.mainTitle);
            artist = (TextView) view.findViewById(R.id.mainArtist);
            starscore = (TextView) view.findViewById(R.id.mainStarScore);
            up = (TextView)view.findViewById(R.id.recycle_update);
            cuttoon = (TextView)view.findViewById(R.id.recycle_cuttoon);
            adult = (TextView)view.findViewById(R.id.recycle_adult);
            latest = (Button)view.findViewById(R.id.latest);
            cardView = (CardView)view.findViewById(R.id.cardview);
        }
    }

    public Main_MyToonAdapter(Context context, int num) {
        this.mContext = context;
        this.adapter = this;
        this.day = num;
        ArrayList<Webtoon> mList = new ArrayList<>();
        coint_sqLiteManager = COINT_SQLiteManager.getInstance(mContext);
        Cursor cursor = coint_sqLiteManager.getMyWebtoons();
        while (cursor.moveToNext()) {
            mList.add(new Webtoon(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getFloat(3),
                    cursor.getInt(4), cursor.getString(5), cursor.getInt(6), cursor.getString(7).charAt(0), cursor.getInt(8)==1?true:false,
                    cursor.getInt(9)==1?true:false, cursor.getInt(10)==1?true:false, cursor.getInt(11)));
        }

        addRemoveItem(mList);
        dialog = new CointProgressDialog(mContext);
        weekday_listItem = new Weekday_ListItem(mContext, num);
        weekdayList = weekday_listItem.getList();
        Log.i("weekday list size : ", Integer.toString(weekdayList.size()));
    }

    public Main_MyToonAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_mywebtoon_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (position == 0) {
            holder.cardView.setCardBackgroundColor(0);
            holder.title.setTextColor(Color.parseColor("#5f5f5f"));
            holder.title.setText("더 보기");
            holder.artist.setText(null);
            holder.starscore.setVisibility(v.GONE);
            holder.latest.setVisibility(v.GONE);

            switch (day){
                case 1:
                    holder.imageView.setImageResource(R.drawable.main_mon_d2);
                    break;
                case 2:
                    holder.imageView.setImageResource(R.drawable.main_tue_d2);
                    break;
                case 3:
                    holder.imageView.setImageResource(R.drawable.main_wed_d2);
                    break;
                case 4:
                    holder.imageView.setImageResource(R.drawable.main_thu_d2);
                    break;
                case 5:
                    holder.imageView.setImageResource(R.drawable.main_fri_d2);
                    break;
                case 6:
                    holder.imageView.setImageResource(R.drawable.main_sat_d2);
                    break;
                case 7:
                    holder.imageView.setImageResource(R.drawable.main_sun_d2);
                    break;
                case 0:
                    holder.imageView.setImageResource(R.drawable.main_finish_d2);
                    holder.title.setText(null);
                    break;
            }
        } else {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#5f5f5f"));
            holder.starscore.setVisibility(v.VISIBLE);
            holder.latest.setVisibility(v.VISIBLE);

            holder.title.setText(arrayList.get(position).getTitle());
            holder.artist.setText(arrayList.get(position).getArtist());
            holder.starscore.setText("★ "+String.valueOf(arrayList.get(position).getStarScore()));
            Glide.with(mContext).load(arrayList.get(position).getThumbURL()).into(holder.imageView);

            if(arrayList.get(position).getToonType() == 'C') {      // 컷툰 여부
                holder.cuttoon.setVisibility(View.VISIBLE);
                holder.cuttoon.setBackgroundResource(R.drawable.week_icon_cuttoon);
                holder.cuttoon.setText("컷툰");
            }else{
                holder.cuttoon.setBackgroundResource(R.drawable.week_icon_cuttoon);
                holder.cuttoon.setText(null);
                holder.cuttoon.setVisibility(v.GONE);
            }

            if(arrayList.get(position).isAdult()==true) {      // 성인툰 여부
                holder.adult.setVisibility(View.VISIBLE);
                holder.adult.setBackgroundResource(R.drawable.main_icon_adult);
                holder.adult.setTextColor(Color.parseColor("#FFFFFF"));
                holder.adult.setText("성인");
            }else{
                holder.adult.setBackgroundResource(R.drawable.main_icon_adult);
                holder.adult.setText(null);
                holder.adult.setVisibility(v.GONE);
            }

            if(arrayList.get(position).isUpdated()==1){             //순서대로 연재일, 휴재, 연재일 아님
                holder.up.setVisibility(View.VISIBLE);
                holder.up.setBackgroundResource(R.drawable.week_icon_update);
                holder.up.setText("UP");
            }else if(arrayList.get(position).isUpdated()==2){
                holder.up.setVisibility(View.VISIBLE);
                holder.up.setBackgroundResource(R.drawable.main_icon_dormant);
                holder.up.setText("휴재");
                holder.up.setTextColor(Color.parseColor("#5F5F5F"));
            }else{
                holder.up.setBackgroundResource(R.drawable.week_icon_cuttoon);
                holder.up.setText(null);
                holder.up.setVisibility(v.GONE);
            }
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                int id = v.getId();
                Intent intent;
                switch (id) {
                    case R.id.cardview:
                        if (position==0) {
                            /**
                             * Activity 연결부
                             */
                        if(day!=0){
                            intent = new Intent(mContext,WeekdayActivity.class);
                            intent.putExtra("requestDay", day-1);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            mContext.startActivity(intent);
                        }
                        } else {
                            Webtoon target = arrayList.get(position);
                            intent = new Intent(mContext, EpisodeActivity.class);
                            intent.putExtra("id", target.getId());
                            intent.putExtra("toontype", target.getToonType());
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            mContext.startActivity(intent);
                        }
                        break;
                    case R.id.latest:
                        Episode episode = coint_sqLiteManager.getLatestEpisode(arrayList.get(position).getId());
                        userInfo = (Application_UserInfo)mContext.getApplicationContext();
                        if(arrayList.get(position).isAdult()){
                            if(userInfo.isLogin()){
                                if(!userInfo.isUserAdult()){
                                    Toast.makeText(mContext, "만 19세 이상 시청 가능한 컨텐츠입니다.", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }else{
                                new AlertDialog.Builder(mContext)
                                        .setTitle("로그인")
                                        .setMessage("로그인이 필요한 서비스 입니다. 로그인 하시겠습니까?")
                                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                mContext.startActivity(new Intent(mContext, LoginActivity.class));
                                            }
                                        }).setNegativeButton("아니요", null).show();
                                break;
                            }

                        }
                        if(episode==null){
                            updateInstance = arrayList.get(position);
                            serverData = new GetServerData(mContext);
                            serverData.registerObserver(Main_MyToonAdapter.this);
                            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    serverData.removeObserver(Main_MyToonAdapter.this);
                                }
                            });
                            dialog.show();
                            serverData.getEpisodesFromServer(arrayList.get(position).getId());
                        }
                        else {
                            switch (arrayList.get(position).getToonType()) {
                                case 'G': {//일반툰
                                    Intent generalIntent = new Intent(mContext, ViewerGeneralActivity.class);
                                    generalIntent.putExtra("id", episode.getId());
                                    generalIntent.putExtra("ep_id", episode.getEpisode_id());
                                    generalIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    mContext.startActivity(generalIntent);
                                    break;
                                }
                                case 'C': {//컷툰
                                    Intent cutIntent = new Intent(mContext, ViewerCutActivity.class);
                                    cutIntent.putExtra("id", episode.getId());
                                    cutIntent.putExtra("ep_id", episode.getEpisode_id());
                                    cutIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    mContext.startActivity(cutIntent);
                                    break;
                                }
                                case 'S': {//스마트툰
                                    Intent smartIntent = new Intent(mContext, ViewerSmartActivity.class);
                                    smartIntent.putExtra("id", episode.getId());
                                    smartIntent.putExtra("ep_id", episode.getEpisode_id());
                                    smartIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    mContext.startActivity(smartIntent);
                                    break;
                                }
                                case 'M': {//모션툰
                                    Intent motionIntent = new Intent(mContext, ViewerMotionActivity.class);
                                    motionIntent.putExtra("id", episode.getId());
                                    motionIntent.putExtra("ep_id", episode.getEpisode_id());
                                    motionIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    mContext.startActivity(motionIntent);
                                    break;
                                }
                            }
                        }
                        break;
                }
            }
        };
        v.findViewById(R.id.cardview).setOnClickListener(onClickListener);
        v.findViewById(R.id.latest).setOnClickListener(onClickListener);

        setAnimation(holder.imageView, position);
    }

    @Override
    public void update(Observable observable, Object data) {
        dialog.dismiss();
        serverData.removeObserver(this);
        if(updateInstance != null){
            switch (updateInstance.getToonType()) {
                case 'G': {//일반툰
                    coint_sqLiteManager.updateEpisodeRead(updateInstance.getId(), 1);
                    Intent generalIntent = new Intent(mContext, ViewerGeneralActivity.class);
                    generalIntent.putExtra("id", updateInstance.getId());
                    generalIntent.putExtra("ep_id", 1);
                    generalIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    mContext.startActivity(generalIntent);
                    break;
                }
                case 'C': {//컷툰
                    coint_sqLiteManager.updateEpisodeRead(updateInstance.getId(), 1);
                    Intent cutIntent = new Intent(mContext, ViewerCutActivity.class);
                    cutIntent.putExtra("id", updateInstance.getId());
                    cutIntent.putExtra("ep_id", 1);
                    cutIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    mContext.startActivity(cutIntent);
                    break;
                }
                case 'S': {//스마트툰
                    coint_sqLiteManager.updateEpisodeRead(updateInstance.getId(), 1);
                    Intent smartIntent = new Intent(mContext, ViewerSmartActivity.class);
                    smartIntent.putExtra("id", updateInstance.getId());
                    smartIntent.putExtra("ep_id", 1);
                    smartIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    mContext.startActivity(smartIntent);
                    break;
                }
                case 'M': {//모션툰
                    coint_sqLiteManager.updateEpisodeRead(updateInstance.getId(), 1);
                    Intent motionIntent = new Intent(mContext, ViewerMotionActivity.class);
                    motionIntent.putExtra("id", updateInstance.getId());
                    motionIntent.putExtra("ep_id", 1);
                    motionIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    mContext.startActivity(motionIntent);
                    break;
                }
            }
        }
    }

    public int getItemCount() {
        return arrayList.size();
    }

    public void addRemoveItem(final ArrayList<Webtoon> list) {

        ArrayList<Webtoon> temparr = new ArrayList<>();
        temparr.clear();
        arrayList.clear();
        //여기 position0에 들어가는 웹툰 따로 넣고 돌리기
        Webtoon resultQuery = new Webtoon();
        resultQuery.setId(0);
        resultQuery.setTitle("더 보기");
        resultQuery.setArtist("");
        resultQuery.setThumbURL("");
        resultQuery.setStarScore(0.0f);
        arrayList.add(resultQuery);

        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < weekdayList.size(); j++) {
                if(list.get(i).getId()==weekdayList.get(j).getId()){
                    arrayList.add(list.get(i));
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void setAnimation(View viewToAnimate, int position) {
        // 새로 보여지는 뷰라면 애니메이션을 해줍니다
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_out_right);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
