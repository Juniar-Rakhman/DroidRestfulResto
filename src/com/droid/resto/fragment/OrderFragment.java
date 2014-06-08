package com.droid.resto.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.droid.resto.R;
import com.droid.resto.api.ServiceHandler;
import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
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
	private TaskCallbacks mCallbacks;
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
		if (!(activity instanceof TaskCallbacks)) {
			throw new IllegalStateException("Activity must implement the TaskCallbacks interface.");
		}
		// Hold a reference to the parent Activity so we can report back the current progress and results.
		mCallbacks = (TaskCallbacks) activity;
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
		View rootView = inflater.inflate(R.layout.fragment_order, container, false);

		mTask = new FetchTask(getActivity(), rootView);
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

	/**
	 * Callback interface through which the fragment can report the task's
	 * progress and results back to the Activity.
	 */
	public static interface TaskCallbacks {
		void onPreExecute();

		void onProgressUpdate(int percent);

		void onCancelled();

		void onPostExecute(String jsonStr);
	}

	private class FetchTask extends AsyncTask<Void, Integer, Void> {
		//TODO: Move these as settings.
		private static final String fetch_url = "http://restfulresto.cloudcontrolled.com/api/pesanan";
		private static final String login_url = "http://restfulresto.cloudcontrolled.com/";
		DefaultHttpClient httpClient;
		ServiceHandler sh;
		ArrayList<NameValuePair> params;
		String jsonStr;
		ArrayList<HashMap<String, String>> orderList;
		Context mContext;
		View rootView;
		TextView tv;
		TableLayout tableLayout;

		public FetchTask(Context context, View rootView) {
			this.mContext = context;
			this.rootView = rootView;
		}

		@Override
		protected void onPreExecute() {
			// Proxy the call to the Activity.
			mCallbacks.onPreExecute();
			mRunning = true;
		}

		/**
		 * Note that we do NOT call the callback object's methods directly from the
		 * background thread, as this could result in a race condition.
		 */

		//TODO : If listview has been populated, do not fetch menu unless refreshed (add option to refresh).
		@Override
		protected Void doInBackground(Void... ignore) {
			if (DEBUG) Log.i(TAG, "doInBackground");
			httpClient = new DefaultHttpClient();
			sh = new ServiceHandler();

			//login
			//TODO : Move this to home as login session.
			params = new ArrayList<NameValuePair>(2);
			params.add(new BasicNameValuePair("LoginForm[username]", "pelayan"));
			params.add(new BasicNameValuePair("LoginForm[password]", "pelayan"));
			String loginStr = sh.makeServiceCall(httpClient, login_url, ServiceHandler.POST, params);

			//TODO : Add loginStr check here. Make sure we have logged in.

			jsonStr = sh.makeServiceCall(httpClient, fetch_url, ServiceHandler.GET, null);

			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... percent) {
			// Proxy the call to the Activity.
			mCallbacks.onProgressUpdate(percent[0]);
		}

		@Override
		protected void onCancelled() {
			// Proxy the call to the Activity.
			mCallbacks.onCancelled();
			mRunning = false;
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

		public void createTableRow(HashMap<String, String> order) {

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