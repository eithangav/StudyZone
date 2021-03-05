package com.example.studyzone.ui.zone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studyzone.R;
import com.example.studyzone.data.user.LoggedInUser;
import com.example.studyzone.data.zone.CheckInFetcher;
import com.example.studyzone.data.zone.RateFetcher;
import com.example.studyzone.data.zone.ZoneFetcher;
import com.example.studyzone.ui.form.FormActivity;
import com.example.studyzone.ui.map.MapActivity;

public class ZoneActivity extends AppCompatActivity {

    private LoggedInUser loggedInUser;
    private int id = 0;

    // status bar's fields
    private TextView logOutTextView;
    private TextView goBackTextView;
    private TextView userEmailTextView;

    // zone's fields
    private TextView zoneNameTextView;
    private TextView zoneDescriptionTextView;
    private RatingBar crowdedRatingBar;
    private RatingBar foodRatingBar;
    private RatingBar priceRatingBar;
    private Button checkInButton;
    private EditText commentEditText;
    // TODO: add layout objects:
    //private XXX reviews
    //private Button rateButton


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone);

        //sets the required loggedInUser derived from the intent's trigger caller
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null){
            LoggedInUser user = (LoggedInUser)bundle.getSerializable("loggedInUser");
            int zoneId = (int)bundle.getSerializable("zoneId");
            if (user != null)
                this.loggedInUser = user;
            if (zoneId > 0)
                this.id = zoneId;
        }

        // use helper method to set up the status bar
        statusBarSetUp();

        // fetch and set up zone's fields
        fetchZone();

        // check in button onClick listener
        checkInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCheckIn(v);
            }
        });

        // TODO: implement onClick for Rate button
    }

    /**
     * sets the status bar's layout fields using the loggedInUser fields
     * and implements onClick for the required status bar links
     */
    public void statusBarSetUp() {
        // get status_bar layout fields
        goBackTextView = findViewById(R.id.back_to_some_page_link);
        logOutTextView = findViewById(R.id.logout_link);
        userEmailTextView = findViewById(R.id.user_email);
        // set fields value
        goBackTextView.setVisibility(View.VISIBLE);
        goBackTextView.setText("Back to map");
        userEmailTextView.setText(loggedInUser.getUserEmail());

        // on Logout click --> move to Login screen
        logOutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to sign in page
                Intent intent = new Intent(v.getContext(), FormActivity.class);
                startActivity(intent);
            }
        });

        // on Go back click --> go back to map screen
        goBackTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to map page
                Bundle bundle = new Bundle();
                bundle.putSerializable("loggedInUser", loggedInUser);
                Intent intent = new Intent(v.getContext(), MapActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    /**
     * fetching zone's fields from the server using ZoneFetcher
     * if succeeded: sets up the zone's field using zoneSetUp helper method
     */
    public void fetchZone() {
        final ZoneFetcher fetcher = new ZoneFetcher(this);

        // server request
        fetcher.dispatchRequest(this.id, new ZoneFetcher.ZoneResponseListener() {
            @Override
            public void onResponse(ZoneFetcher.ZoneResponse response) {
                if (response.isError)
                    Log.e("ZoneActivity", "Error fetching zone's fields");
                else
                    zoneSetUp(response);
            }
        });
    }

    /**
     * sets up the zone's field by the response's content
     * @param response the response of the server brought by ZoneFetcher
     */
    public void zoneSetUp(ZoneFetcher.ZoneResponse response) {
        if(response!=null){
            // get zone's layout fields
            zoneNameTextView = findViewById(R.id.zone_name);
            zoneDescriptionTextView = findViewById(R.id.zone_description);
            crowdedRatingBar = findViewById(R.id.crowded_rating_bar);
            foodRatingBar = findViewById(R.id.food_rating_bar);
            priceRatingBar = findViewById(R.id.price_rating_bar);
            // TODO: after adding layout objects:
            // reviews = findView...

            // set zone's layout fields according to the response
            zoneNameTextView.setText(response.name);
            zoneDescriptionTextView.setText(response.description);
            crowdedRatingBar.setRating((float)response.crowdedRating);
            foodRatingBar.setRating((float)response.foodRating);
            priceRatingBar.setRating((float)response.priceRating);
            // TODO: after adding layout objects:
            // reviews. ...
        }
    }

    /**
     * uses CheckInFetcher in order to request the server to check in
     * @param view to Toast in if check in succeeded
     */
    public void fetchCheckIn(View view) {
        final CheckInFetcher fetcher = new CheckInFetcher(this);

        // server request
        fetcher.dispatchRequest(id, loggedInUser.getUserEmail(),
                new CheckInFetcher.CheckInResponseListener() {
            @Override
            public void onResponse(CheckInFetcher.CheckInResponse response) {
                if (response.isError)
                    Log.e("ZoneActivity", "Error fetching CheckIn");
                else
                    Toast.makeText(view.getContext(), "Successfully checked in!", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * uses RateFetcher in order to request the server to update user's ratings and comment
     * @param view
     */
    public void fetchRate(View view) {
        final RateFetcher fetcher = new RateFetcher(this);

        final int crowdedUserRating;
        final int foodUserRating;
        final int priceUserRating;
        final String userReview;

        // TODO: get users rating from the layout objects and fetch

        //fetcher.dispatchRequest(id, );
    }
}