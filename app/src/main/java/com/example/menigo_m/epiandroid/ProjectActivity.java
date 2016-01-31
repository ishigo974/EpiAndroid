package com.example.menigo_m.epiandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class ProjectActivity extends MyActivities {
    private String scolaryear = null;
    private String codemodule = null;
    private String codeinstance = null;
    private String codeacti = null;
    private Date today = new Date();
    private DateFormat dateFormat;
    private DateFormat displayFormat;
    private DateFormat displayEndFormat;
    private TextView name = null;
    private TextView description = null;
    private TextView size = null;
    private TextView module = null;
    private TextView begin = null;
    private TextView end = null;
    private Button register = null;
    private TextView note = null;
    private boolean registered = false;

    private String getDate(DateFormat displayFormat, Date date) {
        return displayFormat.format(date);
    }

    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put(getString(R.string.token), getToken());
        params.put(getString(R.string.scolaryear), scolaryear);
        params.put(getString(R.string.codemodule), codemodule);
        params.put(getString(R.string.codeinstance), codeinstance);
        params.put(getString(R.string.codeacti), codeacti);
        return params;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        dateFormat = new SimpleDateFormat(getString(R.string.activityDateFormat));
        displayFormat = new SimpleDateFormat(getString(R.string.activityDisplayFormat));
        displayEndFormat = new SimpleDateFormat(getString(R.string.activityEndFormat));

        Intent intent = getIntent();
        try {
            JSONObject object = new JSONObject(intent.getStringExtra(getString(R.string.objectString)));
            scolaryear = object.getString(getString(R.string.scolaryear));
            codemodule = object.getString(getString(R.string.codemodule));
            codeinstance = object.getString(getString(R.string.codeinstance));
            codeacti = object.getString(getString(R.string.codeacti));
            registered = object.getString(getString(R.string.registered_api)).equals(getString(R.string.oneString));
        } catch (JSONException ignored) {
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
                        register = (Button) findViewById(R.id.subscribeButton);
                        module = (TextView) findViewById(R.id.projectModule);
                        name = (TextView) findViewById(R.id.projectName);
                        description = (TextView) findViewById(R.id.projectDescription);
                        begin = (TextView) findViewById(R.id.projectBegin);
                        end = (TextView) findViewById(R.id.projectEnd);
                        note = (TextView) findViewById(R.id.projectNote);
                        size = (TextView) findViewById(R.id.groupSize);
                        try {
                            name.setText(response.getString(getString(R.string.project_title)));
                            description.setText(response.getString(getString(R.string.description_api)));
                            module.setText(response.getString(getString(R.string.module_title)));
                            String nb_min = response.getString(getString(R.string.nb_min_api));
                            String nb_max = response.getString(getString(R.string.nb_max_api));
                            size.append(nb_min);
                            if (!nb_min.equals(nb_max)) {
                                size.append("-");
                                size.append(nb_max);
                            }
                            String project_note = response.getString(getString(R.string.note_api));
                            if (!project_note.equals(getString(R.string.nullString))) {
                                note.setText(R.string.note_info);
                                note.append(project_note);
                            }
                            Date begin_date = dateFormat.parse(response.getString(getString(R.string.begin_api)));
                            Date end_date = dateFormat.parse(response.getString(getString(R.string.end_register_api)));

                            if (end_date.before(today))
                                register.setEnabled(false);
                            if (registered) {
                                register.setText(R.string.unregister);
                            } else {
                                register.setText(R.string.register);
                            }
                            begin.setText(getDate(displayFormat, begin_date));
                            end.setText(getDate(displayEndFormat, end_date));
                        } catch (JSONException | ParseException ignored) {
                        }
                    }

                    @Override
                    public void onSuccess(JSONArray response) throws JSONException, ParseException {

                    }

                    @Override
                    public void onError() {
                        Toast.makeText(getApplicationContext(), R.string.subscribe_success, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void project_register_button_clicked(View view) {
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
                            Toast.makeText(getApplicationContext(), R.string.unsubscribe_success, Toast.LENGTH_SHORT).show();
                            register.setText(R.string.register);
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.subscribe_success, Toast.LENGTH_SHORT).show();
                            register.setText(R.string.unregister);
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
