package org.evilsoft.pathfinder.reference;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(formKey = "dHdZVlYzOGRMaDRQajNReXR5QzBiV1E6MQ", socketTimeout = 5000)
public class PathfinderOpenReferenceApplication extends Application {
	@Override
	public void onCreate() {
		try {
			ACRA.init(this);
		} catch (Exception e) {
		}
		super.onCreate();
	}
}
