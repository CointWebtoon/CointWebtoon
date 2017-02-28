package importClasses;

import java.util.HashSet;

public class Webtoon {
    private int id;                                        //웹툰 고유 ID
    private String title;                               //웹툰 제목
    private String artist;                             //작가명
    private float starScore;                        //평균 평점
    private String thumbURL;                    //썸네일 URL
    private boolean is_cuttoon;                //컷툰, 일반툰 구분
    private boolean is_smarttoon;            //컷툰, 스마트툰 구분
    private boolean is_charged;               //유료 웹툰 구분(스토어)
    private int is_updated;
    private HashSet<Integer> weekday;                            //요일 코드
    private HashSet<String> genre;          //장르

    //Constructor
    public Webtoon(int id, String title, String artist, float starScore, String thumbURL, boolean is_cuttoon, boolean is_smarttoon, boolean is_charged, int is_updated, int weekday) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.starScore = starScore;
        this.thumbURL = thumbURL;
        this.is_cuttoon = is_cuttoon;
        this.is_smarttoon = is_smarttoon;
        this.is_charged = is_charged;
        this.weekday = new HashSet<>();
        this.weekday.add(weekday);
        this.genre = new HashSet<>();
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

    public boolean isCuttoon() {
        return is_cuttoon;
    }

    public boolean isSmarttoon() {
        return is_smarttoon;
    }

    public boolean isCharged(){return is_charged;}

    public int isUpdated(){return is_updated;}

    public HashSet<Integer> getWeekday() {
        return weekday;
    }

    public HashSet<String> getGenre(){return genre;}

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

    /*
        현재 자신의 Instance 를 출력하는 description method
     */
    public void printToon() {

        System.out.print(this.getId() + " " + this.getTitle() + " " + this.getArtist() + " " +
                this.getStarScore() + " " + this.getThumbURL() + " ");
        if (this.isCuttoon())
            System.out.print("컷툰 ");
        if (this.isSmarttoon())
            System.out.print("스마트툰 ");
        System.out.print(this.getWeekday() + "\n");
    }
}
