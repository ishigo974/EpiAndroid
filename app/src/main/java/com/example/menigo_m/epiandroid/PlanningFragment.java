package com.example.menigo_m.epiandroid;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlanningFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlanningFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlanningFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private Date date = new Date();

    private void prevDay() {
        date.setTime(date.getTime() - 86400000);
    }

    private void nextDay() {
        date.setTime(date.getTime() + 86400000);
    }

    private String getDate() {
        return dateFormat.format(date);
    }

    public PlanningFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PlanningFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlanningFragment newInstance() {
        return new PlanningFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Map<String, String> params = new HashMap<>();
        params.put(getString(R.string.token), ((HomeActivity) getActivity()).getToken());
        params.put("start", getDate());
        params.put("end", getDate());

        ((MyActivities) getActivity()).getApiConnection().doPost(params,
                getString(R.string.api_url).concat(getString(R.string.planning_url)),
                Request.Method.GET, ((HomeActivity) getActivity()).getQueue(),
                new ApiRequest.INetworkCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                    }

                    @Override
                    public void onSuccess(JSONArray response) throws JSONException {
                        ((TextView) (getActivity().findViewById(R.id.date))).setText(getDate());
                        LinkedList<JSONObject> objects = new LinkedList<>();
                        for (int i = 0; i < response.length(); i++)
                            if (response.getJSONObject(i).getString("module_registered").equals("true"))
                                objects.add(response.getJSONObject(i));
                        final ListView listView = (ListView) getActivity().findViewById(R.id.planning_element);
                        PlanningAdapter adapter = new PlanningAdapter(getActivity(), objects);
                        listView.setAdapter(adapter);
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG).show();
                    }
                });
        return inflater.inflate(R.layout.fragment_planning, container, false);
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
