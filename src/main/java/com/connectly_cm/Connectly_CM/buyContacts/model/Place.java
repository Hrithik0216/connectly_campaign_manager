package com.connectly_cm.Connectly_CM.buyContacts.model;

public class Place {
    private String country;
    private String state;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Place(String country, String state) {
        this.country = country;
        this.state = state;
    }
}
