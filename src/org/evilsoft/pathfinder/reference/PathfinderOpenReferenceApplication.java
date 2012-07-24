package org.evilsoft.pathfinder.reference;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(formKey = "dGNUN1FrMjdBSm02bUVrX1JYVzVMamc6MQ", socketTimeout = 5000)
public class PathfinderOpenReferenceApplication extends Application {
	@Override
	public void onCreate() {
		ACRA.init(this);
		super.onCreate();
	}
}
