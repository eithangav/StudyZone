package com.example.studyzone.ui.form;

import android.view.View;

public interface FormPresenterListener {
    void moveToLoginScreen();
    void moveToRegisterScreen();
    void moveToMapScreen();
    void setButtonState();
    void showValidationError(String fieldName, boolean valid);
    void fetchLogin(View view);
    void fetchRegistration(View view);
}
