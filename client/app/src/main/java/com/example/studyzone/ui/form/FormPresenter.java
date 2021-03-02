package com.example.studyzone.ui.form;

public interface FormPresenter {
    String getScreenTitle();
    String getSubtitlePrefix();
    String getSubtitleSuffix();

    String getFirstTextFieldHint();
    String getSecondTextFieldHint();
    String getSubmitButtonTitle();

    void setListener(FormPresenterListener listener);
    void subtitleLinkTapped();

    void submitButtonTapped();

    void validateEmail(FormState formState, String string);
    void validatePassword(FormState formState, String string);
}
