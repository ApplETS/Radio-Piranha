package com.applets;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.TextView;

import com.applets.radio.R;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = manager.getConnectionInfo();
		if (info.getBSSID() != null) {
			int str = WifiManager.calculateSignalLevel(info.getRssi(), 5);
			int spd = info.getLinkSpeed();
			String u = WifiInfo.LINK_SPEED_UNITS;

			String ssid = info.getSSID();

			((TextView) findViewById(R.id.about_connection_info_lbl))
					.setText(String.format(
							"Connected to %s at %s%s. Strength: %s/5", ssid,
							spd, u, str));
		}
	}
}
