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
        setContentView(R.layout.activity_main);

        queue = Volley.newRequestQueue(getApplicationContext());
        submit = (Button) findViewById(R.id.submit_button);
        loading_progress = (ProgressBar) findViewById(R.id.loading_progress);
    }

    public void submit_button_clicked(View view) {
        ApiRequest apiConnection = new ApiRequest();
        final EditText login = (EditText) findViewById(R.id.login_input);
        EditText password = (EditText) findViewById(R.id.password_input);
        Map<String, String> params = new HashMap<>();
        params.put("login", login.getText().toString());
        params.put("password", password.getText().toString());
        apiConnection.doPost(params, "login", Request.Method.POST, queue, new ApiRequest.INetworkCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                try {
                    editor.putString("token", response.getString("token"));
                    editor.putString("login", login.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                editor.apply();
                Intent homeActivity = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(homeActivity);
            }

            @Override
            public void onError() {
                Toast.makeText(getApplicationContext(), "Authentication error", Toast.LENGTH_LONG).show();
                submit.setEnabled(true);
                loading_progress.setVisibility(View.GONE);
            }
        });
        submit.setEnabled(false);
        loading_progress.setVisibility(View.VISIBLE);
    }
}
