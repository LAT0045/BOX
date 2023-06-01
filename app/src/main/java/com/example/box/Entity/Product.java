package com.example.box.Entity;

public class Product {
    private String productImg;
    private String productName;
    private double productPrice;

    public Product(String productImg, String productName, double productPrice) {
        this.productImg = productImg;
        this.productName = productName;
        this.productPrice = productPrice;
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
}
