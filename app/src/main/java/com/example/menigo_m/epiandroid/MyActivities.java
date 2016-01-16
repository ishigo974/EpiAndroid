package com.example.menigo_m.epiandroid;

import android.content.Intent;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by menigo_m on 16/01/16.
 */
public abstract class MyActivities extends AppCompatActivity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent preferenceActivity = new Intent(MyActivities.this, PreferenceActivity.class);
                startActivity(preferenceActivity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
