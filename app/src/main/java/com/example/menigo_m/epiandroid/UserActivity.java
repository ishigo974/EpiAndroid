package com.example.menigo_m.epiandroid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserActivity extends MyActivities {
    private TextView login_text = null;
    private TextView city_promo = null;
    private TextView credits = null;
    private TextView gpa = null;
    private TextView logTime = null;
    private String tel = null;
    private String mail = null;

    public void displayUserInformation(JSONObject response, String login) {
        new ImageIntra((ImageView) findViewById(R.id.profilePicture))
                .execute(getString(R.string.images_url).concat("ballot_g".concat(".jpg")));
        login_text = (TextView) findViewById(R.id.login_home);
        city_promo = (TextView) findViewById(R.id.cityPromo);
        credits = (TextView) findViewById(R.id.credits);
        gpa = (TextView) findViewById(R.id.gpa);
        logTime = (TextView) findViewById(R.id.logTime);
        try {
            login_text.setText(response.getString(getString(R.string.login)));
            city_promo.setText("Epitech ");
            city_promo.append(response.getString("promo"));
            city_promo.append(" ");
            city_promo.append(response.getString("location"));

            credits.setText(response.getString("credits"));
            credits.append(" credits and ");
            credits.append(response.getJSONObject("spice").getString("available_spice"));
            credits.append(" spices");
            JSONObject gpaObj = (JSONObject) response.getJSONArray("gpa").get(0);
            gpa.setText("GPA : ");
            gpa.append(gpaObj.getString("gpa"));
            Double logs = Double.valueOf(response.getJSONObject("nsstat").getString("active"));
            Double minLog = Double.valueOf(response.getJSONObject("nsstat").getString("nslog_norm"));
            logTime.setText(logs.toString());
            logTime.append(" active hours. Minimum required : ");
            logTime.append(minLog.toString());
            if (logs < minLog)
                logTime.setTextColor(getResources().getColor(R.color.red));
            else
                logTime.setTextColor(getResources().getColor(R.color.darkGreen));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put(getString(R.string.token), getToken());
        params.put(getString(R.string.user), "ballot_g");
        return params;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        queue = Volley.newRequestQueue(getApplicationContext());
        Log.d("res", getParams().toString());
        getApiConnection().doPost(getParams(),
                getString(R.string.api_url).concat(getString(R.string.user_url)),
                Request.Method.GET, getQueue(),
                new ApiRequest.INetworkCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        displayUserInformation(response, "lopes_n");
                        try {
                            tel = response.getJSONObject("userinfo").getJSONObject("telephone").getString("value");
                        } catch (JSONException e) {
                            tel = null;
                        }
                        try {
                            mail = response.getString("internal_email");
                        } catch (JSONException e) {
                            mail = null;
                        }
                        Button tel_button = (Button) findViewById(R.id.call_user);
                        Button mail_button = (Button) findViewById(R.id.mail_user);
                        if (tel == null)
                            tel_button.setEnabled(false);
                        if (mail == null)
                            mail_button.setEnabled(false);
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

    public void call_user(View view) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + tel));
        startActivity(callIntent);
    }

    public void mail_user(View view) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                getString(R.string.mailto_intent), mail, null));
        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)));
    }

    public RequestQueue getQueue() {
        return queue;
    }
}
