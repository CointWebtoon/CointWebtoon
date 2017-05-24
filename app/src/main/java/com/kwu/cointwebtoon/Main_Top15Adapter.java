package com.kwu.cointwebtoon;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class Main_Top15Adapter extends PagerAdapter {
    LayoutInflater inflater;
    Context mContext;
    COINT_SQLiteManager coint_sqLiteManager;
    Cursor c;
    int returnPosition;

//adapter의 데이터는 보통 액티비티라던가 다른 클래스에서 해서 생성자로 넘겨줌.

    public Main_Top15Adapter(Context mContext) {
        //전달 받은 LayoutInflater를 멤버변수로 전달
        this.inflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        coint_sqLiteManager = COINT_SQLiteManager.getInstance(mContext);
    }

    //PagerAdapter가 가지고 있는 View의 개수를 리턴
    //보통 보여줘야하는 이미지 배열 데이터의 길이를 리턴
    @Override
    public int getCount() {
        return 5;                       //이미지 개수 리턴(3개씩 5쪽짜리)
    }

    //ViewPager가 현재 보여질 Item(View객체)를 생성할 필요가 있는 때 자동으로 호출
    //쉽게 말해, 스크롤을 통해 현재 보여져야 하는 View를 만들어냄.
    //첫번째 파라미터 : ViewPager
    //두번째 파라미터 : ViewPager가 보여줄 View의 위치(처음부터 0,1,2,3...)
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view;
        ImageView imgTop, imgMid, imgBot;
        TextView rankTop, rankMid, rankBot, starTop, starMid, starBot, artistTop, artistMid, artistBot, titleTop, titleMid, titleBot,
                cuttoonTop, cuttoonMid, cuttoonBot, upTop, upMid, upBot, adultTop, adultMid, adultBot;
        returnPosition = position;

        c = coint_sqLiteManager.topHits(position);             //현재 페이지에 맞는 순위 세개를 가져옴

        //새로운 View 객체를 Layoutinflater를 이용해서 생성
        //만들어질 View의 설계는 top15레이아웃 파일 사용
        view = inflater.inflate(R.layout.main_top15_item, null);

        //OnClickListener를 통해 버튼을 누르면 즐겨찾는 웹툰에 추가를 하고,
        // RelativeLayout을 누르면 회차정보가 뜰 수 있도록 인텐트를 보내는 코드로 수정 할 예정
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c = coint_sqLiteManager.topHits(position);
                int id = v.getId();
                Intent intent;
                switch (id) {
                    case R.id.top:
                        c.moveToFirst();
                        /**
                         * Episode Activity 연결부
                         */
                        intent = new Intent(mContext, EpisodeActivity.class);
                        intent.putExtra("id", c.getInt(0));
                        intent.putExtra("toontype", c.getString(7).charAt(0));
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        mContext.startActivity(intent);
                        break;
                    case R.id.middle:
                        c.moveToPosition(1);
                        /**
                         * Episode Activity 연결부
                         */
                        intent = new Intent(mContext, EpisodeActivity.class);
                        intent.putExtra("id", c.getInt(0));
                        intent.putExtra("toontype", c.getString(7).charAt(0));
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        mContext.startActivity(intent);
                        break;
                    case R.id.bottom:
                        c.moveToLast();
                        /**
                         * Episode Activity 연결부
                         */
                        intent = new Intent(mContext, EpisodeActivity.class);
                        intent.putExtra("id", c.getInt(0));
                        intent.putExtra("toontype", c.getString(7).charAt(0));
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        mContext.startActivity(intent);
                        break;
                }
            }
        };

        //추가버튼과 각각의 RelativeLayout에 setOnClickListener 설정

        view.findViewById(R.id.top).setOnClickListener(onClickListener);
        view.findViewById(R.id.middle).setOnClickListener(onClickListener);
        view.findViewById(R.id.bottom).setOnClickListener(onClickListener);


        //만들어진 View안에 있는 ImageView, TextView들을 가져옴
        //위에서 inflated 되어 만들어진 view로부터 findViewById()를 해야한다.
        ImageView topPlusBtn = (ImageView) view.findViewById(R.id.addTopBtn);
        topPlusBtn.setTag(Integer.valueOf(position));

        ImageView midPlusBtn = (ImageView) view.findViewById(R.id.addMidBtn);
        midPlusBtn.setTag(Integer.valueOf(position));

        ImageView botPlusBtn = (ImageView) view.findViewById(R.id.addBotBtn);
        botPlusBtn.setTag(Integer.valueOf(position));

        cuttoonTop = (TextView) view.findViewById(R.id.cuttonTop);
        cuttoonMid = (TextView) view.findViewById(R.id.cuttonMid);
        cuttoonBot = (TextView) view.findViewById(R.id.cuttonBot);

        upTop = (TextView) view.findViewById(R.id.upTop);
        upMid = (TextView) view.findViewById(R.id.upMid);
        upBot = (TextView) view.findViewById(R.id.upBot);

        adultTop = (TextView) view.findViewById(R.id.adultTop);
        adultMid = (TextView) view.findViewById(R.id.adultMid);
        adultBot = (TextView) view.findViewById(R.id.adultBot);

        imgTop = (ImageView) view.findViewById(R.id.webtoonImg);
        imgMid = (ImageView) view.findViewById(R.id.webtoonImg1);
        imgBot = (ImageView) view.findViewById(R.id.webtoonImg2);

        titleTop = (TextView) view.findViewById(R.id.webtoonName);
        titleMid = (TextView) view.findViewById(R.id.webtoonName1);
        titleBot = (TextView) view.findViewById(R.id.webtoonName2);
        titleTop.setSelected(true);
        titleMid.setSelected(true);
        titleBot.setSelected(true);

        artistTop = (TextView) view.findViewById(R.id.artistName);
        artistMid = (TextView) view.findViewById(R.id.artistName1);
        artistBot = (TextView) view.findViewById(R.id.artistName2);

        starTop = (TextView) view.findViewById(R.id.starScore);
        starMid = (TextView) view.findViewById(R.id.starScore1);
        starBot = (TextView) view.findViewById(R.id.starScore2);

        rankTop = (TextView) view.findViewById(R.id.rankTop);
        rankTop.setText(Integer.toString(position * 3 + 1));

        rankMid = (TextView) view.findViewById(R.id.rankMiddle);
        rankMid.setText(Integer.toString(position * 3 + 2));

        rankBot = (TextView) view.findViewById(R.id.rankBottom);
        rankBot.setText(Integer.toString(position * 3 + 3));

        while (c.moveToNext()) {
            //Glide를 통해 img에 이미지를 로드, 타이틀과 작가, 별점을 set
            Glide.with(mContext).load(c.getString(5).toString()).into(imgTop);
            titleTop.setText(c.getString(1));
            artistTop.setText(c.getString(2));
            starTop.setText("★ " + String.valueOf(c.getFloat(3)));
            if (c.getInt(10) == 1) {        //마이웹툰이면 -버튼으로
                topPlusBtn.setImageResource(R.drawable.my_star_active);
            } else {
                topPlusBtn.setImageResource(R.drawable.my_star_unactive);
            }

            if (c.getString(7).equals("C")) {
                cuttoonTop.setVisibility(view.VISIBLE);
                cuttoonTop.setBackgroundResource(R.drawable.week_icon_cuttoon);
                cuttoonTop.setTextColor(Color.parseColor("28dcbe"));
                cuttoonTop.setText("컷툰");
            } else if (c.getString(7).equals("M")) {
                cuttoonTop.setVisibility(view.VISIBLE);
                cuttoonTop.setBackgroundResource(R.drawable.week_icon_motiontoon);
                cuttoonTop.setTextColor(Color.parseColor("28dcbe"));
                cuttoonTop.setText("모션");
            } else if (c.getString(7).equals("S")) {
                cuttoonTop.setVisibility(View.VISIBLE);
                cuttoonTop.setBackgroundResource(R.drawable.week_icon_smarttoon);
                cuttoonTop.setTextColor(Color.parseColor("#0050b4"));
                cuttoonTop.setText("스마트");
            } else {
                cuttoonTop.setBackgroundResource(R.drawable.week_icon_cuttoon);
                cuttoonTop.setText(null);
                cuttoonTop.setVisibility(view.GONE);
            }

            if (c.getString(8).equals("1")) {
                adultTop.setVisibility(view.VISIBLE);
                adultTop.setBackgroundResource(R.drawable.main_icon_adult);
                adultTop.setText("성인");
            } else {
                adultTop.setBackgroundResource(R.drawable.main_icon_adult);
                adultTop.setText(null);
                adultTop.setVisibility(view.GONE);
            }

            if (c.getString(11).equals("1")) {
                upTop.setVisibility(view.VISIBLE);
                upTop.setBackgroundResource(R.drawable.week_icon_update);
                upTop.setText("UP");
            } else if (c.getString(11).equals("2")) {
                upTop.setVisibility(view.VISIBLE);
                upTop.setBackgroundResource(R.drawable.week_icon_dormant);
                upTop.setText("휴재");
                upTop.setTextColor(Color.parseColor("#ffffff"));
            } else {
                upTop.setBackgroundResource(R.drawable.week_icon_cuttoon);
                upTop.setText(null);
                upTop.setVisibility(view.GONE);
            }
            c.moveToNext();

            Glide.with(mContext).load(c.getString(5)).into(imgMid);
            titleMid.setText(c.getString(1));
            artistMid.setText(c.getString(2));
            starMid.setText("★ " + String.valueOf(c.getFloat(3)));

            if (c.getInt(10) == 1) {        //마이웹툰이면 -버튼으로
                midPlusBtn.setImageResource(R.drawable.my_star_active);
            } else {
                midPlusBtn.setImageResource(R.drawable.my_star_unactive);
            }

            if (c.getString(7).equals("C")) {
                cuttoonMid.setVisibility(view.VISIBLE);
                cuttoonMid.setBackgroundResource(R.drawable.week_icon_cuttoon);
                cuttoonTop.setTextColor(Color.parseColor("28dcbe"));
                cuttoonMid.setText("컷툰");
            } else if (c.getString(7).equals("M")) {
                cuttoonMid.setVisibility(view.VISIBLE);
                cuttoonMid.setBackgroundResource(R.drawable.week_icon_motiontoon);
                cuttoonMid.setTextColor(Color.parseColor("28dcbe"));
                cuttoonMid.setText("모션");
            } else if (c.getString(7).equals("S")) {
                cuttoonMid.setVisibility(View.VISIBLE);
                cuttoonMid.setBackgroundResource(R.drawable.week_icon_smarttoon);
                cuttoonMid.setTextColor(Color.parseColor("#0050b4"));
                cuttoonMid.setText("스마트");
            } else {
                cuttoonMid.setBackgroundResource(R.drawable.week_icon_cuttoon);
                cuttoonMid.setText(null);
                cuttoonMid.setVisibility(view.GONE);
            }

            if (c.getString(8).equals("1")) {
                adultMid.setVisibility(view.VISIBLE);
                adultMid.setBackgroundResource(R.drawable.main_icon_adult);
                adultMid.setText("성인");
            } else {
                adultMid.setBackgroundResource(R.drawable.main_icon_adult);
                adultMid.setText(null);
                adultMid.setVisibility(view.GONE);
            }

            if (c.getString(11).equals("1")) {       //업데이트, 휴재, 연재일 아닌 날
                upMid.setVisibility(view.VISIBLE);
                upMid.setBackgroundResource(R.drawable.week_icon_update);
                upMid.setText("UP");
            } else if (c.getString(11).equals("2")) {
                upMid.setVisibility(view.VISIBLE);
                upMid.setBackgroundResource(R.drawable.week_icon_dormant);
                upMid.setText("휴재");
                upMid.setTextColor(Color.parseColor("#ffffff"));
            } else {
                upMid.setBackgroundResource(R.drawable.week_icon_cuttoon);
                upMid.setText(null);
                upMid.setVisibility(view.GONE);
            }
            c.moveToNext();

            Glide.with(mContext).load(c.getString(5)).into(imgBot);
            titleBot.setText(c.getString(1));
            artistBot.setText(c.getString(2));
            starBot.setText("★ " + String.valueOf(c.getFloat(3)));

            if (c.getInt(10) == 1) {        //마이웹툰이면 -버튼으로
                botPlusBtn.setImageResource(R.drawable.my_star_active);
            } else {
                botPlusBtn.setImageResource(R.drawable.my_star_unactive);
            }

            if (c.getString(7).equals("C")) {
                cuttoonBot.setVisibility(view.VISIBLE);
                cuttoonBot.setBackgroundResource(R.drawable.week_icon_cuttoon);
                cuttoonBot.setTextColor(Color.parseColor("28dcbe"));
                cuttoonBot.setText("컷툰");
            } else if (c.getString(7).equals("M")) {
                cuttoonBot.setVisibility(view.VISIBLE);
                cuttoonBot.setBackgroundResource(R.drawable.week_icon_motiontoon);
                cuttoonBot.setTextColor(Color.parseColor("28dcbe"));
                cuttoonBot.setText("모션");
            } else if (c.getString(7).equals("S")) {
                cuttoonBot.setVisibility(View.VISIBLE);
                cuttoonBot.setBackgroundResource(R.drawable.week_icon_smarttoon);
                cuttoonBot.setTextColor(Color.parseColor("#0050b4"));
                cuttoonBot.setText("스마트");
            } else {
                cuttoonBot.setBackgroundResource(R.drawable.week_icon_cuttoon);
                cuttoonBot.setText(null);
                cuttoonBot.setVisibility(view.GONE);
            }

            if (c.getString(8).equals("1")) {
                adultBot.setVisibility(view.VISIBLE);
                adultBot.setBackgroundResource(R.drawable.main_icon_adult);
                adultBot.setText("성인");
            } else {
                adultBot.setBackgroundResource(R.drawable.main_icon_adult);
                adultBot.setText(null);
                adultBot.setVisibility(view.GONE);
            }

            if (c.getString(11).equals("1")) {
                upBot.setVisibility(view.VISIBLE);
                upBot.setBackgroundResource(R.drawable.week_icon_update);
                upBot.setText("UP");
            } else if (c.getString(11).equals("2")) {
                upBot.setVisibility(view.VISIBLE);
                upBot.setBackgroundResource(R.drawable.week_icon_dormant);
                upBot.setText("휴재");
                upBot.setTextColor(Color.parseColor("#ffffff"));
            } else {
                upBot.setBackgroundResource(R.drawable.week_icon_cuttoon);
                upBot.setText(null);
                upBot.setVisibility(view.GONE);
            }
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
        //ViewPager에서 보이지 않는 View는 제거
        //세번째 파라미터가 View 객체 이지만 데이터 타입이 Object여서 형변환 실시
        container.removeView((View) object);
    }

    //instantiateItem() 메소드에서 리턴된 Ojbect가 View가  맞는지 확인하는 메소드
    @Override
    public boolean isViewFromObject(View v, Object obj) {
        return v == obj;
    }

    public int getReturnPosition() {
        return returnPosition;
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    /*
    *  지니처럼 옆 화면이 보이는 뷰 페이저 구현을 위해 추가
    * */
    public float getPageWidth(int position) {
        return (0.9f);
    }
}
