package com.example.travelmantics;

import java.io.Serializable;

public class TravelDeal implements Serializable {
    private String id;
    private String title;
    private String price;
    private String description;
    private String url;

    public TravelDeal(){}

    public TravelDeal(String userId, String title, String price, String description) {
        id = userId;
        this.setTitle(title);
        this.setPrice(price);
        this.setDescription(description);
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
