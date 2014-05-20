package jp.kusatabiapp;

import android.graphics.Bitmap;

public class CustomListData {
	private Bitmap imageData_;
	private String mtextData;
	private String mcategoryTextData;
	private String mdistanceTextData;
	private String mlatitudeTextData;
	private String mlongitudeTextData;

	public void setImagaData(Bitmap image) {
		imageData_ = image;
	}

	public Bitmap getImageData() {
		return imageData_;
	}

	public void setTextData(String text) {
		mtextData = text;
	}

	public void setCategoryTextData(String text) {
		mcategoryTextData = text;
	}

	public void setDistanceTextData(String text) {
		mdistanceTextData = text;
	}

	public void setLatitudeTextData(String text) {
		mlatitudeTextData = text;
	}

	public void setLongitudeTextData(String text) {
		mlongitudeTextData = text;
	}

	public String getTextData() {
		return mtextData;
	}

	public String getCategoryTextData() {
		return mcategoryTextData;
	}

	public String getDistanceTextData() {
		return mdistanceTextData;
	}

	public String getLatitudeTextData() {
		return mlatitudeTextData;
	}

	public String getLongitudeTextData() {
		return mlongitudeTextData;
	}

}