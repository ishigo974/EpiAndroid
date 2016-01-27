package com.example.menigo_m.epiandroid;

import android.os.Bundle;
import android.widget.Button;
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
    private Button register_button = null;
    private Date today = new Date();
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
        params.put("scolaryear", scolaryear); //TODO changer les trucs en dur
        params.put("codemodule", codemodule); //TODO changer les trucs en dur
        params.put("codeinstance", codeinstance); //TODO changer les trucs en dur
        params.put("codeacti", codeacti); //TODO changer les trucs en dur
        params.put("codeevent", codeevent); //TODO changer les trucs en dur
        return params;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        scolaryear = "2015";
        codemodule = "G-EPI-007";
        codeinstance = "PAR-0-1";
        codeacti = "acti-192900";
        codeevent = "event-206964";
        // TODO get registered par les paramètres

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
//                            if (begin_date != null) {
//                                if (today.getTime() + 86400000 > begin_date.getTime())
//                                    register_button.setEnabled(false);
//                            }
//                            if (response.getString("student_registered").equals("1")) {
//                                register_button.setText("Unregister");
//                                registered = true;
//                            } else {
//                                register_button.setText("Register");
//                                registered = false;
//                            }
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

//    public void register_button_clicked(View view) {
//        Integer method;
//        if (registered)
//            method = Request.Method.DELETE;
//        else
//            method = Request.Method.POST;
//        getApiConnection().doPost(getParams(),
//                getString(R.string.api_url).concat(getString(R.string.event_url)),
//                method, queue,
//                new ApiRequest.INetworkCallback() {
//                    @Override
//                    public void onSuccess(JSONObject response) {
//                        if (registered) {
//                            Toast.makeText(getApplicationContext(), "You have been successfully unsubscribed", Toast.LENGTH_SHORT).show();
//                            register_button.setText("Register");
//                        } else {
//                            Toast.makeText(getApplicationContext(), "You have been successfully subscribed", Toast.LENGTH_SHORT).show();
//                            register_button.setText("Unregister");
//                        }
//                        registered = !registered;
//                    }
//
//                    @Override
//                    public void onSuccess(JSONArray response) throws JSONException {
//                    }
//
//                    @Override
//                    public void onError() {
//                        Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG).show();
//                    }
//                });
//    }
}
