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
    private String semester = getString(R.string.semester);
    private String year = getString(R.string.year);
    ArrayList<String> spinnerArray = new ArrayList<>();
    LinkedList<JSONObject> objects = new LinkedList<>();
    private String mail_content = "";

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
            ((TextView) (getActivity().findViewById(R.id.registeredButton))).setText(R.string.all_activities);
        else
            ((TextView) (getActivity().findViewById(R.id.registeredButton))).setText(R.string.registered_only);
    }

    public PlanningFragment() {
    }

    private void fillPlanning() {
        Map<String, String> params = new HashMap<>();
        params.put(getString(R.string.token), ((HomeActivity) getActivity()).getToken());
        params.put(getString(R.string.start), getDate(apiFormat));
        params.put(getString(R.string.end), getDate(apiFormat));

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
                        mail_content = activity.getString(R.string.my_planning);
                        mail_content = mail_content.concat(getDate(displayFormat).concat("\n\n"));
                        ((TextView) (activity.findViewById(R.id.date))).setText(getDate(displayFormat));
                        objects.clear();
                        for (int i = 0; i < response.length(); i++)
                            if (response.getJSONObject(i).getString(activity.getString(R.string.module_registered_api)).equals("true") &&
                                    (!registeredOnly || (registeredOnly && response.getJSONObject(i).getString(getString(R.string.event_registered_api)).equals(getString(R.string.registered_api)))) &&
                                    (semester.equals(getString(R.string.semester)) || semester.equals(response.getJSONObject(i).getString(getString(R.string.semester_api)))) &&
                                    (year.equals(getString(R.string.year)) || year.equals(response.getJSONObject(i).getString(getString(R.string.scolaryear))))) {
                                objects.add(response.getJSONObject(i));
                                mail_content = mail_content.concat(response.getJSONObject(i).getString(getString(R.string.acti_title)));
                                mail_content = mail_content.concat(" : ");
                                mail_content = mail_content.concat(response.getJSONObject(i).getString(getString(R.string.start)).split(" ")[1]);
                                mail_content = mail_content.concat("\n\n");
                            }
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


        spinnerArray.add(getString(R.string.year));
        for (int i = 1999; i <= Calendar.getInstance().get(Calendar.YEAR); i++)
            spinnerArray.add(String.valueOf(i));
        Spinner yearSpinner = ((Spinner) view.findViewById(R.id.yearSpinner));
        yearSpinner.setAdapter(new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                spinnerArray));

        fillPlanning();
        ((Spinner) (view.findViewById(R.id.semestersSpinner))).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                semester = getResources().getStringArray(R.array.semester_array)[position];
                fillPlanning();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                semester = getString(R.string.semester);
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
                year = getString(R.string.year);
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

    public void send_mail() {
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{""});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.my_planning_view));
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, mail_content);
        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)));
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
