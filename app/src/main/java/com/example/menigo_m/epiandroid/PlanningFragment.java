package com.example.menigo_m.epiandroid;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    private DateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd");
    private DateFormat displayFormat = new SimpleDateFormat("EEEE yyyy-MM-dd");
    private boolean registeredOnly = false;
    private String semester = "Semester";
    private String year = "Year";
    ArrayList<String> spinnerArray = new ArrayList<>();
    LinkedList<JSONObject> objects = new LinkedList<>();

    private Date date = new Date();

    private void prevDay() {
        date.setTime(date.getTime() - 86400000);
    }
    private void nextDay() {
        date.setTime(date.getTime() + 86400000);
    }
    private String getDate(DateFormat dateFormat) {
        return dateFormat.format(date);
    }

    private void changeRegistered() {
        registeredOnly = !registeredOnly;
        if (registeredOnly)
            ((TextView) (getActivity().findViewById(R.id.registeredButton))).setText("All activities");
        else
            ((TextView) (getActivity().findViewById(R.id.registeredButton))).setText("Where i'm registered only");
    }

    public PlanningFragment() {
    }

    private void fillPlanning() {
        Map<String, String> params = new HashMap<>();
        params.put(getString(R.string.token), ((HomeActivity) getActivity()).getToken());
        params.put("start", getDate(apiFormat));
        params.put("end", getDate(apiFormat));

        ((MyActivities) getActivity()).getApiConnection().doPost(params,
                getString(R.string.api_url).concat(getString(R.string.planning_url)),
                Request.Method.GET, ((HomeActivity) getActivity()).getQueue(),
                new ApiRequest.INetworkCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                    }

                    @Override
                    public void onSuccess(JSONArray response) throws JSONException {
                        final Activity activity = getActivity();
                        if (activity == null)
                            return;
                        ((TextView) (activity.findViewById(R.id.date))).setText(getDate(displayFormat));
                        objects.clear();
                        for (int i = 0; i < response.length(); i++)
                            if (response.getJSONObject(i).getString("module_registered").equals("true") &&
                                    (!registeredOnly || (registeredOnly && response.getJSONObject(i).getString("event_registered").equals("registered"))) &&
                                    (semester.equals("Semester") || semester.equals(response.getJSONObject(i).getString("semester"))) &&
                                    (year.equals("Year") || year.equals(response.getJSONObject(i).getString("scolaryear"))))
                                objects.add(response.getJSONObject(i));
                        final ListView listView = (ListView) activity.findViewById(R.id.planning_element);
                        PlanningAdapter adapter = new PlanningAdapter(activity, objects);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent eventActivity = new Intent(activity, EventActivity.class);
                                eventActivity.putExtra("object", objects.get(position).toString());
                                activity.startActivity(eventActivity);
                            }
                        });
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
        final View view = inflater.inflate(R.layout.fragment_planning, container, false);


        spinnerArray.add("Year");
        for (int i = 1999; i <= Calendar.getInstance().get(Calendar.YEAR); i++)
            spinnerArray.add(String.valueOf(i));
        Spinner yearSpinner = ((Spinner) view.findViewById(R.id.yearSpinner));
        yearSpinner.setAdapter(new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                spinnerArray));

        fillPlanning();
        ((Spinner)(view.findViewById(R.id.semestersSpinner))).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                semester = getResources().getStringArray(R.array.semester_array)[position];
                fillPlanning();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                semester = "Semester";
                fillPlanning();
            }
        });
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                year = spinnerArray.get(position);
                fillPlanning();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                year = "Year";
                fillPlanning();
            }
        });
        return view;
    }

    public void prev_button_clicked(View view) {
        prevDay();
        fillPlanning();
    }

    public void next_button_clicked(View view) {
        nextDay();
        fillPlanning();
    }

    public void registered_button_clicked(View view) {
        changeRegistered();
        fillPlanning();
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
