package com.jmapplication.com.episodeactivity;

/**
 * Created by epcej on 2017-03-02.
 */

public class Weekday {
    private int id_W;                                        //웹툰 고유 ID
    private int weekday;                       //에피소드 ID

    //Constructor
    public Weekday(int id_W, int weekday) {
        this.id_W = id_W;
        this.weekday = weekday;
    }

    //getter methods
    public int getId() {
        return id_W;
    }

    public int getWeekday(){ return weekday;}

    //Setter Method
    public void setId(int id) {
        this.id_W = id;
    }

    public void setWeekday(int weekday){ this.weekday = weekday;}

    ///------------- ArrayList.Contains 기능 사용하기 위해 만든 메소드------//////
    @Override
    public boolean equals(Object obj) {
        Weekday weekday = (Weekday) obj;
        if (this.id_W == weekday.id_W)
            return true;
        else
            return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id_W;
        return result;
    }
    ///------------- ArrayList.Contains 기능 사용하기 위해 만든 메소드------end//
}
