package com.droid.resto.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.droid.resto.R;
import com.droid.resto.adapter.MenuListAdapter;
import com.droid.resto.api.FetchTask;
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
		View rootView = inflater.inflate(R.layout.fragment_menu, container, false);

		if (!mRunning) {
			mTask = new MenuFetchTask(getActivity(), rootView);
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

	private class MenuFetchTask extends FetchTask {

		ArrayList<HashMap<String, String>> menuList;

		public MenuFetchTask(Context context, View rootView) {
			this.mContext = context;
			this.rootView = rootView;
			this.mCallbacks = (TaskCallbacks) getActivity();
			this.fetch_url = "/api/menu";
		}

		@Override
		protected void onPostExecute(Void ignore) {
			if (DEBUG) Log.i(TAG, jsonStr);

			menuList = new ArrayList<>();

			if (jsonStr != null) {
				try {
					JSONObject jsonObj = new JSONObject(jsonStr);
					JSONObject menuObj = jsonObj.getJSONObject("data");
					JSONArray menuArray = menuObj.getJSONArray("menu");

					for (int i = 0; i < menuArray.length(); i++) {
						JSONObject c = menuArray.getJSONObject(i);
						String nama = c.getString("nama");
						String harga = c.getString("harga");

						HashMap<String, String> makanan = new HashMap<>();
						makanan.put("nama", nama);
						makanan.put("harga", harga);
						menuList.add(makanan);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Log.e("ServiceHandler", "Couldn't get any data from the url");
			}

			MenuListAdapter listAdapter = new MenuListAdapter(getActivity(), menuList);
			ListView list = (ListView) rootView.findViewById(R.id.menu_list);

			//TODO : Add item click listener

			list.setAdapter(listAdapter);
			mCallbacks.onPostExecute(jsonStr);
			mRunning = false;
		}
	}
}