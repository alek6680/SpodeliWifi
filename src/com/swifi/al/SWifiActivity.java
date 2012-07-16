package com.swifi.al;

import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.swifi.al.utils.Utils;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class SWifiActivity extends SherlockFragmentActivity implements
		OnClickListener {

	private boolean mIsDisplayedAsHome = false;
	public ActionBar mActionBarSpodeliWifi;
	private ArrayList<AccessPoint> mAccessPointsFromServer;
	private DatabaseAccessPoints mAccessPointDatabase;
	private View mAlertDialogView;
	private WifiManager mWiFiManager;
	private WifiInfo mWifiInfo;
	private Menu mMainMenu;
	public static final int ICECREAM_SANDWICH_VERSION_CODE = 14;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.main);
		setSupportProgressBarIndeterminateVisibility(false);
		mAccessPointDatabase = new DatabaseAccessPoints(this);
		mWiFiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		if (savedInstanceState == null) {
			showMatchingAPs();
		} else {
			getSupportActionBar().setDisplayHomeAsUpEnabled(
					savedInstanceState.getBoolean(Constants.HOME_BUTTON));
		}
	}

	public boolean onCreateOptionsMenu(final Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_items, menu);

		mMainMenu = menu;
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case android.R.id.home:
			getSupportFragmentManager().popBackStack();
			getSupportActionBar().setDisplayHomeAsUpEnabled(false);
			mMainMenu.findItem(R.id.ShowAll).setVisible(true);
			return true;

		case R.id.Sync:
			if (Utils.checkFor3GOrWifi(getApplicationContext())) {

				setSupportProgressBarIndeterminateVisibility(true);
				String param = mAccessPointDatabase.getLastAccessPointId();
				GetAccessPointsFromServer mSyncTask = new GetAccessPointsFromServer();
				mSyncTask.execute(param);

			} else {
				Utils.showToastMsg(getApplicationContext(),
						"Please make sure your Network Connection is ON");
			}
			return true;
		case R.id.AddNew:
			if (Utils.checkFor3GOrWifi(getApplicationContext())) {
				addAccessPoint();
			} else {
				Utils.showToastMsg(getApplicationContext(),
						"Please make sure your Network Connection is ON");
			}

			return true;
		case R.id.ShowAll:
			showAllAPs();
			item.setVisible(false);
			return true;

		default:
		}
		return super.onOptionsItemSelected(item);

	}

	/** Shows Matching Access Point Fragment */
	public void showMatchingAPs() {

		mActionBarSpodeliWifi = getSupportActionBar();
		mActionBarSpodeliWifi.setIcon(R.drawable.icon);
		MatchingAccesPointsFragment newFragment = new MatchingAccesPointsFragment();
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.setCustomAnimations(R.anim.anim, R.anim.anim_right);
		transaction.replace(R.id.fragment_container, newFragment);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		mIsDisplayedAsHome = false;
		transaction.commit();
	}

	/** Shows All Access Point Fragment */
	public void showAllAPs() {

		AllAccesPointsFragment newFragment = new AllAccesPointsFragment();
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();

		transaction.setCustomAnimations(R.anim.anim, R.anim.anim_right);
		transaction.replace(R.id.fragment_container, newFragment);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mIsDisplayedAsHome = true;

		transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(Constants.HOME_BUTTON, mIsDisplayedAsHome);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

	public class GetAccessPointsFromServer extends
			AsyncTask<String, Void, String[]> {
		@Override
		protected void onPreExecute() {
			setSupportProgressBarIndeterminateVisibility(true);
			super.onPreExecute();
		}

		@Override
		protected String[] doInBackground(String... param) {
			String uri = Constants.URL_SERVER_GET + param[0];

			String[] result = new String[2];
			result[0] = param[0];
			if (Utils.isServerReachable(getApplicationContext())) {
				try {

					// send GET to the server for featured products
					HttpGet request = new HttpGet(uri);
					// inform the server we want json object
					request.setHeader("Accept", "application/json");
					request.setHeader("Content-type", "application/json");

					// execute GET method
					DefaultHttpClient httpClient = new DefaultHttpClient();
					// httpClient.getParams()
					// .setParameter("http.socket.timeout", 2000);

					HttpResponse response = httpClient.execute(request);
					// publishProgress(5);
					// get the response
					HttpEntity responseEntity = response.getEntity();

					// get the json string
					// result[1] = response.toString();
					result[1] = EntityUtils.toString(responseEntity);

				} catch (Exception e) {
					// Toast t = Toast.makeText(getActivity(),
					// R.string.toast_soap_error, Toast.LENGTH_SHORT);
					// t.show();
					e.printStackTrace();
				}

			}
			return result;

		}

		@Override
		protected void onPostExecute(String[] result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result[1] == null) {
				Utils.showToastMsg(getApplicationContext(),
						"Connection to server failed");
			} else {

				parseJSON(result[1]);
			}
			setSupportProgressBarIndeterminateVisibility(false);

		}

	}

	private ArrayList<AccessPoint> parseJSON(String string) {
		mAccessPointsFromServer = new ArrayList<AccessPoint>();
		JSONObject jsonObj = null;
		JSONArray jsonArray;

		try {
			jsonArray = new JSONArray(string);
			for (int i = 0; i < jsonArray.length(); i++) {
				AccessPoint mTemp = new AccessPoint();
				jsonObj = jsonArray.getJSONObject(i);
				mTemp.setmId(jsonObj.getInt("id"));
				mTemp.setmAPName(jsonObj.getString("name"));
				mTemp.setmBssid(jsonObj.getString("bssid"));
				mTemp.setmPassword(jsonObj.getString("password"));
				mAccessPointsFromServer.add(mTemp);
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (mAccessPointDatabase != null) {

			// mAccessPointDatabase.addAllAP(mAccessPointsFromServer);
			mAccessPointDatabase.addAllAccessPoints(mAccessPointsFromServer);
		} else {
			Utils.showToastMsg(getApplicationContext(),
					"No access point available...");
		}
		return mAccessPointsFromServer;
	}

	public void addAccessPoint() {
		mWifiInfo = mWiFiManager.getConnectionInfo();
		LayoutInflater factory = LayoutInflater.from(this);
		mAlertDialogView = factory.inflate(R.layout.add_dialog, null);
		final EditText name = (EditText) mAlertDialogView
				.findViewById(R.id.apn);
		name.setText(mWifiInfo.getSSID());
		final EditText pass = (EditText) mAlertDialogView
				.findViewById(R.id.password);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Add Access Point")
				.setView(mAlertDialogView)
				.setPositiveButton("Save",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								String mApName = name.getText().toString();
								String mPassword = pass.getText().toString();
								String mBssid = mWifiInfo.getBSSID();
								String mChangedBssid = mBssid;
								mChangedBssid = mChangedBssid.replace(":", "_");
								String param = mApName.replace(" ", "%20")
										+ "/" + mPassword.replace(" ", "%20")
										+ "/" + mChangedBssid;
								AddAccessPointToServer mAdd = new AddAccessPointToServer();
								mAdd.execute(param);

							}
						});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private class AddAccessPointToServer extends
			AsyncTask<String, Integer, String[]> {
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(SWifiActivity.this);
			dialog.setTitle("Syncing!");
			dialog.setMessage("Please wait..");
			dialog.setCancelable(false);
			dialog.setIndeterminate(true);
			dialog.show();
		}

		@Override
		protected String[] doInBackground(String... param) {

			String uri = Constants.URL_SERVER_ADD + param[0];

			String[] result = new String[2];
			result[0] = param[0];

			try {
				// send GET to the server for featured products
				HttpGet request = new HttpGet(uri);
				// inform the server we want json object
				request.setHeader("Accept", "application/json");
				request.setHeader("Content-type", "application/json");

				DefaultHttpClient httpClient = new DefaultHttpClient();
				// httpClient.getParams()
				// .setParameter("http.socket.timeout", 2000);

				HttpResponse response = httpClient.execute(request);
				HttpEntity responseEntity = response.getEntity();
				result[1] = EntityUtils.toString(responseEntity);

			} catch (Exception e) {

				e.printStackTrace();
			}

			return result;

		}

		@Override
		protected void onPostExecute(String[] result) {

			if (result[1].toString().equalsIgnoreCase("true")) {

				Toast.makeText(getApplicationContext(), "Access point added",
						Toast.LENGTH_SHORT).show();

			} else {

				Toast.makeText(getApplicationContext(),
						"Failed to add Access point ", Toast.LENGTH_LONG)
						.show();

			} // add for connection problem

			// linearProgress.setVisibility(View.GONE);
			dialog.dismiss();
			super.onPostExecute(result);
		}

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (Utils.isOnline(getApplicationContext())) {
			Toast.makeText(getApplicationContext(), "Connected wifi",
					Toast.LENGTH_LONG).show();
		} else {
		}
	}

}