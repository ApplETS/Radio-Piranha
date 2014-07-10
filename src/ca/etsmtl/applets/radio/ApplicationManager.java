package ca.etsmtl.applets.radio;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

import com.bugsense.trace.BugSenseHandler;

@ReportsCrashes(formUri = "http://www.bugsense.com/api/acra?api_key=54b7315a", formKey = "")
public class ApplicationManager extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		ACRA.init(this);
		BugSenseHandler.initAndStartSession(ApplicationManager.this, "54b7315a");
	}
}

/**
 * A dummy fragment representing a section of the app, but that simply
 * displays dummy text.
 */
//public static class DummySectionFragment extends Fragment {
//	/**
//	 * The fragment argument representing the section number for this
//	 * fragment.
//	 */
//	public static final String ARG_SECTION_NUMBER = "section_number";
//
//	public DummySectionFragment() {
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		View v = inflater.inflate(R.layout.about, null, false);
//		return v;
//	}
//}
