package com.example.studyzone.ui.form;

import android.util.Log;

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
            listener.moveToScreenY();
    }

    @Override
    public void submitButtonTapped() {
        // send login request
        // onDone ----> move to next ...
        if (listener != null) {
            listener.moveToScreenX();
        }
    }

    @Override
    public void validate(String string) {
        if (listener != null)
            listener.setButtonEnable(Validator.emailValidate(string));
    }
}