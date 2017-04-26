package importClasses;

import java.util.ArrayList;

public class ImagesInEpisode {
    private int id_i;   //웹툰 아이디
    private int ep_id;  //회차(몇 화인가?)
    private char toonType;
    private ArrayList<String> images;   //imageURL들이 순서대로 담길 ArrayList
    private String mention;

    //Constructor
    public ImagesInEpisode(int id_i, int ep_id, char toonType) {
        this.id_i = id_i;
        this.ep_id = ep_id;
        this.toonType = toonType;
        images = null;
        mention = null;
    }

    //Getter methods
    public int getId_i() {
        return id_i;
    }
    public int getEp_id() {
        return ep_id;
    }
    public char getToonType(){return toonType;}
    public ArrayList<String> getImages() {
        return images;
    }
    public String getMention(){return mention;}
    //Setter methods
    public void setMention(String mention){this.mention = mention;}
    public void setImages(ArrayList<String> images){this.images = images;}

    @Override
    public String toString(){
        return "[ID : " + id_i + "(EP : " + ep_id + ")]";
    }
}