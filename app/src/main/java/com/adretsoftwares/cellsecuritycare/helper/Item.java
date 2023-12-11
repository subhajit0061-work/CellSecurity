package com.adretsoftwares.cellsecuritycare.helper;

public class Item {
    String name;

    public String getName() {
        return name;
    }

    public Item(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    String image;
}
