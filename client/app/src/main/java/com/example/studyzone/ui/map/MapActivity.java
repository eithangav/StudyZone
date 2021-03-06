package com.example.studyzone.ui.map;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.studyzone.R;
import com.example.studyzone.data.user.LoggedInUser;
import com.example.studyzone.data.user.UserMetaData;
import com.example.studyzone.data.zone.MarkersFetcher;
import com.example.studyzone.ui.form.FormActivity;
import com.example.studyzone.ui.zone.ZoneActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private LoggedInUser loggedInUser;
    private JSONArray markers;

    // status bar objects initialization
    private TextView logOutTextView;
    private TextView goBackTextView;
    private TextView userEmailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //sets the required loggedInUser derived from the intent's trigger caller
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            LoggedInUser user = (LoggedInUser) bundle.getSerializable("loggedInUser");
            UserMetaData.getInstance().addListener(new LocationUpdateEvent() {
                @Override
                public void callback(LatLng location) {
                    user.updateUserLocation(location);
                }
            });
            if (user != null)
                this.loggedInUser = user;
        }

        // use helper method to set up the status bar
        statusBarSetUp();

        // fetch zones' markers from the server and update the markers JSON Array
        fetchMarkers();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // load all zones markers from DB
        if (markers == null)
            fetchMarkers();

        LatLng userLocation = new LatLng(UserMetaData.getInstance().getLatitude(),
                UserMetaData.getInstance().getLongitude());
        Marker current_location = mMap.addMarker(new MarkerOptions().position(userLocation).title("You are here"));
        current_location.setTag(-1);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

    }

    /**
     * fetches the zones' location and name using MarkersFetcher
     * to be used later in loadZonesMarkers method
     */
    public void fetchMarkers() {
        MarkersFetcher fetcher = new MarkersFetcher(this);

        fetcher.dispatchRequest(new MarkersFetcher.MarkersResponseListener() {
            @Override
            public void onResponse(MarkersFetcher.MarkersResponse response) {
                if (response.isError)
                    Log.e("MapActivity", "Error fetching markers");
                else{
                    markers = response.markers;
                    loadZonesMarkers();
                }
            }
        });
    }

    /**
     * iterates markers JSON Array that was fetched and adds the markers to the map
     */
    public void loadZonesMarkers() {
        try {
            for (int i=0; i<markers.length(); i++) {
                JSONObject marker = markers.getJSONObject(i);
                LatLng location = new LatLng(marker.getDouble("latitude"),
                        marker.getDouble("longitude"));
                String name = marker.getString("name");
                Marker current = mMap.addMarker(new MarkerOptions().position(location).title(name));
                current.setTag(marker.getInt("_id"));
                current.setSnippet(name);
            }
            mMap.setOnMarkerClickListener(this);
        }
        catch (JSONException e) {
            Log.e("MapActivity", "Error loading markers");
        }
    }



    public void statusBarSetUp() {
        // get status_bar layout fields
        goBackTextView = findViewById(R.id.back_to_some_page_link);
        logOutTextView = findViewById(R.id.logout_link);
        userEmailTextView = findViewById(R.id.user_email);
        // set fields value
        goBackTextView.setVisibility(View.GONE);
        userEmailTextView.setText(loggedInUser.getUserEmail());

        logOutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to sign in page
                Intent intent = new Intent(v.getContext(), FormActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        int id = (int) marker.getTag();
        Log.i("Marker", "Marker clicked : " + id);
        if(id != -1){
            Bundle bundle = new Bundle();
            bundle.putSerializable("loggedInUser", loggedInUser);
            bundle.putInt("zoneId", id);
            Intent intent = new Intent(this, ZoneActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        return false;
    }

}