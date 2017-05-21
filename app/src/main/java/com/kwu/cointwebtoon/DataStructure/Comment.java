package com.kwu.cointwebtoon.DataStructure;

public class Comment implements Comparable<Comment>{
    private int comment_id;
    private int id;
    private int ep_id;
    private String writer;
    private String nickname;
    private String content;
    private int likes;
    private String time;
    private int cutNumber;
    private boolean isBest;

    /**
     * Constructor
     */
    public Comment(int comment_id, int id, int ep_id, String writer, String nickname, String content,
                   int likes, String time, int cutNumber,boolean isBest){
        this.comment_id = comment_id;
        this.id = id;
        this. ep_id = ep_id;
        this.writer = writer;
        this.nickname = nickname;
        this.content = content;
        this.likes = likes;
        this.time = time;
        this.cutNumber = cutNumber;
        this.isBest = isBest;
    }

    /**
     * Getter Methods
     */
    public int getComment_id(){return comment_id;}
    public int getId(){return id;}
    public int getEp_id(){return ep_id;}
    public String getWriter(){return writer;}
    public String getNickname(){return nickname;}
    public String getContent(){return content;}
    public int getLikes(){return likes;}
    public String getTime(){return time;}
    public int getCutNumber(){return cutNumber;}
    public boolean isBest(){return isBest;}

    /**
     *Setter Methods
     */
    public void setBest(boolean best){this.isBest = best;}
    public void setLikes(int likes){this.likes = likes;}


    ///------------- ArrayList.Contains 기능 사용하기 위해 만든 메소드------//////
    @Override
    public boolean equals(Object obj) {
        Comment comment = (Comment) obj;
        if (this.comment_id == comment.getComment_id())
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


    @Override
    public int compareTo(Comment another) {
        if(this.likes > another.getLikes()){
            return 1;
        }else if(this.likes < another.getLikes()){
            return -1;
        }else{
            return 0;
        }
    }
}
