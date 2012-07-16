package com.swifi.al;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class AllAccesPointsFragment extends Fragment {

	public static ArrayList<String> mMatchingAP = new ArrayList<String>();
	private ListViewOverscrollBasic mShowAllListView;
	private View mViewAllAccesPoints;
	private AdapterShowAllAps mAdapterShowAllAccessPoints;
	private DatabaseAccessPoints mAccessPointsDatabase;
	private ArrayList<AccessPoint> mArrayAllAccessPoints;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mViewAllAccesPoints = inflater.inflate(R.layout.all_accesspoints_layout, container, false);
		mShowAllListView = (ListViewOverscrollBasic) mViewAllAccesPoints.findViewById(R.id.two);
		return mViewAllAccesPoints;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setRetainInstance(true);
		mAdapterShowAllAccessPoints= new AdapterShowAllAps(getActivity());
		mAccessPointsDatabase = new DatabaseAccessPoints(getActivity());
		mArrayAllAccessPoints = new ArrayList<AccessPoint>();
		mArrayAllAccessPoints=mAccessPointsDatabase.getAllAps();
		mShowAllListView.setAdapter(mAdapterShowAllAccessPoints);
		mAdapterShowAllAccessPoints.setItems(mArrayAllAccessPoints);


	}

	public void onListItemClick(ListView arg0, View arg1, int arg2, long arg3) {

		Toast.makeText(getActivity(),
				"First scan for matching APs, if exists, you can connect",
				Toast.LENGTH_SHORT).show();

	}
}
