package com.example.menigo_m.epiandroid;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.RequestQueue;

import java.util.LinkedList;

public class HomeActivity extends MyActivities {

    private Fragment[] fragments = new Fragment[]{
            new HomeFragment(),
            new ModuleFragment(),
            new PlanningFragment(),
            new YearbookFragment(),
            new ProjectsFragment(),
            new MarkFragment(),
            new LogoutFragment()
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
        menus.add(getString(R.string.projects));
        menus.add(getString(R.string.marks));
        menus.add(getString(R.string.logout));

        final ListView listView = (ListView) findViewById(R.id.menu_elements);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(HomeActivity.this, R.layout.my_list_item, menus);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getFragmentManager().beginTransaction().replace(R.id.frame_layout, fragments[position]).commit();
                drawer.closeDrawer(listView);
            }
        });
    }

    public RequestQueue getQueue() {
        return queue;
    }

    public void prev_button_clicked(View view) {
        ((PlanningFragment) fragments[2]).prev_button_clicked(view);
    }

    public void next_button_clicked(View view) {
        ((PlanningFragment) fragments[2]).next_button_clicked(view);
    }

    public void registered_button_clicked(View view) {
        ((PlanningFragment) fragments[2]).registered_button_clicked(view);
    }

    public void registered_module_clicked(View view) {
        ((ModuleFragment) fragments[1]).registered_module_clicked(view);
    }

    public void progress_button_clicked(View view) {
        ((ProjectsFragment) fragments[4]).progress_button_clicked(view);
    }
}
