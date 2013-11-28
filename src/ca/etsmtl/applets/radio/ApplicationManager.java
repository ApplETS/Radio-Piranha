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
