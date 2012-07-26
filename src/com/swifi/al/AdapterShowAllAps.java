package com.swifi.al;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdapterShowAllAps extends BaseAdapter {
	private ArrayList<AccessPoint> items = new ArrayList<AccessPoint>();
	private LayoutInflater inflater;

	public AdapterShowAllAps(Context context) {
		inflater = LayoutInflater.from(context);
	}

	public void setItems(ArrayList<AccessPoint> items) {
		this.items = items;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.size();
	}

	@Override
	public AccessPoint getItem(int position) {
		// TODO Auto-generated method stub
		return items.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AccessPoint item = getItem(position);
		Holder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.custom_layout, null);
			holder = new Holder();
			holder.mAPName = (TextView) convertView
					.findViewById(R.id.textViewAP);

			holder.mPass = (TextView) convertView
					.findViewById(R.id.textViewPassword);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.mAPName.setText("AP name: " +item.getmAPName());
		holder.mPass.setText("Password: " + item.getmPassword());
		// TODO Auto-generated method stub
		return convertView;
	}

	class Holder {
		TextView mAPName;
		TextView mPass;
	}

}
