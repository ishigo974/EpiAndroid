package com.example.menigo_m.epiandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent i = getIntent();
        String message = i.getStringExtra(MainActivity.SUCCESS);
        if (!message.isEmpty())
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
