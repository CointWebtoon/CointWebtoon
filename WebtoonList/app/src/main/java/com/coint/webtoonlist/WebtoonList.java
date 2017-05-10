package com.coint.webtoonlist;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.coint.webtoonlist.databinding.ActivityWebtoonListBinding;
import com.coint.webtoonlist.sqlite.GetServerData;
import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;



public class WebtoonList extends AppCompatActivity {

    ActivityWebtoonListBinding binding;

    int selectedDay = 0;
    static public ListItem[] listItems = new ListItem[8];
    private Button[] btnWeekdays = new Button[8];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_webtoon_list);
        btnWeekdays[0] = binding.btnMon;
        btnWeekdays[1] = binding.btnTue;
        btnWeekdays[2] = binding.btnWed;
        btnWeekdays[3] = binding.btnThur;
        btnWeekdays[4] = binding.btnFri;
        btnWeekdays[5] = binding.btnSat;
        btnWeekdays[6] = binding.btnSun;
        btnWeekdays[7] = binding.btnNew;


        //폰트 적용
        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "NanumGothic.ttf"))
                .addBold(Typekit.createFromAsset(this, "NanumGothicBold.ttf"));

        //DB 생성
        GetServerData getServerData = new GetServerData(this);
        getServerData.getWebtoonFromServer();

        //dayButtons(요일별 버튼) 태그 설정
        for(int i = 0; i < 8; i++){
            btnWeekdays[i].setTag(i);
        }

        //listItem 생성
        for(int i = 0; i<8; i++){
          listItems[i] =  new ListItem(getApplicationContext());
          listItems[i].generateList(i);
        }

        //viewPager Adapter, PageTransformer 설정
        binding.viewPager.setAdapter(new FSPagerAdapter(getSupportFragmentManager()));
        binding.viewPager.setPageTransformer(true, new mPageTransformer());
        binding.viewPager.addOnPageChangeListener(new OPCListener());
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    private class OPCListener implements ViewPager.OnPageChangeListener{
        @Override
        public void onPageSelected(int position) {
            btnWeekdays[selectedDay].setBackgroundResource(R.drawable.round_button1);
            btnWeekdays[selectedDay].setTextAppearance(android.R.style.TextAppearance_DeviceDefault_Small);
            btnWeekdays[selectedDay].setTextColor(Color.parseColor("#000000"));

            selectedDay = position;

            btnWeekdays[selectedDay].setBackgroundResource(R.drawable.round_button2);
            btnWeekdays[selectedDay].setTextAppearance(android.R.style.TextAppearance_DeviceDefault_Medium);
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


    private class FSPagerAdapter extends FragmentStatePagerAdapter{
        public FSPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new Day1Fragment();
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
