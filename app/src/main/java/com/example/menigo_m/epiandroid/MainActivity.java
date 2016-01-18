package com.example.menigo_m.epiandroid;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends MyActivities {
    public final static String SUCCESS = "com.example.menigo_m.epiandroid.intent.SUCCESS";
    private ProgressBar loading_progress = null;
    private RequestQueue queue = null;
    private Button submit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queue = Volley.newRequestQueue(getApplicationContext());
        submit = (Button) findViewById(R.id.submit_button);
        loading_progress = (ProgressBar) findViewById(R.id.loading_progress);
    }

    public void submit_button_clicked(View view) {
        ApiConnection apiConnection = new ApiConnection();
        EditText login = (EditText) findViewById(R.id.login_input);
        EditText password = (EditText) findViewById(R.id.password_input);
        Map<String, String> params = new HashMap<String, String>();
        params.put("login", login.getText().toString());
        params.put("password", password.getText().toString());
        apiConnection.doPost(params);
        submit.setEnabled(false);
        loading_progress.setVisibility(View.VISIBLE);
    }

    private class ApiConnection {

        private Map <String, String> params = null;

        protected void doPost(Map <String, String> args) {

            params = args;
            String url = "https://epitech-api.herokuapp.com/login";
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Intent homeActivity = new Intent(MainActivity.this, HomeActivity.class);
                            homeActivity.putExtra(MainActivity.SUCCESS, "Authentication success");
                            startActivity(homeActivity);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Authentication error", Toast.LENGTH_LONG).show();
                            submit.setEnabled(true);
                            loading_progress.setVisibility(View.GONE);
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    return params;
                }
            };
            queue.add(postRequest);
        }
    }
}
