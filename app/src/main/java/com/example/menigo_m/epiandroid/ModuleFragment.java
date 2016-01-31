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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ModuleFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    LinkedList<JSONObject> objects = new LinkedList<>();

    private boolean registeredOnly = false;

    private String year = null;

    private String semester = null;

    ArrayList<String> spinnerArray = new ArrayList<>();

    public ModuleFragment() {
    }

    public static ModuleFragment newInstance() {
        return new ModuleFragment();
    }

    private void changeRegistered() {
        registeredOnly = !registeredOnly;
        if (registeredOnly)
            ((TextView) (getActivity().findViewById(R.id.registeredButton))).setText(R.string.all_activities);
        else
            ((TextView) (getActivity().findViewById(R.id.registeredButton))).setText(R.string.registered_only);
    }

    private void fillModule() {
        Map<String, String> params = new HashMap<>();
        params.put(getString(R.string.token), ((HomeActivity) getActivity()).getToken());
        params.put(getString(R.string.scolaryear), year);
        params.put(getString(R.string.location_api), ((HomeActivity) getActivity()).getLocation());
        params.put(getString(R.string.course_api), ((HomeActivity) getActivity()).getCourse());
        ((MyActivities) getActivity()).getApiConnection().doPost(params,
                getString(R.string.api_url).concat(getString(R.string.module_url)),
                Request.Method.GET, ((HomeActivity) getActivity()).getQueue(),
                new ApiRequest.INetworkCallback() {
                    @Override
                    public void onSuccess(JSONObject response) throws JSONException {
                        final Activity activity = getActivity();
                        if (activity == null)
                            return;
                        JSONArray jsonarray = response.getJSONArray(getString(R.string.items_api));
                        objects.clear();
                        for (int i = 0; i < jsonarray.length(); i++) {
                            try {
                                if ((!registeredOnly || !jsonarray.getJSONObject(i).getString(getString(R.string.status_api)).equals(getString(R.string.not_registered_api))) &&
                                        (semester.equals(getString(R.string.semester)) || semester.equals(jsonarray.getJSONObject(i).getString(getString(R.string.semester_api)))) &&
                                        (year.equals(getString(R.string.year)) || year.equals(jsonarray.getJSONObject(i).getString(getString(R.string.scolaryear)))))
                                    objects.add(jsonarray.getJSONObject(i));
                            } catch (JSONException ignored) {
                            }
                        }
                        final ListView listView = (ListView) activity.findViewById(R.id.module_element);
                        ModuleAdapter adapter = new ModuleAdapter(activity, objects);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent moduleActivity = new Intent(activity, ModuleActivity.class);
                                moduleActivity.putExtra(activity.getString(R.string.objectString), objects.get(position).toString());
                                activity.startActivity(moduleActivity);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_module, container, false);
        year = getActivity().getString(R.string.year);
        semester = getActivity().getString(R.string.semester);
        spinnerArray.clear();
        spinnerArray.add(getString(R.string.year));
        for (int i = 1999; i <= Calendar.getInstance().get(Calendar.YEAR); i++)
            spinnerArray.add(String.valueOf(i));
        Spinner yearSpinner = ((Spinner) view.findViewById(R.id.yearSpinner));
        yearSpinner.setAdapter(new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                spinnerArray));
        fillModule();
        ((Spinner) (view.findViewById(R.id.semestersSpinner))).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                semester = getResources().getStringArray(R.array.semester_array)[position];
                fillModule();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                semester = getString(R.string.semester);
                fillModule();
            }
        });
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                year = spinnerArray.get(position);
                fillModule();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                year = getString(R.string.year);
                fillModule();
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void registered_module_clicked(View view) {
        changeRegistered();
        fillModule();
    }
}
