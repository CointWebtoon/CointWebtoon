/* 페이지 구성을 위한 페이져 */
package com.kwu.cointwebtoon;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.daimajia.swipe.util.Attributes;
import com.kwu.cointwebtoon.DataStructure.Webtoon;
import com.kwu.cointwebtoon.DataStructure.Weekday_ListItem;


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
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.weekday_fragment, container, false);
        listView = (ListView) layout.findViewById(R.id.listView);
        //리스트 조회순 정렬
        listItem.orderByHits();
        weekdayAdapter = new WeekdayAdapter(listView, listItem, getContext());
        listView.setAdapter(weekdayAdapter);
        weekdayAdapter.setMode(Attributes.Mode.Single);
        listView.setClipToPadding(false);
        listView.setClipChildren(false);
        listView.setFocusable(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                //웹툰연결
                Webtoon target = (Webtoon) parent.getItemAtPosition(position);
                Intent episodeIntent = new Intent(getContext(), EpisodeActivity.class);
                episodeIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                episodeIntent.putExtra("id", target.getId());
                episodeIntent.putExtra("toontype", target.getToonType());
                startActivity(episodeIntent);
            }
        });


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
