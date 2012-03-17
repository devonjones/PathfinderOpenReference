package org.evilsoft.pathfinder.reference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.psrd.CharacterAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.ClassAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.FeatAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.MonsterAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbHelper;
import org.evilsoft.pathfinder.reference.db.psrd.RuleAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.SpellAdapter;
import org.evilsoft.pathfinder.reference.db.user.PsrdUserDbAdapter;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SearchView;

public class PathfinderOpenReferenceActivity extends FragmentActivity implements
		ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupClickListener
		{
	private static final String TAG = "PathfinderOpenReferenceActivity";
	private PsrdDbAdapter dbAdapter;
	private PsrdUserDbAdapter userDbAdapter;
	private List<HashMap<String, Object>> subjects;
	private List<ArrayList<HashMap<String, Object>>> children;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userDbAdapter = new PsrdUserDbAdapter(getApplicationContext());
		userDbAdapter.open();
		PsrdDbHelper dbh = new PsrdDbHelper(getApplicationContext());
		try {
			dbh.createDataBase(userDbAdapter);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		dbAdapter = new PsrdDbAdapter(this);
		dbAdapter.open();
		setContentView(R.layout.main);
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

	public List<HashMap<String, Object>> createGroupList() {
		ArrayList<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
		Cursor curs = dbAdapter.fetchSectionByParentId("1");
		HashMap<String, Object> child = new HashMap<String, Object>();
		child.put("sectionName", "Characters");
		child.put("id", "0");
		result.add(child);
		boolean has_next = curs.moveToFirst();
		while (has_next) {
			String section_id = curs.getString(0);
			String name = curs.getString(1);
			String type = curs.getString(2);
			if(type.equals("list")) {
				child = new HashMap<String, Object>();
				child.put("sectionName", name);
				child.put("id", section_id);
				result.add(child);
			}
			has_next = curs.moveToNext();
		}
		this.subjects = result;
		return result;
	}

	public List<ArrayList<HashMap<String, Object>>> createChildList() {
		ArrayList<ArrayList<HashMap<String, Object>>> result = new ArrayList<ArrayList<HashMap<String, Object>>>();
		for (int i = 0; i < subjects.size(); ++i) {
			HashMap<String, Object> sub = subjects.get(i);
			Log.e(TAG, sub.get("sectionName").toString());
			String sectionName = sub.get("sectionName").toString();
			String id = sub.get("id").toString();
			if (sectionName.equals("Feats")) {
				FeatAdapter fa = new FeatAdapter(dbAdapter);
				result.add(fa.createFeatTypeList());
			} else if (sectionName.equals("Spells")) {
				SpellAdapter sa = new SpellAdapter(dbAdapter);
				result.add(sa.createSpellClassList());
			} else if (sectionName.startsWith("Rules")) {
				RuleAdapter ra = new RuleAdapter(dbAdapter);
				result.add(ra.createRuleList(id));
			} else if (sectionName.equals("Classes")) {
				ClassAdapter ca = new ClassAdapter(dbAdapter);
				result.add(ca.createClassTypeList());
			} else if (sectionName.equals("Monsters")) {
				MonsterAdapter ma = new MonsterAdapter(dbAdapter);
				result.add(ma.createMonsterTypeList());
			} else if (sectionName.equals("Characters")) {
				PsrdUserDbAdapter userDbAdapter = new PsrdUserDbAdapter(getApplicationContext());
				CharacterAdapter ca = new CharacterAdapter(userDbAdapter);
				ArrayList<HashMap<String, Object>> charList = ca.createCharacterList();

				HashMap<String, Object> adder = new HashMap<String, Object>();
				adder.put("id", 0);
				adder.put("specificName", getString(R.string.add_character));
				charList.add(adder);

				result.add(charList);
			} else {
			    
			}
		}
		this.children = result;
		return result;
	}

	@Override
	public boolean onChildClick(ExpandableListView arg0, View v, int groupPosition, int childPosition, long arg4) {
		HashMap<String, Object> sub = subjects.get(groupPosition);
		HashMap<String, Object> specific = children.get(groupPosition).get(childPosition);
		String uri = getUri(sub, specific);
		SectionViewFragment viewer = (SectionViewFragment) getSupportFragmentManager().findFragmentById(
				R.id.section_view_fragment);

		if (viewer == null || !viewer.isInLayout()) {
			Intent showContent = new Intent(getApplicationContext(), SectionViewActivity.class);

			showContent.setData(Uri.parse(uri));
			startActivity(showContent);
		} else {
			viewer.updateUrl(uri);
		}
		return false;
	}

	private String getUri(HashMap<String, Object> subject, HashMap<String, Object> specific) {
		StringBuffer sb = new StringBuffer();
		sb.append("pfsrd://");
		sb.append(addSectionName((String) subject.get("sectionName")));
		sb.append("/");
		sb.append(subject.get("id"));
		sb.append("/");
		sb.append(specific.get("specificName"));
		if (addId(subject.get("sectionName").toString())) {
			sb.append("/");
			sb.append(specific.get("id"));
		}
		return sb.toString();
	}

	private String addSectionName(String sectionName) {
		return sectionName;
	}

	private boolean addId(String sectionName) {
		if (sectionName.startsWith("Rules")) {
			return true;
		} else if (sectionName.endsWith("Classes")) {
			return true;
		}
		return false;
	}

	@Override
	public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
		HashMap<String, Object> sub = subjects.get(groupPosition);
		String uri = "pfsrd://" + sub.get("sectionName") + "/" + sub.get("id");
		SectionViewFragment viewer = (SectionViewFragment) getSupportFragmentManager().findFragmentById(
				R.id.section_view_fragment);

		if (viewer == null || !viewer.isInLayout()) {
			Intent showContent = new Intent(getApplicationContext(), SectionViewActivity.class);

			showContent.setData(Uri.parse(uri));
			startActivity(showContent);
		} else {
			viewer.updateUrl(uri);
		}
		return false;
	}

	public void onLinkSelected(String pfsUri) {
		SectionViewFragment viewer = (SectionViewFragment) getSupportFragmentManager().findFragmentById(
				R.id.section_view_fragment);

		if (viewer == null || !viewer.isInLayout()) {
			Intent showContent = new Intent(getApplicationContext(), SectionViewActivity.class);
			showContent.setData(Uri.parse(pfsUri));
			startActivity(showContent);
		} else {
			viewer.updateUrl(pfsUri);
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