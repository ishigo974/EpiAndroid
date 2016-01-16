package com.example.menigo_m.epiandroid;

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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends MyActivities {
    public final static String SUCCESS = "com.example.menigo_m.epiandroid.intent.SUCCESS";
    private Button submit = null;
    private ProgressBar loading_progress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        submit = (Button) findViewById(R.id.submit_button);
        loading_progress = (ProgressBar) findViewById(R.id.loading_progress);
    }

    public void submit_button_clicked(View view) {
        ApiConnection apiConnection = new ApiConnection();
        EditText login = (EditText) findViewById(R.id.login_input);
        EditText password = (EditText) findViewById(R.id.password_input);
        apiConnection.execute(login.getText().toString(), password.getText().toString());
        submit.setEnabled(false);
        loading_progress.setVisibility(View.VISIBLE);
    }

    private class ApiConnection extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Integer doInBackground(String... args) {
            try {
                URL url = new URL("https://epitech-api.herokuapp.com/login");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("login", args[0])
                        .appendQueryParameter("password", args[1]);
                String query = builder.build().getEncodedQuery();

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                Log.d("Code response : ", Integer.toString(conn.getResponseCode()));

                if (conn.getResponseCode() == 200) {
                    try {
                        BufferedReader br =
                                new BufferedReader(
                                        new InputStreamReader(conn.getInputStream()));
                        String response = null;
                        while ((response = br.readLine()) != null) {
                            Log.d("res", response);
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("token", response);
                            editor.apply();
                        }
                        br.close();
                        return (1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    conn.connect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return -1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == -1) {
                Toast.makeText(getApplicationContext(), "Authentication error", Toast.LENGTH_LONG).show();
                submit.setEnabled(true);
                loading_progress.setVisibility(View.GONE);
            } else {
                Intent homeActivity = new Intent(MainActivity.this, HomeActivity.class);
                homeActivity.putExtra(MainActivity.SUCCESS, "Authentication success");
                startActivity(homeActivity);
            }
        }
    }
}
