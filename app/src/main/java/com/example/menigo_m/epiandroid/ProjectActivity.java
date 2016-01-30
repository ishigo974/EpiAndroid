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
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DateFormat displayFormat = new SimpleDateFormat("dd-MM-yyyy");
    private DateFormat displayEndFormat = new SimpleDateFormat("dd-MM-yyyy, HH:mm:ss");
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
        params.put("scolaryear", scolaryear);
        params.put("codemodule", codemodule);
        params.put("codeinstance", codeinstance);
        params.put("codeacti", codeacti);
        return params;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        Intent intent = getIntent();
        try {
            JSONObject object = new JSONObject(intent.getStringExtra("object"));
            scolaryear = object.getString("scolaryear");
            codemodule = object.getString("codemodule");
            codeinstance = object.getString("codeinstance");
            codeacti = object.getString("codeacti");
            if (object.getString("registered").equals("1"))
                registered = true;
            else
                registered = false;
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
                            name.setText(response.getString("project_title"));
                            description.setText(response.getString("description"));
                            module.setText(response.getString("module_title"));
                            String nb_min = response.getString("nb_min");
                            String nb_max = response.getString("nb_max");
                            size.append(nb_min);
                            if (!nb_min.equals(nb_max)) {
                                size.append("-");
                                size.append(nb_max);
                            }
                            String project_note = response.getString("note");
                            if (!project_note.equals("null")) {
                                note.setText("Note : ");
                                note.append(project_note);
                            }
                            Date begin_date = dateFormat.parse(response.getString("begin"));
                            Date end_date = dateFormat.parse(response.getString("end_register"));

                            if (end_date.before(today))
                                register.setEnabled(false);
                            if (registered) {
                                register.setText("Unregister");
                            } else {
                                register.setText("Register");
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
                        Toast.makeText(getApplicationContext(), "You have been successfully subscribed", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getApplicationContext(), "You have been successfully unsubscribed", Toast.LENGTH_SHORT).show();
                            register.setText("Register");
                        } else {
                            Toast.makeText(getApplicationContext(), "You have been successfully subscribed", Toast.LENGTH_SHORT).show();
                            register.setText("Unregister");
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
