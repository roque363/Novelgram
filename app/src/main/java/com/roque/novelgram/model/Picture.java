package com.roque.novelgram.model;

public class Picture {

    private String picture;
    private String name;
    private String time;
    private String like_number = "0";

    public Picture () {}

    public Picture(String picture, String name, String time, String like_number) {
        this.picture = picture;
        this.name = name;
        this.time = time;
        this.like_number = like_number;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getName() {
        return name;
    }

    public void setName(String username) {
        this.name = username;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLike_number() {
        return like_number;
    }

    public void setLike_number(String like_number) {
        this.like_number = like_number;
    }
}
