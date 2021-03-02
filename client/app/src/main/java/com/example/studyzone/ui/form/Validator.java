package com.example.studyzone.ui.form;

public class Validator {

    public static Boolean email(String email) {

        return !email.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

    }

    public static Boolean password(String password){

        return !password.isEmpty() && password.length() > 5 && password.length() < 11;

    }

    //some more validations
}
