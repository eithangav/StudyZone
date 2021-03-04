package com.example.studyzone.data.user;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Data class that captures logged in user's information
 */
public class LoggedInUser implements Serializable {

    private String email;
    private LatLng location;

    public LoggedInUser(String email, LatLng location) {
        this.email = email;
        this.location = location;
    }

    public String getUserEmail() {
        return email;
    }

    public LatLng getUserLocation() {
        return location;
    }
}