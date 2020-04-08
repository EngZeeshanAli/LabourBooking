package com.example.labourbooking.chatapp;

public class Message {
    private String sender;
    private String recievier;
    private String dateAndTime;
    private String msg;

    public Message() {
    }

    public Message(String sender, String recievier, String dateAndTime, String msg) {
        this.sender = sender;
        this.recievier = recievier;
        this.dateAndTime = dateAndTime;
        this.msg = msg;
    }

    public String getSender() {
        return sender;
    }

    public String getRecievier() {
        return recievier;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public String getMsg() {
        return msg;
    }
}
