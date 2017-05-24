package com.kwu.cointwebtoon.DataStructure;

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
    private boolean is_mine;                    //마이웹툰

    //서버에서 데이터 저장할 때 사용하는 생성자
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

    //SQLite에서 데이터 꺼내쓸 때 사용하는 생성자.
    //Id Title Artist Starscore Hits Thumburl Likes Toontype Is_adult Is_charged Is_mine Is_updated 순서
    public Webtoon(int id, String title, String artist, float starScore,
                   int hits, String thumbURL, int likes, char toonType, boolean is_adult, boolean is_charged, boolean is_mine, int is_updated) {
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
        this.is_mine = is_mine;
    }

    //먼저 인스턴스를 생성해놓고 setting하는 경우
    public Webtoon() {
        this.id = -1;
        this.title = null;
        this.artist = null;
        this.starScore = -1;
        this.thumbURL = null;
        this.likes = -1;
        this.hits = -1;
        this.is_charged = false;
        this.toonType = 'U';
        this.is_adult = false;
        this.is_updated = -1;
        this.is_mine = false;
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

    public char getToonType() {
        return toonType;
    }

    public boolean isCharged() {
        return is_charged;
    }

    public int isUpdated() {
        return is_updated;
    }

    public boolean isAdult() {
        return is_adult;
    }

    public boolean isMine() {
        return is_mine;
    }

    public int getHits() {
        return hits;
    }

    public int getLikes() {
        return likes;
    }

    //Setter Method
    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setStarScore(float starScore) {
        this.starScore = starScore;
    }

    public void setThumbURL(String thumbURL) {
        this.thumbURL = thumbURL;
    }

    public void setToonType(char toonType) {
        this.toonType = toonType;
    }

    public void setIsCharged(boolean is_charged) {
        this.is_charged = is_charged;
    }

    public void setIsUpdated(int is_updated) {
        this.is_updated = is_updated;
    }

    public void setIs_adult(boolean is_adult) {
        this.is_adult = is_adult;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setIs_mine(boolean is_mine) {
        this.is_mine = is_mine;
    }

    ///------------- ArrayList.Contains 기능 사용하기 위해 만든 메소드------//////
    @Override
    public boolean equals(Object obj) {
        Webtoon webtoon = (Webtoon) obj;
        if (this.id == webtoon.getId())
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