package org.evilsoft.pathfinder.reference;

import java.util.ArrayList;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

public class SectionViewFragment extends ListFragment implements OnItemClickListener {
	private static final String TAG = "SectionViewFragment";
	private PsrdDbAdapter dbAdapter;
	private String currentUrl;
	private BaseAdapter currentListAdapter;

	public void updateUrl(String newUrl) {
		Log.e(TAG, newUrl);
		this.getListView().setOnItemClickListener(this);
		this.getListView().setCacheColorHint(Color.WHITE);
		currentUrl = newUrl;
		String[] parts = newUrl.split("\\/");
		if (parts[2].equals("Classes")) {
			ClassAdapter ca = new ClassAdapter(dbAdapter);
			String id = parts[parts.length - 1];
			Cursor curs = ca.fetchClassList(id);
			currentListAdapter = new ClassListAdapter(getActivity().getApplicationContext(), curs);
		} else if (parts[2].equals("Feats")) {
			if (parts.length > 4) {
				FeatAdapter fa = new FeatAdapter(dbAdapter);
				String featType = parts[parts.length - 1];
				Cursor curs;
				if (featType.equals("All Feats")) {
					curs = fa.fetchFeatList();
				} else {
					curs = fa.fetchFeatList(featType);
				}
				currentListAdapter = new FeatListAdapter(getActivity().getApplicationContext(), curs, true);
			}
		} else if (parts[2].equals("Races")) {
			RaceAdapter ra = new RaceAdapter(dbAdapter);
			Cursor curs = ra.fetchRaceList();
			currentListAdapter = new RaceListAdapter(getActivity().getApplicationContext(), curs);
		} else if (parts[2].startsWith("Rules")) {
			if (parts.length > 4) {
				RuleAdapter ra = new RuleAdapter(dbAdapter);
				String ruleId = parts[parts.length - 1];
				Cursor curs = ra.fetchRuleList(ruleId);
				currentListAdapter = new RuleListAdapter(getActivity().getApplicationContext(), curs);
			}
		} else if (parts[2].equals("Skills")) {
			SkillAdapter sa = new SkillAdapter(dbAdapter);
			Cursor curs = sa.fetchSkillList();
			currentListAdapter = new SkillListAdapter(getActivity().getApplicationContext(), curs, true);
		} else if (parts[2].equals("Spells")) {
			SpellAdapter sa = new SpellAdapter(dbAdapter);
			String spellClass = parts[parts.length - 1];
			Cursor curs;
			if (spellClass.equals("All")) {
				curs = sa.fetchSpellList();
			} else {
				curs = sa.fetchSpellList(spellClass);
			}
			currentListAdapter = new SpellListAdapter(getActivity().getApplicationContext(), curs, true);
		} else if (parts[2].equals("Monsters")) {
			if (parts.length > 4) {
				MonsterAdapter ma = new MonsterAdapter(dbAdapter);
				String monsterId = parts[parts.length - 1];
				Cursor curs;
				if (monsterId.equals("All Monsters")) {
					monsterId = parts[parts.length - 2];
					curs = ma.fetchMonsterList(monsterId);
				} else {
					curs = ma.fetchMonstersByType(monsterId);
				}
				currentListAdapter = new MonsterListAdapter(getActivity().getApplicationContext(), curs, true);
			}
		} else {
			ArrayList<String> list = new ArrayList<String>();
			for (int i = 0; i < 6; i++) {
				list.add(newUrl);
			}
			currentListAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.list_item,
					list);
		}
		setListAdapter(currentListAdapter);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.top_titles,
				R.layout.list_item));
		dbAdapter = new PsrdDbAdapter(getActivity().getApplicationContext());
		dbAdapter.open();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (dbAdapter != null) {
			dbAdapter.close();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String uri = currentUrl + "/" + currentListAdapter.getItemId(position);
		Log.e(TAG, uri);
		Intent showContent = new Intent(getActivity().getApplicationContext(), DetailsActivity.class);
		showContent.setData(Uri.parse(uri));
		startActivity(showContent);
	}
}
