package com.jmapplication.com.episodeactivity;

/**
 * Created by jm on 2017-02-27.
 */

public class Episode {
    public int ep_id;
    public String subtitle;
    public String thumbURL;

    public Episode(int ep_id, String subtitle, String thumbURL){
        this.ep_id = ep_id;
        this.subtitle = subtitle;
        this.thumbURL = thumbURL;
    }
}
