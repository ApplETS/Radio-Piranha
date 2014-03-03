package ca.etsmtl.applets.radio;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

import com.bugsense.trace.BugSenseHandler;

@ReportsCrashes(formUri = "http://www.bugsense.com/api/acra?api_key=4422c148", formKey = "")
public class ApplicationManager extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		ACRA.init(this);
		BugSenseHandler.initAndStartSession(this, "4422c148");
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
