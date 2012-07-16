package com.swifi.al.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.sax.StartElementListener;
import android.widget.Toast;

public class Utils {
	public static final int ICECREAM_SANDWICH_VERSION_CODE = 14;

	public static boolean checkFor3GOrWifi(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		// For 3G check
		boolean mIs3G = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.isConnectedOrConnecting();
		// For WiFi Check
		boolean mIsWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.isConnectedOrConnecting();

		System.out.println(mIs3G + " net " + mIsWifi);

		if (!mIs3G && !mIsWifi) {

			// Toast.makeText(context,
			// "Please make sure your Network Connection is ON ",
			// Toast.LENGTH_LONG).show();
			return false;
		} else {

			// Toast.makeText(context, "Already Connected", Toast.LENGTH_LONG)
			// .show();
			return true;
		}
	}

	public static boolean checkIceCream() {
		if (android.os.Build.VERSION.SDK_INT >= ICECREAM_SANDWICH_VERSION_CODE) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isOnline(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	public static void showToastMsg(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}

	public static boolean isServerReachable(Context context) {
		URL url = null;
		try {
			url = new URL("http://spodeliwifi.apphb.com/Wifi.svc");

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		URLConnection conn = null;
		try {
			conn = url.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		conn.setConnectTimeout(3000);
		conn.setReadTimeout(3000);
		try {
			conn.getInputStream();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
