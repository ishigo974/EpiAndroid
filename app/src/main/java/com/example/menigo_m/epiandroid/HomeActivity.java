package com.example.menigo_m.epiandroid;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class HomeActivity extends MyActivities {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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

//        Fragment fragment = getFragmentManager().;
    }
}
