package com.example.studyzone.data.user;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Data class that captures logged in user's information
 */
public class LoggedInUser implements Serializable {

    private String email;
    private double longitude;
    private double latitude;

    public LoggedInUser(String email, LatLng location) {
        this.email = email;
        this.latitude = location.latitude;
        this.longitude = location.longitude;
    }

    public String getUserEmail() {
        return email;
    }

    public LatLng getUserLocation() {
        return new LatLng(latitude, longitude);
    }
}