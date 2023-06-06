package com.example.box.Entity;

public class Store {
    private String storeId;
    private String storeName;
    private String storeImage;
    private String openingTime;
    private String closingTime;
    private String phoneNumber;
    private String storeAddress;
    private String status;
    private String ratingScore;
    private double ratings;

    public Store(String storeId, String storeName, String storeImage, String storeAddress) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.storeImage = storeImage;
        this.storeAddress = storeAddress;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreImage() {
        return storeImage;
    }

    public void setStoreImage(String storeImage) {
        this.storeImage = storeImage;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRatingScore() {
        return ratingScore;
    }

    public void setRatingScore(String ratingScore) {
        this.ratingScore = ratingScore;
    }

    public double getRatings() {
        return ratings;
    }

    public void setRatings(double ratings) {
        this.ratings = ratings;
    }
}
