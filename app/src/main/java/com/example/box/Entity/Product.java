package com.example.box.Entity;

public class Product {
    public static final int FAST_FOOD_TYPE = 1;
    public static final int DRINK_TYPE = 2;
    public static final int VIETNAMESE_TYPE = 3;
    public static final int KOREAN_TYPE = 4;
    public static final int JAPANESE_TYPE = 5;
    public static final int OTHER_TYPE = 6;

    private String productId;
    private String productImg;
    private String productName;
    private double productPrice;
    private String note;
    private String description;
    private String status;
    private int numberOfBuyer;
    private String section;

    private int curQuantity;
    private String customerNote;

    public Product(String productImg, String productName, double productPrice, String section) {
        this.productImg = productImg;
        this.productName = productName;
        this.productPrice = productPrice;
        this.section = section;
    }

    public Product(String productId, String productImg, String productName,
                   double productPrice, String section, String description) {
        this.productId = productId;
        this.productImg = productImg;
        this.productName = productName;
        this.productPrice = productPrice;
        this.section = section;
        this.description = description;
        this.curQuantity = 0;
    }

    public Product(String productImg, String productName, double productPrice,
                   String note, String description, String status, int numberOfBuyer) {
        this.productImg = productImg;
        this.productName = productName;
        this.productPrice = productPrice;
        this.note = note;
        this.description = description;
        this.status = status;
        this.numberOfBuyer = numberOfBuyer;
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

    public int getNumberOfBuyer() {
        return numberOfBuyer;
    }

    public void setNumberOfBuyer(int numberOfBuyer) {
        this.numberOfBuyer = numberOfBuyer;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getCustomerNote() {
        return customerNote;
    }

    public void setCustomerNote(String customerNote) {
        this.customerNote = customerNote;
    }

    public int getCurQuantity() {
        return curQuantity;
    }

    public void setCurQuantity(int curQuantity) {
        this.curQuantity = curQuantity;
    }
}
