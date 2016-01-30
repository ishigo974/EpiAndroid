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

/**
 * A simple {@link android.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ModuleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ModuleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MarkFragment extends android.app.Fragment {
    private OnFragmentInteractionListener mListener;

    private String mail_content = "My Marks\n\n";

    public MarkFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ModuleFragment.
     */
    public static MarkFragment newInstance() {
        return new MarkFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Map<String, String> params = new HashMap<>();
        params.put(getString(R.string.token), ((HomeActivity) getActivity()).getToken());
        ((MyActivities) getActivity()).getApiConnection().doPost(params,
                getString(R.string.api_url).concat(getActivity().getString(R.string.mark_url)),
                Request.Method.GET, ((HomeActivity) getActivity()).getQueue(),
                new ApiRequest.INetworkCallback() {
                    @Override
                    public void onSuccess(JSONObject response) throws JSONException {
                        JSONArray jsonArray = response.getJSONArray("notes");
                        LinkedList<JSONObject> objects = new LinkedList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            objects.add(jsonArray.getJSONObject(i));
                            mail_content = mail_content.concat(jsonArray.getJSONObject(i).getString("title").concat(" - "));
                            mail_content = mail_content.concat(jsonArray.getJSONObject(i).getString("titlemodule"));
                            mail_content = mail_content.concat(" : ".concat(jsonArray.getJSONObject(i).getString("final_note")));
                            mail_content = mail_content.concat("\n");
                        }
                        Activity activity = getActivity();
                        if (activity == null)
                            return;
                        final ListView listView = (ListView) activity.findViewById(R.id.mark_element);
                        MarkAdapter adapter = new MarkAdapter(activity, objects);
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
                        Toast.makeText(activity.getApplicationContext(), getString(R.string.auth_error), Toast.LENGTH_LONG).show();
                    }
                });
        return inflater.inflate(R.layout.fragment_mark, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void send_mail() {
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{""});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My marks");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, mail_content);
        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
