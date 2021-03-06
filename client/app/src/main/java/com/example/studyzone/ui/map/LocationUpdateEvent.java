package com.example.studyzone.ui.map;

import com.google.android.gms.maps.model.LatLng;

public interface LocationUpdateEvent {
    void callback(LatLng location);
}
