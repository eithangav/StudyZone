package com.example.studyzone.ui.form;

import android.view.View;

import com.example.studyzone.data.user.LoginFetcher;

import java.io.Serializable;

public class LoginPresenter implements FormPresenter, Serializable {

    FormPresenterListener listener;

    @Override
    public String getScreenTitle() {
        return "Sign in";
    }

    @Override
    public String getSubtitlePrefix() {
        return "New user?";
    }

    @Override
    public String getSubtitleSuffix() {
        return "Create an account";
    }

    @Override
    public String getFirstTextFieldHint() {
        return "Email";
    }

    @Override
    public String getSecondTextFieldHint() {
        return "Password";
    }

    @Override
    public String getSubmitButtonTitle() {
        return "Sign In";
    }

    @Override
    public void setListener(FormPresenterListener listener) {
        if (listener != null)
            this.listener = listener;
    }

    @Override
    public void subtitleLinkTapped() {
        if (listener != null)
            listener.moveToRegisterScreen();
    }

    @Override
    public void submitButtonTapped(View view) {
        // send login request
        if (listener != null) {
            listener.fetchLogin(view);
        }
    }

    @Override
    public void validateEmail(FormState formState, String email) {
        if (listener != null) {
            boolean valid = Validator.email(email);
            listener.showValidationError("first", valid);
            formState.setFirstFieldValidation(valid);
            listener.setButtonState();
        }
    }

    @Override
    public void validatePassword(FormState formState, String password) {
        if (listener != null) {
            boolean valid = Validator.password(password);
            listener.showValidationError("second", valid);
            formState.setSecondFieldValidation(valid);
            listener.setButtonState();
        }
    }

}
