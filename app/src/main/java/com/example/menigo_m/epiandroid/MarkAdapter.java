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
 * Created by lopes_n on 1/26/16.
 */
public class MarkAdapter extends BaseAdapter {

    private List<JSONObject> _list;

    private Context _context;

    private LayoutInflater _inflater;

    public MarkAdapter(Context context, List<JSONObject> list) {
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
            layoutItem = (RelativeLayout) _inflater.inflate(R.layout.my_mark_item, parent, false);
        } else {
            layoutItem = (RelativeLayout) convertView;
        }

        TextView my_mark_final = (TextView) layoutItem.findViewById(R.id.my_mark_final);
        TextView my_mark_title = (TextView) layoutItem.findViewById(R.id.my_mark_title);
        TextView my_mark_date = (TextView) layoutItem.findViewById(R.id.my_mark_date);

        if (my_mark_date == null || my_mark_final == null || my_mark_title == null)
            return layoutItem;

        try {
            my_mark_final.setText(_list.get(position).getString(_context.getString(R.string.final_note_api)));
            my_mark_title.setText(_list.get(position).getString(_context.getString(R.string.title))
                    .concat(" - ").concat(_list.get(position).getString(_context.getString(R.string.title_module_api))));
            my_mark_date.setText(_list.get(position).getString(_context.getString(R.string.scolaryear)));

        } catch (JSONException ignored) {
        }
        return layoutItem;
    }

}