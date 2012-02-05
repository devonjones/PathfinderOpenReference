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

public class SpellListAdapter extends DisplayListAdapter {
	private boolean mainList = false;

	public SpellListAdapter(Context context, Cursor c) {
		super(context, c);
	}

	public SpellListAdapter(Context context, Cursor c, boolean mainList) {
		super(context, c);
		this.mainList = mainList;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		moveCursor(index);
		View V = convertView;

		int layout = R.layout.spell_list_item;
		if (mainList) {
			layout = R.layout.spell_main_list_item;
		}
		if (V == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			V = vi.inflate(layout, null);
		}

		TextView title = (TextView) V.findViewById(R.id.spell_list_name);
		title.setText(c.getString(1));
		if (mainList) {
			TextView schoolV = (TextView) V.findViewById(R.id.spell_list_school);
			String school = c.getString(3);
			String subschool = c.getString(4);
			schoolV.setText(SpellListItem.buildSchoolLine(school, subschool));
			TextView description = (TextView) V.findViewById(R.id.spell_list_description);
			String desc = c.getString(2);
			description.setText(SpellListItem.shortDescription(desc));
			if (c.getColumnCount() > 5) {
				int level = c.getInt(5);
				TextView qualities = (TextView) V.findViewById(R.id.spell_list_qualities);
				qualities.setText("Level " + level);
			}
		} else {
			if (c.getColumnCount() > 5) {
				int level = c.getInt(5);
				TextView qualities = (TextView) V.findViewById(R.id.spell_list_qualities);
				qualities.setText("Level " + level);
			} else {
				TextView schoolV = (TextView) V.findViewById(R.id.spell_list_school);
				String school = c.getString(3);
				String subschool = c.getString(4);
				schoolV.setText(SpellListItem.buildSchoolLine(school, subschool));
			}
		}
		return V;
	}

	public Object buildItem(Cursor c) {
		int section_id = c.getInt(0);
		String name = c.getString(1);
		String description = c.getString(2);
		String school = c.getString(3);
		String subschool = c.getString(4);
		int level;
		if (c.getColumnCount() > 5) {
			level = c.getInt(5);
			return buildSpell(section_id, name, description, school, subschool, level);
		}
		return buildSpell(section_id, name, description, school, subschool);
	}

	public SpellListItem buildSpell(int section_id, String name, String description, String school, String subschool) {
		return buildSpell(section_id, name, description, school, subschool, null);
	}

	public SpellListItem buildSpell(int section_id, String name, String description, String school, String subschool,
			Integer level) {
		SpellListItem sla = new SpellListItem();
		sla.setSectionId(section_id);
		sla.setName(name);
		sla.setDescription(description);
		sla.setSchool(school);
		sla.setSubschool(subschool);
		sla.setLevel(level);
		return sla;
	}
}
