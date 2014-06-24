package com.droid.resto.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class TransFragment extends Fragment {

    private static final String TAG = TransFragment.class.getSimpleName();
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
        //TODO: Add fragment_login/logout behaviour here (i.e if logged in show welcome instead of home)
        View rootView = inflater.inflate(R.layout.fragment_trans, container, false);

        mTask = new TransFetchTask(getActivity(), rootView);
        mTask.execute();
        mRunning = true;

        return rootView;
    }

    /**
     * Note that this method is NOT called when the Fragment is being
     * retained across Activity instances. It will, however, be called when its
     * parent Activity is being destroyed for good (such as when the user clicks
     * the back button, etc.).
     */
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
    public void onActivityCreated(Bundle savedInstanceState) {
        if (DEBUG) Log.i(TAG, "onActivityCreated(Bundle)");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        if (DEBUG) Log.i(TAG, "onStart()");
        super.onStart();
    }

    @Override
    public void onResume() {
        if (DEBUG) Log.i(TAG, "onResume()");
        super.onResume();
    }

    @Override
    public void onPause() {
        if (DEBUG) Log.i(TAG, "onPause()");
        super.onPause();
    }

    @Override
    public void onStop() {
        if (DEBUG) Log.i(TAG, "onStop()");
        super.onStop();
    }

    private class TransFetchTask extends FetchTask {

        ArrayList<HashMap<String, String>> transList;
        TextView tv;
        TableLayout tableLayout;

        public TransFetchTask(Context context, View rootView) {
            this.mContext = context;
            this.rootView = rootView;
            this.mCallbacks = (TaskCallbacks) getActivity();
            this.fetch_url = "/api/transaksi";
        }

        @Override
        protected void onPostExecute(Void ignore) {
            // Proxy the call to the Activity.
            if (DEBUG) Log.i(TAG, jsonStr);

            transList = new ArrayList<>();

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject dataObj = jsonObj.getJSONObject("data");
                    JSONArray orderArray = dataObj.getJSONArray("transaksi");

                    for (int i = 0; i < orderArray.length(); i++) {
                        JSONObject c = orderArray.getJSONObject(i);
                        String tanggal = c.getString("tanggal");
                        String total = c.getString("harga_total");
                        String pemesan = c.getJSONObject("pesanan").getString("nama_pelanggan");
                        String pegawai = c.getJSONObject("pegawai").getString("nama");

                        HashMap<String, String> trans = new HashMap<>();
                        trans.put("tanggal", tanggal);
                        trans.put("total", total);
                        trans.put("pemesan", pemesan);
                        trans.put("pelayan", pegawai);

                        transList.add(trans);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            tableLayout = (TableLayout) rootView.findViewById(R.id.trans_table);
            tableLayout.removeAllViews();

            TableRow row = (TableRow) getActivity().getLayoutInflater().inflate(R.layout.trans_row_item, null);

            tv = (TextView) row.findViewById(R.id.cell_tanggal);
            tv.setText("Tanggal ");
            tv = (TextView) row.findViewById(R.id.cell_pemesan);
            tv.setText("Pemesan");
            tv = (TextView) row.findViewById(R.id.cell_pelayan);
            tv.setText("Pelayan");
            tv = (TextView) row.findViewById(R.id.cell_total);
            tv.setText("Total");

            tableLayout.addView(row);

            for (int i = 0; i < transList.size(); i++) {
                createTableRow(transList.get(i));
            }

            mCallbacks.onPostExecute(jsonStr);
            mRunning = false;
        }

        public void createTableRow(HashMap<String, String> trans) {

            TableRow row = (TableRow) getActivity().getLayoutInflater().inflate(R.layout.trans_row_item, null);

            tv = (TextView) row.findViewById(R.id.cell_tanggal);
            tv.setText(trans.get("tanggal"));
            tv = (TextView) row.findViewById(R.id.cell_pemesan);
            tv.setText(trans.get("pemesan"));
            tv = (TextView) row.findViewById(R.id.cell_pelayan);
            tv.setText(trans.get("pelayan"));
            tv = (TextView) row.findViewById(R.id.cell_total);
            tv.setText("Rp. " + trans.get("total"));

            tableLayout.addView(row);
        }
    }
}