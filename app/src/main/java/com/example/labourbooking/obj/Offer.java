package com.example.labourbooking.obj;

public class Offer {
    String offererId;
    String offer;

    public Offer() {
    }

    public Offer(String offererId, String offer) {
        this.offererId = offererId;
        this.offer = offer;
    }

    public String getOffererId() {
        return offererId;
    }

    public String getOffer() {
        return offer;
    }
}
