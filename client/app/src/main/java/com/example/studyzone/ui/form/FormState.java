package com.example.studyzone.ui.form;

/**
 * a class to hold the form state validation
 * combining both first and second fields validation
 */
public class FormState {

    private boolean firstField;
    private boolean secondField;

    public FormState(){
        this.firstField = false;
        this.secondField = false;
    }

    public void setFirstFieldValidation (boolean valid){
        this.firstField = valid;
    }

    public void setSecondFieldValidation (boolean valid){
        this.secondField = valid;
    }

    public boolean getFormState (){
        return this.firstField && this.secondField;
    }

}
