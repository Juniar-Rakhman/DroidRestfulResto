package com.droid.resto.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.droid.resto.R;
import com.droid.resto.api.FetchTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by a9jr5626 on 4/27/2014.
 */
public class OrderFragment extends Fragment {
    private static final String TAG = MenuFragment.class.getSimpleName();
    private static final boolean DEBUG = true; // Set this to false to disable logs.
    private FetchTask mTask;
    private boolean mRunning;

    /**
     * Hold a reference to the parent Activity so we can report the task's current
     * progress and results. The Android framework will pass us a reference to the
     * newly created Activity after each configuration change.
     */
    @Override
    public void onAttach(Activity activity) {
        if (DEBUG) Log.i(TAG, "onAttach(Activity)");
        super.onAttach(activity);
//		if (!(activity instanceof TaskCallbacks)) {
//			throw new IllegalStateException("Activity must implement the TaskCallbacks interface.");
//		}
//		// Hold a reference to the parent Activity so we can report back the current progress and results.
//		mCallbacks = (TaskCallbacks) activity;
    }

    /**
     * This method is called once when the Fragment is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (DEBUG) Log.i(TAG, "onCreate(Bundle)");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_order, container, false);

        if (!mRunning) {
            mTask = new OrderFetchTask(getActivity(), rootView);
            mTask.execute();
            mRunning = true;
        }

        return rootView;
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.i(TAG, "onDestroy()");
        super.onDestroy();
        cancel();
    }

    public void cancel() {
        if (mRunning) {
            mTask.cancel(false);
            mTask = null;
            mRunning = false;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (DEBUG) Log.i(TAG, "onCreateOptionsMenu()");
        getActivity().getMenuInflater().inflate(R.menu.order_add, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private class OrderFetchTask extends FetchTask {

        ArrayList<HashMap<String, String>> orderList;
        TextView tv;
        TableLayout tableLayout;

        public OrderFetchTask(Context context, View rootView) {
            this.mContext = context;
            this.rootView = rootView;
            this.mCallbacks = (TaskCallbacks) getActivity();
            this.fetch_url = "/api/pesanan";
        }

        @Override
        protected void onPostExecute(Void ignore) {
            // Proxy the call to the Activity.
            if (DEBUG) Log.i(TAG, jsonStr);

            orderList = new ArrayList<HashMap<String, String>>();

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject dataObj = jsonObj.getJSONObject("data");
                    JSONArray orderArray = dataObj.getJSONArray("pesanan");

                    for (int i = 0; i < orderArray.length(); i++) {
                        JSONObject c = orderArray.getJSONObject(i);
                        String meja = c.getString("nomor_meja");
                        String nama = c.getString("nama_pelanggan");
                        String harga = c.getString("total_harga");

                        HashMap<String, String> order = new HashMap<String, String>();
                        order.put("meja", meja);
                        order.put("nama", nama);
                        order.put("harga", harga);
                        orderList.add(order);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            tableLayout = (TableLayout) rootView.findViewById(R.id.order_table);
            tableLayout.removeAllViews();

            TableRow row = (TableRow) getActivity().getLayoutInflater().inflate(R.layout.order_row_item, null);

            tv = (TextView) row.findViewById(R.id.cell_meja);
            tv.setText("No Meja ");
            tv = (TextView) row.findViewById(R.id.cell_nama);
            tv.setText("Nama Pelanggan");
            tv = (TextView) row.findViewById(R.id.cell_harga);
            tv.setText("Total Harga ");

            tableLayout.addView(row);

            for (int i = 0; i < orderList.size(); i++) {
                createTableRow(orderList.get(i));
            }

            mCallbacks.onPostExecute(jsonStr);
            mRunning = false;
        }

        protected void createTableRow(HashMap<String, String> order) {

            TableRow row = (TableRow) getActivity().getLayoutInflater().inflate(R.layout.order_row_item, null);

            tv = (TextView) row.findViewById(R.id.cell_meja);
            tv.setText(order.get("meja"));
            tv = (TextView) row.findViewById(R.id.cell_nama);
            tv.setText(order.get("nama"));
            tv = (TextView) row.findViewById(R.id.cell_harga);
            tv.setText("Rp. " + order.get("harga"));

            tableLayout.addView(row);
        }
    }

}