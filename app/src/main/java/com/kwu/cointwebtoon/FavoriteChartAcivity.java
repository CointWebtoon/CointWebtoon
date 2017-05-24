package com.kwu.cointwebtoon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.kwu.cointwebtoon.DataStructure.Episode;
import com.kwu.cointwebtoon.DataStructure.Genre;
import com.kwu.cointwebtoon.DataStructure.Webtoon;

import java.util.ArrayList;

public class FavoriteChartAcivity extends TypeKitActivity {
    private RelativeLayout favoriteLayout;
    private Context mContext;
    private PieChart pieChart;
    private FavoriteAdpater favoriteAdpater;
    private ViewPager pager;
    private float[] yData = {0,0,0,0,0,0,0,0,0,0};
    private int[] count = {0,0,0,0,0,0,0,0,0,0};
    private String[] xData = {"코믹", "일상", "순정", "스릴러", "판타지", "역사", "드라마", "스포츠", "액션", "기타"};
    private float find = 0;
    private int getGenre = 0;
    private String genre = "";
    private COINT_SQLiteManager coint_sqLiteManager;
    private CointProgressDialog cointProgressDialog;
    private Application_UserInfo userInfo;
    private ArrayList<Webtoon> one = new ArrayList<>(), two = new ArrayList<>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_chart_activity);
        mContext = this;

        cointProgressDialog = new CointProgressDialog(mContext);
        cointProgressDialog.show();

        userInfo = (Application_UserInfo)getApplication();

        favoriteLayout = (RelativeLayout) findViewById(R.id.favoriteChart);
        pieChart = (PieChart) findViewById(R.id.pieChart);
        //add pie chart to layout

        favoriteLayout.setBackgroundColor(Color.WHITE);

        //configure pie chart
        pieChart.setUsePercentValues(true);

        //enable hole and configure
        pieChart.setDescription("");
        pieChart.setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(1); //왜 없어 여긴
        pieChart.setHoleRadius(7);
        pieChart.setTransparentCircleRadius(10);

        //enable rotation of the chart by touch
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);

        //set a chart value selected listener
