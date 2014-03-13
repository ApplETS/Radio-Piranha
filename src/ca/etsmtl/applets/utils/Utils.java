package ca.etsmtl.applets.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {

	
	public static  boolean connectedToInternet( Activity activity){
		boolean connected = false;
		ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if(ni!=null){
			connected = ni.isConnected();
		}
		return connected;
	}

}
