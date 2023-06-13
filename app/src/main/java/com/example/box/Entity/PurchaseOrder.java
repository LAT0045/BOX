package com.example.box.Entity;

public class PurchaseOrder {

    private String storeName;
    private String status;
    private String firstProductName;
    private String firstProductImg;
    private String price;

    public PurchaseOrder(String storeName, String status, String firstProductName, String firstProductImg, String price) {
        this.storeName = storeName;
        this.status = status;
        this.firstProductName = firstProductName;
        this.firstProductImg = firstProductImg;
        this.price = price;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFirstProductName() {
        return firstProductName;
    }

    public void setFirstProductName(String firstProductName) {
        this.firstProductName = firstProductName;
    }

    public String getFirstProductImg() {
        return firstProductImg;
    }

    public void setFirstProductImg(String firstProductImg) {
        this.firstProductImg = firstProductImg;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
