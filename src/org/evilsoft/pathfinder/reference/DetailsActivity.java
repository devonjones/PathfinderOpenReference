package org.evilsoft.pathfinder.reference;

import org.evilsoft.pathfinder.reference.db.psrd.CharacterAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;
import org.evilsoft.pathfinder.reference.db.user.PsrdUserDbAdapter;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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
	private PsrdDbAdapter dbAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbAdapter = new PsrdDbAdapter(this);
		dbAdapter.open();

		setContentView(R.layout.details);

		Intent launchingIntent = getIntent();
		final DetailsViewFragment viewer = (DetailsViewFragment) getSupportFragmentManager()
				.findFragmentById(R.id.details_view_fragment);
		DetailsListFragment list = (DetailsListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.details_list_fragment);
		String newUri;
		if (Intent.ACTION_SEARCH.equals(launchingIntent.getAction())) {
			String query = launchingIntent.getStringExtra(SearchManager.QUERY);
			int count = dbAdapter.countSearchArticles(query);
			if (count == 1) {
				newUri = buildSearchUrl(query);
			} else {
				newUri = "pfsrd://Search/" + query;
			}
		} else {
			newUri = launchingIntent.getData().toString();
		}
		viewer.updateUrl(newUri);
		String uri = buildDetailsListUri(newUri);
		list.updateUrl(uri);

		// Set up action bar
		ActionBar action = this.getSupportActionBar();
		action.setDisplayHomeAsUpEnabled(true);
		action.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		CharacterAdapter ca = new CharacterAdapter(new PsrdUserDbAdapter(this));
		SimpleCursorAdapter sca = new SimpleCursorAdapter(
				this,
				android.R.layout.simple_spinner_dropdown_item,
				ca.fetchCharacterList(), // this returns a cursor and won't be required automatically!
				new String[] { "name" },
				new int[] { android.R.id.text1 },
				0);
		action.setListNavigationCallbacks(sca, new ActionBar.OnNavigationListener() {
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				viewer.setCharacter(itemId);
				return true;
			}
		});

		ca.closeDb();

		String characterId = launchingIntent.getStringExtra("currentCharacter");
		if (characterId != null) {
			action.setSelectedNavigationItem(Integer.parseInt(characterId) - 1);
		}
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (dbAdapter != null) {
			dbAdapter.close();
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