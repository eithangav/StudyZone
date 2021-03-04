package com.example.studyzone.ui.map;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.studyzone.R;
import com.example.studyzone.data.user.LoggedInUser;
import com.example.studyzone.ui.form.FormActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LoggedInUser loggedInUser = null;

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
        if (bundle != null){
            LoggedInUser user = (LoggedInUser)bundle.getSerializable("loggedInUser");
            if (user != null)
                this.loggedInUser = user;
        }

        // use helper method to set up the status bar
        statusBarSetUp();

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
        loadZonesMarkers();

        // TODO: change userPosition to loggedInUser.getUserLocation()
        LatLng userLocation = new LatLng(32.07220007424625, 34.7750024858776);
        mMap.addMarker(new MarkerOptions().position(userLocation).title("You are here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));

        //mMap.SetOnMarkerClickListener - override onMarkerClick function
    }

    public void loadZonesMarkers() {
        // TODO: GET request and add markers by locations
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
}