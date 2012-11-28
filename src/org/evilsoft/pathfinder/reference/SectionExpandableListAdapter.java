package org.evilsoft.pathfinder.reference;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.DbWrangler;
import org.evilsoft.pathfinder.reference.db.book.SectionAdapter;
import org.evilsoft.pathfinder.reference.db.index.CreatureTypeAdapter;
import org.evilsoft.pathfinder.reference.db.index.FeatTypeAdapter;
import org.evilsoft.pathfinder.reference.db.index.MenuAdapter;
import org.evilsoft.pathfinder.reference.db.index.SpellClassAdapter;
import org.evilsoft.pathfinder.reference.db.user.CollectionAdapter;

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
	private List<MenuItem> subjects;
	private List<List<MenuItem>> children;
	private LayoutInflater inflater;
	private Context context;

	public SectionExpandableListAdapter(Context context) {
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void refresh(DbWrangler dbWrangler) {
		createGroupList(dbWrangler);
		createChildList(dbWrangler);
		notifyDataSetChanged();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return children.get(groupPosition).get(childPosition).getName();
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		try {
			return children.get(groupPosition)
					.get(childPosition).getId();
		} catch (NumberFormatException nfe) {
			return 0;
		}
	}

	public Integer getPfChildId(int groupPosition, int childPosition) {
		return children.get(groupPosition).get(childPosition).getId();
	}

	public String getPfChildUrl(int groupPosition, int childPosition) {
		return children.get(groupPosition).get(childPosition).getUrl();
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
		return subjects.get(groupPosition).getName();
	}

	@Override
	public int getGroupCount() {
		return subjects.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		try {
			return subjects.get(groupPosition).getId();
		} catch (NumberFormatException nfe) {
			return 0;
		}
	}

	public Integer getPfGroupId(int groupPosition) {
		return subjects.get(groupPosition).getId();
	}

	public String getPfGroupUrl(int groupPosition) {
		return subjects.get(groupPosition).getUrl();
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
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public List<MenuItem> createGroupList(DbWrangler dbWrangler) {
		Cursor curs = dbWrangler.getIndexDbAdapter().getMenuAdapter().fetchMenu();
		try {
			List<MenuItem> result = new ArrayList<MenuItem>();
			boolean has_next = curs.moveToFirst();
			while (has_next) {
				MenuItem mi = MenuAdapter.MenuUtils.genMenuItem(curs);
				result.add(mi);
				has_next = curs.moveToNext();
			}
			this.subjects = result;
			return result;
		} finally {
			curs.close();
		}
	}

	public List<List<MenuItem>> createChildList(DbWrangler dbWrangler) {
		List<List<MenuItem>> retList = new ArrayList<List<MenuItem>>();
		for (int i = 0; i < subjects.size(); ++i) {
			MenuItem sub = subjects.get(i);
			Log.d(TAG, sub.getName());
			Integer id = sub.getId();
			Cursor curs = dbWrangler.getIndexDbAdapter().getMenuAdapter().fetchMenu(id.toString());
			try {
				ArrayList<MenuItem> result = new ArrayList<MenuItem>();
				if(curs.getCount() > 0) {
					boolean has_next = curs.moveToFirst();
					while (has_next) {
						String group = MenuAdapter.MenuUtils.getGrouping(curs);
						if("feat_type".equals(group)) {
							result.addAll(getFeatTypeList(dbWrangler));
						} else if ("creature_type".equals(group)) {
							result.addAll(getCreatureTypeList(dbWrangler));
						} else if ("spell_classes".equals(group)) {
							result.addAll(getSpellClassList(dbWrangler));
						} else if ("bookmarks".equals(group)) {
							result.addAll(getBookmarks(dbWrangler));
						} else if ("children".equals(group)) {
							String db = MenuAdapter.MenuUtils.getDb(curs);
							String listUri = MenuAdapter.MenuUtils.getListUrl(curs);
							result.addAll(getChildren(dbWrangler, db, listUri));
						} else {
							MenuItem mi = MenuAdapter.MenuUtils.genMenuItem(curs);
							result.add(mi);
						}
						has_next = curs.moveToNext();
					}
				}
				retList.add(result);
			} finally {
				curs.close();
			}
		}
		this.children = retList;
		return retList;
	}

	public List<MenuItem> getFeatTypeList(DbWrangler dbWrangler) {
		ArrayList<MenuItem> results = new ArrayList<MenuItem>();
		Cursor curs = dbWrangler.getIndexDbAdapter().getFeatTypeAdapter().fetchFeatTypes();
		try {
			boolean has_next = curs.moveToFirst();
			while (has_next) {
				MenuItem mi = new MenuItem();
				mi.setId(0);
				String name = FeatTypeAdapter.FeatTypeUtils.getFeatType(curs);
				mi.setName(name);
				mi.setUrl("pfsrd://Menu/Feats/feats/" + name);
				results.add(mi);
				has_next = curs.moveToNext();
			}
		} finally {
			curs.close();
		}
		return results;
	}

	public List<MenuItem> getCreatureTypeList(DbWrangler dbWrangler) {
		ArrayList<MenuItem> results = new ArrayList<MenuItem>();
		Cursor curs = dbWrangler.getIndexDbAdapter().getCreatureTypeAdapter().fetchCreatureTypes();
		try {
			boolean has_next = curs.moveToFirst();
			while (has_next) {
				MenuItem mi = new MenuItem();
				mi.setId(0);
				String name = CreatureTypeAdapter.CreatureTypeUtils.getCreatureType(curs);
				mi.setName(name);
				mi.setUrl("pfsrd://Menu/Creatures/creatures/" + name);
				results.add(mi);
				has_next = curs.moveToNext();
			}
		} finally {
			curs.close();
		}
		return results;
	}

	public List<MenuItem> getSpellClassList(DbWrangler dbWrangler) {
		ArrayList<MenuItem> results = new ArrayList<MenuItem>();
		Cursor curs = dbWrangler.getIndexDbAdapter().getSpellClassAdapter().fetchSpellClasses();
		try {
			boolean has_next = curs.moveToFirst();
			while (has_next) {
				MenuItem mi = new MenuItem();
				mi.setId(0);
				String name = SpellClassAdapter.SpellListUtils.getClass(curs);
				mi.setName(name);
				mi.setUrl("pfsrd://Menu/Spells/spells/" + name);
				results.add(mi);
				has_next = curs.moveToNext();
			}
		} finally {
			curs.close();
		}
		return results;
	}

	public List<MenuItem> getBookmarks(DbWrangler dbWrangler) {
		CollectionAdapter ca = new CollectionAdapter(dbWrangler.getUserDbAdapter());
		List<MenuItem> charList = ca.createCollectionList();

		MenuItem adder = new MenuItem();
		adder.setId(0);
		adder.setName(context.getString(R.string.add_collection));
		charList.add(adder);
		if (charList.size() > 1) {
			MenuItem remover = new MenuItem();
			remover.setId(1);
			remover.setName(context.getString(R.string.del_collection));
			charList.add(remover);
		}
		return charList;
	}

	public List<MenuItem> getChildren(DbWrangler dbWrangler, String db, String listUri) {
		ArrayList<MenuItem> results = new ArrayList<MenuItem>();
		Cursor curs = dbWrangler.getBookDbAdapter(db).getSectionAdapter().fetchSectionByParentUrl(listUri);
		try {
			boolean has_next = curs.moveToFirst();
			while (has_next) {
				MenuItem mi = new MenuItem();
				mi.setId(SectionAdapter.SectionUtils.getSectionId(curs));
				mi.setName(SectionAdapter.SectionUtils.getName(curs));
				mi.setUrl(SectionAdapter.SectionUtils.getUrl(curs));
				results.add(mi);
				has_next = curs.moveToNext();
			}
		} finally {
			curs.close();
		}
		return results;
	}
}
