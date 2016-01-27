package com.example.menigo_m.epiandroid;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.RequestQueue;

/**
 * Created by menigo_m on 16/01/16.
 */
public abstract class MyActivities extends AppCompatActivity {
    static protected RequestQueue queue = null;
    static protected ApiRequest apiConnection = new ApiRequest();

    public ApiRequest getApiConnection() {
        return apiConnection;
    }

    public void storeValue(String key, String value)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getToken() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString(getString(R.string.token), null);
    }

    public String getLogin() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString(getString(R.string.login), null);
    }
}
