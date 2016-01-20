package com.example.menigo_m.epiandroid;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class HomeActivity extends MyActivities {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        Toast.makeText(getApplicationContext(), preferences.getString(getString(R.string.token), getString(R.string.token_not_found)), Toast.LENGTH_LONG).show();
//        Toast.makeText(getApplicationContext(), preferences.getString(getString(R.string.login), getString(R.string.login_not_found)), Toast.LENGTH_LONG).show();

        String[] menus = new String[]
                {
                        getString(R.string.home),
                        getString(R.string.modules),
                        getString(R.string.planning),
                        getString(R.string.trombi),
                        getString(R.string.activities),
                        getString(R.string.projects)
                };

        ListView listView = (ListView) findViewById(R.id.menu_elements);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(HomeActivity.this, R.layout.my_list_item, menus);
        listView.setAdapter(adapter);
    }
}
