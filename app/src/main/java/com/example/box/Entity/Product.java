package com.example.box.Entity;

public class Product {
    private String productImg;
    private String productName;
    private double productPrice;
    private String note;
    private String description;
    private String status;



    public Product(String productImg, String productName, double productPrice) {
        this.productImg = productImg;
        this.productName = productName;
        this.productPrice = productPrice;
    }

    public Product(String productImg, String productName, double productPrice, String note, String description, String status) {
        this.productImg = productImg;
        this.productName = productName;
        this.productPrice = productPrice;
        this.note = note;
        this.description = description;
        this.status = status;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
