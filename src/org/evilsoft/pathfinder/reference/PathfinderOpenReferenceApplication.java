package org.evilsoft.pathfinder.reference;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

/*@ReportsCrashes(
		formKey = "dElRcVRQa0J0dHBVSjBIYmloWjdXb2c6MQ",
		socketTimeout = 5000)*/
@ReportsCrashes(
		formKey = "",
		formUri = "http://www.legolas.org/crashreports/submit.php",
		formUriBasicAuthLogin = "por",
		formUriBasicAuthPassword = "wQx3z42n",
		socketTimeout = 5000)
public class PathfinderOpenReferenceApplication extends Application {
	@Override
	public void onCreate() {
		ACRA.init(this);
		super.onCreate();
	}
}
