package com.example.deliveryproject;

public class Items {
    public interface IListItems {
    }

    public static class Shop implements IListItems {
        String name;
        String photoPath;
        boolean isOpen;

        public Shop(String name, String photoPath, boolean isOpen) {
            this.name = name;
            this.photoPath = photoPath;
            this.isOpen = isOpen;
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

        public boolean isOpen() {
            return isOpen;
        }

        public void setOpen(boolean open) {
            isOpen = open;
        }
    }


}

