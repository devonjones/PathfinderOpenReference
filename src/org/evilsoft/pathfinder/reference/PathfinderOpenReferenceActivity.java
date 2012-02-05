package org.evilsoft.pathfinder.reference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.psrd.ClassAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.FeatAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.MonsterAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbHelper;
import org.evilsoft.pathfinder.reference.db.psrd.RuleAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.SpellAdapter;
import org.evilsoft.pathfinder.reference.db.user.PsrdUserDbHelper;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ExpandableListView;

public class PathfinderOpenReferenceActivity extends FragmentActivity implements
		ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupClickListener {
	private static final String TAG = "PathfinderOpenReferenceActivity";
	private PsrdDbAdapter dbAdapter;
	private PsrdUserDbHelper userDbAdapter;
	private List<HashMap<String, Object>> subjects;
	private List<ArrayList<HashMap<String, Object>>> children;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PsrdDbHelper dbh = new PsrdDbHelper(this.getApplicationContext());
		try {
			dbh.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		dbAdapter = new PsrdDbAdapter(this);
		dbAdapter.open();
		userDbAdapter = new PsrdUserDbHelper(this.getApplicationContext());
		SQLiteDatabase usld = userDbAdapter.getWritableDatabase();
		usld.close();

		boolean useTitleFeature = false;
		if (getWindow().getContainer() == null) {
			useTitleFeature = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		}
		setContentView(R.layout.main);
		if (useTitleFeature) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
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
			child = new HashMap<String, Object>();
			child.put("sectionName", name);
			child.put("id", section_id);
			result.add(child);
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
			} else {
				result.add(new ArrayList<HashMap<String, Object>>());
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
	}
}