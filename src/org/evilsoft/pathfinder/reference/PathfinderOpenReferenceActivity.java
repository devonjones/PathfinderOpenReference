package org.evilsoft.pathfinder.reference;

import java.io.IOException;

import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbHelper;
import org.evilsoft.pathfinder.reference.db.user.PsrdUserDbAdapter;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.SearchView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class PathfinderOpenReferenceActivity extends SherlockFragmentActivity {
	private PsrdDbAdapter dbAdapter;
	private PsrdUserDbAdapter userDbAdapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setScreenOrientation();
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
		setContentView(R.layout.main);
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
		switch (item.getItemId()) {
			case R.id.menu_ogl:
				Cursor curs = dbAdapter.fetchSectionByParentIdAndName("1", "OGL");
				curs.moveToFirst();
				String sectionId = curs.getString(0);
				Intent showContent = new Intent(getApplicationContext(), DetailsActivity.class);
				showContent.setData(Uri.parse("pfsrd://Ogl/" + sectionId));
				startActivity(showContent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
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