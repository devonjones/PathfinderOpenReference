package org.evilsoft.pathfinder.reference;

import java.io.IOException;
import java.io.InputStream;

import org.acra.ErrorReporter;
import org.evilsoft.pathfinder.reference.db.DbWrangler;
import org.evilsoft.pathfinder.reference.utils.LimitedSpaceException;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;

public class StartActivity extends Activity {
	private static final String TAG = "StartActivity";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.startup);
		try {
			ImageView i;
			Bitmap bm = getBitmapFromAsset("hq_icon.png");
			i = (ImageView) findViewById(R.id.appIcon);
			i.setImageBitmap(bm);
		} catch (IOException e) {
			Log.e(TAG, "Failed to load app image", e);
		}

		// The default values must be set here to bypass a bug in Android
		// See
		// http://stackoverflow.com/questions/3907830/android-checkboxpreference-default-value
		PreferenceManager.setDefaultValues(this, R.xml.source_filter, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		new SetupDBTask().execute();
	}

	private Bitmap getBitmapFromAsset(String strName) throws IOException {
		AssetManager assetManager = getAssets();
		InputStream istr = assetManager.open(strName);
		Bitmap bitmap = BitmapFactory.decodeStream(istr);
		return bitmap;
	}

	private class SetupDBTask extends
			AsyncTask<Object, Integer, AsyncTaskResult<Boolean>> {
		protected AsyncTaskResult<Boolean> doInBackground(Object... obj) {
			try {
				DbWrangler dbw = new DbWrangler(
						StartActivity.this.getApplicationContext());
				dbw.checkDatabases();
			} catch (IOException e) {
				throw new RuntimeException(e);
			} catch (LimitedSpaceException e) {
				return new AsyncTaskResult<Boolean>(e);
			}
			return new AsyncTaskResult<Boolean>(true);
		}

		protected void onPostExecute(AsyncTaskResult<Boolean> result) {
			if (result.getResult()) {
				Intent showContent = new Intent(
						StartActivity.this.getApplicationContext(),
						PathfinderOpenReferenceActivity.class);
				StartActivity.this.startActivity(showContent);
			} else {
				DbWrangler.showLowSpaceError(StartActivity.this,
						(LimitedSpaceException) result.getError(),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								ErrorReporter e = ErrorReporter.getInstance();
								e.putCustomData("LastClick",
										"StartActivity.onCreate.onClick: Ok");
								StartActivity.this.finish();
							}
						});
			}
		}
	}
}
