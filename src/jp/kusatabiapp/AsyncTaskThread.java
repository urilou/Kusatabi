package jp.kusatabiapp;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

@SuppressWarnings("unused")
public class AsyncTaskThread extends AsyncTask<String, Void, String> {
	Activity mActivity;
	String fsqJsonData;
	HttpResponse response = null;
	static String fsqcode;
	static String venueName[];
	static String venueCategory[];
	static String venueDistance[];
	static String venueLatitude[];
	static String venueLongitude[];

	JSONArray jArray;
	TextView process;
	Button retry;
	StringBuilder sb = new StringBuilder();

	public AsyncTaskThread(MainActivity mainActivity) {
		this.mActivity = mainActivity;
	}

	@Override
	protected String doInBackground(String... uriString) {
		// TODO Auto-generated method stub
		response = null;
		process = (TextView) this.mActivity.findViewById(R.id.process);
		retry = (Button) this.mActivity.findViewById(R.id.retryButton);
		try {
			HttpGet httpGet = new HttpGet(uriString[0]);
			DefaultHttpClient httpClient = new DefaultHttpClient();

			// 応答の確認
			response = httpClient.execute(httpGet);
			int status = response.getStatusLine().getStatusCode();
			if (status == HttpStatus.SC_OK) {
				fsqJsonData = EntityUtils.toString(response.getEntity(),
						"UTF-8");

				try {
					// 4sqからのベニュー取得
					JSONObject rootObject = new JSONObject(fsqJsonData);
					JSONObject metaObject = rootObject.getJSONObject("meta");
					JSONObject resObject = rootObject.getJSONObject("response");
					JSONArray venuesArray = resObject.getJSONArray("venues");

					venueName = new String[venuesArray.length()];
					venueCategory = new String[venuesArray.length()];
					venueDistance = new String[venuesArray.length()];
					venueLatitude = new String[venuesArray.length()];
					venueLongitude = new String[venuesArray.length()];

					fsqcode = metaObject.getString("code").toString();

					if (fsqcode.equals("200")) {
						for (int i = 0; i < venuesArray.length(); i++) {
							venueName[i] = venuesArray.getJSONObject(i)
									.getString("name").toString();

							JSONObject venueObject = venuesArray
									.getJSONObject(i);
							JSONArray venusCategoryArray = venueObject
									.getJSONArray("categories");
							venueCategory[i] = venusCategoryArray
									.getJSONObject(0).getString("name")
									.toString();

							venueDistance[i] = venueObject
									.getJSONObject("location")
									.getString("distance").toString();
							venueLatitude[i] = venueObject
									.getJSONObject("location").getString("lat")
									.toString();
							venueLongitude[i] = venueObject
									.getJSONObject("location").getString("lng")
									.toString();
						}
					}
				} catch (JSONException e) {
					// 例外処理
				}
			} else {
				fsqcode = "000";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		process = (TextView) this.mActivity.findViewById(R.id.process);
		try {
			if (fsqcode.equals("200")) {
				((MainActivity) mActivity).callList();
			} else if (fsqcode.equals("500")) {
				process.setText(R.string.err07Title);
				retry.setVisibility(View.VISIBLE);
			} else if (fsqcode.equals("400")) {
				process.setText(R.string.err08Title);
				retry.setVisibility(View.VISIBLE);
			} else if (fsqcode.equals("000")) {
				process.setText(R.string.err09Title);
				retry.setVisibility(View.VISIBLE);
			} else {
				process.setText(R.string.err10Title + fsqcode + ")");
				retry.setVisibility(View.VISIBLE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
