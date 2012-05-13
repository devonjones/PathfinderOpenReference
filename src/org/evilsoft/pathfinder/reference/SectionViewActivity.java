package org.evilsoft.pathfinder.reference;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class SectionViewActivity extends SherlockFragmentActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setScreenOrientation();
		setContentView(R.layout.section_view);

		Intent launchingIntent = getIntent();
		String content = launchingIntent.getData().toString();

		SectionViewFragment viewer = (SectionViewFragment) getSupportFragmentManager().findFragmentById(
				R.id.section_view_fragment);
		viewer.updateUrl(content);
	}

	private void setScreenOrientation() {
		if (Build.VERSION.SDK_INT >= 11) {
			int smallest = getResources().getConfiguration().screenWidthDp;
			if (getResources().getConfiguration().screenHeightDp < smallest) {
				smallest = getResources().getConfiguration().screenHeightDp;
			}
			if (smallest >= 750) {
				if ((getResources().getConfiguration().screenLayout
						& Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
					return;
				}
			}
		}
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}
}
