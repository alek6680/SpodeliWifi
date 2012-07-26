package com.swifi.al;

import java.util.ArrayList;
import java.util.List;

import com.swifi.al.utils.Utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MatchingAccesPointsFragment extends Fragment implements
		OnClickListener {
	private ListViewOverscrollBasic mListViewMatched;
	private AdapterShowAllAps adapter;;
	private Button scan;
	private WifiManager mWiFiManager;
	public boolean isWEP = false;
	private ArrayList<AccessPoint> mAvailableApS;
	public boolean mIsConnectedOrFailed = false;
	private View mMatchingAPLayout;
	private DatabaseAccessPoints mAccessPointsDatabase;
	private ArrayList<AccessPoint> mArrayAllAccessPoints;
	ArrayList<AccessPoint> mMatchedAccessPointsArrayList = new ArrayList<AccessPoint>();
	private int netId;
	public boolean isConnectedOrFailed = false;
	private AlertDialog mDialog;
	private IntentFilter intentFilter;
	private BroadcastReceiver br;
	private IntentFilter ifil;

	private BroadcastReceiver mBroadcastReceiverWifiChanges;
	View v;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mMatchingAPLayout = inflater.inflate(R.layout.scan_layout, null);
		mListViewMatched = (ListViewOverscrollBasic) mMatchingAPLayout
				.findViewById(R.id.listMatched);
		scan = (Button) mMatchingAPLayout.findViewById(R.id.buttonScan);
		adapter = new AdapterShowAllAps(getActivity());
		return mMatchingAPLayout;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mWiFiManager = (WifiManager) getActivity().getSystemService(
				Context.WIFI_SERVICE);

		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		scan.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				mMatchedAccessPointsArrayList.clear();
				if (mWiFiManager.isWifiEnabled()) {

					new isScanning().execute();

				} else if (mWiFiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
					Toast.makeText(getActivity(),
							"Please wait a bit until your WiFi is enabled",
							Toast.LENGTH_SHORT).show();
				} else if (mWiFiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING
						|| mWiFiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
					noInternetConnection();
				}
			}

		});

		mBroadcastReceiverWifiChanges = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String reason = intent
						.getStringExtra(ConnectivityManager.EXTRA_REASON);
				NetworkInfo currentNetworkInfo = (NetworkInfo) intent
						.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

				String state = currentNetworkInfo.getDetailedState().name();

				// CONNECTED or DISCONNECTED
				if (currentNetworkInfo.getTypeName().equalsIgnoreCase("WIFI")) {
					if (currentNetworkInfo.isConnected()) {
						Toast.makeText(getActivity(), "Wifi Connected...",
								Toast.LENGTH_SHORT).show();
						// If the phone has successfully connected to the AP,
						// save it!
						mWiFiManager.saveConfiguration();
						isConnectedOrFailed = true;
						getActivity().unregisterReceiver(
								mBroadcastReceiverWifiChanges);
					} else if (reason != null)
						Toast.makeText(getActivity(), reason,
								Toast.LENGTH_SHORT).show();
					else if (state.equalsIgnoreCase("DISCONNECTED")) {

						Toast.makeText(getActivity(), "Wifi Disconnected...",
								Toast.LENGTH_SHORT).show();
						mWiFiManager.removeNetwork(netId);
						isConnectedOrFailed = true;
						getActivity().unregisterReceiver(
								mBroadcastReceiverWifiChanges);

					}
				}
				
			}
		};

		intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		intentFilter.addAction(ConnectivityManager.EXTRA_REASON);

		super.onActivityCreated(savedInstanceState);
	}

	public void getAvailableAPs() {

		mWiFiManager.startScan();
		List<ScanResult> mScanResults = mWiFiManager.getScanResults();
		mAvailableApS = new ArrayList<AccessPoint>();
		if (mScanResults != null) {
			for (ScanResult mScanResult : mScanResults) {
				AccessPoint mAvailibleAP = new AccessPoint();
				String apname = mScanResult.SSID.toUpperCase();
				String bssid = mScanResult.BSSID;
				mAvailibleAP.setmAPName(apname);
				mAvailibleAP.setmBssid(bssid);
				mAvailableApS.add(mAvailibleAP);

			}
		}
	}

	public void getAllAccessPoints() {
		mAccessPointsDatabase = new DatabaseAccessPoints(getActivity());
		mArrayAllAccessPoints = new ArrayList<AccessPoint>();
		mArrayAllAccessPoints = mAccessPointsDatabase.getAllAps();
	}

	public void getSavedAPs() {
		for (int i = 0; i < mAvailableApS.size(); i++) {
			for (int j = 0; j < mArrayAllAccessPoints.size(); j++) {
				AccessPoint currenAP = mAvailableApS.get(i);
				AccessPoint allcurrentAP = mArrayAllAccessPoints.get(j);
				if (currenAP.getmBssid().equals(allcurrentAP.getmBssid())) {
					AccessPoint r = allcurrentAP;
					System.out.println("access point" + r.toString());

					mMatchedAccessPointsArrayList.add(r);
					if (mMatchedAccessPointsArrayList != null) {
						// Utils.showToastMsg(getActivity(),
						// "No Access points");
					}
				}
			}
		}

	}

	public class isScanning extends AsyncTask<String, Void, String> {
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(getActivity());
			dialog.setTitle("Scanning!");
			dialog.setMessage("Please wait..");
			dialog.setCancelable(true);
			dialog.setIndeterminate(true);
			dialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			String result = " ";

			if (mWiFiManager.isWifiEnabled()) {

				getAllAccessPoints();
				getAvailableAPs();
				getSavedAPs();

			}
			return result;

		}

		public void onPostExecute(String result) {
			if (result.equals(" ")) {
				if (mMatchedAccessPointsArrayList != null) {
					mListViewMatched.setAdapter(adapter);
					adapter.setItems(mMatchedAccessPointsArrayList);
					mListViewMatched
							.setOnItemClickListener(new OnItemClickListener() {

								public void onItemClick(AdapterView<?> arg0,
										View arg1, int arg2, long arg3) {
									AccessPoint mTemp = (AccessPoint) adapter
											.getItem(arg2);

									connectTo(mTemp);

								}
							});

				}
			} else {
				Utils.showToastMsg(getActivity(), "No AP's found");
			}

			dialog.dismiss();
		}

	}

	public void connectTo(AccessPoint accessPoint) {

		boolean exists = false;
		String mApName = accessPoint.getmAPName();
		String mPass = accessPoint.getmPassword();
		String mBssid = accessPoint.getmBssid();

		// List available networks
		List<WifiConfiguration> configs = mWiFiManager.getConfiguredNetworks();
		for (WifiConfiguration config : configs) {

			if (config.SSID.equalsIgnoreCase("\"" + mApName + "\"")) {
				exists = true;
			}
		}

		if (!exists) {

			WifiConfiguration wifiConfig = new WifiConfiguration();
			wifiConfig.SSID = "\"" + mApName + "\"";
			wifiConfig.BSSID = mBssid;
			if (isWEP) {
				wifiConfig.wepKeys[0] = "\"" + mPass + "\"";
			} else
				wifiConfig.preSharedKey = "\"" + mPass + "\"";
			wifiConfig.status = WifiConfiguration.Status.ENABLED;
			//
			mWiFiManager.setWifiEnabled(true);
			netId = mWiFiManager.addNetwork(wifiConfig);
			mWiFiManager.enableNetwork(netId, true);
			getActivity().registerReceiver(mBroadcastReceiverWifiChanges,
					intentFilter);
			
			// receiverRegistered = true;
			new isConnected().execute();

		} else
			Utils.showToastMsg(getActivity(), "Network is already configured");
	}

	public class isConnected extends AsyncTask<String, Void, String> {
		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			isConnectedOrFailed = true;
			dialog = new ProgressDialog(getActivity());
			dialog.setTitle("Connecting!");
			dialog.setMessage("Please wait..");
			dialog.setCancelable(true);
			dialog.setIndeterminate(true);
			dialog.show();
		}

		protected String doInBackground(String... vlezni) {
			while (true) {
				if (isConnectedOrFailed) {
					isConnectedOrFailed = false;
					break;
				}
			}
			return "";
		}

		public void onPostExecute(String result) {
			// Remove the progress dialog.
			dialog.dismiss();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	public void noInternetConnection() {
		if (mDialog != null)
			mDialog.dismiss();
		mDialog = new AlertDialog.Builder(getActivity())
				.setIcon(R.drawable.icon)
				.setCancelable(true)
				.setTitle("SpodeliWifi")
				.setMessage(
						"Your internet connection is disabled. Please enable the Wifi")
				.setPositiveButton("Enable",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								mWiFiManager.setWifiEnabled(true);

							}
						}).create();

		mDialog.show();

	}
}