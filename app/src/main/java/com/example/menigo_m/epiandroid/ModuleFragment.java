package com.example.menigo_m.epiandroid;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ModuleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ModuleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ModuleFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
//    private Spinner semestersSpinner;

    private boolean registeredOnly = false;

    private String year = "Year";

    private String semester = "Semester";

    ArrayList<String> spinnerArray = new ArrayList<>();

    public ModuleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ModuleFragment.
     */
    public static ModuleFragment newInstance() {
        return new ModuleFragment();
    }

    private void changeRegistered() {
        registeredOnly = !registeredOnly;
        if (registeredOnly)
            ((TextView) (getActivity().findViewById(R.id.registeredButton))).setText("All activities");
        else
            ((TextView) (getActivity().findViewById(R.id.registeredButton))).setText("Where i'm registered only");
    }

    private void fillModule() {
        Map<String, String> params = new HashMap<>();
        params.put(getString(R.string.token), ((HomeActivity) getActivity()).getToken());
        params.put("scolaryear", year);
        params.put("location", ((HomeActivity) getActivity()).getLocation());
        params.put("course", ((HomeActivity) getActivity()).getCourse());
        ((MyActivities) getActivity()).getApiConnection().doPost(params,
                getString(R.string.api_url).concat(getString(R.string.module_url)),
                Request.Method.GET, ((HomeActivity) getActivity()).getQueue(),
                new ApiRequest.INetworkCallback() {
                    @Override
                    public void onSuccess(JSONObject response) throws JSONException {
                        JSONArray jsonarray = response.getJSONArray("items");
                        LinkedList<JSONObject> objects = new LinkedList<>();
                        for (int i = 0; i < jsonarray.length(); i++) {
                            try {
                                if ((!registeredOnly || !jsonarray.getJSONObject(i).getString("status").equals("notregistered")) &&
                                        (semester.equals("Semester") || semester.equals(jsonarray.getJSONObject(i).getString("semester"))) &&
                                        (year.equals("Year") || year.equals(jsonarray.getJSONObject(i).getString("scolaryear"))))
                                    objects.add(jsonarray.getJSONObject(i));
                            } catch (JSONException e) {}
                        }
                        final ListView listView = (ListView) getActivity().findViewById(R.id.module_element);
                        ModuleAdapter adapter = new ModuleAdapter(getActivity(), objects);
                        listView.setAdapter(adapter);
                    }

                    @Override
                    public void onSuccess(JSONArray response) throws JSONException {
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_module, container, false);
        spinnerArray.add("Year");
        for (int i = 1999; i <= Calendar.getInstance().get(Calendar.YEAR); i++)
            spinnerArray.add(String.valueOf(i));
        Spinner yearSpinner = ((Spinner) view.findViewById(R.id.yearSpinner));
        yearSpinner.setAdapter(new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                spinnerArray));
        fillModule();
        ((Spinner)(view.findViewById(R.id.semestersSpinner))).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                semester = getResources().getStringArray(R.array.semester_array)[position];
                fillModule();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                semester = "Semester";
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
                year = "Year";
                fillModule();
            }
        });
        return view;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void registered_module_clicked(View view) {
        changeRegistered();
        fillModule();
    }
}
