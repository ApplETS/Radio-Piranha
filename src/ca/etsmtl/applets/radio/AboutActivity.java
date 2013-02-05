package ca.etsmtl.applets.radio;


import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.about);

	final WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
	final WifiInfo info = manager.getConnectionInfo();
	if (info.getBSSID() != null) {
	    final int str = WifiManager.calculateSignalLevel(info.getRssi(), 5);
	    final int spd = info.getLinkSpeed();
	    final String u = WifiInfo.LINK_SPEED_UNITS;

	    final String ssid = info.getSSID();

	    ((TextView) findViewById(R.id.about_connection_info_lbl)).setText(String.format(
		    "Connected to %s at %s%s. Strength: %s/5", ssid, spd, u, str));
	}
    }
}
