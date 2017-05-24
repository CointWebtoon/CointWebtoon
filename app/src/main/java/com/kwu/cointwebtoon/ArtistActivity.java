package com.kwu.cointwebtoon;

import android.app.AlertDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kwu.cointwebtoon.DataStructure.Webtoon;
import com.kwu.cointwebtoon.DataStructure.Weekday_ListItem;
import com.kwu.cointwebtoon.Views.FastScrollRecyclerViewItemDecoration;
import com.kwu.cointwebtoon.databinding.ArtistActivityBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class ArtistActivity extends TypeKitActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnKeyListener, View.OnClickListener {
    private final static char[] KO_INIT_S =
            {
                    'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ',
                    'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
            }; // 19
    private final static char[] KO_INIT_M =
            {
                    'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ', 'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ',
                    'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ'
            }; // 21
    private final static char[] KO_INIT_E =
            {
                    0, 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ',
                    'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
            }; // 28

    ArtistActivityBinding binding;

    private Button navHeader;
    private TextView navStatus;
    private Application_UserInfo userInfo;
    private EditText search;


    private ArrayList<Webtoon> dataSet;
    private ArtistActivityAdapter artistAdapter;
    private RecyclerView.LayoutManager artistLayoutManager;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.artist_activity);

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
        navHeader.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (userInfo.isLogin()) {
                    userInfo.onLogOut(ArtistActivity.this);
                    Toast.makeText(ArtistActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                    navHeader.setBackgroundResource(R.drawable.login);
                    navStatus.setText("로그인 해주세요");
                } else {
                    Intent intent = new Intent(ArtistActivity.this, LoginActivity.class);
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

        //dataSet 생성 및 업데이트
        dataSet = new ArrayList<>();
        this.updateDataset();


        //Recycler view 설정
        binding.artistRecyclerView.setHasFixedSize(true);

        //Layout manager 설정
        artistLayoutManager = new LinearLayoutManager(this);
        binding.artistRecyclerView.setLayoutManager(artistLayoutManager);

        //Adapter 설정
        HashMap<String, Integer> mapIndex = calculateIndexesForName(dataSet);
        for (int i = 0; i < dataSet.size(); i++) {
            Log.d("onCreate", dataSet.get(i).getArtist());
        }
        artistAdapter = new ArtistActivityAdapter(dataSet, mapIndex);
        binding.artistRecyclerView.setAdapter(artistAdapter);

        //Fast scroll decoration 설정
        FastScrollRecyclerViewItemDecoration decoration = new FastScrollRecyclerViewItemDecoration(this);
        binding.artistRecyclerView.addItemDecoration(decoration);
        binding.artistRecyclerView.setItemAnimator(new DefaultItemAnimator());


    }

    private void updateDataset() {
        dataSet.clear();
        //listItem 생성
        for (int i = 0; i < 8; i++) {
            dataSet.addAll(new Weekday_ListItem(this, i).getList());
        }
        //중복 제거
        HashSet hashSet = new HashSet(dataSet);
        dataSet = new ArrayList<>(hashSet);

        //작가기준 오름차순 정렬
        Collections.sort(dataSet, new Comparator<Webtoon>() {
            @Override
            public int compare(Webtoon lhs, Webtoon rhs) {
                return lhs.getArtist().compareTo(rhs.getArtist());
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.updateDataset();
        artistAdapter.setDataSet(dataSet);
        artistAdapter.notifyDataSetChanged();
    }


    private HashMap<String, Integer> calculateIndexesForName(ArrayList<Webtoon> items) {
        HashMap<String, Integer> mapIndex = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < items.size(); i++) {
            String name = items.get(i).getArtist();
            String index = name.substring(0, 1);
            if (Character.getType(index.charAt(0)) == Character.OTHER_LETTER) {
                String token = tokenJASO(index).substring(0, 1);
                switch (token) {
                    case "ㄲ": {
                        token = "ㄱ";
                        break;
                    }
                    case "ㄸ": {
                        token = "ㄷ";
                        break;
                    }
                    case "ㅃ": {
                        token = "ㅂ";
                        break;
                    }
                    case "ㅆ": {
                        token = "ㅅ";
                        break;
                    }
                    case "ㅉ": {
                        token = "ㅈ";
                        break;
                    }
                }
                index = token;
            } else if (isNumeric(index))
                index = "#";
            else
                index = index.toUpperCase();

            if (!mapIndex.containsKey(index)) {
                mapIndex.put(index, i);
            }
        }
        return mapIndex;
    }

    private static String tokenJASO(String text) {
        if (text == null) {
            return null;
        }
        // StringBuilder의 capacity가 0으로 등록되는 것 방지.
        if (text.length() == 0) {
            return "";
        }
        StringBuilder rv = new StringBuilder(text.length() * 3);
        for (char ch : text.toCharArray()) {
            if (ch >= '가' && ch <= '힣') {
                // 한글의 시작부분을 구함
                int ce = ch - '가';
                // 초성을 구함
                rv.append(KO_INIT_S[ce / (588)]); // 21 * 28
                // 중성을 구함
                rv.append(KO_INIT_M[(ce = ce % (588)) / 28]); // 21 * 28
                // 종성을 구함
                if ((ce = ce % 28) != 0) {
                    rv.append(KO_INIT_E[ce]);
                }
            } else {
                rv.append(ch);
            }
        }
        return rv.toString();
    }

    private boolean isNumeric(String s) {
        return s.matches("[-+]?\\d*\\.?\\d+");
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
                Toast.makeText(this, "검색어를 입력하세요", Toast.LENGTH_SHORT).show();
            } else {
                intent = new Intent(ArtistActivity.this, SearchActivity.class);
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
                intent = new Intent(ArtistActivity.this, Top100Activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
            case R.id.moreMyWebtoon:
                intent = new Intent(ArtistActivity.this, MyWebtoonActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
            case R.id.personalFavorite:
                intent = new Intent(ArtistActivity.this, FavoriteChartAcivity.class);
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
                    Toast.makeText(this, "검색어를 입력하세요", Toast.LENGTH_SHORT).show();
                } else {
                    intent = new Intent(ArtistActivity.this, SearchActivity.class);
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
                Toast.makeText(this, "검색어를 입력하세요", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(ArtistActivity.this, SearchActivity.class);
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
