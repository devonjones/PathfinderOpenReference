package org.evilsoft.pathfinder.reference;

import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

public class DetailsActivity extends FragmentActivity {
	private PsrdDbAdapter dbAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbAdapter = new PsrdDbAdapter(this);
		dbAdapter.open();

		boolean useTitleFeature = false;
		if (getWindow().getContainer() == null) {
			useTitleFeature = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		}
		setContentView(R.layout.details);
		if (useTitleFeature) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
			AutoCompleteTextView searchAc = (AutoCompleteTextView) findViewById(R.id.searchAc);
			ImageButton searchButton = (ImageButton) findViewById(R.id.imageSearch);
			new AutoCompleteHandler(this.getApplicationContext(), this, dbAdapter, searchAc, searchButton);
		}
		Intent launchingIntent = getIntent();
		String newUri = launchingIntent.getData().toString();
		DetailsViewFragment viewer = (DetailsViewFragment) getSupportFragmentManager().findFragmentById(
				R.id.details_view_fragment);
		viewer.updateUrl(newUri);
		DetailsListFragment list = (DetailsListFragment) getSupportFragmentManager().findFragmentById(
				R.id.details_list_fragment);
		String uri = buildDetailsListUri(newUri);
		list.updateUrl(uri);
	}

	public static String buildDetailsListUri(String uri) {
		String[] parts = uri.split("\\/");
		if(parts[2].equals("Search")) {
			return uri;
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < parts.length - 1; i++) {
			if (i != 0) {
				sb.append("/");
			}
			sb.append(parts[i]);
		}
		return sb.toString();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (dbAdapter != null) {
			dbAdapter.close();
		}
	}
}