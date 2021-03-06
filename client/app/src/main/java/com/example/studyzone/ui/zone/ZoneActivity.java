package com.example.studyzone.ui.zone;

import androidx.annotation.ColorRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.text.Editable;
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

import org.json.JSONArray;

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
    private TextView clearCrowdedRating;
    private TextView clearFoodRating;
    private TextView clearPriceRating;
    private Button checkInButton;
    private EditText commentEditText;
    private RecyclerView reviewsRecyclerView;
    private Button rateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone);

        //sets the required loggedInUser and zone's ID derived from the intent's trigger caller
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
            clearCrowdedRating = findViewById(R.id.clear_crowded_rating);
            clearFoodRating = findViewById(R.id.clear_food_rating);
            clearPriceRating = findViewById(R.id.clear_price_rating);
            reviewsRecyclerView = findViewById(R.id.reviews);
            checkInButton = findViewById(R.id.check_in_button);
            rateButton = findViewById(R.id.rate_button);
            commentEditText = findViewById(R.id.multiline_comment);


            // set zone's layout fields according to the response
            zoneNameTextView.setText(response.name);
            zoneDescriptionTextView.setText(response.description);
            crowdedRatingBar.setRating((float)response.crowdedRating);
            foodRatingBar.setRating((float)response.foodRating);
            priceRatingBar.setRating((float)response.priceRating);
            clearCrowdedRating.setVisibility(View.INVISIBLE);
            clearFoodRating.setVisibility(View.INVISIBLE);
            clearPriceRating.setVisibility(View.INVISIBLE);

            // load reviews
            JSONArray reviews = response.reviews;
            reviewsRecyclerView.setHasFixedSize(true);
            reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            reviewsRecyclerView.setAdapter(new ReviewsAdapter(reviews));

            // rating bars editors
            crowdedRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    ratingBar.setRating(rating);
                    clearCrowdedRating.setVisibility(View.VISIBLE);
                }
            });
            foodRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    ratingBar.setRating(rating);
                    clearFoodRating.setVisibility(View.VISIBLE);
                }
            });
            priceRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    ratingBar.setRating(rating);
                    clearPriceRating.setVisibility(View.VISIBLE);
                }
            });

            // "reset" rating bars listeners
            clearCrowdedRating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearCrowdedRating.setVisibility(View.INVISIBLE);
                    crowdedRatingBar.setRating((float)response.crowdedRating);
                }
            });
            clearFoodRating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearFoodRating.setVisibility(View.INVISIBLE);
                    foodRatingBar.setRating((float)response.crowdedRating);
                }
            });
            clearPriceRating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearPriceRating.setVisibility(View.INVISIBLE);
                    foodRatingBar.setRating((float)response.crowdedRating);
                }
            });

            // check in button listener
            checkInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fetchCheckIn(v);
                }
            });

            // rate button listener
            rateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fetchRate(v);
                }
            });
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
                        if (response.isError || !response.hasSucceeded)
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

        final float crowdedUserRating = crowdedRatingBar.getRating();
        final float foodUserRating = foodRatingBar.getRating();
        final float priceUserRating = priceRatingBar.getRating();
        String userReview = commentEditText.getText().toString();

        fetcher.dispatchRequest(id, crowdedUserRating, foodUserRating, priceUserRating, userReview,
                new RateFetcher.RateResponseListener() {
                    @Override
                    public void onResponse(RateFetcher.RateResponse response) {
                        if (response.isError || !response.hasSucceeded)
                            Log.e("ZoneActivity", "Error fetching Rate");
                        else
                            Toast.makeText(view.getContext(), "Thank you for your rating!", Toast.LENGTH_LONG).show();
                    }
                });
    }
}