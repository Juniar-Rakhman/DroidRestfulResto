

package com.droid.resto.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.droid.resto.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by a9jr5626 on 4/27/2014.
 */

public class MenuListAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;

    //TODO: Move these as strings.xml
    private static final String KEY_MAKANAN = "nama";
    private static final String KEY_HARGA = "harga";

    public MenuListAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data = d;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.menu_list_item, null);

        TextView makanan = (TextView) vi.findViewById(R.id.makanan);
        TextView harga = (TextView) vi.findViewById(R.id.harga);

        HashMap<String, String> menu = new HashMap<String, String>();

        menu = data.get(position);
	    makanan.setText(menu.get(KEY_MAKANAN));
	    harga.setText(menu.get(KEY_HARGA));

        return vi;
    }
}