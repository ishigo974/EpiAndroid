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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProjectsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProjectsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProjectsFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    LinkedList<JSONObject> objects = new LinkedList<>();

    private String semester = getString(R.string.semester);

    private boolean progress = false;

    private Date currentDate = new Date();

    private DateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd");

    public ProjectsFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProjectsFragment.
     */

    private boolean isProgress(JSONObject obj)
    {
        try {
            if (currentDate.before(apiFormat.parse(obj.getString(getString(R.string.end_acti_api)).split(" ")[0])) &&
                    currentDate.after(apiFormat.parse(obj.getString(getString(R.string.begin_acti_api)).split(" ")[0])))
                return true;

        } catch (ParseException | JSONException ignored) {
        }
        return false;
    }

    // TODO: Rename and change types and number of parameters
    public static ProjectsFragment newInstance() {
        return new ProjectsFragment();
    }

    private void fillProjects() {
        Map<String, String> params = new HashMap<>();
        params.put(getString(R.string.token), ((HomeActivity) getActivity()).getToken());
        final Activity activity = getActivity();
        if (activity == null)
            return;
        ((MyActivities) getActivity()).getApiConnection().doPost(params,
                getString(R.string.api_url).concat(getString(R.string.projects_url)),
                Request.Method.GET, ((HomeActivity) getActivity()).getQueue(),
                new ApiRequest.INetworkCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                    }

                    @Override
                    public void onSuccess(JSONArray response) throws JSONException {
                        objects.clear();
                        for (int i = 0; i < response.length(); i++)
                            if (!response.getJSONObject(i).getString(getString(R.string.project_api)).equals("null") &&
                                    ((semester.equals(getString(R.string.semester)) || semester.equals(response.getJSONObject(i).getString(getString(R.string.codeinstance)).split("-")[1]))) &&
                                    (!progress || isProgress(response.getJSONObject(i))))
                                objects.add(response.getJSONObject(i));
                        final Activity activity = getActivity();
                        if (activity == null)
                            return;
                        final ListView listView = (ListView) activity.findViewById(R.id.projects_element);
                        ProjectsAdapter adapter = new ProjectsAdapter(activity, objects);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent projectActivity = new Intent(activity, ProjectActivity.class);
                                projectActivity.putExtra("object", objects.get(position).toString());
                                activity.startActivity(projectActivity);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_projects, container, false);

        fillProjects();
        ((Spinner)(view.findViewById(R.id.semestersSpinner))).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                semester = getResources().getStringArray(R.array.semester_array)[position];
                fillProjects();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                semester = getString(R.string.semester);
                fillProjects();
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
                    + context.getString(R.string.fragment_attach));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void progress_button_clicked(View view) {
        progress = !progress;
        if (progress)
            ((Button)getActivity().findViewById(R.id.progressButton)).setText(R.string.all_projects);
        else
            ((Button)getActivity().findViewById(R.id.progressButton)).setText(R.string.in_progress_projects);
        fillProjects();
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
