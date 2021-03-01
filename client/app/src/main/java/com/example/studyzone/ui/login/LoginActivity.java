package com.example.studyzone.ui.login;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studyzone.R;
import com.example.studyzone.ui.login.LoginViewModel;
import com.example.studyzone.ui.login.LoginViewModelFactory;

import java.io.Serializable;

interface FormPresenter {
    String getScreenTitle();
    String getSubtitle();
    String getLinkTitle();

    String getFirstTextFieldHint();
    String getSecondTextFieldHint();
    String getButtonTitle();

    void setListener(FormPresenterListener listener);
    void linkTapped();
    void buttonTapped();
    void validate(String string);
}

interface FormPresenterListener {
    void moveToScreenX();
    void moveToScreenY();
    void setButtonEnable(Boolean enable);
}

class EmailValidator {
    static Boolean validate(String string) {
        return string.contains("@");
    }
}

class LoginPresenter implements FormPresenter, Serializable {

    FormPresenterListener listener;

    @Override
    public String getScreenTitle() {
        return "Login";
    }

    @Override
    public String getSubtitle() {
        return null;
    }

    @Override
    public String getLinkTitle() {
        return null;
    }

    @Override
    public String getFirstTextFieldHint() {
        return null;
    }

    @Override
    public String getSecondTextFieldHint() {
        return null;
    }

    @Override
    public String getButtonTitle() {
        return null;
    }

    @Override
    public void setListener(FormPresenterListener listener) {
        this.listener = listener;
    }

    @Override
    public void linkTapped() {

    }

    @Override
    public void buttonTapped() {
        // send login request
        // onDone ----> move to next ...
        listener.moveToScreenX();
    }

    @Override
    public void validate(String string) {
        listener.setButtonEnable(EmailValidator.validate(string));
    }
}

class RegisterPresenter implements FormPresenter, Serializable {

    FormPresenterListener listener;

    @Override
    public String getScreenTitle() {
        return "Register";
    }

    @Override
    public String getSubtitle() {
        return null;
    }

    @Override
    public String getLinkTitle() {
        return null;
    }

    @Override
    public String getFirstTextFieldHint() {
        return null;
    }

    @Override
    public String getSecondTextFieldHint() {
        return null;
    }

    @Override
    public String getButtonTitle() {
        return null;
    }

    @Override
    public void setListener(FormPresenterListener listener) {
        this.listener = listener;
    }

    @Override
    public void linkTapped() {

    }

    @Override
    public void buttonTapped() {
        // send reuqest to the server fort registeration
        // onDone ---> move to next screen
        listener.moveToScreenY();
    }

    @Override
    public void validate(String string) {
        listener.setButtonEnable(EmailValidator.validate(string));
    }
}


class FormActivity extends AppCompatActivity implements FormPresenterListener {

    private FormPresenter presenter = new LoginPresenter();

    private TextView titleTextView;
    private TextView subtitleTextView;
    private TextView link;
    private EditText firstEditText;
    private EditText secondEditText;
    private Button submitButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        titleTextView = findViewById(R.id.login_header);
        subtitleTextView = findViewById(R.id.new_user_header);
        link = findViewById(R.id.create_account_clickable_header);
        firstEditText = findViewById(R.id.username);
        secondEditText = findViewById(R.id.password);
        submitButton = findViewById(R.id.login);

        titleTextView.setText(presenter.getScreenTitle());
        subtitleTextView.setText(presenter.getSubtitle());
        link.setText(presenter.getLinkTitle());
        firstEditText.setHint(presenter.getFirstTextFieldHint());
        secondEditText.setHint(presenter.getSecondTextFieldHint());
        submitButton.setText(presenter.getButtonTitle());
        submitButton.setEnabled(false);

//        Intent intent = new Intent(this, FormActivity.class);
//        intent.putExtra("presenter", new RegisterPresenter());
//        startActivity(intent);


        firstEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                presenter.validate(s.toString());
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.buttonTapped();
            }
        });

        presenter.setListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        this.presenter = (FormPresenter) intent.getSerializableExtra("presenter");
    }

    @Override
    public void moveToScreenX() {
        // go to screen x
    }

    @Override
    public void moveToScreenY() {
        // go to screen y
    }

    @Override
    public void setButtonEnable(Boolean enable) {
        submitButton.setEnabled(enable);
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