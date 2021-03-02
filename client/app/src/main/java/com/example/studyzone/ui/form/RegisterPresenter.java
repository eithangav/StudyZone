package com.example.studyzone.ui.form;

import android.util.Log;

import java.io.Serializable;

public class RegisterPresenter implements FormPresenter, Serializable {

    FormPresenterListener listener;

    @Override
    public String getScreenTitle() {
        return "Sign up";
    }

    @Override
    public String getSubtitlePrefix() {
        return "Already registered?";
    }

    @Override
    public String getSubtitleSuffix() {
        return "Sign in";
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
        return "Register";
    }

    @Override
    public void setListener(FormPresenterListener listener) {
        if (listener != null)
            this.listener = listener;
    }

    @Override
    public void subtitleLinkTapped() {

    }

    @Override
    public void submitButtonTapped() {
        // send request to the server fort registration
        // onDone ---> move to next screen
        if (listener != null) {
            listener.moveToScreenY();
        }
    }

    @Override
    public void validate(String string) {
        if (listener != null)
            listener.setButtonEnable(Validator.emailValidate(string));
    }
}
