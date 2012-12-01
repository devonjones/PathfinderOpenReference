package org.evilsoft.pathfinder.reference;

import java.util.ArrayList;
import java.util.List;

import org.acra.ErrorReporter;
import org.evilsoft.pathfinder.reference.db.DbWrangler;
import org.evilsoft.pathfinder.reference.db.book.SectionAdapter;
import org.evilsoft.pathfinder.reference.db.index.SearchAdapter;
import org.evilsoft.pathfinder.reference.db.user.CollectionAdapter;
import org.evilsoft.pathfinder.reference.utils.UrlAliaser;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.webkit.WebView;
import android.widget.SearchView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class DetailsActivity extends SherlockFragmentActivity {
	private DbWrangler dbWrangler;
	public static final String PREFS_NAME = "psrd.prefs";
	private List<Cursor> cursorList = new ArrayList<Cursor>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		openDb();

		Intent launchingIntent = getIntent();

		String newUri;
		boolean showList = false; // Phone only
		if (Intent.ACTION_SEARCH.equals(launchingIntent.getAction())) {
			String query = launchingIntent.getStringExtra(SearchManager.QUERY);
			int count = dbWrangler.getIndexDbAdapter().getSearchAdapter().countSearchArticles(query);
			if (count == 1) {
				newUri = buildSearchUrl(query);
			} else {
				newUri = "pfsrd://Search/" + query;
				showList = true;
			}
		} else {
			newUri = UrlAliaser.aliasUrl(dbWrangler, launchingIntent.getData().toString());
		}

		// Set up action bar
		ActionBar action = this.getSupportActionBar();
		action.setDisplayHomeAsUpEnabled(true);
		action.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		List<Integer> collectionList = new ArrayList<Integer>();
		if (PathfinderOpenReferenceActivity.isTabletLayout(this)) {
			setContentView(R.layout.details);
			if (showList) {
				DetailsListFragment list = (DetailsListFragment) getSupportFragmentManager()
						.findFragmentById(R.id.details_list_fragment);
				list.updateUrl(newUri);
			} else {
				collectionList = setUpViewer(newUri, action);
			}
		} else {
			if (showList) {
				setContentView(R.layout.details_phone_list);
				DetailsListFragment list = (DetailsListFragment) getSupportFragmentManager()
						.findFragmentById(R.id.details_list_fragment);
				list.updateUrl(newUri);
			} else {
				setContentView(R.layout.details_phone);
				collectionList = setUpViewer(newUri, action);
			}
		}

		Integer collectionId = getCurrentCollection(collectionList);
		if (collectionId != null) {
			action.setSelectedNavigationItem(collectionId);
		}
	}

	private Integer getCurrentCollection(List<Integer> collectionList) {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		CollectionAdapter ca = new CollectionAdapter(dbWrangler.getUserDbAdapter());
		Integer defaultColId = ca.fetchFirstCollectionId();
		if (defaultColId != null) {
			Integer collectionId = settings.getInt(
					"collectionId", defaultColId);
			if (!ca.collectionExists(collectionId.toString())) {
				collectionId = ca.fetchFirstCollectionId();
			}
			for (int i = 0; i < collectionList.size(); i++) {
				if (collectionId == collectionList.get(i)) {
					return i;
				}
			}
		}
		return null;
	}

	private List<Integer> setUpViewer(String newUri, ActionBar action) {
		final DetailsViewFragment viewer = (DetailsViewFragment) getSupportFragmentManager()
				.findFragmentById(R.id.details_view_fragment);

		viewer.updateUrl(newUri);
		CollectionAdapter ca = new CollectionAdapter(dbWrangler.getUserDbAdapter());
		Cursor curs = ca.fetchCollectionList();
		cursorList.add(curs);
		ArrayList<Integer> retList = new ArrayList<Integer>();
		boolean has_next = curs.moveToFirst();
		while (has_next) {
			retList.add(curs.getInt(0));
			has_next = curs.moveToNext();
		}
		curs.moveToFirst();
		SimpleCursorAdapter sca = new SimpleCursorAdapter(
				this,
				R.layout.actionbar_spinner,
				curs,
				new String[] { "name" },
				new int[] { android.R.id.text1 },
				0);
		action.setListNavigationCallbacks(sca,
				new ActionBar.OnNavigationListener() {
					public boolean onNavigationItemSelected(
							int itemPosition, long itemId) {
						StringBuffer sb = new StringBuffer();
						sb.append("DetailsActivity.setUpViewer.onNavigationItemSelected: itemPosition:");
						sb.append(itemPosition);
						sb.append(", itemId:");
						sb.append(itemId);
						ErrorReporter e = ErrorReporter.getInstance();
						e.putCustomData("LastClick", sb.toString());
						viewer.setCharacter(itemId);
						SharedPreferences settings = getSharedPreferences(
								PREFS_NAME, 0);
						SharedPreferences.Editor editor = settings.edit();
						editor.putInt(
								"collectionId", ((Long) itemId).intValue());
						editor.commit();
						return true;
					}
				});
		return retList;
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = this.getSupportMenuInflater();
		inflater.inflate(R.menu.display_menu, menu);
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
			SearchManager searchManager = (SearchManager) getSystemService(
					Context.SEARCH_SERVICE);
			searchView.setSearchableInfo(
					searchManager.getSearchableInfo(getComponentName()));
		}
		return true;
	}

	public String buildSearchUrl(String searchText) {
		Cursor cursor = dbWrangler.getIndexDbAdapter().getSearchAdapter().getSingleSearchArticle(searchText);
		try {
			cursor.moveToFirst();
			String url = SearchAdapter.SearchUtils.getUrl(cursor);
			return url;
		} finally {
			cursor.close();
		}
	}


	private void openDb() {
		if (dbWrangler == null) {
			dbWrangler = new DbWrangler(getApplicationContext());
		}
		if (dbWrangler.isClosed()) {
			dbWrangler.open();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		for (Cursor curs : cursorList) {
			if (!curs.isClosed()) {
				curs.close();
			}
		}
		if (dbWrangler != null) {
			dbWrangler.close();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Cursor curs = null;
		String sectionId;

		SectionAdapter sa = dbWrangler.getBookDbAdapter("book-ogl.db").getSectionAdapter();
		switch (item.getItemId()) {
			case R.id.menu_ogl:
				curs = sa.fetchSectionByParentIdAndName("1", "OGL");
				curs.moveToFirst();
				sectionId = curs.getString(0);
				Intent showContent = new Intent(getApplicationContext(),
						DetailsActivity.class);
				showContent.setData(Uri.parse("pfsrd://Ogl/" + sectionId));
				startActivity(showContent);
				return true;
			case R.id.menu_cul:
				curs = sa.fetchSectionByParentIdAndName("1",
						"Community Use License");
				curs.moveToFirst();
				sectionId = curs.getString(0);
				showContent = new Intent(getApplicationContext(),
						DetailsActivity.class);
				showContent.setData(Uri.parse("pfsrd://Ogl/" + sectionId));
				startActivity(showContent);
				return true;
			case R.id.menu_toggle_toc:
				DetailsViewFragment viewer = (DetailsViewFragment) getSupportFragmentManager()
					.findFragmentById(R.id.details_view_fragment);
				WebView view = viewer.getWebView();
				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit(); 
				boolean showToc = settings.getBoolean("showToc", true);
				if(showToc) {
					editor.putBoolean("showToc", false);
					view.loadUrl("javascript:window.psrd_toc.hide()");
				}
				else {
					editor.putBoolean("showToc", true);
					if(PathfinderOpenReferenceActivity.isTabletLayout(this)) {
						view.loadUrl("javascript:window.psrd_toc.side()");
					}
					else {
						view.loadUrl("javascript:window.psrd_toc.full()");
					}
				}
				editor.commit();
				return true;
			case android.R.id.home:
				// app icon in action bar clicked; go home
				Intent intent = new Intent(this,
						PathfinderOpenReferenceActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return true;
			case R.id.menu_search:
				this.onSearchRequested();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}