package com.example.studyzone.ui.form;

import android.view.View;

import com.example.studyzone.data.user.LoggedInUser;

public interface FormPresenterListener {
    void moveToLoginScreen();
    void moveToRegisterScreen();
    void moveToMapScreen(LoggedInUser user);
    void setButtonState();
    void showValidationError(String fieldName, boolean valid);
    void fetchLogin(View view);
    void fetchRegistration(View view);
}