/*        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                // display msg when value selected
                if (entry == null)
                    return;
                else
                    return;
                //startActivity(new Intent(mContext, GenreActivity.class));
                //누르면 해당 장르로 이동!!!하도록 구현
            }

            @Override
            public void onNothingSelected() {
            }
        });*/
        SettingChart settingChart = new SettingChart();
        settingChart.execute(1);

        //customize legends
        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);
    }

    public void moveToGenre(View view){
        Intent intent = new Intent(this, GenreActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    public void onClick(View view) {
        Webtoon target = (Webtoon)view.getTag();
        Webtoon emptyItem = new Webtoon(0, "", "", 0.0f, 0, "", 0, 'U', false, false, false, 0);
        if(emptyItem.equals(target) | target == null)
            return;
        if(target.isAdult()){
            if(userInfo.isLogin()){
                if(!userInfo.isUserAdult()){
                    Toast.makeText(this, "만 19세 이상 시청 가능한 컨텐츠입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }else{
                new AlertDialog.Builder(this)
                        .setTitle("로그인")
                        .setMessage("로그인이 필요한 서비스 입니다. 로그인 하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(mContext, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                            }
                        }).setNegativeButton("아니요", null).show();
                return;
            }
        }
        String result = coint_sqLiteManager.updateMyWebtoon(String.valueOf(target.getId()));
        Toast.makeText(mContext, target.getTitle() + " " + result,Toast.LENGTH_LONG).show();
        if(result == "마이 웹툰 설정"){
            target.setIs_mine(true);
        }else{
            target.setIs_mine(false);
        }
        favoriteAdpater.notifyDataSetChanged();
    }


    private class SettingChart extends AsyncTask<Integer, Integer, Integer>{
        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            ArrayList<Entry> yVals1 = new ArrayList<>();

            int totalread = 0;
            for(int i=0;i<count.length;i++){
                totalread+=count[i];
            }

            if(totalread==0){
                Toast.makeText(mContext,"좋아하는 웹툰을 즐겨찾기 해보세요",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(mContext, GenreActivity.class));
                finish();
            }else {

                for(int i=0;i<count.length;i++){
                    yData[i] = (float)count[i]/totalread*100;
                }

                for (int i = 0; i < yData.length; i++){
                    if(yData[i]>0){
                        yVals1.add(new Entry(yData[i], i));
                        if(find< yData[i]){
                            getGenre = i;
                        }
                    }
                }

                ArrayList<String> xVals = new ArrayList<>();

                for (int i = 0; i < xData.length; i++)
                    xVals.add(xData[i]);

                //create pie data set
                PieDataSet dataSet = new PieDataSet(yVals1, "Favorite Chart");
                dataSet.setSliceSpace(1);
                dataSet.setSelectionShift(5);

                //add many colors
                ArrayList<Integer> colors = new ArrayList<>();

                for (int c : ColorTemplate.VORDIPLOM_COLORS)
                    colors.add(c);

                for (int c : ColorTemplate.JOYFUL_COLORS)
                    colors.add(c);

                for (int c : ColorTemplate.COLORFUL_COLORS)
                    colors.add(c);

                for (int c : ColorTemplate.LIBERTY_COLORS)
                    colors.add(c);

                for (int c : ColorTemplate.PASTEL_COLORS)
                    colors.add(c);

                colors.add(ColorTemplate.getHoloBlue());
                dataSet.setColors(colors);

                //instantiate pie data object now
                PieData data = new PieData(xVals, dataSet);
                data.setValueFormatter(new PercentFormatter());
                data.setValueTextSize(14f);
                data.setValueTextColor(Color.parseColor("#5f5f5f"));

                pieChart.setData(data);

                //undo all highlights
                pieChart.highlightValues(null);

                //update pie chart
                pieChart.invalidate();

                switch (getGenre){
                    //COMIC DAILY PURE THRILL FANTASY HISTORICAL DRAMA SPORTS ACTION
                    case 0:
                        genre = "COMIC";
                        break;
                    case 1:
                        genre = "DAILY";
                        break;
                    case 2:
                        genre = "PURE";
                        break;
                    case 3:
                        genre = "THRILL";
                        break;
                    case 4:
                        genre = "FANTASY";
                        break;
                    case 5:
                        genre = "HISTORICAL";
                        break;
                    case 6:
                        genre = "DRAMA";
                        break;
                    case 7:
                        genre = "SPORTS";
                        break;
                    case 8:
                        genre = "ACTION";
                        break;
                    case 9:
                        genre = "COMIC";
                        break;
                }

                genre = "SPORTS";

                new UpdateViewPager().execute();
            }
        }

        @Override
        protected Integer doInBackground(Integer... params) {

            int genreIndex=0;
            coint_sqLiteManager = COINT_SQLiteManager.getInstance(mContext);
            ArrayList<Genre> genreArrayList;
            genreArrayList = coint_sqLiteManager.getGenreAll();
            for(int i=0;i<genreArrayList.size();i++){
                if(genreArrayList.get(i).getGenre().equals("COMIC")){
                    genreIndex = 0;
                }else if(genreArrayList.get(i).getGenre().equals("DAILY")){
                    genreIndex = 1;
                }else if(genreArrayList.get(i).getGenre().equals("PURE")){
                    genreIndex = 2;
                }else if(genreArrayList.get(i).getGenre().equals("THRILL")){
                    genreIndex = 3;
                }else if(genreArrayList.get(i).getGenre().equals("FANTASY")){
                    genreIndex = 4;
                }else if(genreArrayList.get(i).getGenre().equals("HISTORICAL")){
                    genreIndex = 5;
                }else if(genreArrayList.get(i).getGenre().equals("DRAMA")){
                    genreIndex = 6;
                }else if(genreArrayList.get(i).getGenre().equals("SPORTS")){
                    genreIndex = 7;
                }else if(genreArrayList.get(i).getGenre().equals("ACTION")){
                    genreIndex = 8;
                }else{
                    genreIndex = 9;
                }

                Cursor cursor = coint_sqLiteManager.getEpisodes(genreArrayList.get(i).getId());
                while(cursor.moveToNext()){
                    if(cursor.getInt(8)==1){
                        count[genreIndex]++;
                    }
                }
                cursor.close();
            }
            return 1;
        }
    }

    private class UpdateViewPager extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {
            one.clear();
            two.clear();

            Webtoon emptyItem = new Webtoon(0, "", "", 0.0f, 0, "", 0, 'U', false, false, false, 0);

            Cursor page1 = coint_sqLiteManager.favorite(0, genre);
            Cursor page2 = coint_sqLiteManager.favorite(1, genre);

            while(page1.moveToNext()){
                one.add(new Webtoon(page1.getInt(0), page1.getString(1), page1.getString(2), page1.getFloat(3),
                        page1.getInt(4), page1.getString(5), page1.getInt(6), page1.getString(7).charAt(0), page1.getInt(8)==1?true:false,
                        page1.getInt(9)==1?true:false, page1.getInt(10)==1?true:false, page1.getInt(11)));
            }

            while(page2.moveToNext()){
                two.add(new Webtoon(page2.getInt(0), page2.getString(1), page2.getString(2), page2.getFloat(3),
                        page2.getInt(4), page2.getString(5), page2.getInt(6), page2.getString(7).charAt(0), page2.getInt(8)==1?true:false,
                        page2.getInt(9)==1?true:false, page2.getInt(10)==1?true:false, page2.getInt(11)));
            }

            int page1Count = one.size(), page2Count = two.size();

            Log.i("coint", String.valueOf(page1Count) + " " + String.valueOf(two.size()));

            if(page1Count < 3){
                for(int i = 0 ; i < 3 - page1Count; i++){
                    one.add(emptyItem);
                }
            }
            if(page2Count < 3){
                for(int i = 0 ; i < 3 - page2Count; i++){
                    two.add(emptyItem);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pager = (ViewPager)findViewById(R.id.favoriteViewPager);                        //뷰페이저에 어댑터를 연결하는 부분
            favoriteAdpater = new FavoriteAdpater(mContext, one, two);
            pager.setAdapter(favoriteAdpater);
            cointProgressDialog.dismiss();
        }
    }
}
