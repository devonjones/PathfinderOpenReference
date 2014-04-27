package org.evilsoft.pathfinder.reference;

import java.util.ArrayList;
import java.util.List;

import org.acra.ErrorReporter;
import org.evilsoft.pathfinder.reference.db.DbWrangler;
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
	protected HistoryManager historyManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		openDb();

		Intent launchingIntent = getIntent();

		String newUrl;
		boolean showList = false; // Phone only
		if (Intent.ACTION_SEARCH.equals(launchingIntent.getAction())) {
			String query = launchingIntent.getStringExtra(SearchManager.QUERY);
			query = query.trim();
			int count = dbWrangler.getIndexDbAdapter().getSearchAdapter()
					.countSearchArticles(query);
			if (count == 1) {
				newUrl = buildSearchUrl(query);
			} else {
				newUrl = "pfsrd://Search/" + query;
				showList = true;
			}
		} else {
			newUrl = UrlAliaser.aliasUrl(dbWrangler, launchingIntent.getData()
					.toString());
		}

		// Set up action bar
		ActionBar action = this.getSupportActionBar();
		action.setDisplayHomeAsUpEnabled(true);
		action.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		List<Integer> collectionList = new ArrayList<Integer>();
		String contextUrl = launchingIntent.getStringExtra("context");
		if (PathfinderOpenReferenceActivity.isTabletLayout(this)) {
			setContentView(R.layout.details);
			if (showList) {
				DetailsListFragment list = (DetailsListFragment) getSupportFragmentManager()
						.findFragmentById(R.id.details_list_fragment);
				list.updateUrl(newUrl);
			} else {
				collectionList = setUpViewer(newUrl, contextUrl, action);
			}
		} else {
			if (showList) {
				setContentView(R.layout.details_phone_list);
				DetailsListFragment list = (DetailsListFragment) getSupportFragmentManager()
						.findFragmentById(R.id.details_list_fragment);
				list.updateUrl(newUrl);
			} else {
				setContentView(R.layout.details_phone);
				collectionList = setUpViewer(newUrl, contextUrl, action);
			}
		}

		Integer collectionId = getCurrentCollection(collectionList);
		if (collectionId != null) {
			action.setSelectedNavigationItem(collectionId);
		}
		historyManager = new HistoryManager(this, dbWrangler, cursorList);
		historyManager.setupDrawer();
	}

	private Integer getCurrentCollection(List<Integer> collectionList) {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		CollectionAdapter ca = new CollectionAdapter(
				dbWrangler.getUserDbAdapter());
		Integer defaultColId = ca.fetchFirstCollectionId();
		if (defaultColId != null) {
			Integer collectionId = settings
					.getInt("collectionId", defaultColId);
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

	private List<Integer> setUpViewer(String newUri, String contextUrl,
			ActionBar action) {
		final DetailsViewFragment viewer = (DetailsViewFragment) getSupportFragmentManager()
				.findFragmentById(R.id.details_view_fragment);

		viewer.updateUrl(newUri, contextUrl);
		CollectionAdapter ca = new CollectionAdapter(
				dbWrangler.getUserDbAdapter());
		Cursor curs = ca.fetchCollectionList();
		cursorList.add(curs);
		ArrayList<Integer> retList = new ArrayList<Integer>();
		boolean has_next = curs.moveToFirst();
		while (has_next) {
			retList.add(curs.getInt(0));
			has_next = curs.moveToNext();
		}
		curs.moveToFirst();
		SimpleCursorAdapter sca = new SimpleCursorAdapter(this,
				R.layout.actionbar_spinner, curs, new String[] { "name" },
				new int[] { android.R.id.text1 }, 0);
		action.setListNavigationCallbacks(sca,
				new ActionBar.OnNavigationListener() {
					public boolean onNavigationItemSelected(int itemPosition,
							long itemId) {
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
						editor.putInt("collectionId",
								((Long) itemId).intValue());
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
			} else {
				searchView.setIconifiedByDefault(true);
			}
			SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
			searchView.setSearchableInfo(searchManager
					.getSearchableInfo(getComponentName()));
		}
		return true;
	}

	public String buildSearchUrl(String searchText) {
		Cursor cursor = dbWrangler.getIndexDbAdapter().getSearchAdapter()
				.getSingleSearchArticle(searchText.trim());
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
	protected void onResume() {
		super.onResume();
		if (historyManager != null) {
			historyManager.refreshDrawer();
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
		switch (item.getItemId()) {
		case R.id.menu_ogl:
			Intent showContent = new Intent(getApplicationContext(),
					DetailsActivity.class);
			showContent.setData(Uri.parse("pfsrd://Open Game License/OGL"));
			startActivity(showContent);
			return true;
		case R.id.menu_cul:
			showContent = new Intent(getApplicationContext(),
					DetailsActivity.class);
			showContent.setData(Uri
					.parse("pfsrd://Open Game License/Community Use License"));
			startActivity(showContent);
			return true;
		case R.id.menu_toggle_toc:
			DetailsViewFragment viewer = (DetailsViewFragment) getSupportFragmentManager()
					.findFragmentById(R.id.details_view_fragment);
			WebView view = viewer.getWebView();
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			boolean showToc = settings.getBoolean("showToc", true);
			if (showToc) {
				editor.putBoolean("showToc", false);
				view.loadUrl("javascript:window.psrd_toc.hide()");
			} else {
				editor.putBoolean("showToc", true);
				if (PathfinderOpenReferenceActivity.isTabletLayout(this)) {
					view.loadUrl("javascript:window.psrd_toc.side()");
				} else {
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
		case R.id.menu_community:
			startActivity(new Intent(
					Intent.ACTION_VIEW,
					Uri.parse("https://plus.google.com/u/0/communities/115367918448287661404")));
			return true;
		case R.id.menu_history:
			historyManager.openDrawer();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}