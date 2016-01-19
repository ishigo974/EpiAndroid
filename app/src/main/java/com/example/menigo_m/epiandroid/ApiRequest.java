package com.example.menigo_m.epiandroid;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by lopes_n on 1/18/16.
 */
public class ApiRequest {

    public interface INetworkCallback {
        void onSuccess(JSONObject response);

        void onError();
    }

    private Map<String, String> params = null;
    private String url = "https://epitech-api.herokuapp.com/";

    protected void doPost(Map<String, String> args, String action, Integer method, RequestQueue queue, final INetworkCallback callback) {
        params = args;
        StringRequest request = new StringRequest(method, url.concat(action),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            callback.onSuccess(new JSONObject(response));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
        queue.add(request);
    }
}