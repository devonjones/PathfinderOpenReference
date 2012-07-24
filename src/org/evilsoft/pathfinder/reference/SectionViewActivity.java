package org.evilsoft.pathfinder.reference;

import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.SearchView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class SectionViewActivity extends SherlockFragmentActivity {
	private PsrdDbAdapter dbAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		openDb();
		setContentView(R.layout.section_view);

		Intent launchingIntent = getIntent();
		String content = launchingIntent.getData().toString();

		// Set up action bar
		ActionBar action = this.getSupportActionBar();
		action.setDisplayHomeAsUpEnabled(true);
		action.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		SectionViewFragment viewer = (SectionViewFragment) getSupportFragmentManager()
				.findFragmentById(
						R.id.section_view_fragment);
		viewer.updateUrl(content);
	}

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
		String sectionId;
		Intent showContent;
		try {
			switch (item.getItemId()) {
				case R.id.menu_ogl:
					curs = dbAdapter.fetchSectionByParentIdAndName("1", "OGL");
					curs.moveToFirst();
					sectionId = curs.getString(0);
					showContent = new Intent(getApplicationContext(),
							DetailsActivity.class);
					showContent.setData(Uri.parse("pfsrd://Ogl/" + sectionId));
					startActivity(showContent);
					return true;
				case R.id.menu_cul:
					curs = dbAdapter.fetchSectionByParentIdAndName("1",
							"Community Use License");
					curs.moveToFirst();
					sectionId = curs.getString(0);
					showContent = new Intent(getApplicationContext(),
							DetailsActivity.class);
					showContent.setData(Uri.parse("pfsrd://Ogl/" + sectionId));
					startActivity(showContent);
					return true;
				case R.id.menu_search:
					this.onSearchRequested();
					return true;
				case android.R.id.home:
					// app icon in action bar clicked; go home
					Intent intent = new Intent(this,
							PathfinderOpenReferenceActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
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
	}
}
