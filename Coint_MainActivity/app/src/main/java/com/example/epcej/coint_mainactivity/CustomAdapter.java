package com.example.epcej.coint_mainactivity;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

/**
 * Created by epcej on 2017-02-14.
 */

public class CustomAdapter extends PagerAdapter{

     LayoutInflater inflater;
    Context mContext;
    COINT_SQLiteManager coint_sqLiteManager;
    Cursor c;

//adapter의 데이터는 보통 액티비티라던가 다른 클래스에서 해서 생성자로 넘겨줌.

    public CustomAdapter(Context mContext) {
        // TODO Auto-generated constructor stub

        //전달 받은 LayoutInflater를 멤버변수로 전달
        this.inflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        coint_sqLiteManager = COINT_SQLiteManager.getInstance(mContext);
    }

    //PagerAdapter가 가지고 잇는 View의 개수를 리턴
    //보통 보여줘야하는 이미지 배열 데이터의 길이를 리턴
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return 5;                       //이미지 개수 리턴(3개씩 5쪽짜리)
    }

    //ViewPager가 현재 보여질 Item(View객체)를 생성할 필요가 있는 때 자동으로 호출
    //쉽게 말해, 스크롤을 통해 현재 보여져야 하는 View를 만들어냄.
    //첫번째 파라미터 : ViewPager
    //두번째 파라미터 : ViewPager가 보여줄 View의 위치(처음부터 0,1,2,3...)
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // TODO Auto-generated method stub
        View view;
        ImageView imgTop,imgMid,imgBot;
        TextView rankTop, rankMid, rankBot, starTop, starMid, starBot, artistTop, artistMid, artistBot, titleTop, titleMid, titleBot;

        c = coint_sqLiteManager.topHits(position);             //현재 페이지에 맞는 순위 세개를 가져옴

        //새로운 View 객체를 Layoutinflater를 이용해서 생성
        //만들어질 View의 설계는 view_pager.xml 레이아웃 파일 사용
        view= inflater.inflate(R.layout.view_pager, null);

        //만들어진 View안에 있는 ImageView, TextView들을 가져옴
        //위에서 inflated 되어 만들어진 view로부터 findViewById()를 해야한다.
        imgTop= (ImageView)view.findViewById(R.id.webtoonImg);
        imgMid= (ImageView)view.findViewById(R.id.webtoonImg1);
        imgBot= (ImageView)view.findViewById(R.id.webtoonImg2);

        titleTop = (TextView)view.findViewById(R.id.webtoonName);
        titleMid = (TextView)view.findViewById(R.id.webtoonName1);
        titleBot = (TextView)view.findViewById(R.id.webtoonName2);

        artistTop = (TextView)view.findViewById(R.id.artistName);
        artistMid = (TextView)view.findViewById(R.id.artistName1);
        artistBot = (TextView)view.findViewById(R.id.artistName2);

        starTop = (TextView)view.findViewById(R.id.starScore);
        starMid = (TextView)view.findViewById(R.id.starScore1);
        starBot = (TextView)view.findViewById(R.id.starScore2);

        rankTop = (TextView)view.findViewById(R.id.rankTop);
        rankTop.setText(Integer.toString(position*3+1));

        rankMid = (TextView)view.findViewById(R.id.rankMiddle);
        rankMid.setText(Integer.toString(position*3+2));

        rankBot = (TextView)view.findViewById(R.id.rankBottom);
        rankBot.setText(Integer.toString(position*3+3));

        while (c.moveToNext()) {
            //Glide를 통해 img에 이미지를 로드, 타이틀과 작가, 별점을 set
            Glide.with(mContext).load(c.getString(3).toString()).into(imgTop);
            titleTop.setText(c.getString(1).toString());
            artistTop.setText(c.getString(2).toString());
            starTop.setText(c.getString(4).toString());
            c.moveToNext();

            Glide.with(mContext).load(c.getString(3).toString()).into(imgMid);
            titleMid.setText(c.getString(1).toString());
            artistMid.setText(c.getString(2).toString());
            starMid.setText(c.getString(4).toString());
            c.moveToNext();

            Glide.with(mContext).load(c.getString(3).toString()).into(imgBot);
            titleBot.setText(c.getString(1).toString());
            artistBot.setText(c.getString(2).toString());
            starBot.setText(c.getString(4).toString());
        }

        //ViewPager에 만들어 낸 View 추가
        container.addView(view);

        //Image가 세팅된 View를 리턴
        return view;
    }


    //화면에 보이지 않은 View는 destroy를 해서 메모리를 관리함.
    //첫번째 파라미터 : ViewPager
    //두번째 파라미터 : 파괴될 View의 인덱스(처음부터 0,1,2,3...)
    //세번째 파라미터 : 파괴될 객체(더 이상 보이지 않은 View 객체)
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // TODO Auto-generated method stub

        //ViewPager에서 보이지 않는 View는 제거
        //세번째 파라미터가 View 객체 이지만 데이터 타입이 Object여서 형변환 실시
        container.removeView((View)object);

    }

    //instantiateItem() 메소드에서 리턴된 Ojbect가 View가  맞는지 확인하는 메소드
    @Override
    public boolean isViewFromObject(View v, Object obj) {
        // TODO Auto-generated method stub
        return v==obj;
    }
}
