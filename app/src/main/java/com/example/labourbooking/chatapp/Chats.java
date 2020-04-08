package com.example.labourbooking.chatapp;

public class Chats {
    String sender, reciver;

    public Chats() {
    }

    public Chats(String sender, String reciver) {
        this.sender = sender;
        this.reciver = reciver;
    }

    public String getSender() {
        return sender;
    }

    public String getReciver() {
        return reciver;
    }

}
