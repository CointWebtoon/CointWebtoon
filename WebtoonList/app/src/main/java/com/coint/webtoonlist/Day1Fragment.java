/* 페이지 구성을 위한 페이져 */
package com.coint.webtoonlist;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class Day1Fragment extends Fragment {
    ListItem listItem = null;
    ListView listView;
    int position;

    public Day1Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        position = getArguments().getInt("position");
        listItem = WebtoonList.listItems[(position + 1)%8];
        LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.fragment_day, container, false);
        listView = (ListView)layout.findViewById(R.id.listView);
        listView.setAdapter(new LVAdapter(listView, listItem, getContext()));
        listView.setClipToPadding(false);
        listView.setClipChildren(false);
        listView.setFocusable(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                String webtoonID = parent.getItemAtPosition(position).toString() ;
                Toast.makeText(getContext(),"웹툰 ID : " + webtoonID, Toast.LENGTH_SHORT).show();
                //TODO - 해당 웹툰 에피소드 액티비티 연결
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
