package com.example.menigo_m.epiandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


public class GradeFragment extends android.app.Fragment {
    private OnFragmentInteractionListener mListener;

    private String mail_content;

    public GradeFragment() {
    }

    public static GradeFragment newInstance() {
        return new GradeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grade, container, false);
        mail_content = getString(R.string.my_grades);
        Map<String, String> params = new HashMap<>();
        params.put(getString(R.string.token), ((HomeActivity) getActivity()).getToken());
        ((MyActivities) getActivity()).getApiConnection().doPost(params,
                getString(R.string.api_url).concat(getActivity().getString(R.string.my_module)),
                Request.Method.GET, ((HomeActivity) getActivity()).getQueue(),
                new ApiRequest.INetworkCallback() {
                    @Override
                    public void onSuccess(JSONObject response) throws JSONException {
                        Activity activity = getActivity();
                        if (activity == null)
                            return;
                        JSONArray jsonarray = response.getJSONArray(getString(R.string.modules_array));
                        LinkedList<JSONObject> objects = new LinkedList<>();
                        for (int i = 0; i < jsonarray.length(); i++) {
                            objects.add(jsonarray.getJSONObject(i));
                            mail_content = mail_content.concat(jsonarray.getJSONObject(i).getString(getString(R.string.title)));
                            mail_content = mail_content.concat(" : ".concat(jsonarray.getJSONObject(i).getString(getString(R.string.grade))));
                            mail_content = mail_content.concat("\n");
                        }
                        final ListView listView = (ListView) activity.findViewById(R.id.grade_element);
                        GradeAdapter adapter = new GradeAdapter(activity, objects);
                        listView.setAdapter(adapter);
                    }

                    @Override
                    public void onSuccess(JSONArray response) throws JSONException {
                    }

                    @Override
                    public void onError() {
                        Activity activity = getActivity();
                        if (activity == null)
                            return;
                        Toast.makeText(activity.getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG).show();
                    }
                });
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + context.getString(R.string.fragment_attach));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void send_mail() {
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType(getActivity().getString(R.string.typeText));
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{""});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.my_grades_mail));
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, mail_content);
        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)));
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
