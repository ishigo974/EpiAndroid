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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class YearbookFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    LinkedList<JSONObject> objects = new LinkedList<>();
    private int total = 0;
    private int page = 0;
    private String course;
    private String promo;

    public YearbookFragment() {
    }

    public static YearbookFragment newInstance() {
        return new YearbookFragment();
    }

    private void fillYearbook() {
        Map<String, String> params = new HashMap<>();
        params.put(getString(R.string.token), ((HomeActivity) getActivity()).getToken());
        params.put(getString(R.string.year_api), getString(R.string.year_api_date));
        params.put(getString(R.string.location_api), ((HomeActivity) getActivity()).getLocation());
        params.put(getString(R.string.offset_api), Integer.toString(page * 48));
        if (!course.equals(getString(R.string.course)))
            params.put(getString(R.string.course_api), course);
        if (!promo.equals(getString(R.string.promo)))
            params.put(getString(R.string.promo_api), promo);
        ((MyActivities) getActivity()).getApiConnection().doPost(params,
                getString(R.string.api_url).concat(getString(R.string.trombi_url)),
                Request.Method.GET, ((HomeActivity) getActivity()).getQueue(),
                new ApiRequest.INetworkCallback() {
                    @Override
                    public void onSuccess(JSONObject response) throws JSONException {
                        final Activity activity = getActivity();
                        if (activity == null)
                            return;
                        total = response.getInt(getString(R.string.total_api));
                        JSONArray jsonArray = response.getJSONArray(getString(R.string.items_api));
                        objects.clear();
                        for (int i = 0; i < jsonArray.length(); i++)
                            objects.add(jsonArray.getJSONObject(i));
                        final ListView listView = (ListView) activity.findViewById(R.id.yearbook_element);
                        YearbookAdapter adapter = new YearbookAdapter(activity, objects);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent userActivity = new Intent(activity, UserActivity.class);
                                userActivity.putExtra(activity.getString(R.string.objectString), objects.get(position).toString());
                                activity.startActivity(userActivity);
                            }
                        });
                        activity.findViewById(R.id.prevButton).setEnabled(page > 0);
                        activity.findViewById(R.id.nextButton).setEnabled((page + 1) * 48 < total);
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
        View view = inflater.inflate(R.layout.fragment_yearbook, container, false);
        course = getString(R.string.course);
        promo = getString(R.string.promo);
        view.findViewById(R.id.prevButton).setEnabled(page > 0);
        view.findViewById(R.id.nextButton).setEnabled((page + 1) * 48 < total);
        fillYearbook();
        ((Spinner)(view.findViewById(R.id.courseSpinner))).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                course = getResources().getStringArray(R.array.course_array)[position];
                fillYearbook();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                course = getString(R.string.course);
                fillYearbook();
            }
        });
        ((Spinner)(view.findViewById(R.id.promoSpinner))).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                promo = getResources().getStringArray(R.array.promo_array)[position];
                fillYearbook();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                promo = getString(R.string.promo);
                fillYearbook();
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

    public void prev_trombi_clicked(View view) {
        if (page > 0)
        {
            page -= 1;
            fillYearbook();
        }
    }

    public void next_trombi_clicked(View view) {
        if ((page + 1) * 48 < total)
        {
            page += 1;
            fillYearbook();
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
