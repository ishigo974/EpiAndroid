package com.example.menigo_m.epiandroid;

import android.content.Intent;
import android.os.Bundle;
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
    private DateFormat dateFormat;
    private DateFormat displayDateFormat;
    private DateFormat displayHourFormat;
    private String scolaryear = null;
    private String codemodule = null;
    private String codeinstance = null;
    private String codeacti = null;
    private String codeevent = null;

    private String getDate(DateFormat dateFormat, Date date) {
        return dateFormat.format(date);
    }

    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put(getString(R.string.token), getToken());
        params.put(getString(R.string.scolaryear), scolaryear);
        params.put(getString(R.string.codemodule), codemodule);
        params.put(getString(R.string.codeinstance), codeinstance);
        params.put(getString(R.string.codeacti), codeacti);
        params.put(getString(R.string.codeevent), codeevent);
        return params;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        dateFormat = new SimpleDateFormat(getString(R.string.dateFormat));
        displayDateFormat = new SimpleDateFormat(getString(R.string.displayDate));
        displayHourFormat = new SimpleDateFormat(getString(R.string.displayHour));

        Intent intent = getIntent();
        try {
            JSONObject object = new JSONObject(intent.getStringExtra(getString(R.string.objectString)));
            scolaryear = object.getString(getString(R.string.scolaryear));
            codemodule = object.getString(getString(R.string.codemodule));
            codeinstance = object.getString(getString(R.string.codeinstance));
            codeacti = object.getString(getString(R.string.codeacti));
            codeevent = object.getString(getString(R.string.codeevent));
        } catch (JSONException ignored) {
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
                            name.setText(response.getString(getString(R.string.type_title)));
                            name.append("\n");
                            name.append(response.getString(getString(R.string.acti_title)));
                            try {
                                String desc = response.getString(getString(R.string.acti_description));
                                if (!desc.equals(getString(R.string.nullString)))
                                    description.setText(desc);
                            } catch (JSONException ignored) {
                            }
                            description.append(response.getJSONObject(getString(R.string.room)).getString(getString(R.string.code)));
                            module.setText(response.getString(getString(R.string.module_title)));

                            Date begin = null;
                            Date end = null;
                            try {
                                begin = dateFormat.parse(response.getString(getString(R.string.start)));
                                end = dateFormat.parse(response.getString(getString(R.string.end)));
                            } catch (ParseException ignored) {
                            }
                            if (begin != null && end != null) {
                                description.append("\n" + getDate(displayHourFormat, begin));
                                description.append(" - " + getDate(displayHourFormat, end));
                                description.append("\n" + getDate(displayDateFormat, begin));
                            }
                        } catch (JSONException ignored) {
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
        if (token_field.getText().toString().length() != 8) {
            Toast.makeText(getApplicationContext(), R.string.bad_token_size, Toast.LENGTH_LONG).show();
            return;
        }
        Map<String, String> params = getParams();
        params.put(getString(R.string.tokenvalidationcode), token_field.getText().toString());
        getApiConnection().doPost(params,
                getString(R.string.api_url).concat(getString(R.string.token_url)),
                Request.Method.POST, queue,
                new ApiRequest.INetworkCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Toast.makeText(getApplicationContext(), R.string.token_validated, Toast.LENGTH_LONG).show();
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
