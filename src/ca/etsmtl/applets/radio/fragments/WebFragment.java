package ca.etsmtl.applets.radio.fragments;

import ca.etsmtl.applets.radio.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class WebFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	View v = inflater.inflate(R.layout.webview, null, false);
	WebView webView = (WebView) v.findViewById(R.id.webView1);
	webView.getSettings().setJavaScriptEnabled(true);
	webView.loadUrl("https://www.google.com/calendar/embed?title=Radio%20Piranha&showTitle=0&showNav=0&showDate=0&showTabs=0&showCalendars=0&mode=WEEK&height=1100&wkst=1&bgcolor=%23ffffff&src=programmation%40radiopiranha.com&color=%2329527A&src=radiopiranha.com_9djglke2jcf651qe72jn1guio8%40group.calendar.google.com&color=%23711616&ctz=America%2FMontreal");
	return v;
    }
}
