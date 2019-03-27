package com.roque.novelgram.model;

import com.google.firebase.firestore.Exclude;

public class Picture {

    private String key;
    private String picture;
    private String name;
    private String time;
    private String like_number = "0";
    private String description;
    private String extra;

    public Picture () {}

    public Picture(String picture, String name, String time, String like_number) {
        this.picture = picture;
        this.name = name;
        this.time = time;
        this.like_number = like_number;
    }

    public Picture(String key, String picture, String name, String time, String like_number, String description, String extra) {
        this.key = key;
        this.picture = picture;
        this.name = name;
        this.time = time;
        this.like_number = like_number;
        this.description = description;
        this.extra = extra;
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

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }

    @Exclude
    public String getDescription() {
        return description;
    }

    @Exclude
    public void setDescription(String description) {
        this.description = description;
    }

    @Exclude
    public String getExtra() {
        return extra;
    }

    @Exclude
    public void setExtra(String extra) {
        this.extra = extra;
    }
}
