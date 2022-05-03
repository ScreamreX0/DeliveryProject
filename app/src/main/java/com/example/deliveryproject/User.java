package com.example.deliveryproject;

public class User {
    private String shopName;
    private String role;

    public User(String shopName, String role) {
        this.shopName = shopName;
        this.role = role;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
