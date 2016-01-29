package com.example.menigo_m.epiandroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProjectActivity extends MyActivities {
    private Button register_button = null;
    private Date today = new Date();
    private DateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String scolaryear = null;
    private String codemodule = null;
    private String codeinstance = null;
    private String codeacti = null;
    private boolean registered = false;

    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put(getString(R.string.token), getToken());
        params.put("scolaryear", scolaryear);
        params.put("codemodule", codemodule);
        params.put("codeinstance", codeinstance);
        params.put("codeacti", codeacti);
        return params;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        
        Intent intent = getIntent();
        try {
            JSONObject object = new JSONObject(intent.getStringExtra("object"));
            scolaryear = object.getString("scolaryear");
            codemodule = object.getString("codemodule");
            codeinstance = object.getString("codeinstance");
            codeacti = object.getString("codeacti");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        display_informations();
    }

    private void display_informations() {
        getApiConnection().doPost(getParams(),
                getString(R.string.api_url).concat(getString(R.string.project_url)),
                Request.Method.GET, queue,
                new ApiRequest.INetworkCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
//                        name = (TextView) findViewById(R.id.module_name);
//                        description = (TextView) findViewById(R.id.module_description);
//                        skills = (TextView) findViewById(R.id.module_skills);
//                        credits = (TextView) findViewById(R.id.module_credits);
//                        semester = (TextView) findViewById(R.id.module_semester);
//                        register_button = (Button) findViewById(R.id.register_button);

//                        try {
////                            name.setText(response.getString("title"));
////                            description.setText(response.getString("description"));
////                            skills.setText(response.getString("competence"));
////                            credits.setText("Credits : " + response.getString("credits"));
////                            semester.setText("Semester : " + response.getString("semester"));
//                            Date end_date = null;
//                            try {
//                                end_date = apiFormat.parse(response.getString("end_register"));
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//                            if (end_date != null) {
//                                if (end_date.before(today))
//                                    register_button.setEnabled(false);
//                            }
//                            if (response.getString("instance_registered").equals("1")) {
//                                register_button.setText("Unregister");
//                                registered = true;
//                            } else {
//                                register_button.setText("Register");
//                                registered = false;
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                    }

                    @Override
                    public void onSuccess(JSONArray response) throws JSONException {
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void register_button_clicked(View view) {
        Integer method;
        if (registered)
            method = Request.Method.DELETE;
        else
            method = Request.Method.POST;
        getApiConnection().doPost(getParams(),
                getString(R.string.api_url).concat(getString(R.string.project_url)),
                method, queue,
                new ApiRequest.INetworkCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        if (registered) {
                            Toast.makeText(getApplicationContext(), "You have been successfully unsubscribed", Toast.LENGTH_SHORT).show();
                            register_button.setText("Register");
                        } else {
                            Toast.makeText(getApplicationContext(), "You have been successfully subscribed", Toast.LENGTH_SHORT).show();
                            register_button.setText("Unregister");
                        }
                        registered = !registered;
                    }

                    @Override
                    public void onSuccess(JSONArray response) throws JSONException {
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
