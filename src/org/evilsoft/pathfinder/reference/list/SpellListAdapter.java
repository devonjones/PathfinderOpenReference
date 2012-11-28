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
		title.setText(IndexGroupAdapter.IndexGroupUtils.getName(c));
		if (mainList) {
			TextView schoolV = (TextView) V.findViewById(R.id.spell_list_school);
			String school = IndexGroupAdapter.IndexGroupUtils.getSpellSchool(c);
			String subschool = IndexGroupAdapter.IndexGroupUtils.getSpellSubschool(c);
			String descriptor = IndexGroupAdapter.IndexGroupUtils.getSpellDescriptor(c);
			schoolV.setText(SpellListItem.buildSchoolLine(school, subschool, descriptor));
			TextView description = (TextView) V.findViewById(R.id.spell_list_description);
			String desc = IndexGroupAdapter.IndexGroupUtils.getDescription(c);
			description.setText(SpellListItem.shortDescription(desc));
			if (IndexGroupAdapter.IndexGroupUtils.hasLevel(c)) {
				Integer level = IndexGroupAdapter.IndexGroupUtils.getSpellLevel(c);
				TextView qualities = (TextView) V.findViewById(R.id.spell_list_qualities);
				qualities.setText("Level " + level);
			}
		} else {
			if (IndexGroupAdapter.IndexGroupUtils.hasLevel(c)) {
				Integer level = IndexGroupAdapter.IndexGroupUtils.getSpellLevel(c);
				TextView qualities = (TextView) V.findViewById(R.id.spell_list_qualities);
				qualities.setText("Level " + level);
			} else {
				TextView schoolV = (TextView) V.findViewById(R.id.spell_list_school);
				String school = IndexGroupAdapter.IndexGroupUtils.getSpellSchool(c);
				String subschool = IndexGroupAdapter.IndexGroupUtils.getSpellSubschool(c);
				String descriptor = IndexGroupAdapter.IndexGroupUtils.getSpellDescriptor(c);
				schoolV.setText(SpellListItem.buildSchoolLine(school, subschool, descriptor));
			}
		}
		return V;
	}

	@Override
	public Object buildItem(Cursor c) {
		Integer sectionId = IndexGroupAdapter.IndexGroupUtils.getSectionId(c);
		String database = IndexGroupAdapter.IndexGroupUtils.getDatabase(c);
		String name = IndexGroupAdapter.IndexGroupUtils.getName(c);
		String url = IndexGroupAdapter.IndexGroupUtils.getUrl(c);
		String description = IndexGroupAdapter.IndexGroupUtils.getDescription(c);
		String school = IndexGroupAdapter.IndexGroupUtils.getSpellSchool(c);
		String subschool = IndexGroupAdapter.IndexGroupUtils.getSpellSubschool(c);
		String descriptor = IndexGroupAdapter.IndexGroupUtils.getSpellDescriptor(c);
		if (IndexGroupAdapter.IndexGroupUtils.hasLevel(c)) {
			Integer level = IndexGroupAdapter.IndexGroupUtils.getSpellLevel(c);
			return buildSpell(sectionId, database, name, url, description, school, subschool, descriptor, level);
		}
		return buildSpell(sectionId, database, name, url, description, school, subschool, descriptor);
	}

	public SpellListItem buildSpell(Integer sectionId, String database,
			String name, String url, String description, String school, String subschool, String descriptor) {
		return buildSpell(sectionId, database, name, url, description, school, subschool, descriptor, null);
	}

	public SpellListItem buildSpell(Integer sectionId, String database,
			String name, String url, String description, String school, String subschool,
			String descriptor, Integer level) {
		SpellListItem sla = new SpellListItem();
		sla.setSectionId(sectionId);
		sla.setDatabase(database);
		sla.setName(name);
		sla.setUrl(url);
		sla.setDescription(description);
		sla.setSchool(school);
		sla.setSubschool(subschool);
		sla.setDescriptor(descriptor);
		if(level != null) {
			sla.setLevel(level);
		}
		return sla;
	}
}
