package com.example.epcej.coint_mainactivity;

public class Webtoon {
    private int id;                                        //웹툰 고유 ID
    private String title;                               //웹툰 제목
    private String artist;                             //작가명
    private float starScore;                        //평균 평점
    private String thumbURL;                    //썸네일 URL
    private int likes;                                      //좋아요
    private int hits;                                       //조회수
    private char toonType;                         //일반툰 :  G 컷툰 : C 스마트툰 : S
    private boolean is_charged;               //유료 웹툰 구분(스토어)
    private boolean is_adult;                   //성인웹툰
    private int is_updated;                         //업데이트

    //Constructor
    public Webtoon(int id, String title, String artist, float starScore, String thumbURL, int likes, int hits, char toonType, boolean is_charged, boolean is_adult, int is_updated) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.starScore = starScore;
        this.thumbURL = thumbURL;
        this.likes = likes;
        this.hits = hits;
        this.is_charged = is_charged;
        this.toonType = toonType;
        this.is_adult = is_adult;
        this.is_updated = is_updated;
    }

    //getter methods
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public float getStarScore() {
        return starScore;
    }

    public String getThumbURL() {
        return thumbURL;
    }

    public char getToonType(){return toonType;}

    public boolean isCharged(){return is_charged;}

    public int isUpdated(){return is_updated;}

    public boolean isAdult(){return is_adult;}

    public int getHits(){return hits;}

    public int getLikes(){return likes;}

    //Setter Method
    public void setId(int id){
        this.id = id;
    }

    ///------------- ArrayList.Contains 기능 사용하기 위해 만든 메소드------//////
    @Override
    public boolean equals(Object obj) {
        Webtoon webtoon = (Webtoon) obj;
        if (this.id == webtoon.id)
            return true;
        else
            return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }
    ///------------- ArrayList.Contains 기능 사용하기 위해 만든 메소드------end//
}