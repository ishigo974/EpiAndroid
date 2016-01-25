package com.example.menigo_m.epiandroid;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by lopes_n on 1/25/16.
 */

public class NotifAdapter<T> extends BaseAdapter {

    private List<JSONObject> _list;

    private Context _context;

    private LayoutInflater _inflater;

    public NotifAdapter(Context context, List<JSONObject> list) {
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
            layoutItem = (RelativeLayout) _inflater.inflate(R.layout.my_notif_item, parent, false);
        } else {
            layoutItem = (RelativeLayout) convertView;
        }

        TextView my_notif_title = (TextView)layoutItem.findViewById(R.id.my_notif_title);
        TextView my_notif_content = (TextView)layoutItem.findViewById(R.id.my_notif_content);
        TextView my_notif_user = (TextView)layoutItem.findViewById(R.id.my_notif_user);
        TextView my_notif_time = (TextView)layoutItem.findViewById(R.id.my_notif_time);

        try {
            my_notif_title.setText(Html.fromHtml(_list.get(position).getString("title")));
            my_notif_content.setText(Html.fromHtml(_list.get(position).getString("content")));
            my_notif_user.setText(Html.fromHtml(_list.get(position).getJSONObject("user").getString("title")));
            my_notif_time.setText(Html.fromHtml(_list.get(position).getString("date")));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return layoutItem;
    }

}