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

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.concurrent.Executor;

public class UserMetaData extends BroadcastReceiver {
    private static UserMetaData mInstance;
    private String token;
    private double latitude;
    private double longitude;
    private LocationManager locationManager;
    private boolean mLocationPermissionGranted = false;
    private Context context;

    private UserMetaData() {
        super();
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
    }
}
