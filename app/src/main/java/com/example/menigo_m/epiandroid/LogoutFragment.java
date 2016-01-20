package com.example.menigo_m.epiandroid;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LogoutFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        Intent loginActivity = new Intent(getActivity(), LoginActivity.class);
        startActivity(loginActivity);
        getActivity().finish();
        return null;
    }
}
