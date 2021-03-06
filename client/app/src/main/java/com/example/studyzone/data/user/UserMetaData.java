package com.example.studyzone.data.user;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.studyzone.ui.map.LocationUpdateEvent;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;

public class UserMetaData extends BroadcastReceiver {
    private static UserMetaData mInstance;
    private String token;
    private double latitude;
    private double longitude;
    private Set<LocationUpdateEvent> location_listeners;

    private UserMetaData() {
        super();
        this.location_listeners = new HashSet<>();
    }

    public static UserMetaData getInstance(){
        if(mInstance == null){
            mInstance = new UserMetaData();
        }
        return mInstance;
    }

    public void setToken(String token){
        this.token = token;
    }

    public String getToken(){
        return this.token;
    }

    public double getLongitude(){
        return this.longitude;
    }

    public double getLatitude(){
        return this.latitude;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.longitude = (double) intent.getExtras().get("longitude");
        this.latitude = (double) intent.getExtras().get("latitude");
        Log.d("asd", "" + this.longitude);
        Log.d("asd", ""+this.latitude);
        Log.d("asd", ""+this.token);
        LatLng location = new LatLng(this.latitude, this.longitude);
        for(LocationUpdateEvent listener: location_listeners){
            listener.callback(location);
        }
    }

    public void addListener(LocationUpdateEvent listener){
        location_listeners.add(listener);
    }

    public void removeListener(LocationUpdateEvent listener){
        location_listeners.remove(listener);
    }
}
