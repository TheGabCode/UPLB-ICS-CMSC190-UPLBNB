package com.cmsc190.ics.uplbnb;

/**
 * Created by Dell on 7 Mar 2018.
 */

public class Review {
    private String authorName;
    private String comment;
    private float rating;
    private String title;



    public Review(String authorName, String comment, float rating, String title) {
        this.authorName = authorName;
        this.comment = comment;
        this.rating = rating;
        this.title = title;
    }

    public Review(){

    }
    public String getAuthorName() {
        return authorName;
    }

    public String getComment() {
        return comment;
    }

    public float getRating() {
        return rating;
    }

    public String getTitle(){ return title;}
}
