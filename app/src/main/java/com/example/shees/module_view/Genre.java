package com.example.shees.module_view;

/**
 * Created by epcej on 2017-03-02.
 */

public class Genre {
    private int id_G;                                        //웹툰 고유 ID
    private String genre;                       //에피소드 ID

    //Constructor
    public Genre(int id_G, String genre) {
        this.id_G = id_G;
        this.genre = genre;
    }

    //getter methods
    public int getId() {
        return id_G;
    }

    public String getGenre() {
        return genre;
    }

    //Setter Method
    public void setId(int id) {
        this.id_G = id;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    ///------------- ArrayList.Contains 기능 사용하기 위해 만든 메소드------//////
    @Override
    public boolean equals(Object obj) {
        Genre genre = (Genre) obj;
        if (this.id_G == genre.id_G)
            return true;
        else
            return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id_G;
        return result;
    }
    ///------------- ArrayList.Contains 기능 사용하기 위해 만든 메소드------end//
}
