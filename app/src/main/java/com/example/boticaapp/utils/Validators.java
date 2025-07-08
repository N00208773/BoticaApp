package com.example.boticaapp.utils;


import android.text.TextUtils;
import android.util.Patterns;

public class Validators {
    public static boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email)
                && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isPasswordValid(String pass) {
        // por ejemplo, mínimo 6 caracteres
        return pass != null && pass.length() >= 6;
    }

    public static boolean isNotEmpty(String s) {
        return !TextUtils.isEmpty(s);
    }
}