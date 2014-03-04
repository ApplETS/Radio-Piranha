package ca.etsmtl.applets.radio.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import ca.etsmtl.applets.radio.R;

public class AboutFragment extends Fragment implements OnClickListener {

	private TextView txt;
	private WifiInfo info;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final WifiManager manager = (WifiManager) getActivity()
				.getSystemService(Context.WIFI_SERVICE);
		info = manager.getConnectionInfo();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.about, null, false);
		txt = ((TextView) v.findViewById(R.id.about_connection_info_lbl));
		TextView gitHubText =((TextView) v.findViewById(R.id.about_applets_lbl));
		gitHubText.setOnClickListener(this);
		if (info.getBSSID() != null) {
			final int str = WifiManager.calculateSignalLevel(info.getRssi(), 5);
			final int spd = info.getLinkSpeed();
			final String u = WifiInfo.LINK_SPEED_UNITS;

			final String ssid = info.getSSID();
			txt.setText(String.format(
					"Connected to %s at %s%s. Strength: %s/5", ssid, spd, u,
					str));
		}

		return v;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.about_applets_lbl){
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_applets)));
			startActivity(intent);
		}
	}
}
