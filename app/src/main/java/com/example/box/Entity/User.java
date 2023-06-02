package com.example.box.Entity;

public class User {
    private String address;
    private String phoneNumber;
    private String name;
    private String avatar;

    public User(String address, String phoneNumber, String name, String avatar) {
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.avatar = avatar;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
