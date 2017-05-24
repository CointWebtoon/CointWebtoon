package com.kwu.cointwebtoon;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class CoachAdapter extends PagerAdapter {
    LayoutInflater inflater;
    Context mContext;
    COINT_SQLiteManager coint_sqLiteManager;
    Cursor c;
    int returnPosition;

//adapter의 데이터는 보통 액티비티라던가 다른 클래스에서 해서 생성자로 넘겨줌.

    public CoachAdapter(Context mContext) {
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
        ImageView imageView;
        returnPosition = position;

        //새로운 View 객체를 Layoutinflater를 이용해서 생성
        //만들어질 View의 설계는 top15레이아웃 파일 사용
        view = inflater.inflate(R.layout.coach_item, null);

        //OnClickListener를 통해 버튼을 누르면 즐겨찾는 웹툰에 추가를 하고,
        // RelativeLayout을 누르면 회차정보가 뜰 수 있도록 인텐트를 보내는 코드로 수정 할 예정
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        };

        //추가버튼과 각각의 RelativeLayout에 setOnClickListener 설정

        // view.findViewById(R.id.top).setOnClickListener(onClickListener);

        //만들어진 View안에 있는 ImageView, TextView들을 가져옴
        //위에서 inflated 되어 만들어진 view로부터 findViewById()를 해야한다.
/*
        ImageView topPlusBtn = (ImageView)view.findViewById(R.id.addTopBtn);
        topPlusBtn.setTag(Integer.valueOf(position));

        ImageView midPlusBtn = (ImageView)view.findViewById(R.id.addMidBtn);
        midPlusBtn.setTag(Integer.valueOf(position));

        ImageView botPlusBtn = (ImageView)view.findViewById(R.id.addBotBtn);
        botPlusBtn.setTag(Integer.valueOf(position));
*/
        imageView = (ImageView) view.findViewById(R.id.coach_image);
        switch (position) {
            case 0:
                imageView.setImageResource(R.drawable.coach_main);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                break;
            case 1:
                imageView.setImageResource(R.drawable.coach_weekday);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                break;
            case 2:
                imageView.setImageResource(R.drawable.coach_episode);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                break;
            case 3:
                imageView.setImageResource(R.drawable.coach_general);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                break;
            case 4:
                imageView.setImageResource(R.drawable.coach_cuttoon);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                break;
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
}
