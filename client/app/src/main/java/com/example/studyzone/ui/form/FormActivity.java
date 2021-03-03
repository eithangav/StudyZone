package com.example.studyzone.ui.form;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studyzone.R;
import com.example.studyzone.data.user.LoginFetcher;
import com.example.studyzone.data.user.RegistrationFetcher;
import com.example.studyzone.ui.map.MapActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

/**
 * Main Activity, used for both Login and Registration forms. Uses Presenters to switch them.
 * Initially loads Login form, and loads a new intent with the required Presenter
 * when the user clicks the subtitle link.
 */
public class FormActivity extends AppCompatActivity implements FormPresenterListener {

    //initial Presenter: Login
    private FormPresenter presenter = new LoginPresenter();

    private TextView titleTextView;
    private TextView subtitlePrefixTextView;
    private TextView subtitleSuffixTextView;
    private EditText firstEditText;
    private EditText secondEditText;
    private Button submitButton;
    private ProgressBar loadingProgressBar;
    private FormState formState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        //loads the required presenter kind (Login/Register)
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null){
            FormPresenter presenter = (FormPresenter)bundle.getSerializable("presenter");
            if (presenter != null)
                this.presenter = presenter;
        }

        //sets the required layer's fields
        titleTextView = findViewById(R.id.title);
        subtitlePrefixTextView = findViewById(R.id.subtitle_prefix);
        subtitleSuffixTextView = findViewById(R.id.subtitle_clickable_suffix);
        firstEditText = findViewById(R.id.first_field);
        secondEditText = findViewById(R.id.second_field);
        submitButton = findViewById(R.id.submit_button);
        loadingProgressBar = findViewById(R.id.loading);

        //sets the fields' initial values
        titleTextView.setText(presenter.getScreenTitle());
        subtitlePrefixTextView.setText(presenter.getSubtitlePrefix());
        subtitleSuffixTextView.setText(presenter.getSubtitleSuffix());
        firstEditText.setHint(presenter.getFirstTextFieldHint());
        secondEditText.setHint(presenter.getSecondTextFieldHint());
        submitButton.setText(presenter.getSubmitButtonTitle());
        submitButton.setEnabled(false);
        formState = new FormState();
        loadingProgressBar.setVisibility(View.GONE);

        //first EditText validation
        firstEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                presenter.validateEmail(formState, s.toString());
            }
        });

        //second EditText validation
        secondEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                presenter.validatePassword(formState, s.toString());
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: handle getting users token when clicked
                loadingProgressBar.setVisibility(View.VISIBLE);
                presenter.submitButtonTapped(v);
            }
        });

        subtitleSuffixTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                presenter.subtitleLinkTapped();
            }
        });

        // listener initialization
        presenter.setListener(this);
    }

    /**------------------FormPresenterListener methods implementation--------------------------
     * ------------------triggered by the Presenters' methods-------------------------------**/

    @Override
    public void fetchLogin(final View view) {
        final LoginFetcher fetcher = new LoginFetcher(view.getContext());
        final String email = ((EditText)findViewById(R.id.first_field)).getText().toString();
        final String password = ((EditText)findViewById(R.id.second_field)).getText().toString();
        final String token = "TEST_TOKEN"; // TODO: get user's real token
        final LatLng location = new LatLng(35, 35); // TODO: get user's real location
        // TODO: consider progressDialog usage instead of progressBar...

        fetcher.dispatchRequest(email, password, token, location, new LoginFetcher.LoginResponseListener() {
                    @Override
                    public void onResponse(LoginFetcher.LoginResponse response) {
                        loadingProgressBar.setVisibility(View.GONE);
                        if (response.isError)
                            Toast.makeText(view.getContext(), "Login server error", Toast.LENGTH_LONG).show();
                        else if (!response.emailExists)
                            Toast.makeText(view.getContext(), "Wrong Email address", Toast.LENGTH_LONG).show();
                        else if (!response.passwordMatches)
                            Toast.makeText(view.getContext(), "Wrong password", Toast.LENGTH_LONG).show();
                        else
                            moveToMapScreen();
                    }
                });
    }

    @Override
    public void fetchRegistration(final View view) {
        final RegistrationFetcher fetcher = new RegistrationFetcher(view.getContext());
        final String email = ((EditText)findViewById(R.id.first_field)).getText().toString();
        final String password = ((EditText)findViewById(R.id.second_field)).getText().toString();
        // TODO: consider progressDialog usage instead of progressBar...

        fetcher.dispatchRequest(email, password, new RegistrationFetcher.RegistrationResponseListener() {
            @Override
            public void onResponse(RegistrationFetcher.RegistrationResponse response) {
                loadingProgressBar.setVisibility(View.GONE);
                if (response.isError)
                    Toast.makeText(view.getContext(), "Registration server error", Toast.LENGTH_LONG).show();
                else if (!response.hasSucceeded)
                    Toast.makeText(view.getContext(), "Email already exists", Toast.LENGTH_LONG).show();
                else {
                    Toast.makeText(view.getContext(), "Successfully registered!", Toast.LENGTH_LONG).show();
                    moveToLoginScreen();
                }
            }
        });
    }

    @Override
    public void moveToRegisterScreen() {
        RegisterPresenter presenter = new RegisterPresenter();
        Bundle bundle = new Bundle();
        bundle.putSerializable("presenter", presenter);
        Intent intent = new Intent(this, FormActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void moveToLoginScreen() {
        LoginPresenter presenter = new LoginPresenter();
        Bundle bundle = new Bundle();
        bundle.putSerializable("presenter", presenter);
        Intent intent = new Intent(this, FormActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void moveToMapScreen() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    @Override
    public void setButtonState() {
        submitButton.setEnabled(formState.getFormState());
    }

    @Override
    public void showValidationError(String fieldName, boolean valid) {
        switch (fieldName){
            case "first":
                if (valid)
                    firstEditText.setError(null);
                else
                    firstEditText.setError("invalid Email address");
                break;
            case "second":
                if (valid)
                    secondEditText.setError(null);
                else
                    secondEditText.setError("Password must contain 6-10 character");
                break;
            default:
                return;
        }
    }

}











//public class LoginActivity extends AppCompatActivity {
//
//    private LoginViewModel loginViewModel;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
//                .get(LoginViewModel.class);
//
//        final EditText usernameEditText = findViewById(R.id.username);
//        final EditText passwordEditText = findViewById(R.id.password);
//        final Button loginButton = findViewById(R.id.login);
//        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
//
//        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
//            @Override
//            public void onChanged(@Nullable LoginFormState loginFormState) {
//                if (loginFormState == null) {
//                    return;
//                }
//                loginButton.setEnabled(loginFormState.isDataValid());
//                if (loginFormState.getUsernameError() != null) {
//                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
//                }
//                if (loginFormState.getPasswordError() != null) {
//                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
//                }
//            }
//        });
//
//        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
//            @Override
//            public void onChanged(@Nullable LoginResult loginResult) {
//                if (loginResult == null) {
//                    return;
//                }
//                loadingProgressBar.setVisibility(View.GONE);
//                if (loginResult.getError() != null) {
//                    showLoginFailed(loginResult.getError());
//                }
//                if (loginResult.getSuccess() != null) {
//                    updateUiWithUser(loginResult.getSuccess());
//                }
//                setResult(Activity.RESULT_OK);
//
//                //Complete and destroy login activity once successful
//                finish();
//            }
//        });
//
//        TextWatcher afterTextChangedListener = new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                // ignore
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                // ignore
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
//                        passwordEditText.getText().toString());
//            }
//        };
//        usernameEditText.addTextChangedListener(afterTextChangedListener);
//        passwordEditText.addTextChangedListener(afterTextChangedListener);
//        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    loginViewModel.login(usernameEditText.getText().toString(),
//                            passwordEditText.getText().toString());
//                }
//                return false;
//            }
//        });
//
//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loadingProgressBar.setVisibility(View.VISIBLE);
//                loginViewModel.login(usernameEditText.getText().toString(),
//                        passwordEditText.getText().toString());
//            }
//        });
//    }
//
//    private void updateUiWithUser(LoggedInUserView model) {
//        String welcome = getString(R.string.welcome) + model.getDisplayName();
//        // TODO : initiate successful logged in experience
//        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
//    }
//
//    private void showLoginFailed(@StringRes Integer errorString) {
//        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
//    }
//}