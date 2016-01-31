package com.example.menigo_m.epiandroid;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private TextView login_text = null;
    private TextView city_promo = null;
    private TextView credits = null;
    private TextView gpa = null;
    private TextView logTime = null;

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public void displayUserInformation(JSONObject response) {
        new ImageIntra((ImageView) getActivity().findViewById(R.id.profilePicture))
                .execute(getActivity().getString(R.string.images_url).concat(((HomeActivity) getActivity()).getLogin()).concat(".jpg"));
        login_text = (TextView) getActivity().findViewById(R.id.login_home);
        city_promo = (TextView) getActivity().findViewById(R.id.cityPromo);
        credits = (TextView) getActivity().findViewById(R.id.credits);
        gpa = (TextView) getActivity().findViewById(R.id.gpa);
        logTime = (TextView) getActivity().findViewById(R.id.logTime);
        try {
            login_text.setText(response.getString(getString(R.string.login)));
            city_promo.setText(R.string.epitech);
            city_promo.append(response.getString(getString(R.string.promo_api)));
            city_promo.append(" ");
            city_promo.append(response.getString(getString(R.string.location_api)));

            credits.setText(response.getString(getString(R.string.credits_api)));
            credits.append(getString(R.string.credits_and));
            credits.append(response.getJSONObject(getString(R.string.spice_api)).getString(getString(R.string.available_spice)));
            credits.append(getString(R.string.spices));
            JSONObject gpaObj = (JSONObject) response.getJSONArray(getString(R.string.gpa_api)).get(0);
            gpa.setText(R.string.gpa);
            gpa.append(gpaObj.getString(getString(R.string.gpa_api)));
            Double logs = Double.valueOf(response.getJSONObject(getString(R.string.nsstat_api)).getString(getString(R.string.active)));
            Double minLog = Double.valueOf(response.getJSONObject(getString(R.string.nsstat_api)).getString(getString(R.string.nslog_norm)));
            logTime.setText(String.valueOf(logs));
            logTime.append(getString(R.string.nslogs));
            logTime.append(minLog.toString());
            if (logs < minLog)
                logTime.setTextColor(getResources().getColor(R.color.red));
            else
                logTime.setTextColor(getResources().getColor(R.color.darkGreen));
        } catch (JSONException ignored) {
        }
    }

    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put(getString(R.string.token), ((HomeActivity) getActivity()).getToken());
        params.put(getString(R.string.user), ((HomeActivity) getActivity()).getLogin());
        return params;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MyActivities) getActivity()).getApiConnection().doPost(getParams(),
                getString(R.string.api_url).concat(getString(R.string.user_url)),
                Request.Method.GET, ((HomeActivity) getActivity()).getQueue(),
                new ApiRequest.INetworkCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Activity activity = getActivity();
                        if (activity == null)
                            return;
                        displayUserInformation(response);
                        ((MyActivities) activity).getApiConnection().doPost(getParams(),
                                getString(R.string.api_url).concat(getString(R.string.messages_url)),
                                Request.Method.GET, ((HomeActivity) activity).getQueue(),
                                new ApiRequest.INetworkCallback() {
                                    @Override
                                    public void onSuccess(JSONObject response) throws JSONException {
                                    }

                                    @Override
                                    public void onSuccess(JSONArray response) throws JSONException {
                                        LinkedList<JSONObject> objects = new LinkedList<>();
                                        for (int i = 0; i < response.length(); i++)
                                            objects.add(response.getJSONObject(i));
                                        Activity activity = getActivity();
                                        if (activity == null)
                                            return;
                                        final ListView listView = (ListView) activity.findViewById(R.id.notif_element);
                                        NotifAdapter adapter = new NotifAdapter(activity, objects);
                                        listView.setAdapter(adapter);
                                    }

                                    @Override
                                    public void onError() {
                                        Activity activity = getActivity();
                                        if (activity == null)
                                            return;
                                        Toast.makeText(activity.getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG).show();
                                    }
                                });
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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
