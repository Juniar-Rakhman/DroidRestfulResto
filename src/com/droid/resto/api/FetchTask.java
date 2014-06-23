package com.droid.resto.api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import com.droid.resto.fragment.MenuFragment;
import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by a9jr5626 on 6/22/2014.
 */
public class FetchTask extends AsyncTask<Void, Integer, Void> {

	protected static final String TAG = MenuFragment.class.getSimpleName();
	protected static final boolean DEBUG = true; // Set this to false to disable logs.
	protected static final String main_url = "http://localhost:8080/restfulresto";

	protected String fetch_url;
	protected DefaultHttpClient httpClient;
	protected ServiceHandler sh;
	protected ArrayList<NameValuePair> params;
	protected String jsonStr;
	protected TaskCallbacks mCallbacks;
	protected Context mContext;
	protected View rootView;
	protected boolean mRunning;

	@Override
	protected void onPreExecute() {
		// Proxy the call to the Activity.
		mCallbacks.onPreExecute();
		mRunning = true;
	}

	@Override
	protected Void doInBackground(Void... ignore) {
		if (DEBUG) Log.i(TAG, "doInBackground");
		httpClient = new DefaultHttpClient();
		sh = new ServiceHandler();

		//TODO : Move this to home as login session. Someday, somehow..
		params = new ArrayList<NameValuePair>(2);
		params.add(new BasicNameValuePair("LoginForm[username]", "pelayan"));
		params.add(new BasicNameValuePair("LoginForm[password]", "pelayan"));
		String loginStr = sh.makeServiceCall(httpClient, main_url, ServiceHandler.POST, params);
		Log.d("Login Response: ", "> " + loginStr);

		//TODO : Add loginStr check here. Make sure we have logged in.
		jsonStr = sh.makeServiceCall(httpClient, main_url + fetch_url, ServiceHandler.GET, null);
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

	public static interface TaskCallbacks {
		void onPreExecute();

		void onProgressUpdate(int percent);

		void onCancelled();

		void onPostExecute(String jsonStr);
	}
}
