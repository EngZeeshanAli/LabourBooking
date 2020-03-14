package com.example.labourbooking.obj;

public class Post {
private String posterId;
private String title;
private String description;
private String img;
private String location;
private String assingTo;
private String timeUtils;

    public Post() {
    }

    public Post(String posterId, String title, String description, String img, String location, String assingTo, String timeUtils) {
        this.posterId = posterId;
        this.title = title;
        this.description = description;
        this.img = img;
        this.location = location;
        this.assingTo = assingTo;
        this.timeUtils = timeUtils;
    }


    public String getPosterId() {
        return posterId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImg() {
        return img;
    }

    public String getLocation() {
        return location;
    }

    public String getAssingTo() {
        return assingTo;
    }

    public String getTimeUtils() {
        return timeUtils;
    }
}
