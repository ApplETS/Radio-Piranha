package ca.etsmtl.applets.radio.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import ca.etsmtl.applets.radio.AppRadioActivity;
import ca.etsmtl.applets.radio.R;
import ca.etsmtl.applets.utils.Utils;

public class WebFragment extends Fragment implements OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setProgressBarIndeterminateVisibility(true);
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		    final View v = inflater.inflate(R.layout.webview, null, false);
		return refresh(v);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.web_connect_fail_reconnect){
			
			AppRadioActivity activity = (AppRadioActivity)getActivity();
			if(Utils.connectedToInternet(activity)){
				activity.startMusic();
				refresh(v.getRootView());
			}
		}
		
	}
	
	private View refresh(View v){

		final WebView webView = (WebView) v.findViewById(R.id.webView1);
	    RelativeLayout  layoutNotConnected = (RelativeLayout) v.findViewById(R.id.webview_connect_fail_layout);
		
		
		if(Utils.connectedToInternet(getActivity())){
	
		    layoutNotConnected.setVisibility(View.GONE);
		    webView.setVisibility(View.VISIBLE);
		    webView.getSettings().setJavaScriptEnabled(true);
		    webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				getActivity().setProgressBarIndeterminateVisibility(false);
			}
		});
			
			webView.loadUrl("https://www.google.com/calendar/embed?title=Radio%20Piranha&showTitle=0&showNav=0&showDate=0&showTabs=0&showCalendars=0&mode=WEEK&height=1100&wkst=1&bgcolor=%23ffffff&src=programmation%40radiopiranha.com&color=%2329527A&src=radiopiranha.com_9djglke2jcf651qe72jn1guio8%40group.calendar.google.com&color=%23711616&ctz=America%2FMontreal");
		}else{
			webView.setVisibility(View.GONE);
			layoutNotConnected.setVisibility(View.VISIBLE);
			ImageButton button = (ImageButton) v.findViewById(R.id.web_connect_fail_reconnect);
			button.setOnClickListener(this);
		}
		
		return v;
	}
	
}
