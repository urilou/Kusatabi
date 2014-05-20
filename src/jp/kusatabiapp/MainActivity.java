package jp.kusatabiapp;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener {

	Handler handler = new Handler();
	LocationManager locationManager = null;
	ListView lv;
	ConnectivityManager cm;
	String latitude;
	String longitude;
	String radius;
	RelativeLayout mainScreen;
	ImageView applogo;
	TextView appname;
	TextView process;
	Button retry;
	ConnectivityManager connectivity;
	NetworkInfo networkStatus;
	SharedPreferences settingData;
	CustomListFragment fragment;
	Boolean apploaded;
	int egg;
	Runnable TimerupCountTask = new Runnable() {
		@Override
		public void run() {
			locationtimeup();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// メイン画面
		mainScreen = (RelativeLayout) this.findViewById(R.id.main);
		applogo = (ImageView) this.findViewById(R.id.applogo);
		appname = (TextView) this.findViewById(R.id.appname);
		process = (TextView) this.findViewById(R.id.process);
		retry = (Button) this.findViewById(R.id.retryButton);
		retry.setVisibility(View.INVISIBLE);

		apploaded = false;
		retry.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				networkStatus();
				retry.setVisibility(View.INVISIBLE);
			}
		});

		// ネットワーク設定の取得
		networkStatus();
	}

	public void networkStatus() {
		connectivity = (ConnectivityManager) this.getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		networkStatus = connectivity.getActiveNetworkInfo();
		process.setText(R.string.checkNetworkStatus);

		if (networkStatus == null) {
			// ネットワーク無効
			networkAlart(R.string.err01Title, R.string.err01Msg,
					R.string.retry, R.string.cancel);
			process.setText(R.string.err01Title);

		} else {
			if (!networkStatus.isAvailable()) {
				// ネットワーク利用不可
				networkAlart(R.string.err02Title, R.string.err02Msg,
						R.string.retry, R.string.cancel);
				process.setText(R.string.err02Title);

			} else if (!networkStatus.isConnectedOrConnecting()) {
				// ネットワーク未接続
				networkAlart(R.string.err03Title, R.string.err03Msg,
						R.string.retry, R.string.cancel);
				process.setText(R.string.err03Title);
			} else {
				// ネットワーク利用可能
				// 位置情報設定の取得
				locationStatus();
			}
		}
	}

	public void locationStatus() {
		@SuppressWarnings("deprecation")
		String gpsStatus = android.provider.Settings.Secure.getString(
				getContentResolver(), Secure.LOCATION_PROVIDERS_ALLOWED);

		process.setText(R.string.checkLocationStatus);
		if (gpsStatus.indexOf("gps", 0) < 0) {
			// GPSが無効の場合
			locationAlart(R.string.err04Title, R.string.err04Msg,
					R.string.locationSettings,
					"android.settings.LOCATION_SOURCE_SETTINGS", R.string.retry);
			process.setText(R.string.err04Title);
		} else {
			// GPSが有効の場合
			Toast.makeText(getApplicationContext(), R.string.checkGpsToast,
					Toast.LENGTH_LONG).show();
			locationManager = (LocationManager) this
					.getSystemService(Context.LOCATION_SERVICE);
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 10000, 100, this);

			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_COARSE);
			criteria.setPowerRequirement(Criteria.POWER_LOW);
			process.setText(R.string.checkGps);

			// 時間切れタイマーの設定（15秒）
			handler.postDelayed(TimerupCountTask, 15000);
		}
	}

	public void locationtimeup() {
		Toast.makeText(getApplicationContext(), R.string.err05Msg,
				Toast.LENGTH_LONG).show();
		// 位置検出の中断
		locationManager.removeUpdates(this);
		process.setText(R.string.err05Title);

		// 設定の読み取り
		settingData = getSharedPreferences("kusatabi", MODE_PRIVATE);
		latitude = settingData.getString("lastestLatitude", null);
		longitude = settingData.getString("lastestLongitude", null);

		if (latitude == null && longitude == null) {
			Toast.makeText(getApplicationContext(), R.string.err06Msg,
					Toast.LENGTH_LONG).show();
			process.setText(R.string.err06Title);
			retry.setVisibility(View.VISIBLE);
			// 地図で選択する画面に変更予定
		} else {
			fsqDtata();
		}
	}

	public void fsqDtata() {
		// 設定の読み取り
		settingData = getSharedPreferences("kusatabi", MODE_PRIVATE);
		radius = settingData.getString("radius", "3000");

		// 3lm 1lm

		// 4sq APIへの接続
		String uriString = "https://api.foursquare.com/v2/venues/search?ll="
				+ latitude
				+ ","
				+ longitude
				+ "&radius="
				+ radius
				+ "&locale="
				+ getString(R.string.locate)
				+ "&intent=browse&categoryId=4d4b7105d754a06377d81259,4d4b7104d754a06370d81259&client_id=AKQL5WCKWTCYK1I1WL3XYJLLP5SLCTVHTGDYFYPLLOJQBK15&client_secret=0TTUIONM1AUJHC3U4RCQBY1DPL2VD2DMHA3I4L3VA1EB5JPC&v=20140224";
		AsyncTaskThread thread = new AsyncTaskThread(this);
		thread.execute(uriString);
	}

	// メニュー追加
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// メニュー設定読み込み
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// 設定の読み取り
		settingData = getSharedPreferences("kusatabi", MODE_PRIVATE);
		radius = settingData.getString("radius", "3000");

		if (radius.equals("3000")) {
		} else {
			menu.findItem(R.id.settingDistanceNearOnly).setChecked(true);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	// メニュー設定書き込み
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settingDistanceNearOnly:
			if (apploaded) {
				if (item.isChecked()) {
					// すべて表示
					item.setChecked(false);
					Toast.makeText(this, R.string.reload, Toast.LENGTH_SHORT)
							.show();
					settingData = getSharedPreferences("kusatabi", MODE_PRIVATE);
					SharedPreferences.Editor editor = settingData.edit();
					editor.putString("radius", "3000");
					editor.commit();
					resetList();
					networkStatus();
				} else {
					// 近いところだけ
					item.setChecked(true);
					Toast.makeText(this, R.string.reload, Toast.LENGTH_SHORT)
							.show();
					settingData = getSharedPreferences("kusatabi", MODE_PRIVATE);
					SharedPreferences.Editor editor = settingData.edit();
					editor.putString("radius", "1000");
					editor.commit();
					resetList();
					networkStatus();
				}
				return true;
			} else {
				Toast.makeText(this, R.string.wait, Toast.LENGTH_SHORT).show();
				egg++;
				if (egg == 8) {
					egg();
				}
			}
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		handler.removeCallbacks(TimerupCountTask);

		latitude = Double.toString(location.getLatitude());
		longitude = Double.toString(location.getLongitude());
		Toast.makeText(this, R.string.checkGpsOK, Toast.LENGTH_LONG).show();

		settingData = getSharedPreferences("kusatabi", MODE_PRIVATE);
		SharedPreferences.Editor editor = settingData.edit();
		editor.putString("lastestLatitude", latitude);
		editor.putString("lastestLongitude", longitude);
		editor.commit();

		// 位置検出の中断
		locationManager.removeUpdates(this);
		fsqDtata();
		process.setText(R.string.wait);
	}

	public void networkAlart(int title, int msg, int retry, int cancel) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setMessage(msg);
		alertDialogBuilder.setPositiveButton(retry,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						networkStatus();
					}
				});
		alertDialogBuilder.setNegativeButton(cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
		alertDialogBuilder.setCancelable(false);
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	public void locationAlart(int title, int msg, int intentbutton,
			final String intent, int retry) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setMessage(msg);
		alertDialogBuilder.setPositiveButton(intentbutton,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						startActivity(new Intent(intent));
						locationStatus();
					}
				});
		alertDialogBuilder.setNegativeButton(retry,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						locationStatus();
					}
				});
		alertDialogBuilder.setCancelable(false);
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	public void callList() {
		// リストの作成
		if (getFragmentManager().findFragmentById(android.R.id.content) == null) {
			fragment = new CustomListFragment();
			getFragmentManager().beginTransaction()
					.add(android.R.id.content, fragment).commit();
		}
		apploaded = true;
	}

	public void resetList() {
		// リストのリセット
		getFragmentManager().beginTransaction().remove(fragment).commit();
		mainScreen.setBackgroundColor(Color.rgb(255, 255, 255));
		applogo.setAlpha(1.0f);
		appname.setText(R.string.app_name);
		process.setText(R.string.wait);
	}

	public void egg() {
		String[] str_items = { getString(R.string.eggGinza),
				getString(R.string.eggSea), getString(R.string.cancel) };
		new AlertDialog.Builder(this).setTitle(R.string.eggTitle)
				.setItems(str_items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						settingData = getSharedPreferences("kusatabi",
								MODE_PRIVATE);
						SharedPreferences.Editor editor = settingData.edit();

						switch (which) {
						case 0:
							// 銀座
							editor.putString("lastestLatitude", "35.672951");
							editor.putString("lastestLongitude", "139.766863");
							editor.commit();
							break;
						// 海辺
						case 1:
							editor.putString("lastestLatitude", "34.964681");
							editor.putString("lastestLongitude", "139.724472");
							editor.commit();
							break;
						// キャンセル
						default:
							break;
						}
					}
				}).show();

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

}
