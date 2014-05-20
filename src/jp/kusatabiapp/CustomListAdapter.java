package jp.kusatabiapp;

import java.math.BigDecimal;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomListAdapter extends ArrayAdapter<CustomListData> {

	LayoutInflater mlayoutInflater;
	CustomListData item;
	String venueKmDistance;
	String venueDistanceX;
	String venueDistanceY;
	ImageView imageView;
	TextView venueNameText;
	TextView venueCategoryText;
	TextView venueDistanceText;
	Double venueKmDistanceDouble;
	BigDecimal venueKmDistanceDecimal;

	public CustomListAdapter(Context context, int textViewResourceId,
			List<CustomListData> objects) {
		super(context, textViewResourceId, objects);
		mlayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		item = (CustomListData) getItem(position);
		if (null == convertView) {
			convertView = mlayoutInflater.inflate(R.layout.item_card, null);
		}

		// Ç®ÇÊÇªÇÃãóó£ÇÃéZèo
		venueKmDistanceDouble = (double) Double.valueOf(
				item.getDistanceTextData()).intValue() / 1000;
		venueKmDistanceDecimal = new BigDecimal(venueKmDistanceDouble)
				.setScale(1, BigDecimal.ROUND_UP);

		venueKmDistance = venueKmDistanceDecimal.toString();

		imageView = (ImageView) convertView.findViewById(R.id.venueImage);
		imageView.setImageBitmap(item.getImageData());

		venueNameText = (TextView) convertView.findViewById(R.id.venueName);
		venueNameText.setText(item.getTextData());

		venueCategoryText = (TextView) convertView
				.findViewById(R.id.venueCategory);
		venueCategoryText.setText(item.getCategoryTextData());

		venueDistanceText = (TextView) convertView
				.findViewById(R.id.venueDistance);

		venueDistanceX = CustomListFragment.venueDistanceX;
		venueDistanceY = CustomListFragment.venueDistanceY;

		venueDistanceText.setText(venueDistanceX + venueKmDistance
				+ venueDistanceY);
		return convertView;
	}
}