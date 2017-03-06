package com.jmapplication.com.episodeactivity;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by jm on 2017-03-05.
 */

public class EpisodeActivityWithRecycler extends AppCompatActivity implements Observer {
    private RecyclerView recycler;
    private ArrayList<Episode> episodes = new ArrayList<>();
    private COINT_SQLiteManager manager;
    private GetServerData getServerData;
    private int currentToonId = 20853;
    private boolean isFirst = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("마음의 소리");
        setContentView(R.layout.recycleractivity);
        recycler = (RecyclerView) findViewById(R.id.episode_recycler);
        recycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            private int mLastFirstVisibleItem = 0;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
                final int currentFirstVisibleItem = layoutManager.findFirstVisibleItemPosition();

                if (currentFirstVisibleItem > this.mLastFirstVisibleItem) {
                    EpisodeActivityWithRecycler.this.getSupportActionBar().hide();
                } else if (currentFirstVisibleItem < this.mLastFirstVisibleItem) {
                    EpisodeActivityWithRecycler.this.getSupportActionBar().show();
                }

                this.mLastFirstVisibleItem = currentFirstVisibleItem;
            }
        });
        manager = COINT_SQLiteManager.getInstance(this);
        getServerData = new GetServerData(this);
        getServerData.registerObserver(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        new Thread(){
            public void run(){
                Cursor episodeCursor = manager.getEpisodes(currentToonId);
                if(episodeCursor.getCount() < 1){
                    //프로그레스바 동작하게 하자
                    getServerData.getEpisodesFromServer(currentToonId);
                }else {
                    //일단 Cursor에 있는 데이터를 띄움
                    updateCursorFromSQLite(episodeCursor);
                    getServerData.getEpisodesFromServer(currentToonId);
                }
            }
        }.start();
    }

    private void updateCursorFromSQLite(Cursor episodeCursor){
        episodes.clear();
        int id_E, episode_id, likes_E, is_read;
        String episode_title, ep_thumburl, reg_date, mention;
        float ep_starscore;
        Episode episode;
        while(episodeCursor.moveToNext()){
            id_E = episodeCursor.getInt(0);
            episode_id = episodeCursor.getInt(1);
            episode_title = episodeCursor.getString(2);
            ep_starscore = episodeCursor.getFloat(3);
            ep_thumburl = episodeCursor.getString(4);
            reg_date = episodeCursor.getString(5);
            mention = episodeCursor.getString(6);
            likes_E = episodeCursor.getInt(7);
            is_read = episodeCursor.getInt(8);

            //Log.i("episodeData", String.valueOf(id_E) + " " +  String.valueOf(episode_id) + " " + episode_title + " " + String.valueOf(ep_starscore) + " " + ep_thumburl + " " + reg_date + " " + mention + " " + is_read);

            episode = new Episode(id_E, episode_id, episode_title, ep_starscore, ep_thumburl, reg_date, mention, likes_E, is_read);
            episodes.add(episode);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recycler.setAdapter(new RecyclerAdapter(EpisodeActivityWithRecycler.this, episodes));
            }
        });
    }

    @Override
    public void update(Observable observable, Object data) {
        Cursor episodeCursor = manager.getEpisodes(currentToonId);
        updateCursorFromSQLite(episodeCursor);
        if(isFirst){
            int readNumber;
            for(readNumber = 0 ; readNumber < episodes.size(); readNumber++){
                if(episodes.get(readNumber).getIs_read() == 1)
                    break;
            }
            if(readNumber > 0 && readNumber < episodes.size())
                readNumber--;
            else if(readNumber >= episodes.size())
                readNumber = 0;
            recycler.getLayoutManager().scrollToPosition(readNumber);
            isFirst = false;
        }
        Log.i("ProgressReport", "Cursor Updated From Server 총 , " + String.valueOf(episodeCursor.getCount()) + "개의 Row");
    }

    @Override
    protected void onDestroy() {
        getServerData.removeObserver(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.episode_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        this.finish();
        return super.onOptionsItemSelected(item);
    }

    public void onRecyclerViewItemClick(View v){
        Episode tag = (Episode)v.findViewById(R.id.reg_date).getTag();
        if(tag != null){
            Toast.makeText(this, String.valueOf(tag.getEpisode_id()) + " " + String.valueOf(tag.getIs_read()), Toast.LENGTH_SHORT).show();
        }
        manager.updateEpisodeRead(tag.getId(), tag.getEpisode_id());
        updateCursorFromSQLite(manager.getEpisodes(currentToonId));
    }

    public void onRecyclerViewLongClick(View v){
        recycler.getLayoutManager().scrollToPosition(recycler.getAdapter().getItemCount() - 1);
    }

    ////////////////////////ADAPTER////////////////////////
    private  class RecyclerAdapter extends RecyclerView.Adapter{
        private Context mContext;
        private ArrayList<Episode> episodes;
        private LayoutInflater inflater;

        private RecyclerAdapter(Context mContext, ArrayList<Episode> episodes){
            this.mContext = mContext;
            this.episodes = episodes;
            inflater = LayoutInflater.from(mContext);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.recycler_item, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewHolder myHolder = (ViewHolder)holder;
            Episode currentItem = episodes.get(position);
            myHolder.background.setBackgroundColor(Color.parseColor("#FFFFFF"));
            if(currentItem.getIs_read() == 1)
                myHolder.background.setBackgroundColor(Color.parseColor("#55000000"));
            Glide.with(mContext)
                    .load(currentItem.getEp_thumbURL())
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .fitCenter()
                    .placeholder(R.drawable.logo)
                    .into(myHolder.thumb);
            myHolder.epTitle.setText(currentItem.getEpisode_title());
            myHolder.regDate.setTag(currentItem);
            myHolder.starScore.setText(String.valueOf(currentItem.getEp_starScore()));
            myHolder.regDate.setText(currentItem.getReg_date());
        }

        @Override
        public int getItemCount() {
            return episodes.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public ImageView thumb;
            public TextView epTitle;
            public TextView regDate;
            public TextView starScore;
            public CardView cardView;
            public RelativeLayout background;
            public View view;
            public ViewHolder(View view){
                super(view);
                this.view = view;
                background = (RelativeLayout)view.findViewById(R.id.backgroundRelative) ;
                cardView = (CardView)view.findViewById(R.id.cardView);
                thumb = (ImageView)view.findViewById(R.id.thumb);
                epTitle = (TextView)view.findViewById(R.id.episode_Title);
                regDate = (TextView)view.findViewById(R.id.reg_date);
                starScore = (TextView)view.findViewById(R.id.episode_Starscore);

                epTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                epTitle.setSingleLine(true);
                epTitle.setMarqueeRepeatLimit(5);
                epTitle.setSelected(true);
            }
        }
    }
}
