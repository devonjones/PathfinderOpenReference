package org.evilsoft.pathfinder.reference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.psrd.ClassAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.FeatAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.MonsterAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.RaceAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.RuleAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.SpellAdapter;
import org.evilsoft.pathfinder.reference.db.user.CollectionAdapter;
import org.evilsoft.pathfinder.reference.db.user.PsrdUserDbAdapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class SectionExpandableListAdapter extends BaseExpandableListAdapter {
	private static final String TAG = "SectionExpandableListAdapter";
	private List<HashMap<String, Object>> subjects;
	private List<List<HashMap<String, Object>>> children;
	private Context context;
	private LayoutInflater inflater;

	public SectionExpandableListAdapter(Context context) {
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void refresh(PsrdDbAdapter dbAdapter, PsrdUserDbAdapter userDbAdapter) {
		createGroupList(dbAdapter);
		createChildList(dbAdapter, userDbAdapter);
		notifyDataSetChanged();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return children.get(groupPosition).get(childPosition)
				.get("specificName");
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		try {
			return Long.parseLong((String) children.get(groupPosition)
					.get(childPosition).get("id"));
		} catch (NumberFormatException nfe) {
			return 0;
		}
	}

	public String getPfChildId(int groupPosition, int childPosition) {
		return (String) children.get(groupPosition).get(childPosition)
				.get("id");
	}

	public String getPfChildUrl(int groupPosition, int childPosition) {
		return (String) children.get(groupPosition).get(childPosition)
				.get("url");
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View v;
		if (convertView == null) {
			v = inflater.inflate(R.layout.child_row, parent, false);
		} else {
			v = convertView;
		}
		TextView name = (TextView) v.findViewById(R.id.childname);
		if (name != null) {
			name.setText((String) getChild(groupPosition, childPosition));
		}
		return v;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return children.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return subjects.get(groupPosition).get("sectionName");
	}

	@Override
	public int getGroupCount() {
		return subjects.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		try {
			return Long.parseLong((String) subjects.get(groupPosition)
					.get("id"));
		} catch (NumberFormatException nfe) {
			return 0;
		}
	}

	public String getPfGroupId(int groupPosition) {
		return (String) subjects.get(groupPosition).get("id");
	}

	public String getPfGroupUrl(int groupPosition) {
		return (String) subjects.get(groupPosition).get("url");
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View v;
		if (convertView == null) {
			v = inflater.inflate(R.layout.group_row, parent, false);
		} else {
			v = convertView;
		}
		TextView name = (TextView) v.findViewById(R.id.groupname);
		if (name != null) {
			name.setText((String) getGroup(groupPosition));
		}
		return v;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public List<HashMap<String, Object>> createGroupList(PsrdDbAdapter dbAdapter) {
		Cursor curs = dbAdapter.fetchSectionByParentId("1");
		try {
			ArrayList<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> child = new HashMap<String, Object>();
			child.put("sectionName", "Bookmarks");
			child.put("id", "0");
			child.put("url", "pfsrd://Bookmarks");
			result.add(child);
			boolean has_next = curs.moveToFirst();
			while (has_next) {
				String section_id = curs.getString(0);
				String name = curs.getString(1);
				String type = curs.getString(2);
				String url = curs.getString(4);
				if (type.equals("list")) {
					child = new HashMap<String, Object>();
					child.put("sectionName", name);
					child.put("id", section_id);
					child.put("url", url);
					result.add(child);
				}
				has_next = curs.moveToNext();
			}
			this.subjects = result;
			return result;
		} finally {
			curs.close();
		}
	}

	public List<List<HashMap<String, Object>>> createChildList(
			PsrdDbAdapter dbAdapter, PsrdUserDbAdapter userDbAdapter) {
		ArrayList<List<HashMap<String, Object>>> result = new ArrayList<List<HashMap<String, Object>>>();
		for (int i = 0; i < subjects.size(); ++i) {
			HashMap<String, Object> sub = subjects.get(i);
			Log.d(TAG, sub.get("sectionName").toString());
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
			} else if (sectionName.equals("Races")) {
				RaceAdapter ra = new RaceAdapter(dbAdapter);
				result.add(ra.createRaceTypeList());
			} else if (sectionName.equals("Monsters")) {
				MonsterAdapter ma = new MonsterAdapter(dbAdapter);
				result.add(ma.createMonsterTypeList());
			} else if (sectionName.equals("Bookmarks")) {
				CollectionAdapter ca = new CollectionAdapter(userDbAdapter);
				ArrayList<HashMap<String, Object>> charList = ca
						.createCollectionList();

				HashMap<String, Object> adder = new HashMap<String, Object>();
				adder.put("id", "0");
				adder.put("specificName",
						context.getString(R.string.add_collection));
				charList.add(adder);
				if (charList.size() > 1) {
					HashMap<String, Object> remover = new HashMap<String, Object>();
					remover.put("id", "1");
					remover.put("specificName",
							context.getString(R.string.del_collection));
					charList.add(remover);
				}
				result.add(charList);
			} else {
				result.add(new ArrayList<HashMap<String, Object>>());
			}
		}
		this.children = result;
		return result;
	}
}
