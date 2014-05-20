package jp.kusatabiapp;

import java.util.ArrayList;
import java.util.List;

import android.app.ListFragment;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CustomListFragment extends ListFragment {

	static String venueDistanceX;
	static String venueDistanceY;
	String[] venueItemName = AsyncTaskThread.venueName;
	String[] venueItemCategory = AsyncTaskThread.venueCategory;
	String[] venueItemDistance = AsyncTaskThread.venueDistance;
	String[] venueItemLatitude = AsyncTaskThread.venueLatitude;
	String[] venueItemLongitude = AsyncTaskThread.venueLongitude;
	LayoutInflater layoutInflater_;
	ListView lv;
	ImageView applogo;
	TextView appname;
	TextView process;
	RelativeLayout main;
	CustomListAdapter customAdapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// データの作成
		List<CustomListData> objects = new ArrayList<CustomListData>();
		lv = getListView();

		// 背画面
		main = (RelativeLayout) this.getActivity().findViewById(R.id.main);
		applogo = (ImageView) this.getActivity().findViewById(R.id.applogo);
		appname = (TextView) this.getActivity().findViewById(R.id.appname);
		process = (TextView) this.getActivity().findViewById(R.id.process);

		// アダプタに引き継ぎ
		venueDistanceX = getString(R.string.venueDistanceX);
		venueDistanceY = getString(R.string.venueDistanceY);

		for (int i = 0; i < venueItemName.length; i++) {
			CustomListData item = new CustomListData();
			item.setTextData(venueItemName[i]);
			item.setCategoryTextData(venueItemCategory[i]);
			item.setDistanceTextData(venueItemDistance[i]);
			item.setLatitudeTextData(venueItemLatitude[i]);
			item.setLongitudeTextData(venueItemLongitude[i]);
			objects.add(item);
		}
		customAdapter = new CustomListAdapter(getActivity(), 0, objects);
		setListAdapter(customAdapter);
		lv.setDividerHeight(0);

		// 背画面制御
		if (venueItemName.length == 0) {
			process.setText(R.string.venueNotFound);
		} else {
			main.setBackgroundColor(Color.rgb(214, 214, 214));
			applogo.setAlpha(0.0f);
			appname.setText("");
			process.setText("");
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Toast.makeText(
				this.getActivity(),
				getString(R.string.venueToGoX) + venueItemName[position]
						+ getString(R.string.venueToGoY), Toast.LENGTH_LONG)
				.show();
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setClassName("com.google.android.apps.maps",
				"com.google.android.maps.MapsActivity");
		intent.setData(Uri.parse("http://maps.google.com/maps?daddr="
				+ venueItemLatitude[position] + ","
				+ venueItemLongitude[position] + "&dirflg=w"));
		startActivity(intent);
	}
}