package com.example.labourbooking.obj;

public class User {
    String name;
    String email;
    String mobile;
    String uid;
    String img;


    public User(String name, String email, String mobile, String uid, String img) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.uid = uid;
        this.img = img;
    }

    public User() {
    }


    public User(String name, String email, String mobile, String uid) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }


    public String getMobile() {
        return mobile;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUid() {
        return uid;
    }

    public String getImg() {
        return img;
    }

}
