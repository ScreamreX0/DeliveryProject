package com.example.deliveryproject;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class Items {
    public interface IListItems {
    }

    public static class Shop {
        Object name;
        Object photoPath;
        Object isOpen;
        Iterable<DataSnapshot> products;

        public Shop(Object name, Object photoPath, Object isOpen, Iterable<DataSnapshot> products) {
            this.name = name;
            this.photoPath = photoPath;
            this.isOpen = isOpen;
            this.products = products;
        }

        public Object getName() {
            return name;
        }

        public void setName(Object name) {
            this.name = name;
        }

        public Object getPhotoPath() {
            return photoPath;
        }

        public void setPhotoPath(Object photoPath) {
            this.photoPath = photoPath;
        }

        public Object getIsOpen() {
            return isOpen;
        }

        public void setIsOpen(Object isOpen) {
            this.isOpen = isOpen;
        }

        public Iterable<DataSnapshot> getProducts() {
            return products;
        }

        public void setProducts(Iterable<DataSnapshot> products) {
            this.products = products;
        }
    }

    public static class Product implements IListItems{
        String name;
        String photoPath;
        String price;

        public Product(String name, String photoPath, String price) {
            this.name = name;
            this.photoPath = photoPath;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhotoPath() {
            return photoPath;
        }

        public void setPhotoPath(String photoPath) {
            this.photoPath = photoPath;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }
    }
}

