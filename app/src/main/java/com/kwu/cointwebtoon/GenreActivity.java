package com.kwu.cointwebtoon;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.kwu.cointwebtoon.DataStructure.Webtoon;

import java.util.ArrayList;

public class GenreActivity extends TypeKitActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnKeyListener, AdapterView.OnItemClickListener, View.OnTouchListener {
    private static final int COMIC = 0, DAILY = 1, PURE = 2, THRILL = 3, FANTASY = 4, HISTORICAL = 5,
            DRAMA = 6, SPORTS = 7, ACTION = 8, SENSIBILITY = 9, BRANDETC = 10;
    private static final String[] genreString = new String[11];
    private int currentIndex = COMIC;

    /**
     * UI Components
     */
    private Toolbar toolbar;
    private EditText search;
    private GridView gridView;
    private SearchAdapter gridAdapter;
    private CointProgressDialog dialog;
    private GetGenreItems taskInstance;
    private TextView[] textViews = new TextView[11];
    private HorizontalScrollView titleBar;

    /**
     * Data
     */
    private COINT_SQLiteManager manager;
    private float x, y;
    private int layoutWidth;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.genre_activity_main);
        initView();
        initData();
    }

    /**
     * UI Components Initialize
     */
    private void initView() {
        /**
         * Nav 공통 요소
         */
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//키보드 숨김
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportActionBar().setDisplayShowTitleEnabled(false);            // 액션바에서 앱 이름 보이지 않게 함
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        search = (EditText) findViewById(R.id.searchbar);
        search.setOnKeyListener(this);
        /**
         * Nav 공통 요소 end
         */
        dialog = new CointProgressDialog(this);
        gridView = (GridView) findViewById(R.id.genre_grid);
        gridAdapter = new SearchAdapter(this, new ArrayList<Webtoon>());
        gridView.setAdapter(gridAdapter);
        gridView.setOnTouchListener(this);
        gridView.setOnItemClickListener(this);
        textViews[COMIC] = (TextView) findViewById(R.id.comic);
        textViews[DAILY] = (TextView) findViewById(R.id.daily);
        textViews[DRAMA] = (TextView) findViewById(R.id.drama);
        textViews[PURE] = (TextView) findViewById(R.id.pure);
        textViews[THRILL] = (TextView) findViewById(R.id.thrill);
        textViews[FANTASY] = (TextView) findViewById(R.id.fantasy);
        textViews[HISTORICAL] = (TextView) findViewById(R.id.history);
        textViews[SPORTS] = (TextView) findViewById(R.id.sports);
        textViews[ACTION] = (TextView) findViewById(R.id.action);
        textViews[BRANDETC] = (TextView) findViewById(R.id.brandetc);
        textViews[SENSIBILITY] = (TextView) findViewById(R.id.sensibility);
        titleBar = (HorizontalScrollView) findViewById(R.id.genre_titlebar);
    }

    private void initData() {
        genreString[COMIC] = "COMIC";
        genreString[DAILY] = "DAILY";
        genreString[DRAMA] = "DRAMA";
        genreString[PURE] = "PURE";
        genreString[THRILL] = "THRILL";
        genreString[FANTASY] = "FANTASY";
        genreString[HISTORICAL] = "HISTORICAL";
        genreString[SPORTS] = "SPORTS";
        genreString[ACTION] = "ACTION";
        genreString[BRANDETC] = "BRANDETC";
        genreString[SENSIBILITY] = "SENSIBILITY";
        manager = COINT_SQLiteManager.getInstance(this);
        taskInstance = new GetGenreItems();
        taskInstance = new GetGenreItems();
        taskInstance.execute(COMIC);
        layoutWidth = getWindowManager().getDefaultDisplay().getWidth();
    }

    /**
     * 장르바꾸기
     */
    public void genreFlipItemClick(View v) {
        TextView target = (TextView) v;
        for (TextView tv : textViews) {
            tv.setBackgroundColor(Color.WHITE);
        }
        v.setBackgroundColor(Color.parseColor("#DAA520"));
        String thisText = target.getText().toString();
        int genreNumber = -1;

        if (thisText.equals("코믹")) {
            genreNumber = COMIC;
        } else if (thisText.equals("일상")) {
            genreNumber = DAILY;
        } else if (thisText.equals("순정")) {
            genreNumber = PURE;
        } else if (thisText.equals("스릴러")) {
            genreNumber = THRILL;
        } else if (thisText.equals("판타지")) {
            genreNumber = FANTASY;
        } else if (thisText.equals("역사")) {
            genreNumber = HISTORICAL;
        } else if (thisText.equals("드라마")) {
            genreNumber = DRAMA;
        } else if (thisText.equals("스포츠")) {
            genreNumber = SPORTS;
        } else if (thisText.equals("액션")) {
            genreNumber = ACTION;
        } else if (thisText.equals("감성")) {
            genreNumber = SENSIBILITY;
        } else if (thisText.equals("기타")) {
            genreNumber = BRANDETC;
        }
        currentIndex = genreNumber;
        taskInstance = new GetGenreItems();
        taskInstance.execute(genreNumber);
    }

    /**
     * GridView Item 가져오기
     */
    private class GetGenreItems extends AsyncTask<Integer, ArrayList<Webtoon>, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected Void doInBackground(Integer... params) {
            publishProgress(manager.getWebtoonsByGenre(genreString[params[0]]));
            return null;
        }

        @Override
        protected void onProgressUpdate(ArrayList<Webtoon>... values) {
            super.onProgressUpdate(values);
            gridAdapter.changeItems(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                dialog.dismiss();
            } catch (Exception e) {
            }
        }
    }

    @Override
    protected void onDestroy() {
        try {
            dialog.dismiss();
        } catch (Exception e) {
        }
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Webtoon target = gridAdapter.getItem(position);
        Toast.makeText(GenreActivity.this, target.getTitle(), Toast.LENGTH_SHORT).show();
        Intent episodeIntent = new Intent(GenreActivity.this, EpisodeActivity.class);
        episodeIntent.putExtra("id", target.getId());
        episodeIntent.putExtra("toontype", target.getToonType());
        startActivity(episodeIntent);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            x = event.getX();
            y = event.getY();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            float gapX = event.getX() - x, gapY = event.getY() - y;
            if (Math.abs(gapX) > 200 && Math.abs(gapY) < 100) {
                if (gapX < 0) {//다음
                    if (currentIndex < BRANDETC) {
                        if (currentIndex >= HISTORICAL) {
                            titleBar.smoothScrollTo(titleBar.getMeasuredWidth(), 0);
                        }
                        textViews[currentIndex].setBackgroundColor(Color.WHITE);
                        currentIndex++;
                        textViews[currentIndex].setBackgroundColor(Color.parseColor("#DAA520"));
                        taskInstance = new GetGenreItems();
                        taskInstance.execute(currentIndex);
                    }
                } else {//이전
                    if (currentIndex > COMIC) {
                        if (currentIndex <= DRAMA) {
                            titleBar.smoothScrollTo(0, 0);
                        }
                        textViews[currentIndex].setBackgroundColor(Color.WHITE);
                        currentIndex--;
                        textViews[currentIndex].setBackgroundColor(Color.parseColor("#DAA520"));
                        taskInstance = new GetGenreItems();
                        taskInstance.execute(currentIndex);
                    }
                }
                return true;
            }
        }
        return false;
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
                intent = new Intent(GenreActivity.this, SearchActivity.class);
                intent.putExtra("Intent", search.getText().toString());
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
                intent = new Intent(GenreActivity.this, Top100Activity.class);
                startActivity(intent);
                break;
            case R.id.moreMyWebtoon:
                intent = new Intent(GenreActivity.this, MyWebtoonActivity.class);
                startActivity(intent);
                break;
            case R.id.personalFavorite:
                intent = new Intent(GenreActivity.this, FavoriteChartAcivity.class);
                startActivity(intent);
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
                    intent = new Intent(GenreActivity.this, SearchActivity.class);
                    intent.putExtra("Intent", search.getText().toString());
                    finish();
                    startActivity(intent);
                }
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
                Intent intent = new Intent(GenreActivity.this, SearchActivity.class);
                intent.putExtra("Intent", search.getText().toString());
                finish();
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