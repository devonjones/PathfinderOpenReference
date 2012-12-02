package org.evilsoft.pathfinder.reference;


import org.evilsoft.pathfinder.reference.db.DbWrangler;
import org.evilsoft.pathfinder.reference.db.book.SectionAdapter;
import org.evilsoft.pathfinder.reference.preference.PathfinderPreferenceActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.SearchView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class PathfinderOpenReferenceActivity extends SherlockFragmentActivity {
	private static final String TAG = "PathfinderOpenReferenceActivity";
	private DbWrangler dbWrangler;

	public static boolean isTabletLayout(Activity act) {
		int smallest;
		try {
			if (Build.VERSION.SDK_INT >= 13) {
				smallest = getSmallestDimension(act);
			}
			else {
				smallest = getSmallestDimensionDeprecated(act);
			}

			if ((act.getResources().getConfiguration().screenLayout
			& Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
				if (smallest >= 750) {
					return true;
				}
				if (act.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
					return true;
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "isTabletLayout failed with exception", e);
		} catch (NoSuchFieldError nsfe) {
			Log.e(TAG, "isTabletLayout failed with exception", nsfe);
		}
		return false;
	}

	@SuppressLint("NewApi")
	public static int getSmallestDimension(Activity act) {
		int smallest = act.getResources().getConfiguration().screenWidthDp;
		if (act.getResources().getConfiguration().screenHeightDp < smallest) {
			smallest = act.getResources().getConfiguration().screenHeightDp;
		}
		return smallest;
	}

	@SuppressWarnings("deprecation")
	public static int getSmallestDimensionDeprecated(Activity act) {
		Display display = ((WindowManager) act
				.getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		int smallest = display.getWidth();
		if (display.getHeight() < smallest) {
			smallest = display.getHeight();
		}
		return smallest;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		openDb();
		if (isTabletLayout(this)) {
			setContentView(R.layout.main);
		} else {
			setContentView(R.layout.main_phone);
		}
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = this.getSupportMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		MenuItem searchItem = menu.findItem(R.id.menu_search);
		searchItem.setVisible(true);
		if (Build.VERSION.SDK_INT >= 11) {
			SearchView searchView = (SearchView) searchItem.getActionView();
			if (PathfinderOpenReferenceActivity.isTabletLayout(this)) {
				searchView.setIconifiedByDefault(false);
			}
			else {
				searchView.setIconifiedByDefault(true);
			}
			SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
			searchView.setSearchableInfo(searchManager
					.getSearchableInfo(getComponentName()));
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Cursor curs = null;
		Integer sectionId;
		Intent showContent;
		SectionAdapter sa = dbWrangler.getBookDbAdapter("book-ogl.db").getSectionAdapter();
		try {
			switch (item.getItemId()) {
				case R.id.menu_ogl:
					curs = sa.fetchSectionByParentIdAndName("1", "OGL");
					curs.moveToFirst();
					sectionId = SectionAdapter.SectionUtils.getSectionId(curs);
					showContent = new Intent(getApplicationContext(),
							DetailsActivity.class);
					showContent.setData(Uri.parse("pfsrd://Ogl/" + sectionId));
					startActivity(showContent);
					return true;
				case R.id.menu_cul:
					curs = sa.fetchSectionByParentIdAndName("1",
							"Community Use License");
					curs.moveToFirst();
					sectionId = SectionAdapter.SectionUtils.getSectionId(curs);
					showContent = new Intent(getApplicationContext(),
							DetailsActivity.class);
					showContent.setData(Uri.parse("pfsrd://Ogl/" + sectionId));
					startActivity(showContent);
					return true;
				case R.id.menu_search:
					this.onSearchRequested();
					return true;
				case R.id.menu_prefs:
					showContent = new Intent(getApplicationContext(),
							PathfinderPreferenceActivity.class);
					startActivity(showContent);
					return true;
				default:
					return super.onOptionsItemSelected(item);
			}
		} finally {
			if (curs != null) {
				curs.close();
			}
		}
	}

	private void openDb() {
		if (dbWrangler == null) {
			dbWrangler = new DbWrangler(this.getApplicationContext());
		}
		if (dbWrangler.isClosed()) {
			dbWrangler.open();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (dbWrangler != null) {
			dbWrangler.close();
		}
	}
}