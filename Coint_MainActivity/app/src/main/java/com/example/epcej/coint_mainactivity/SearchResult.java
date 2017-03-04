package com.example.epcej.coint_mainactivity;

/**
 * Created by epcej on 2017-03-04.
 */

public class SearchResult {
        int id;
        String title, artist, thumbUrl;
        float starScore;

        public SearchResult(int id, String title, String artist, String thumbUrl, float starScore){
            this.id  = id;
            this.title = title;
            this.artist = artist;
            this.thumbUrl = thumbUrl;
            this.starScore = starScore;
        }
}
