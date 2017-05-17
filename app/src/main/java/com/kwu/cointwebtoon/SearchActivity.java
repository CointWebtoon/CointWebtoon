package com.kwu.cointwebtoon;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.kwu.cointwebtoon.DataStructure.Webtoon;

import java.util.ArrayList;

public class SearchActivity extends TypeKitActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnKeyListener, AdapterView.OnItemClickListener {
    ArrayList<Webtoon> resultQueries = new ArrayList<>();
    GridView gridView;
    SearchAdapter searchAdapter = null;
    Cursor cursor = null;
    COINT_SQLiteManager coint_sqLiteManager;
    Toolbar toolbar;
    TextView resultview;
    private EditText search;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
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
        Intent intent = getIntent();
        String something = intent.getStringExtra("Intent");
        search = (EditText) findViewById(R.id.searchbar);
        search.setOnKeyListener(this);
        search.setText(something);
        resultview = (TextView) findViewById(R.id.noresult);
        coint_sqLiteManager = COINT_SQLiteManager.getInstance(this);
        gridView = (GridView) findViewById(R.id.searchView);
        searchAdapter = new SearchAdapter(this, new ArrayList<Webtoon>());
        gridView.setAdapter(searchAdapter);
        gridView.setOnItemClickListener(this);
        onSearch(something);
    }

    private void onSearch(String keyword) {
        GetSearchResult result = new GetSearchResult();
        result.execute(keyword);
    }

    private class GetSearchResult extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            cursor = coint_sqLiteManager.searchquery(params[0]);
            resultQueries.clear();
            while (cursor.moveToNext()) {
                resultQueries.add(new Webtoon(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getFloat(3),
                        cursor.getInt(4), cursor.getString(5), cursor.getInt(6), cursor.getString(7).charAt(0), cursor.getInt(8) == 1 ? true : false,
                        cursor.getInt(9) == 1 ? true : false, cursor.getInt(10) == 1 ? true : false, cursor.getInt(11)));
            }
            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            if (resultQueries.size() == 0) {
                resultview.setVisibility(View.VISIBLE);
            } else {
                resultview.setVisibility(View.GONE);
            }
            searchAdapter.changeItems(resultQueries);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Webtoon target = searchAdapter.getItem(position);
        Toast.makeText(SearchActivity.this, target.getTitle(), Toast.LENGTH_SHORT).show();
        Intent episodeIntent = new Intent(SearchActivity.this, EpisodeActivity.class);
        episodeIntent.putExtra("id", target.getId());
        episodeIntent.putExtra("toontype", target.getToonType());
        startActivity(episodeIntent);
    }

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
                onSearch(search.getText().toString());
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
                intent = new Intent(SearchActivity.this, Top100Activity.class);
                startActivity(intent);
                break;
            case R.id.moreMyWebtoon:
                intent = new Intent(SearchActivity.this, MyWebtoonActivity.class);
                startActivity(intent);
                break;
            case R.id.personalFavorite:
                intent = new Intent(SearchActivity.this, FavoriteChartAcivity.class);
                startActivity(intent);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onClick(View view) {        // 베스트도전은 웹뷰로 띄우고, 나머지는 액티비티
        int id = view.getId();
        String result;
        Intent intent;

        switch (id) {
            case R.id.action_search:
                String searchString = search.getText().toString();
                if (searchString.equals("")) {
                    Toast.makeText(this, "검색어를 입력하세요", Toast.LENGTH_SHORT).show();
                } else {
                    intent = new Intent(SearchActivity.this, SearchActivity.class);
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
                onSearch(search.getText().toString());
            }
            return true;
        }
        return false;
    }
}
