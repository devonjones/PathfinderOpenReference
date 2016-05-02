package org.evilsoft.pathfinder.reference.list;

import org.evilsoft.pathfinder.reference.DisplayListAdapter;
import org.evilsoft.pathfinder.reference.R;
import org.evilsoft.pathfinder.reference.db.index.IndexGroupAdapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CreatureListAdapter extends DisplayListAdapter {
	private boolean mainList = false;

	public CreatureListAdapter(Context context, Cursor c) {
		super(context, c);
	}

	public CreatureListAdapter(Context context, Cursor c, boolean mainList) {
		super(context, c);
		this.mainList = mainList;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		moveCursor(index);
		View V = convertView;

		int layout = R.layout.monster_list_item;
		if (mainList) {
			layout = R.layout.monster_main_list_item;
		}
		if (V == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			V = vi.inflate(layout, null);
		}
		TextView title = (TextView) V.findViewById(R.id.monster_list_name);
		title.setText(IndexGroupAdapter.IndexGroupUtils.getName(c));
		TextView monsterType = (TextView) V
				.findViewById(R.id.monster_list_type);
		monsterType.setText(createTypeLine(c));
		if (mainList) {
			String description = IndexGroupAdapter.IndexGroupUtils.getDescription(c);
			TextView descTitle = (TextView) V
					.findViewById(R.id.monster_list_description_title);
			TextView tDescription = (TextView) V
					.findViewById(R.id.monster_list_description);
			if (description != null && !description.equals("")) {
				descTitle.setText("Description: ");
				tDescription.setText(description);
			}
			else {
				descTitle.setText("");
				tDescription.setText("");
			}
		}
		return V;
	}

	private String createTypeLine(Cursor curs) {
		// cd.size cd.alignment cd.creature_type (cd.creature_subtype)
		StringBuilder sb = new StringBuilder();
		String size = IndexGroupAdapter.IndexGroupUtils.getCreatureSize(c);
		String space = "";
		if (size != null) {
			sb.append(size);
			space = " ";
		}
		String alignment = IndexGroupAdapter.IndexGroupUtils.getCreatureAlign(c);
		if (alignment != null) {
			sb.append(space);
			sb.append(alignment);
			space = " ";
		}
		String creatureType = IndexGroupAdapter.IndexGroupUtils.getCreatureType(c);
		if (creatureType != null) {
			sb.append(space);
			sb.append(creatureType);
			space = " ";
		}
		String creatureSubtype = IndexGroupAdapter.IndexGroupUtils.getCreatureSubtype(c);
		if (creatureSubtype != null) {
			sb.append(space);
			sb.append("(");
			sb.append(creatureSubtype);
			sb.append(")");
		}
		if (sb.length() == 0) {
			sb.append("Monster article");
		}
		return sb.toString();
	}

	@Override
	public Object buildItem(Cursor c) {
		Integer sectionId = IndexGroupAdapter.IndexGroupUtils.getSectionId(c);
		String database = IndexGroupAdapter.IndexGroupUtils.getDatabase(c);
		String name = IndexGroupAdapter.IndexGroupUtils.getName(c);
		String url = IndexGroupAdapter.IndexGroupUtils.getUrl(c);
		String description = IndexGroupAdapter.IndexGroupUtils.getDescription(c);
		String creatureType = IndexGroupAdapter.IndexGroupUtils.getCreatureType(c);
		String creatureSubtype = IndexGroupAdapter.IndexGroupUtils.getCreatureSubtype(c);
		String cr = IndexGroupAdapter.IndexGroupUtils.getCreatureCr(c);
		String xp = IndexGroupAdapter.IndexGroupUtils.getCreatureXp(c);
		String size = IndexGroupAdapter.IndexGroupUtils.getCreatureSize(c);
		String alignment = IndexGroupAdapter.IndexGroupUtils.getCreatureAlign(c);
		return buildMonster(sectionId, database, name, url, description,
				creatureType, creatureSubtype, cr, xp, size, alignment);
	}

	public CreatureListItem buildMonster(Integer sectionId, String database,
			String name, String url, String description, String creatureType,
			String creatureSubtype, String cr, String xp, String size,
			String alignment) {
		CreatureListItem mla = new CreatureListItem();
		mla.setSectionId(sectionId);
		mla.setDatabase(database);
		mla.setName(name);
		mla.setUrl(url);
		mla.setDescription(description);
		mla.setCreatureType(creatureType);
		mla.setCreatureSubtype(creatureSubtype);
		mla.setCr(cr);
		mla.setXp(xp);
		mla.setSize(size);
		mla.setAlignment(alignment);
		return mla;
	}
}
