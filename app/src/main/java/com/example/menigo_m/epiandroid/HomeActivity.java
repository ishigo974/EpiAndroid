package com.example.menigo_m.epiandroid;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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


        final ListView listView = (ListView) findViewById(R.id.menu_elements);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(HomeActivity.this, R.layout.my_list_item, menus);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("clicked", Integer.toString(position));
                Fragment fragment = null;
                switch (position) {
                    case 0:
                        fragment = new HomeFragment();
                        break;
                    case 1:
                        fragment = new ModuleFragment();
                        break;
                }
                if (fragment != null) {
                    getFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();
                }
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(listView);
            }
        });
    }
}
