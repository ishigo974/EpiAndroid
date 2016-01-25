package com.example.menigo_m.epiandroid;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
            city_promo.setText("Epitech ");
            city_promo.append(response.getString("promo"));
            city_promo.append(" ");
            city_promo.append(response.getString("location"));

            credits.setText(response.getString("credits"));
            credits.append(" credits and ");
            credits.append(response.getJSONObject("spice").getString("available_spice"));
            credits.append(" spices");
            JSONObject gpaObj = (JSONObject) response.getJSONArray("gpa").get(0);
            gpa.setText("GPA : ");
            gpa.append(gpaObj.getString("gpa"));
            Double logs = Double.valueOf(response.getJSONObject("nsstat").getString("active"));
            Double minLog = Double.valueOf(response.getJSONObject("nsstat").getString("nslog_norm"));
            logTime.setText(logs.toString());
            logTime.append(" active hours. Minimum required : ");
            logTime.append(minLog.toString());
            if (logs < minLog)
                logTime.setTextColor(getResources().getColor(R.color.red));
            else
                logTime.setTextColor(getResources().getColor(R.color.darkGreen));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> getParams()
    {
        Map<String, String> params = new HashMap<>();
        params.put(getString(R.string.token), ((HomeActivity) getActivity()).getToken());
        params.put(getString(R.string.user), ((HomeActivity) getActivity()).getLogin());
        return params;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // TODO rajouter les éléments supplémentaires à envoyer pour /user, ajouter les requêtes /alerts et /messages

        ((MyActivities) getActivity()).getApiConnection().doPost(getParams(),
                getString(R.string.api_url).concat(getString(R.string.user_url)),
                Request.Method.GET, ((HomeActivity) getActivity()).getQueue(),
                new ApiRequest.INetworkCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        displayUserInformation(response);
                        ((MyActivities) getActivity()).getApiConnection().doPost(getParams(),
                                getString(R.string.api_url).concat(getString(R.string.messages_url)),
                                Request.Method.GET, ((HomeActivity) getActivity()).getQueue(),
                                new ApiRequest.INetworkCallback() {
                                    @Override
                                    public void onSuccess(JSONObject response) throws JSONException {
                                    }

                                    @Override
                                    public void onSuccess(JSONArray response) throws JSONException {
                                        // TODO afficher les notifications
                                        LinkedList<JSONObject> objects = new LinkedList<>();
                                        for (int i = 0; i < response.length(); i++)
                                            objects.add(response.getJSONObject(i));
                                        final ListView listView = (ListView) getActivity().findViewById(R.id.notif_element);
                                        NotifAdapter adapter = new NotifAdapter(getActivity(), objects);
                                        listView.setAdapter(adapter);
                                    }

                                    @Override
                                    public void onError() {
                                        Toast.makeText(getActivity().getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG).show();
                                    }
                                });
                    }

                    @Override
                    public void onSuccess(JSONArray response) throws JSONException {
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG).show();
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
