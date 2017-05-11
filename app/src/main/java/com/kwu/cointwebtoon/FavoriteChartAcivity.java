package com.kwu.cointwebtoon;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class FavoriteChartAcivity extends TypeKitActivity {
    private RelativeLayout favoriteLayout;
    private PieChart pieChart;
    private  float[] yData = {5,10,15,20,30,40};
    private String[] xData = {"A","B","C","D","E","F"};

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_chart_activity);

        favoriteLayout = (RelativeLayout)findViewById(R.id.favoriteChart);
        pieChart = (PieChart)findViewById(R.id.pieChart);
        //add pie chart to layout

        favoriteLayout.setBackgroundColor(Color.WHITE);

        //configure pie chart
        pieChart.setUsePercentValues(true);
        pieChart.setDescription("Chart Test");

        //enable hole and configure
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
                if(entry==null)
                    return;

                Toast.makeText(FavoriteChartAcivity.this, xData[entry.getXIndex()]+"="+entry.getVal()+"%",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        addData();

        //customize legends
/*        Legend legend = pieChart.getLegend();
        legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        legend.setXEntrySpace(7);
        legend.setYEntrySpace(5);*/
    }

    private void addData(){
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for(int i=0;i<yData.length;i++)
            yVals1.add(new Entry(yData[i],i));

        ArrayList<String> xVals = new ArrayList<String>();

        for(int i=0;i<xData.length;i++)
            xVals.add(xData[i]);

        //create pie data set
        PieDataSet dataSet  = new PieDataSet(yVals1, "TestTesT");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);

        //add many colors
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c: ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c: ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c: ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c: ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c: ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        //instantiate pie data object now
        PieData data = new PieData(xVals,dataSet);
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
