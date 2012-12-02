package org.evilsoft.pathfinder.reference;

import org.evilsoft.pathfinder.reference.db.DbWrangler;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SearchView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class SectionViewActivity extends SherlockFragmentActivity {
	private DbWrangler dbWrangler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		openDb();

		// Set up action bar
		ActionBar action = this.getSupportActionBar();
		action.setDisplayHomeAsUpEnabled(true);
		action.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		setContentView(R.layout.section_view);

		SectionViewFragment viewer = (SectionViewFragment) getSupportFragmentManager()
				.findFragmentById(
						R.id.section_view_fragment);
		Intent launchingIntent = getIntent();
		String url = launchingIntent.getData().toString();
		viewer.updateUrl(url);
	}

	

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		return super.onCreateView(name, context, attrs);
	}

	@TargetApi(11) @Override
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
		Intent showContent;
		switch (item.getItemId()) {
			case R.id.menu_ogl:
				showContent = new Intent(getApplicationContext(),
						DetailsActivity.class);
				showContent.setData(Uri.parse("pfsrd://Open Game License/OGL"));
				startActivity(showContent);
				return true;
			case R.id.menu_cul:
				showContent = new Intent(getApplicationContext(),
						DetailsActivity.class);
				showContent.setData(Uri.parse("pfsrd://Open Game License/Community Use License"));
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
		if (dbWrangler != null) {
			dbWrangler.close();
		}
	}
}
