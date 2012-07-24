package org.evilsoft.pathfinder.reference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.acra.ErrorReporter;
import org.evilsoft.pathfinder.reference.db.psrd.ClassAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.FeatAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.MonsterAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.RaceAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.RuleAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.SkillAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.SpellAdapter;
import org.evilsoft.pathfinder.reference.list.ClassListAdapter;
import org.evilsoft.pathfinder.reference.list.FeatListAdapter;
import org.evilsoft.pathfinder.reference.list.MonsterListAdapter;
import org.evilsoft.pathfinder.reference.list.RaceListAdapter;
import org.evilsoft.pathfinder.reference.list.RuleListAdapter;
import org.evilsoft.pathfinder.reference.list.SearchListAdapter;
import org.evilsoft.pathfinder.reference.list.SkillListAdapter;
import org.evilsoft.pathfinder.reference.list.SpellListAdapter;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import com.actionbarsherlock.app.SherlockListFragment;

public class DetailsListFragment extends SherlockListFragment implements OnItemClickListener {
	private static final String TAG = "DetailsListFragment";
	private PsrdDbAdapter dbAdapter;
	private List<Cursor> cursorList = new ArrayList<Cursor>();
	private String currentUrl;
	private String subtype;
	private BaseAdapter currentListAdapter;
	private boolean empty = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.top_titles,
				R.layout.list_item));
		openDb();
	}

	private void openDb() {
		if (dbAdapter == null) {
			dbAdapter = new PsrdDbAdapter(this.getActivity().getApplicationContext());
		}
		if (dbAdapter.isClosed()) {
			dbAdapter.open();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		for (Cursor curs : cursorList) {
			if(!curs.isClosed()) {
				curs.close();
			}
		}
		if (dbAdapter != null) {
			dbAdapter.close();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		StringBuffer sb = new StringBuffer();
		sb.append("DetailsListFragment.onItemClick: position:");
		sb.append(position);
		sb.append(", id:");
		sb.append(id);
		ErrorReporter e = ErrorReporter.getInstance();
		e.putCustomData("LastClick", sb.toString());
		if(empty) {
			return;
		}
		String uri = getNextUrl(id);
		Log.d(TAG, uri);
		if(PathfinderOpenReferenceActivity.isTabletLayout(getActivity())) {
			DetailsViewFragment viewer = (DetailsViewFragment) this.getActivity().getSupportFragmentManager()
				.findFragmentById(R.id.details_view_fragment);
			viewer.updateUrl(uri);
		} else {
			Intent showContent = new Intent(this.getActivity().getApplicationContext(), DetailsActivity.class);

			showContent.setData(Uri.parse(uri));
			startActivity(showContent);
		}
	}

	private String getNextUrl(Long id) {
		String uri = null;
		Cursor curs = dbAdapter.fetchSection(id.toString());
		try {
			boolean has_next = curs.moveToNext();
			if (has_next) {
				uri = curs.getString(3);
			}
		} finally {
			curs.close();
		}
		if (uri == null) {
			uri = currentUrl + "/" + id;
		}
		return uri;
	}

	public boolean checkUrlEqual(String newUrl) {
		if(currentUrl == null) {
			return false;
		}
		if(newUrl.indexOf("?") > -1) {
			newUrl = TextUtils.split(newUrl, "\\?")[0];
		}
		String localUrl = currentUrl;
		if(localUrl.indexOf("?") > -1) {
			localUrl = TextUtils.split(localUrl, "\\?")[0];
		}
		return newUrl.equals(localUrl);
	}
	public void updateUrl(String newUrl) {
		ErrorReporter e = ErrorReporter.getInstance();
		e.putCustomData("LastDetailsListUrl", newUrl);
		this.getListView().setOnItemClickListener(this);
		this.getListView().setCacheColorHint(Color.WHITE);
		if (newUrl == null || checkUrlEqual(newUrl)) {
			return;
		}
		currentUrl = newUrl;
		Uri uri = Uri.parse(currentUrl);
		subtype = uri.getQueryParameter("subtype");
		String localUrl = currentUrl;
		if(currentUrl.indexOf("?") > -1) {
			localUrl = TextUtils.split(currentUrl, "\\?")[0];
		}
		
		Cursor curs = dbAdapter.fetchSectionByUrl(localUrl);
		try {
			boolean has_item = curs.moveToFirst();
			String[] parts = newUrl.split("\\/");
			if (has_item) {
				String name = curs.getString(2);
				String type = curs.getString(3);
				getListAdapter(name, type, subtype);
			}
			else if (parts[2].equals("Search")) {
				if (parts.length == 4) {
					Cursor searchcurs = dbAdapter.search(parts[3]);
					cursorList.add(searchcurs);
					currentListAdapter = new SearchListAdapter(getActivity().getApplicationContext(), searchcurs);
					if (currentListAdapter.isEmpty()) {
						empty = true;
						ArrayList<String> list = new ArrayList<String>();
						list.add("(No Results)");
						currentListAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.list_item,
							list);
					}
				}
			} else {
				empty = false;
			}
			setListAdapter(currentListAdapter);
		} finally {
			curs.close();
		}
	}

	public void getListAdapter(String name, String type, String subtype) {
		String localUrl = currentUrl;
		if(currentUrl.indexOf("?") > -1) {
			localUrl = TextUtils.split(currentUrl, "\\?")[0];
		}
		ArrayList<HashMap<String, String>> path = dbAdapter.getPathByUrl(localUrl);
		if (path != null && path.size() > 1 && path.get(path.size() - 2).get("name").startsWith("Rules")) {
			RuleAdapter ra = new RuleAdapter(dbAdapter);
			Cursor curs = ra.fetchRuleListByUrl(localUrl);
			cursorList.add(curs);
			currentListAdapter = new RuleListAdapter(getActivity()
					.getApplicationContext(), curs);
		} else if (name.equals("Classes")) {
			ClassAdapter ca = new ClassAdapter(dbAdapter);
			Cursor curs = ca.fetchClassList(subtype);
			cursorList.add(curs);
			currentListAdapter = new ClassListAdapter(getActivity().getApplicationContext(), curs);
		} else if (name.equals("Feats")) {
			FeatAdapter fa = new FeatAdapter(dbAdapter);
			Cursor curs;
			if (subtype != null) {
				curs = fa.fetchFeatList(subtype);
			} else {
				curs = fa.fetchFeatList();
			}
			cursorList.add(curs);
			currentListAdapter = new FeatListAdapter(getActivity().getApplicationContext(), curs, false);
		} else if (name.equals("Monsters")) {
			MonsterAdapter ma = new MonsterAdapter(dbAdapter);
			Cursor curs;
			if (subtype != null) {
				curs = ma.fetchMonstersByType(subtype);
			} else {
				curs = ma.fetchMonsterList();
			}
			cursorList.add(curs);
			currentListAdapter = new MonsterListAdapter(getActivity().getApplicationContext(), curs, false);
		} else if (name.equals("Races")) {
			RaceAdapter ra = new RaceAdapter(dbAdapter);
			Cursor curs2 = ra.fetchRaceList();
			cursorList.add(curs2);
			currentListAdapter = new RaceListAdapter(getActivity().getApplicationContext(), curs2);
		} else if (name.equals("Skills")) {
			SkillAdapter sa = new SkillAdapter(dbAdapter);
			Cursor curs2 = sa.fetchSkillList();
			cursorList.add(curs2);
			currentListAdapter = new SkillListAdapter(getActivity().getApplicationContext(), curs2, false);
		} else if (name.equals("Spells")) {
			SpellAdapter sa = new SpellAdapter(dbAdapter);
			Cursor curs;
			if (subtype != null) {
				curs = sa.fetchSpellList(subtype);
			} else {
				curs = sa.fetchSpellList();
			}
			cursorList.add(curs);
			currentListAdapter = new SpellListAdapter(getActivity().getApplicationContext(), curs, false);
		}
	}

	/*public void updateUrl(String newUrl) {
		ErrorReporter e = ErrorReporter.getInstance();
		e.putCustomData("LastDetailsListUrl", newUrl);
		this.getListView().setOnItemClickListener(this);
		this.getListView().setCacheColorHint(Color.WHITE);
		if (currentUrl == newUrl) {
			return;
		}
		currentUrl = newUrl;
		String[] parts = newUrl.split("\\/");
		} else if (parts[2].startsWith("Rules")) {
			RuleAdapter ra = new RuleAdapter(dbAdapter);
			String ruleId = parts[parts.length - 1];
			Cursor curs = ra.fetchRuleList(ruleId);
			cursorList.add(curs);
			currentListAdapter = new RuleListAdapter(getActivity().getApplicationContext(), curs);
		} else if (parts[2].equals("Search")) {
			if (parts.length == 4) {
				Cursor curs = dbAdapter.search(parts[3]);
				cursorList.add(curs);
				currentListAdapter = new SearchListAdapter(getActivity().getApplicationContext(), curs);
				if (currentListAdapter.isEmpty()) {
					empty = true;
					ArrayList<String> list = new ArrayList<String>();
					list.add("(No Results)");
					currentListAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.list_item,
							list);
				}
			}
		}
		else {
			empty = false;
		}
		setListAdapter(currentListAdapter);
	}*/
}
