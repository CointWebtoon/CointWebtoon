package com.kwu.cointwebtoon;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kwu.cointwebtoon.DataStructure.Weekday_ListItem;
import com.kwu.cointwebtoon.databinding.WeekdayActivityBinding;

public class WeekdayActivity extends TypeKitActivity {
    WeekdayActivityBinding binding;
    int selectedDay = 0;
    static public Weekday_ListItem[] listItems = new Weekday_ListItem[8];
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
        btnWeekdays[7] = binding.btnNew;

        //dayButtons(요일별 버튼) 태그 설정
        for(int i = 0; i < 8; i++){
            btnWeekdays[i].setTag(i);
        }

        //listItem 생성
        for(int i = 0; i<8; i++){
          listItems[i] =  new Weekday_ListItem(this);
          listItems[i].generateList(i);
        }

        //viewPager Adapter, PageTransformer 설정
        binding.viewPager.setAdapter(new FSPagerAdapter(getSupportFragmentManager()));
        binding.viewPager.setPageTransformer(true, new WeekdayTransformer());
        binding.viewPager.addOnPageChangeListener(new OPCListener());
    }

    private class OPCListener implements ViewPager.OnPageChangeListener{
        @Override
        public void onPageSelected(int position) {
            btnWeekdays[selectedDay].setBackgroundResource(R.drawable.week_round_button1);
            btnWeekdays[selectedDay].setTextAppearance(WeekdayActivity.this, android.R.style.TextAppearance_DeviceDefault_Small);
            btnWeekdays[selectedDay].setTextColor(Color.parseColor("#000000"));

            selectedDay = position;

            btnWeekdays[selectedDay].setBackgroundResource(R.drawable.week_round_button2);
            btnWeekdays[selectedDay].setTextAppearance(WeekdayActivity.this, android.R.style.TextAppearance_DeviceDefault_Medium);
            btnWeekdays[selectedDay].setTextColor(Color.parseColor("#ffffff"));
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
            return 8;
        }
    }

}
