package org.evilsoft.pathfinder.reference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;
import org.evilsoft.pathfinder.reference.db.user.CollectionAdapter;
import org.evilsoft.pathfinder.reference.db.user.PsrdUserDbAdapter;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.SearchView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class DetailsActivity extends SherlockFragmentActivity {
	public static final String PREFS_NAME = "psrd.prefs";
	private PsrdDbAdapter dbAdapter;
	private PsrdUserDbAdapter userDbAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		openDb();

		Intent launchingIntent = getIntent();

		String newUri;
		boolean showList = false; //Phone only
		if (Intent.ACTION_SEARCH.equals(launchingIntent.getAction())) {
			String query = launchingIntent.getStringExtra(SearchManager.QUERY);
			int count = dbAdapter.countSearchArticles(query);
			if (count == 1) {
				newUri = buildSearchUrl(query);
			} else {
				newUri = "pfsrd://Search/" + query;
				showList = true;
			}
		} else {
			newUri = launchingIntent.getData().toString();
		}

		// Set up action bar
		ActionBar action = this.getSupportActionBar();
		action.setDisplayHomeAsUpEnabled(true);
		action.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		List<Integer> collectionList = new ArrayList<Integer>();
		if (PathfinderOpenReferenceActivity.isTabletLayout(this)) {
			setContentView(R.layout.details);
			setUpList(newUri);
			collectionList = setUpViewer(newUri, action);
		} else {
			if(showList) {
				setContentView(R.layout.details_phone_list);
				setUpList(newUri);
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
		CollectionAdapter ca = new CollectionAdapter(userDbAdapter);
		Integer collectionId = settings.getInt("collectionId", ca.fetchFirstCollectionId());
		if(!ca.collectionExists(collectionId.toString())) {
			collectionId = ca.fetchFirstCollectionId();
		}
		for(int i = 0; i < collectionList.size(); i++) {
			if(collectionId == collectionList.get(i)) {
				return i;
			}
		}
		return null;
	}

	private void setUpList(String newUri) {
		DetailsListFragment list = (DetailsListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.details_list_fragment);
		String uri = buildDetailsListUri(newUri);
		list.updateUrl(uri);
	}

	private List<Integer> setUpViewer(String newUri, ActionBar action) {
		final DetailsViewFragment viewer = (DetailsViewFragment) getSupportFragmentManager()
				.findFragmentById(R.id.details_view_fragment);
		viewer.updateUrl(newUri);
		CollectionAdapter ca = new CollectionAdapter(userDbAdapter);
		Cursor curs = ca.fetchCollectionList();
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
				curs, // this returns a cursor and won't be required automatically!
				new String[] { "name" },
				new int[] { android.R.id.text1 },
				0);
		action.setListNavigationCallbacks(sca, new ActionBar.OnNavigationListener() {
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				viewer.setCharacter(itemId);
				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putInt("collectionId", ((Long)itemId).intValue());
				editor.commit();
				return true;
			}
		});
		return retList;
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
			if(PathfinderOpenReferenceActivity.isTabletLayout(this)) {
				searchView.setIconifiedByDefault(false);
			}
			else {
				searchView.setIconifiedByDefault(true);
			}
			SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
			searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		}
		return true;
	}

	public String buildSearchUrl(String searchText) {
		Cursor c = dbAdapter.getSingleSearchArticle(searchText);
		c.moveToFirst();
		String sectionId = c.getString(0);
		String type = c.getString(1);
		String parentId = c.getString(5);
		StringBuffer sb = new StringBuffer();
		sb.append("pfsrd://");
		if (type.equals("feat")) {
			sb.append("Feats/0/All Feats/");
			sb.append(sectionId);
		} else if (type.equals("skill")) {
			sb.append("Skills/");
			sb.append(sectionId);
		} else if (type.equals("class")) {
			sb.append("Classes/");
			sb.append(sectionId);
		} else if (type.equals("creature")) {
			sb.append("Monsters/0/All Monsters/");
			sb.append(sectionId);
		} else if (type.equals("race")) {
			sb.append("Races/");
			sb.append(sectionId);
		} else if (type.equals("spell")) {
			sb.append("Spells/0/All/");
			sb.append(sectionId);
		} else {
			sb.append("Rules/");
			sb.append(parentId);
			sb.append("/");
			sb.append(sectionId);
		}
		return sb.toString();
	}

	public static String buildDetailsListUri(String uri) {
		String[] parts = uri.split("\\/");
		if (parts[2].equals("Search")) {
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_ogl:
				Intent showContent = new Intent(getApplicationContext(), DetailsActivity.class);
				showContent.setData(Uri.parse("pfsrd://Ogl"));
				startActivity(showContent);
				return true;
			case android.R.id.home:
				// app icon in action bar clicked; go home
				Intent intent = new Intent(this, PathfinderOpenReferenceActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}