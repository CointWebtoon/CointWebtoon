package com.kwu.cointwebtoon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kwu.cointwebtoon.DataStructure.Weekday_ListItem;
import com.kwu.cointwebtoon.databinding.WeekdayActivityBinding;

import java.util.Calendar;

public class WeekdayActivity extends TypeKitActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnKeyListener, View.OnClickListener {
    private WeekdayActivityBinding binding;
    private int selectedDay = 0;
    static public Weekday_ListItem[] listItems = new Weekday_ListItem[7];
    private Button[] btnWeekdays = new Button[8];

    private Button navHeader;
    private TextView navStatus;
    private Application_UserInfo userInfo;
    private EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.weekday_activity);

        /**
         * Nav 공통 요소
         */
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//키보드 숨김
        setSupportActionBar(binding.toolbar);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportActionBar().setDisplayShowTitleEnabled(false);            // 액션바에서 앱 이름 보이지 않게 함
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        View headerview = navigationView.getHeaderView(0);


        navStatus = (TextView) headerview.findViewById(R.id.nav_status);
        navHeader = (Button) headerview.findViewById(R.id.nav_login);
        userInfo = (Application_UserInfo) getApplication();
        navHeader.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (userInfo.isLogin()) {
                    userInfo.onLogOut(WeekdayActivity.this);
                    navHeader.setBackgroundResource(R.drawable.login);
                    navStatus.setText("로그인 해주세요");
                } else {
                    Intent intent = new Intent(WeekdayActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    drawer.closeDrawer(GravityCompat.START);
                }
            }
        });
        search = (EditText) findViewById(R.id.searchbar);
        search.setOnKeyListener(this);
        /**
         * Nav 공통 요소 end
         */

        btnWeekdays[0] = binding.btnMon;
        btnWeekdays[1] = binding.btnTue;
        btnWeekdays[2] = binding.btnWed;
        btnWeekdays[3] = binding.btnThur;
        btnWeekdays[4] = binding.btnFri;
        btnWeekdays[5] = binding.btnSat;
        btnWeekdays[6] = binding.btnSun;
        btnWeekdays[7] = binding.btnMy;

        Log.d("INTENT", "StartActivity");
        Intent intent = getIntent();
        if (intent.getExtras() == null) {
            //현재 요일 설정
            Log.d("INTENT", "Not Empty");
            Calendar calendar = Calendar.getInstance();
            selectedDay = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7;
        } else {
            //요청 요일 설정
            Log.d("INTENT", "Empty");
            selectedDay = intent.getExtras().getInt("requestDay");
        }
        changeBtnDayState(btnWeekdays[selectedDay], true);

        //dayButtons(요일별 버튼) 태그 설정
        for (int i = 0; i < 8; i++) {
            btnWeekdays[i].setTag(i);
        }

        //listItem 생성
        for (int i = 0; i < 7; i++) {
            listItems[i] = new Weekday_ListItem(this, i + 1);
        }

        //viewPager Adapter, PageTransformer 설정
        binding.viewPager.setAdapter(new FSPagerAdapter(getSupportFragmentManager()));
        //binding.viewPager.setPageTransformer(true, new WeekdayTransformer());
        binding.viewPager.setPagingEnabled(false);
        binding.viewPager.addOnPageChangeListener(new OPCListener());
        binding.viewPager.setCurrentItem(selectedDay);

    }


    private class OPCListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            changeBtnDayState(btnWeekdays[selectedDay], false);
            selectedDay = position;
            changeBtnDayState(btnWeekdays[selectedDay], true);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    public void btnWeekdaysOnClick(View v) {
        int tag = (int) v.getTag();
        binding.viewPager.setCurrentItem(tag);
    }

    public void btnMyOnClick(View v) {
        finish();
    }

    public void setButtonTextAppearance(Context context, Button button, int resId) {
        if (Build.VERSION.SDK_INT < 23) {
            button.setTextAppearance(context, resId);
        } else {
            button.setTextAppearance(resId);
        }
    }

    public void changeBtnDayState(Button button, boolean state) {
        if (state) {
            button.setBackgroundResource(R.drawable.week_round_button2);
            setButtonTextAppearance(WeekdayActivity.this, button, android.R.style.TextAppearance_DeviceDefault_Medium);
            button.setTextColor(Color.parseColor("#ffffff"));
        } else {
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

    /**
     * Nav 공통 요소
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {     // 검색 메뉴 만들어주는 부분
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_search_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {       // 검색 누르면 실행
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;
        String searchString = search.getText().toString();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            if (searchString.equals("")) {
            } else {
                intent = new Intent(WeekdayActivity.this, SearchActivity.class);
                intent.putExtra("Intent", search.getText().toString());
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                finish();
                startActivity(intent);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {        // navigation drawer에 있는 메뉴 선택시 실행
        // Handle navigation view item clicks here.
        Intent intent;
        int id = item.getItemId();

        switch (id) {
            case R.id.webtoonRanking:
                intent = new Intent(WeekdayActivity.this, Top100Activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
            case R.id.moreMyWebtoon:
                intent = new Intent(WeekdayActivity.this, MyWebtoonActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
            case R.id.personalFavorite:
                intent = new Intent(WeekdayActivity.this, FavoriteChartAcivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
            case R.id.customService:
                new AlertDialog.Builder(this)
                        .setTitle("코인트 고객센터")
                        .setMessage("광운대학교\n" +
                                "서울특별시 노원구 광운로 20\n" +
                                "융합SW교육혁신추진단\n(새빛관 404호)\n" +
                                "Tel : 02-940-5654\nE-Mail : syjin@kw.ac.kr\n")
                        .setPositiveButton("닫기", null)
                        .show();
                break;
            case R.id.error:
                new AlertDialog.Builder(this)
                        .setTitle("오류 신고")
                        .setMessage("광운대학교\n" +
                                "컴퓨터 소프트웨어학과\nTEAM COINT 팀장 최은주\n" +
                                "E-Mail : epcej0020@gmail.com\n")
                        .setPositiveButton("닫기", null)
                        .show();
                break;
            case R.id.appInfo:
                AppInfoDialog dialog = new AppInfoDialog(this);
                dialog.show();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onClick(View view) {        // 베스트도전은 웹뷰로 띄우고, 나머지는 액티비티
        int id = view.getId();
        Intent intent;

        switch (id) {
            case R.id.action_search:
                String searchString = search.getText().toString();
                if (searchString.equals("")) {
                } else {
                    intent = new Intent(WeekdayActivity.this, SearchActivity.class);
                    intent.putExtra("Intent", search.getText().toString());
                    finish();
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
                break;
            case R.id.genre_floating_home:
                intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finishAffinity();
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
            Log.i("epcej", "ENTER");
            String searchString = search.getText().toString();
            if (searchString.equals("")) {
            } else {
                Intent intent = new Intent(WeekdayActivity.this, SearchActivity.class);
                intent.putExtra("Intent", search.getText().toString());
                finish();
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
            return true;
        }
        return false;
    }
    /**
     * Nav 공통 요소 end
     */
}
