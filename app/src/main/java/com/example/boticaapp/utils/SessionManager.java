package com.example.boticaapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.boticaapp.models.User;

public class SessionManager {
    private static SessionManager instance;
    private final SharedPreferences prefs;

    private SessionManager(Context ctx) {
        prefs = ctx.getApplicationContext()
                .getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SessionManager getInstance(Context ctx) {
        if (instance == null) {
            instance = new SessionManager(ctx);
        }
        return instance;
    }

    public void saveUser(User user) {
        SharedPreferences.Editor e = prefs.edit();
        e.putBoolean(Constants.PREFS_KEY_IS_LOGGED, true);
        e.putInt    (Constants.PREFS_KEY_USER_ID,  user.getId());
        e.putString (Constants.PREFS_KEY_NAME,     user.getName());
        e.putString (Constants.PREFS_KEY_EMAIL,    user.getEmail());
        e.putString (Constants.PREFS_KEY_ROLE,     user.getRole());
        // Guardamos siempre pharmacy_id, aunque sea cero o -1
        e.putInt    (Constants.PREFS_KEY_PHARMACY, user.getPharmacyId());
        e.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(Constants.PREFS_KEY_IS_LOGGED, false);
    }

    public User getUser() {
        if (!isLoggedIn()) return null;
        User u = new User();
        u.setId        (prefs.getInt   (Constants.PREFS_KEY_USER_ID, -1));
        u.setName      (prefs.getString(Constants.PREFS_KEY_NAME,    ""));
        u.setEmail     (prefs.getString(Constants.PREFS_KEY_EMAIL,   ""));
        u.setRole      (prefs.getString(Constants.PREFS_KEY_ROLE,    ""));
        // Valor por defecto -1 para indicar ausencia
        u.setPharmacyId(prefs.getInt   (Constants.PREFS_KEY_PHARMACY, -1));
        return u;
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }
}
