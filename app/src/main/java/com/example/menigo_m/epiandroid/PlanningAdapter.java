package com.example.menigo_m.epiandroid;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by lopes_n on 1/25/16.
 */

public class PlanningAdapter extends BaseAdapter {

    private List<JSONObject> _list;

    private Context _context;

    private LayoutInflater _inflater;

    public PlanningAdapter(Context context, List<JSONObject> list) {
        _context = context;
        _list = list;
        _inflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return _list.size();
    }

    public Object getItem(int position) {
        return _list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout layoutItem;
        if (convertView == null) {
            layoutItem = (RelativeLayout) _inflater.inflate(R.layout.my_planning_item, parent, false);
        } else {
            layoutItem = (RelativeLayout) convertView;
        }

        TextView my_activity_title = (TextView)layoutItem.findViewById(R.id.my_activity_title);

        try {
            JSONObject room = _list.get(position).getJSONObject("room");
            String content = _list.get(position).getString("titlemodule");
            content = content.concat(" - ");
            content = content.concat(_list.get(position).getString("acti_title"));
            content = content.concat("|");
            content = content.concat(room.getString("code"));
            my_activity_title.setText(content);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return layoutItem;
    }

}