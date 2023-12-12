package com.adretsoftwares.cellsecuritycare;

import java.io.Serializable;

public class Notify implements Serializable {

    private String imageUrl;

    public String getImageUr2() {
        return imageUr2;
    }

    public void setImageUr2(String imageUr2) {
        this.imageUr2 = imageUr2;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String imageUr2;
    private String message;
    private String date;

    private String coordinates;

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public String getCoordinates() {
        return coordinates;
    }

}
