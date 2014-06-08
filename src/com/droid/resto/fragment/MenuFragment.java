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
import android.widget.ListView;
import com.droid.resto.R;
import com.droid.resto.adapter.MenuListAdapter;
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
public class MenuFragment extends Fragment {
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
		View rootView = inflater.inflate(R.layout.fragment_menu, container, false);

		if (!mRunning) {
			mTask = new FetchTask(getActivity(), rootView);
			mTask.execute();
			mRunning = true;
		}

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
	 * Returns the current state of the background task.
	 */
	public boolean isRunning() {
		return mRunning;
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
		private static final String fetch_url = "http://restfulresto.cloudcontrolled.com/api/menu";
		private static final String login_url = "http://restfulresto.cloudcontrolled.com/";
		DefaultHttpClient httpClient;
		ServiceHandler sh;
		ArrayList<NameValuePair> params;
		String jsonStr;
		ArrayList<HashMap<String, String>> foodList;
		Context mContext;
		View rootView;

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
			Log.d("Login Response: ", "> " + loginStr);

			//TODO : Add loginStr check here. Make sure we have logged in.

			jsonStr = sh.makeServiceCall(httpClient, fetch_url, ServiceHandler.GET, null);
			Log.d("Get Response: ", "> " + jsonStr);

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

			foodList = new ArrayList<HashMap<String, String>>();

			if (jsonStr != null) {
				try {
					JSONObject jsonObj = new JSONObject(jsonStr);
					JSONObject menuObj = jsonObj.getJSONObject("data");
					JSONArray menuArray = menuObj.getJSONArray("menu");

					for (int i = 0; i < menuArray.length(); i++) {
						JSONObject c = menuArray.getJSONObject(i);
						String nama = c.getString("nama");
						String harga = c.getString("harga");

						HashMap<String, String> makanan = new HashMap<String, String>();
						makanan.put("nama", nama);
						makanan.put("harga", harga);
						foodList.add(makanan);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Log.e("ServiceHandler", "Couldn't get any data from the url");
			}

			MenuListAdapter listAdapter = new MenuListAdapter(getActivity(), foodList);
			ListView list = (ListView) rootView.findViewById(R.id.menu_list);

			//TODO : Add item click listener
//			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//				@Override
//				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//					// getting values from selected ListItem
//				}
//			});

			list.setAdapter(listAdapter);
			mCallbacks.onPostExecute(jsonStr);
			mRunning = false;
		}
	}

}