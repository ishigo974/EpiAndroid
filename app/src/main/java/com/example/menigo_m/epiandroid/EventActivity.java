package com.example.menigo_m.epiandroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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

public class EventActivity extends MyActivities {
    private TextView name = null;
    private TextView description = null;
    private TextView module = null;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DateFormat displayDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private DateFormat displayHourFormat = new SimpleDateFormat("HH:mm:ss");
    private String scolaryear = null;
    private String codemodule = null;
    private String codeinstance = null;
    private String codeacti = null;
    private String codeevent = null;
    private boolean registered = false;

    private String getDate(DateFormat dateFormat, Date date) {
        return dateFormat.format(date);
    }

    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put(getString(R.string.token), getToken());
        params.put("scolaryear", scolaryear);
        params.put("codemodule", codemodule);
        params.put("codeinstance", codeinstance);
        params.put("codeacti", codeacti);
        params.put("codeevent", codeevent);
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
            codeevent = object.getString("codeevent");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        display_informations();
    }

    private void display_informations() {
        getApiConnection().doPost(getParams(),
                getString(R.string.api_url).concat(getString(R.string.event_url)),
                Request.Method.GET, queue,
                new ApiRequest.INetworkCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        name = (TextView) findViewById(R.id.activityName);
                        description = (TextView) findViewById(R.id.activityDescription);
                        module = (TextView) findViewById(R.id.activityModule);

                        try {
                            name.setText(response.getString("type_title") + "\n" + response.getString("acti_title"));
                            try {
                                String desc = response.getString("acti_description");
                                if (!desc.equals("null"))
                                    description.setText(desc);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            description.append(response.getJSONObject("room").getString("code"));
                            module.setText(response.getString("module_title"));

                            Date begin = null;
                            Date end = null;
                            try {
                                begin = dateFormat.parse(response.getString("start"));
                                end = dateFormat.parse(response.getString("end"));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (begin != null && end != null) {
                                description.append("\n" + getDate(displayHourFormat, begin));
                                description.append(" - " + getDate(displayHourFormat, end));
                                description.append("\n" + getDate(displayDateFormat, begin));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    public void validate_token(View view) {
        TextView token_field = (TextView) findViewById(R.id.tokenField);
        if (token_field.getText().toString().length() != 8)
        {
            Toast.makeText(getApplicationContext(), R.string.bad_token_size, Toast.LENGTH_LONG).show();
            return ;
        }
        Map<String, String> params = getParams();
        params.put("tokenvalidationcode", token_field.getText().toString());
        getApiConnection().doPost(params,
                getString(R.string.api_url).concat(getString(R.string.token_url)),
                Request.Method.POST, queue,
                new ApiRequest.INetworkCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Toast.makeText(getApplicationContext(), "Token successfully validated", Toast.LENGTH_LONG).show();
                        Log.d("response", response.toString());
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
