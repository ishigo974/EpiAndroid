package com.example.menigo_m.epiandroid;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Map;

/**
 * Created by lopes_n on 1/18/16.
 */
public class ApiRequest {

    public interface INetworkCallback {
        void onSuccess(JSONObject response) throws JSONException;

        void onSuccess(JSONArray response) throws JSONException, ParseException;

        void onError();
    }

    private Map<String, String> params = null;

    private String setGetParams(String action) {
        if (!params.isEmpty()) {
            action = action.concat("?");
            while (!params.isEmpty()) {
                String tmp = (String) params.keySet().toArray()[0];
                action = action.concat("&");
                action = action.concat(tmp);
                action = action.concat("=");
                action = action.concat(params.get(tmp));
                params.remove(tmp);
            }
        }
        params = null;
        return action;
    }

    protected void doPost(Map<String, String> args, String action, Integer method, RequestQueue queue, final INetworkCallback callback) {
        params = args;
        if (method == Request.Method.GET || method == Request.Method.DELETE)
            action = setGetParams(action);
        StringRequest request = new StringRequest(method, action,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            callback.onSuccess(new JSONObject(response));
                        } catch (JSONException e) {
                            try {
                                callback.onSuccess(new JSONArray(response));
                            } catch (JSONException | ParseException ignored) {
                            }
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