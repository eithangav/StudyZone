package com.example.studyzone.ui.form;

public interface FormPresenterListener {
    void moveToLoginScreen();
    void moveToRegisterScreen();
    void setButtonState();
    void showValidationError(String fieldName, boolean valid);
}
