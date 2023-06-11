package com.example.box.Entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Product implements Parcelable {
    public static final int FAST_FOOD_TYPE = 1;
    public static final int DRINK_TYPE = 2;
    public static final int VIETNAMESE_TYPE = 3;
    public static final int KOREAN_TYPE = 4;
    public static final int JAPANESE_TYPE = 5;
    public static final int OTHER_TYPE = 6;

    private String productId;
    private String storeId;
    private String productImg;
    private String productName;
    private int productPrice;
    private String note;
    private String description;
    private String status;
    private int numberOfBuyer;
    private String section;

    private int curQuantity;
    private String customerNote = "";
    private List<Product> toppingList;

    public Product(String productImg, String productName, int productPrice, String section) {
        this.productImg = productImg;
        this.productName = productName;
        this.productPrice = productPrice;
        this.section = section;
    }

    public Product(String productId, String productImg, String productName,
                   int productPrice, String section, String description, String storeId) {
        this.productId = productId;
        this.productImg = productImg;
        this.productName = productName;
        this.productPrice = productPrice;
        this.section = section;
        this.description = description;
        this.storeId = storeId;
        this.curQuantity = 0;
        this.toppingList = new ArrayList<>();
    }

    public Product(String productImg, String productName, int productPrice,
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

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
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

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public List<Product> getToppingList() {
        return toppingList;
    }

    public void setToppingList(List<Product> toppingList) {
        this.toppingList = toppingList;
    }

    protected Product(Parcel in) {
        this.productId = in.readString();
        this.productImg = in.readString();
        this.productName = in.readString();
        this.productPrice = in.readInt();
        this.section = in.readString();
        this.description = in.readString();
        this.storeId = in.readString();
        this.curQuantity = in.readInt();
        this.toppingList = in.createTypedArrayList(Product.CREATOR);
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(productId);
        dest.writeString(productImg);
        dest.writeString(productName);
        dest.writeInt(productPrice);
        dest.writeString(section);
        dest.writeString(description);
        dest.writeString(storeId);
        dest.writeInt(curQuantity);
        dest.writeTypedList(toppingList);
    }
}
