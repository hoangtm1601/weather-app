package com.example.tnc.weather.Model;

import java.io.Serializable;

public class LocationInfo implements Serializable {
    private String locationKey;
    private String locationName;

    public LocationInfo() {
    }

    public LocationInfo(String locationKey, String locationName) {
        this.locationKey = locationKey;
        this.locationName = locationName;
    }

    public String getLocationKey() {
        return locationKey;
    }

    public void setLocationKey(String locationKey) {
        this.locationKey = locationKey;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}
