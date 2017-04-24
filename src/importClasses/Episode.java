package importClasses;

public class Episode {
    private int id_e; //웹툰 고유 id
    private int episode_id; // 회차 고유 id
    private String episode_title; // 회차 제목
    private float ep_starscore; // 회차 별점
    private String ep_thumburl; // 회차 썸네일 URL
    private String reg_date; // 등록일

    //Constructor
    public Episode(int id_e, int episode_id, String episode_title, float ep_starscore, String ep_thmburl, String reg_date)
    {
        this.id_e = id_e;
        this.episode_id = episode_id;
        this.episode_title = episode_title;
        this.ep_starscore = ep_starscore;
        this.ep_thumburl = ep_thmburl;
        this.reg_date = reg_date;
    }

    //getter methods
    public int getId_e(){return id_e;}
    public int getEpisode_id(){return episode_id;}
    public String getEpisode_title(){return episode_title;}
    public float getEp_starscore(){return ep_starscore;}
    public String getEp_thumburl(){return ep_thumburl;}
    public String getReg_date(){return reg_date;}

    /*
     * 현재 자신이 가지고 있는 멤버들을 출력하는 descriptor method
     */
    public void printToon(){
        System.out.println("id = " + id_e + " episode_id = " + episode_id + " ep_title : " + episode_title + " ep_ss : " + ep_starscore + " ep_thumb : " + ep_thumburl + " reg_date : " + reg_date);
    }
}
