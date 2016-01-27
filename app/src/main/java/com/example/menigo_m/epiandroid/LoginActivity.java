package com.example.menigo_m.epiandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends MyActivities {
    private ProgressBar loading_progress = null;
    private Button submit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.contains(getString(R.string.token))) {
            Intent homeActivity = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(homeActivity);
            finish();
        }

        queue = Volley.newRequestQueue(getApplicationContext());
        submit = (Button) findViewById(R.id.submit_button);
        loading_progress = (ProgressBar) findViewById(R.id.loading_progress);
    }

    public void submit_button_clicked(View view) {
        final EditText login = (EditText) findViewById(R.id.login_input);
        final EditText password = (EditText) findViewById(R.id.password_input);
        Map<String, String> params = new HashMap<>();
        params.put(getString(R.string.login), login.getText().toString());
        params.put(getString(R.string.password), password.getText().toString());
        apiConnection.doPost(params, getString(R.string.api_url).concat(getString(R.string.login)), Request.Method.POST, queue, new ApiRequest.INetworkCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                storeValue(getString(R.string.login), login.getText().toString());
                try {
                    storeValue(getString(R.string.token), response.getString(getString(R.string.token)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Map<String, String> params = new HashMap<>();
                try {
                    params.put(getString(R.string.token), response.getString(getString(R.string.token)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("user", login.getText().toString());
                apiConnection.doPost(params, getString(R.string.api_url).concat("user"), Request.Method.GET, queue, new ApiRequest.INetworkCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            storeValue("location", response.getString("location"));
                            storeValue("course_code", response.getString("course_code"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onSuccess(JSONArray response) throws JSONException {
                    }

                    @Override
                    public void onError() {
                    }
                });

                Intent homeActivity = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(homeActivity);
                finish();
            }

            @Override
            public void onSuccess(JSONArray response) throws JSONException {
            }

            @Override
            public void onError() {
                Toast.makeText(getApplicationContext(), getString(R.string.auth_error), Toast.LENGTH_LONG).show();
                login.setEnabled(true);
                password.setEnabled(true);
                submit.setEnabled(true);
                loading_progress.setVisibility(View.GONE);
            }
        });
        login.setEnabled(false);
        password.setEnabled(false);
        submit.setEnabled(false);
        loading_progress.setVisibility(View.VISIBLE);
    }
}
