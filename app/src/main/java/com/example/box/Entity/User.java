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
}
