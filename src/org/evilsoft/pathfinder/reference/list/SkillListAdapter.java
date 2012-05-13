package org.evilsoft.pathfinder.reference.list;

import org.evilsoft.pathfinder.reference.DisplayListAdapter;
import org.evilsoft.pathfinder.reference.R;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SkillListAdapter extends DisplayListAdapter {
	private boolean mainList = false;

	public SkillListAdapter(Context context, Cursor c) {
		super(context, c);
	}

	public SkillListAdapter(Context context, Cursor c, boolean mainList) {
		super(context, c);
		this.mainList = mainList;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		moveCursor(index);
		View V = convertView;

		int layout = R.layout.skill_list_item;
		if (mainList) {
			layout = R.layout.skill_main_list_item;
		}
		if (V == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			V = vi.inflate(layout, null);
		}

		TextView title = (TextView) V.findViewById(R.id.skill_list_name);
		title.setText(c.getString(1));
		TextView attr = (TextView) V.findViewById(R.id.skill_list_attribute);
		attr.setText(c.getString(3));
		if (mainList) {
			TextView description = (TextView) V.findViewById(R.id.skill_list_description);
			String desc = c.getString(2);
			description.setText(SkillListItem.shortDescription(desc));
			boolean armorCheckPenalty = (c.getInt(4) != 0);
			boolean trainedOnly = (c.getInt(5) != 0);
			TextView qualities = (TextView) V.findViewById(R.id.skill_list_qualities);
			qualities.setText(SkillListItem.buildQualitiesDisplay(armorCheckPenalty, trainedOnly));
		}
		return V;
	}

	public Object buildItem(Cursor c) {
		int section_id = c.getInt(0);
		String name = c.getString(1);
		String description = c.getString(2);
		String attribute = c.getString(3);
		boolean armorCheckPenalty = (c.getInt(4) != 0);
		boolean trainedOnly = (c.getInt(5) != 0);
		return buildSkill(section_id, name, description, attribute, armorCheckPenalty, trainedOnly);
	}

	public SkillListItem buildSkill(int section_id, String name, String description, String attribute,
			boolean armorCheckPenalty, boolean trainedOnly) {
		SkillListItem sla = new SkillListItem();
		sla.setSectionId(section_id);
		sla.setName(name);
		sla.setDescription(description);
		sla.setAttribute(attribute);
		sla.setArmorCheckPenalty(armorCheckPenalty);
		sla.setTrainedOnly(trainedOnly);
		return sla;
	}
}
