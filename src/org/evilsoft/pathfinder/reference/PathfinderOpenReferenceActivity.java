package org.evilsoft.pathfinder.reference;

import java.io.IOException;

import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbHelper;
import org.evilsoft.pathfinder.reference.db.user.PsrdUserDbAdapter;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.WindowManager;
import android.widget.SearchView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class PathfinderOpenReferenceActivity extends SherlockFragmentActivity {
	private PsrdDbAdapter dbAdapter;
	private PsrdUserDbAdapter userDbAdapter;

	public static boolean isTabletLayout(Activity act) {
		int smallest;
		if (Build.VERSION.SDK_INT >= 11) {
			smallest = act.getResources().getConfiguration().screenWidthDp;
			if (act.getResources().getConfiguration().screenHeightDp < smallest) {
				smallest = act.getResources().getConfiguration().screenHeightDp;
			}
		}
		else {
			Display display = ((WindowManager) act.getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
			smallest = display.getWidth();
			if (display.getHeight() < smallest) {
				smallest = display.getHeight();
			}
		}

		if ((act.getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
			if (smallest >= 750) {
				return true;
			}
			if(act.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				return true;
			}
		}
		return false;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setScreenOrientation();
		PsrdDbHelper dbh = new PsrdDbHelper(this.getApplicationContext());
		userDbAdapter = new PsrdUserDbAdapter(this.getApplicationContext());
		userDbAdapter.open();
		try {
			dbh.createDataBase(userDbAdapter);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		openDb();
		if(isTabletLayout(this)) {
			setContentView(R.layout.main);
			addDynamicFragment();
		} else {
			setContentView(R.layout.main_phone);
		}
	}

	public void addDynamicFragment() {
		Cursor curs = dbAdapter.fetchSectionByParentIdAndName("1", "OGL");
		curs.moveToFirst();
		String sectionId = curs.getString(0);
		curs.close();
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		DetailsViewFragment viewer = new DetailsViewFragment("pfsrd://Ogl/" + sectionId);
		ft.replace(R.id.section_view_layout, viewer, "viewer");
		ft.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = this.getSupportMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		if (Build.VERSION.SDK_INT >= 11) {
			MenuItem searchItem = menu.findItem(R.id.menu_search);
			searchItem.setVisible(true);
			SearchView searchView = (SearchView) searchItem.getActionView();
			searchView.setIconifiedByDefault(false);
			SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
			searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Cursor curs;
		String sectionId;
		Intent showContent;
		switch (item.getItemId()) {
			case R.id.menu_ogl:
				curs = dbAdapter.fetchSectionByParentIdAndName("1", "OGL");
				curs.moveToFirst();
				sectionId = curs.getString(0);
				showContent = new Intent(getApplicationContext(), DetailsActivity.class);
				showContent.setData(Uri.parse("pfsrd://Ogl/" + sectionId));
				startActivity(showContent);
				return true;
			case R.id.menu_cul:
				curs = dbAdapter.fetchSectionByParentIdAndName("1", "Community Use License");
				curs.moveToFirst();
				sectionId = curs.getString(0);
				showContent = new Intent(getApplicationContext(), DetailsActivity.class);
				showContent.setData(Uri.parse("pfsrd://Ogl/" + sectionId));
				startActivity(showContent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void openDb() {
		if (userDbAdapter == null) {
			userDbAdapter = new PsrdUserDbAdapter(this.getApplicationContext());
		}
		if (userDbAdapter.isClosed()) {
			userDbAdapter.open();
		}
		if (dbAdapter == null) {
			dbAdapter = new PsrdDbAdapter(this.getApplicationContext());
		}
		if (dbAdapter.isClosed()) {
			dbAdapter.open();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (dbAdapter != null) {
			dbAdapter.close();
		}
		if (userDbAdapter != null) {
			userDbAdapter.close();
		}
	}
}