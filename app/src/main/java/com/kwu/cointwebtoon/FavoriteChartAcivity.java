package com.kwu.cointwebtoon;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;

public class FavoriteChartAcivity extends TypeKitActivity {
    private RelativeLayout favoriteLayout;
    private Context mContext;
    private PieChart pieChart;
    private float[] yData = {0,0,0,0,0,0,0,0,0,0};
    private int[] count = {0,0,0,0,0,0,0,0,0,0};
    private String[] xData = {"코믹", "일상", "순정", "스릴러", "판타지", "역사", "드라마", "스포츠", "액션", "기타"};
    private COINT_SQLiteManager coint_sqLiteManager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_chart_activity);
        mContext = this;

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
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                // display msg when value selected
                if (entry == null)
                    return;
                //startActivity(new Intent(mContext, GenreActivity.class));
                //누르면 해당 장르로 이동!!!하도록 구현
            }

            @Override
            public void onNothingSelected() {
            }
        });

        addData();

        //customize legends
        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);
    }

    private void addData() {
        ArrayList<Entry> yVals1 = new ArrayList<>();
        int genreIndex=0;
        coint_sqLiteManager = COINT_SQLiteManager.getInstance(mContext);
        ArrayList<Genre> genreArrayList;
        genreArrayList = coint_sqLiteManager.getGenreAll();
        for(int i=0;i<genreArrayList.size();i++){
            Log.i("GENRE : " ,genreArrayList.get(i).getGenre() );
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
            data.setValueTextSize(11f);
            data.setValueTextColor(Color.BLACK);

            pieChart.setData(data);

            //undo all highlights
            pieChart.highlightValues(null);

            //update pie chart
            pieChart.invalidate();
        }
    }

}
