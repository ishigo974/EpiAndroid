package com.example.menigo_m.epiandroid;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.LinkedList;
import java.util.List;

public class HomeActivity extends MyActivities {

    private Fragment[] fragments = new Fragment[] {
            new HomeFragment(),
            new ModuleFragment()
    };

    DrawerLayout drawer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getFragmentManager().beginTransaction().replace(R.id.frame_layout, fragments[0]).commit();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        LinkedList<String> menus = new LinkedList<>();
        menus.add(getString(R.string.home));
        menus.add(getString(R.string.modules));
        menus.add(getString(R.string.planning));
        menus.add(getString(R.string.trombi));
        menus.add(getString(R.string.activities));
        menus.add(getString(R.string.projects));

        final ListView listView = (ListView) findViewById(R.id.menu_elements);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(HomeActivity.this, R.layout.my_list_item, menus);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getFragmentManager().beginTransaction().replace(R.id.frame_layout, fragments[position % fragments.length]).commit();
                drawer.closeDrawer(listView);
            }
        });
    }
}
