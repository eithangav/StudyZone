package com.example.studyzone.ui.form;

import android.view.View;

/**
 * an interface for FormActivity modes- Login / Register
 */
public interface FormPresenter {
    String getScreenTitle();
    String getSubtitlePrefix();
    String getSubtitleSuffix();

    String getFirstTextFieldHint();
    String getSecondTextFieldHint();
    String getSubmitButtonTitle();

    void setListener(FormPresenterListener listener);
    void subtitleLinkTapped();

    void submitButtonTapped(View view);

    void validateEmail(FormState formState, String string);
    void validatePassword(FormState formState, String string);
}
