package com.example.labourbooking.obj;

public class Offer {
    String offererId;
    String offer;
    String offerId;
    public Offer() {
    }

    public Offer(String offererId, String offer, String offerId) {
        this.offererId = offererId;
        this.offer = offer;
        this.offerId = offerId;
    }

    public String getOffererId() {
        return offererId;
    }

    public String getOffer() {
        return offer;
    }

    public String getOfferId() {
        return offerId;
    }
}
