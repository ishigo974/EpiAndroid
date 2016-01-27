package com.example.menigo_m.epiandroid;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ModuleActivity extends MyActivities {
    private TextView name = null;
    private TextView description = null;
    private TextView skills = null;
    private TextView credits = null;
    private TextView semester = null;

    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put(getString(R.string.token), getToken());
        params.put("scolaryear", "2015"); // changer les trucs en dur
        params.put("codemodule", "G-EPI-007"); // changer les trucs en dur
        params.put("codeinstance", "PAR-0-1"); // changer les trucs en dur
        return params;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module);
        getApiConnection().doPost(getParams(),
                getString(R.string.api_url).concat(getString(R.string.module_url)),
                Request.Method.GET, queue,
                new ApiRequest.INetworkCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        name = (TextView) findViewById(R.id.module_name);
                        description = (TextView) findViewById(R.id.module_description);
                        skills = (TextView) findViewById(R.id.module_skills);
                        credits = (TextView) findViewById(R.id.module_credits);
                        semester = (TextView) findViewById(R.id.module_semester);

                        try {
                            name.setText(response.getString("title"));
                            description.setText(response.getString("description"));
                            skills.setText(response.getString("competence"));
                            credits.setText("Credits : " + response.getString("credits"));
                            semester.setText("Semester : " + response.getString("semester"));
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
}
