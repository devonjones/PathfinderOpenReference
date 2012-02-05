package org.evilsoft.pathfinder.reference.list;

import org.evilsoft.pathfinder.reference.DisplayListAdapter;
import org.evilsoft.pathfinder.reference.R;
import org.evilsoft.pathfinder.reference.R.id;
import org.evilsoft.pathfinder.reference.R.layout;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MonsterListAdapter extends DisplayListAdapter {
	private boolean mainList = false;

	public MonsterListAdapter(Context context, Cursor c) {
		super(context, c);
	}

	public MonsterListAdapter(Context context, Cursor c, boolean mainList) {
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
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			V = vi.inflate(layout, null);
		}
		TextView title = (TextView) V.findViewById(R.id.monster_list_name);
		title.setText(c.getString(1));
		TextView monsterType = (TextView) V.findViewById(R.id.monster_list_type);
		monsterType.setText(createTypeLine(c));
		if (mainList) {
			String description = c.getString(2);
			if (description != null && !description.equals("")) {
				TextView descTitle = (TextView) V.findViewById(R.id.monster_list_description_title);
				descTitle.setText("Description: ");
				TextView tDescription = (TextView) V.findViewById(R.id.monster_list_description);
				tDescription.setText(c.getString(2));
			}
		}
		return V;
	}

	private String createTypeLine(Cursor curs) {
		// cd.size cd.alignment cd.creature_type (cd.creature_subtype)
		StringBuffer sb = new StringBuffer();
		String size = c.getString(7);
		String space = "";
		if (size != null) {
			sb.append(size);
			space = " ";
		}
		String alignment = c.getString(8);
		if (alignment != null) {
			sb.append(space);
			sb.append(alignment);
			space = " ";
		}
		String creatureType = c.getString(3);
		if (creatureType != null) {
			sb.append(space);
			sb.append(creatureType);
			space = " ";
		}
		String creatureSubtype = c.getString(4);
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

	public Object buildItem(Cursor c) {
		int section_id = c.getInt(0);
		String name = c.getString(1);
		String description = c.getString(2);
		String creatureType = c.getString(3);
		String creatureSubtype = c.getString(4);
		String cr = c.getString(5);
		String xp = c.getString(6);
		String size = c.getString(7);
		String alignment = c.getString(8);
		return buildMonster(section_id, name, description, creatureType, creatureSubtype, cr, xp, size, alignment);
	}

	public MonsterListItem buildMonster(int section_id, String name, String description, String creatureType,
			String creatureSubtype, String cr, String xp, String size, String alignment) {
		MonsterListItem mla = new MonsterListItem();
		mla.setSectionId(section_id);
		mla.setName(name);
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
