package ca.etsmtl.applets.radio.fragments;

import ca.etsmtl.applets.radio.R;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutFragment extends Fragment {

    private TextView txt;
    private WifiInfo info;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	final WifiManager manager = (WifiManager) getActivity().getSystemService(
		Context.WIFI_SERVICE);
	info = manager.getConnectionInfo();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	final View v = inflater.inflate(R.layout.about, null, false);
	txt = ((TextView) v.findViewById(R.id.about_connection_info_lbl));
	
	if (info.getBSSID() != null) {
	    final int str = WifiManager.calculateSignalLevel(info.getRssi(), 5);
	    final int spd = info.getLinkSpeed();
	    final String u = WifiInfo.LINK_SPEED_UNITS;

	    final String ssid = info.getSSID();
	    txt.setText(String.format("Connected to %s at %s%s. Strength: %s/5", ssid, spd, u, str));
	}
	
	return v;
    }
}
