package com.example.menigo_m.epiandroid;

import android.content.Context;
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
 * Created by lopes_n on 1/28/16.
 */
public class GradeAdapter extends BaseAdapter {

    private List<JSONObject> _list;

    private Context _context;

    private LayoutInflater _inflater;

    public GradeAdapter(Context context, List<JSONObject> list) {
        _context = context;
        _list = list;
        _inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return _list.size();
    }

    @Override
    public Object getItem(int position) {
        return _list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout layoutItem;
        if (convertView == null) {
            layoutItem = (RelativeLayout) _inflater.inflate(R.layout.my_grade_item, parent, false);
        } else {
            layoutItem = (RelativeLayout) convertView;
        }

        TextView module_name = (TextView)layoutItem.findViewById(R.id.module_name);
        TextView module_credits = (TextView)layoutItem.findViewById(R.id.module_grade);

        if (module_name == null || module_credits == null)
            return layoutItem;

        try {
            module_name.setText(_list.get(position).getString("title"));
            module_credits.setText(_list.get(position).getString("grade"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return layoutItem;
    }
}
