package com.kwu.cointwebtoon;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.kwu.cointwebtoon.DataStructure.Webtoon;
import com.kwu.cointwebtoon.DataStructure.Weekday_ListItem;
import com.kwu.cointwebtoon.Views.FastScrollRecyclerView;
import com.kwu.cointwebtoon.Views.FastScrollRecyclerViewItemDecoration;
import com.kwu.cointwebtoon.databinding.ArtistActivityBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class ArtistActivity extends TypeKitActivity {
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
    private ArrayList<Webtoon> dataSet;
    private ArtistActivityAdapter artistAdapter;
    private RecyclerView.LayoutManager artistLayoutManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.artist_activity);

        //dataSet 생성 및 업데이트
        dataSet = new ArrayList<>();
        updateDataset(dataSet);


        //Recycler view 설정
        binding.artistRecyclerView.setHasFixedSize(true);

        //Layout manager 설정
        artistLayoutManager = new LinearLayoutManager(this);
        binding.artistRecyclerView.setLayoutManager(artistLayoutManager);

        //Adapter 설정
        HashMap<String, Integer> mapIndex = calculateIndexesForName(dataSet);
        artistAdapter = new ArtistActivityAdapter(dataSet, mapIndex);
        binding.artistRecyclerView.setAdapter(artistAdapter);

        //Fast scroll decoration 설정
        FastScrollRecyclerViewItemDecoration decoration = new FastScrollRecyclerViewItemDecoration(this);
        binding.artistRecyclerView.addItemDecoration(decoration);
        binding.artistRecyclerView.setItemAnimator(new DefaultItemAnimator());


    }

    private void updateDataset(ArrayList<Webtoon> dataSet){
        dataSet.clear();
        //listItem 생성
        for(int i = 0; i<8; i++){
            dataSet.addAll(new Weekday_ListItem(this, i).getList());
        }
        //중복 제거
        HashSet hashSet = new HashSet(dataSet);
        dataSet = new ArrayList<>(hashSet);

        //작가기준 오름차순 정렬
        Collections.sort(dataSet, new Comparator<Webtoon>() {
            @Override
            public int compare(Webtoon lhs, Webtoon rhs) {
                return  lhs.getArtist().compareTo(rhs.getArtist());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDataset(dataSet);
        artistAdapter.setDataSet(dataSet);
        artistAdapter.notifyDataSetChanged();
    }



    private HashMap<String, Integer> calculateIndexesForName(ArrayList<Webtoon> items){
        HashMap<String, Integer> mapIndex = new LinkedHashMap<String, Integer>();
        for (int i = 0; i<items.size(); i++){
            String name = items.get(i).getArtist();
            String index = name.substring(0,1);
            if(Character.getType(index.charAt(0))==Character.OTHER_LETTER){
                String token = tokenJASO(index).substring(0, 1);
                switch (token){
                    case "ㄲ":{
                        token = "ㄱ"; break;
                    }
                    case "ㄸ":{
                        token = "ㄷ"; break;
                    }
                    case "ㅃ":{
                        token = "ㅂ"; break;
                    }
                    case "ㅆ":{
                        token = "ㅅ"; break;
                    }
                    case "ㅉ":{
                        token = "ㅈ"; break;
                    }
                }
                index = token;
            }
            else if(isNumeric(index))
                index = "#";
            else
                index = index.toUpperCase();

            if (!mapIndex.containsKey(index)) {
                mapIndex.put(index, i);
            }
        }
        return mapIndex;
    }
    private static String tokenJASO(String text)
    {
        if (text == null) { return null; }
        // StringBuilder의 capacity가 0으로 등록되는 것 방지.
        if (text.length() == 0) { return ""; }
        StringBuilder rv = new StringBuilder(text.length() * 3);
        for (char ch : text.toCharArray())
        {
            if (ch >= '가' && ch <= '힣')
            {
                // 한글의 시작부분을 구함
                int ce = ch - '가';
                // 초성을 구함
                rv.append(KO_INIT_S[ce / (588)]); // 21 * 28
                // 중성을 구함
                rv.append(KO_INIT_M[(ce = ce % (588)) / 28]); // 21 * 28
                // 종성을 구함
                if ((ce = ce % 28) != 0)
                {
                    rv.append(KO_INIT_E[ce]);
                }
            }
            else
            {
                rv.append(ch);
            }
        }
        return rv.toString();
    }
    private boolean isNumeric(String s) {
        return s.matches("[-+]?\\d*\\.?\\d+");
    }

}
