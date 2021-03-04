package com.example.studyzone.ui.zone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.studyzone.R;
import com.example.studyzone.data.user.LoggedInUser;
import com.example.studyzone.ui.form.FormActivity;
import com.example.studyzone.ui.map.MapActivity;

public class ZoneActivity extends AppCompatActivity {

    private LoggedInUser loggedInUser = null;

    // status bar objects initialization
    private TextView logOutTextView;
    private TextView goBackTextView;
    private TextView userEmailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone);

        //sets the required loggedInUser derived from the intent's trigger caller
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null){
            LoggedInUser user = (LoggedInUser)bundle.getSerializable("loggedInUser");
            if (user != null)
                this.loggedInUser = user;
        }

        // use helper method to set up the status bar
        statusBarSetUp();
    }

    public void statusBarSetUp() {
        // get status_bar layout fields
        goBackTextView = findViewById(R.id.back_to_some_page_link);
        logOutTextView = findViewById(R.id.logout_link);
        userEmailTextView = findViewById(R.id.user_email);
        // set fields value
        goBackTextView.setVisibility(View.VISIBLE);
        goBackTextView.setText("Back to map");
        userEmailTextView.setText(loggedInUser.getUserEmail());

        logOutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to sign in page
                Intent intent = new Intent(v.getContext(), FormActivity.class);
                startActivity(intent);
            }
        });

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
}