/* 페이지 구성을 위한 페이져 */
package com.kwu.cointwebtoon;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.kwu.cointwebtoon.DataStructure.Webtoon;
import com.kwu.cointwebtoon.DataStructure.Weekday_ListItem;

import java.util.Collections;
import java.util.Comparator;

public class WeekdayFragment extends Fragment {
    Weekday_ListItem listItem = null;
    ListView listView;
    WeekdayAdapter weekdayAdapter;
    int position;

    public WeekdayFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        listItem.updateList();
        listItem.orderByHits();
        weekdayAdapter.setItemList(listItem);
        weekdayAdapter.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        position = getArguments().getInt("position");
        listItem = WeekdayActivity.listItems[(position)];
        LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.weekday_fragment, container, false);
        listView = (ListView)layout.findViewById(R.id.listView);

        //리스트 조회순 정렬
        listItem.orderByHits();
        weekdayAdapter = new WeekdayAdapter(listView, listItem, getContext());
        listView.setAdapter(weekdayAdapter);
        listView.setClipToPadding(false);
        listView.setClipChildren(false);
        listView.setFocusable(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                Webtoon target = (Webtoon)parent.getItemAtPosition(position);
                Toast.makeText(getContext(),"웹툰 ID : " + target.getTitle(), Toast.LENGTH_SHORT).show();
                Intent episodeIntent = new Intent(getContext(), EpisodeActivity.class);
                episodeIntent.putExtra("id", target.getId());
                episodeIntent.putExtra("toontype", target.getToonType());
                startActivity(episodeIntent);
                //TODO - 해당 웹툰 에피소드 액티비티 연결 --> 완료
            }
        }) ;



        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                for (int i = 0; i < listView.getChildCount(); i++) {
                    listView.getChildAt(i).invalidate();
                }
            }
        });
        return layout;
    }

}
