package com.kwu.cointwebtoon;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v4.widget.TextViewCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kwu.cointwebtoon.DataStructure.Weekday_ListItem;
import com.kwu.cointwebtoon.databinding.WeekdayActivityBinding;

import java.util.Calendar;

public class WeekdayActivity extends TypeKitActivity {
    private WeekdayActivityBinding binding;
    private int selectedDay = 0;
    static public Weekday_ListItem[] listItems = new Weekday_ListItem[7];
    private Button[] btnWeekdays = new Button[8];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.weekday_activity);
        btnWeekdays[0] = binding.btnMon;
        btnWeekdays[1] = binding.btnTue;
        btnWeekdays[2] = binding.btnWed;
        btnWeekdays[3] = binding.btnThur;
        btnWeekdays[4] = binding.btnFri;
        btnWeekdays[5] = binding.btnSat;
        btnWeekdays[6] = binding.btnSun;
        btnWeekdays[7] = binding.btnMy;

        //현재 요일 설정
        Calendar calendar = Calendar.getInstance();
        selectedDay = (calendar.get(Calendar.DAY_OF_WEEK) + 5)%7;
        changeBtnDayState(btnWeekdays[selectedDay], true);

        //dayButtons(요일별 버튼) 태그 설정
        for(int i = 0; i < 8; i++){
            btnWeekdays[i].setTag(i);
        }

        //listItem 생성
        for(int i = 0; i<7; i++){
          listItems[i] =  new Weekday_ListItem(this, i+1);
        }

        //viewPager Adapter, PageTransformer 설정

        binding.viewPager.setAdapter(new FSPagerAdapter(getSupportFragmentManager()));
        //binding.viewPager.setPageTransformer(true, new WeekdayTransformer());
        binding.viewPager.setPagingEnabled(false);
        binding.viewPager.addOnPageChangeListener(new OPCListener());
        binding.viewPager.setCurrentItem(selectedDay);

    }


    private class OPCListener implements ViewPager.OnPageChangeListener{
        @Override
        public void onPageSelected(int position) {
            changeBtnDayState(btnWeekdays[selectedDay], false);
            selectedDay = position;
            changeBtnDayState(btnWeekdays[selectedDay], true);
        }
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
        @Override
        public void onPageScrollStateChanged(int state) {}
    }

    public void btnWeekdaysOnClick(View v){
        int tag = (int)v.getTag();
        binding.viewPager.setCurrentItem(tag);
    }
    public void btnMyOnClick(View v){
        Toast.makeText(getBaseContext(), "MY 메뉴로 이동", Toast.LENGTH_SHORT).show();
        //TODO-MY 메뉴로 이동
    }

    public void setButtonTextAppearance(Context context, Button button,  int resId){
        if (Build.VERSION.SDK_INT < 23) {
            button.setTextAppearance(context, resId);
        } else {
            button.setTextAppearance(resId);
        }
    }

    public void changeBtnDayState(Button button, boolean state){
        if(state){
            button.setBackgroundResource(R.drawable.week_round_button2);
            setButtonTextAppearance(WeekdayActivity.this, button, android.R.style.TextAppearance_DeviceDefault_Medium);
            button.setTextColor(Color.parseColor("#ffffff"));
        }
        else {
            button.setBackgroundResource(R.drawable.week_round_button1);
            setButtonTextAppearance(WeekdayActivity.this, button, android.R.style.TextAppearance_DeviceDefault_Small);
            button.setTextColor(Color.parseColor("#000000"));
        }
    }
    private class FSPagerAdapter extends FragmentStatePagerAdapter {
        public FSPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new WeekdayFragment();
            Bundle bundle = new Bundle(1);
            bundle.putInt("position", position);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return 7;
        }
    }

}
