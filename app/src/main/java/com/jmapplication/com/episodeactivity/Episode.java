package com.jmapplication.com.episodeactivity;

/**
 * Created by epcej on 2017-03-02.
 */

public class Episode {
    private int id_E;                                        //웹툰 고유 ID
    private int episode_id;                       //에피소드 ID
    private String episode_title;                    //에피소드 제목
    private float ep_starScore;                      //평균 평점
    private String ep_thumbURL;                 //썸네일 URL
    private String reg_date;                               //등록일
    private String mention;                                //작가의말
    private int likes_E;                                //좋아요
    private boolean is_saved;                      //임시저장 여부
    private boolean is_read;                        //읽음 여부
    private int location;                               //책갈피한 이미지 url 위치

    //Constructor
    public Episode(int id_E, int episode_id, String episode_title, float ep_starScore, String ep_thumbURL, String reg_date, String mention, int likes_E) {
        this.id_E = id_E;
        this.episode_id = episode_id;
        this.episode_title = episode_title;
        this.ep_starScore = ep_starScore;
        this.ep_thumbURL = ep_thumbURL;
        this.reg_date = reg_date;
        this.mention = mention;
        this.likes_E = likes_E;
/*        this.is_saved = is_saved;
        this.is_read = is_read;
        this.location = location;*/
    }

    //getter methods
    public int getId() {
        return id_E;
    }

    public int getEpisode_id() {
        return episode_id;
    }

    public String getEpisode_title() {
        return episode_title;
    }

    public float getEp_starScore() {
        return ep_starScore;
    }

    public String getEp_thumbURL() {
        return ep_thumbURL;
    }

    public String getReg_date() {
        return reg_date;
    }

    public String getMention() {
        return mention;
    }

    public int getLikes_E() {
        return likes_E;
    }

/*    public boolean getIs_saved() { return is_saved;}

    public boolean getIs_read(){ return is_read;}

    public int getLocation(){return location;}*/

    //Setter Method
    public void setId(int id) {
        this.id_E = id;
    }

    public void setEpisode_id(int episode_id) {
        this.episode_id = episode_id;
    }

    ///------------- ArrayList.Contains 기능 사용하기 위해 만든 메소드------//////
    @Override
    public boolean equals(Object obj) {
        Episode episode = (Episode) obj;
        if (this.id_E == episode.id_E)
            return true;
        else
            return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id_E;
        return result;
    }
    ///------------- ArrayList.Contains 기능 사용하기 위해 만든 메소드------end//
}
