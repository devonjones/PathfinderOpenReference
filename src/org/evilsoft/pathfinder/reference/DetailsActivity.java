package org.evilsoft.pathfinder.reference;

import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

public class DetailsActivity extends FragmentActivity {
	private PsrdDbAdapter dbAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbAdapter = new PsrdDbAdapter(this);
		dbAdapter.open();

		ActionBar action = this.getActionBar();
		action.setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.details);

		Intent launchingIntent = getIntent();
		DetailsViewFragment viewer = (DetailsViewFragment) getSupportFragmentManager().findFragmentById(
				R.id.details_view_fragment);
		DetailsListFragment list = (DetailsListFragment) getSupportFragmentManager().findFragmentById(
				R.id.details_list_fragment);
		String newUri;
		if (Intent.ACTION_SEARCH.equals(launchingIntent.getAction())) {
			String query = launchingIntent.getStringExtra(SearchManager.QUERY);
			int count = dbAdapter.countSearchArticles(query);
			if(count == 1) {
				newUri = buildSearchUrl(query);
			}
			else {
				newUri = "pfsrd://Search/" + query;
			}
		}
		else {
			newUri = launchingIntent.getData().toString();
		}
		viewer.updateUrl(newUri);
		String uri = buildDetailsListUri(newUri);
		list.updateUrl(uri);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		MenuItem searchItem = menu.findItem(R.id.menu_search);
		SearchView searchView = (SearchView) searchItem.getActionView();
		searchView.setIconifiedByDefault(false);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
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
		if(type.equals("feat")) {
			sb.append("Feats/0/All Feats/");
			sb.append(sectionId);
		}
		else if(type.equals("skill")) {
			sb.append("Skills/");
			sb.append(sectionId);
		}
		else if(type.equals("class")) {
			sb.append("Classes/");
			sb.append(sectionId);
		}
		else if(type.equals("creature")) {
			sb.append("Monsters/0/All Monsters/");
			sb.append(sectionId);
		}
		else if(type.equals("race")) {
			sb.append("Races/");
			sb.append(sectionId);
		}
		else if(type.equals("spell")) {
			sb.append("Spells/0/All/");
			sb.append(sectionId);
		}
		else {
			sb.append("Rules/");
			sb.append(parentId);
			sb.append("/");
			sb.append(sectionId);
		}
		return sb.toString();
	}



	public static String buildDetailsListUri(String uri) {
		String[] parts = uri.split("\\/");
		if(parts.length <= 3) {
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