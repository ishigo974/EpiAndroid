package com.example.menigo_m.epiandroid;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link YearbookFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link YearbookFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YearbookFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    LinkedList<JSONObject> objects = new LinkedList<>();
    private int total = 0;
    private int page = 0;

    public YearbookFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment YearbookFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static YearbookFragment newInstance() {
        return new YearbookFragment();
    }

    private void fillYearbook() {
        Map<String, String> params = new HashMap<>();
        params.put(getString(R.string.token), ((HomeActivity) getActivity()).getToken());
        params.put("year", "2014");
        params.put("location", ((HomeActivity) getActivity()).getLocation());
        params.put("offset", Integer.toString(page * 48));
        ((MyActivities) getActivity()).getApiConnection().doPost(params,
                getString(R.string.api_url).concat(getString(R.string.trombi_url)),
                Request.Method.GET, ((HomeActivity) getActivity()).getQueue(),
                new ApiRequest.INetworkCallback() {
                    @Override
                    public void onSuccess(JSONObject response) throws JSONException {
                        total = response.getInt("total");
                        JSONArray jsonArray = response.getJSONArray("items");
                        objects.clear();
                        for (int i = 0; i < jsonArray.length(); i++)
                            objects.add(jsonArray.getJSONObject(i));
                        final ListView listView = (ListView) getActivity().findViewById(R.id.yearbook_element);
                        YearbookAdapter adapter = new YearbookAdapter(getActivity(), objects);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent userActivity = new Intent(getActivity(), UserActivity.class);
                                userActivity.putExtra("object", objects.get(position).toString());
                                getActivity().startActivity(userActivity);
                            }
                        });
                        getActivity().findViewById(R.id.prevButton).setEnabled(page > 0);
                        getActivity().findViewById(R.id.nextButton).setEnabled((page + 1) * 48 < total);
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
        View view = inflater.inflate(R.layout.fragment_yearbook, container, false);
        view.findViewById(R.id.prevButton).setEnabled(page > 0);
        view.findViewById(R.id.nextButton).setEnabled((page + 1) * 48 < total);
        fillYearbook();
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
}
