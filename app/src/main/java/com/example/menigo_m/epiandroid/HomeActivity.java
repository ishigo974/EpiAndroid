package com.example.menigo_m.epiandroid;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class HomeActivity extends MyActivities {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Toast.makeText(getApplicationContext(), preferences.getString("token", "Token not found"), Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), preferences.getString("login", "Login not found"), Toast.LENGTH_LONG).show();

        String[] menus = new String[]
                {
                        "Home",
                        "Modules",
                        "Projects"
                };

        ListView listView = (ListView) findViewById(R.id.menu_elements);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(HomeActivity.this, R.layout.my_list_item, menus);
        listView.setAdapter(adapter);
    }
}
