package com.adretsoftwares.cellsecuritycare.model;

public class LocationEvent {
    private Double latitude;
    private Double longitude;

    public LocationEvent(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}