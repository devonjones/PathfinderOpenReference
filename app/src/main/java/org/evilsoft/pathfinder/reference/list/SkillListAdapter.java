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
		title.setText(IndexGroupAdapter.IndexGroupUtils.getName(c));
		TextView attr = (TextView) V.findViewById(R.id.skill_list_attribute);
		attr.setText(IndexGroupAdapter.IndexGroupUtils.getSkillAttr(c));
		if (mainList) {
			TextView description = (TextView) V.findViewById(R.id.skill_list_description);
			String desc = IndexGroupAdapter.IndexGroupUtils.getDescription(c);
			description.setText(SkillListItem.shortDescription(desc));
			boolean armorCheckPenalty = (IndexGroupAdapter.IndexGroupUtils.getSkillArmor(c) != 0);
			boolean trainedOnly = (IndexGroupAdapter.IndexGroupUtils.getSkillTrained(c) != 0);
			TextView qualities = (TextView) V.findViewById(R.id.skill_list_qualities);
			qualities.setText(SkillListItem.buildQualitiesDisplay(armorCheckPenalty, trainedOnly));
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
		String attribute = IndexGroupAdapter.IndexGroupUtils.getSkillAttr(c);
		boolean armorCheckPenalty = (IndexGroupAdapter.IndexGroupUtils.getSkillArmor(c) != 0);
		boolean trainedOnly = (IndexGroupAdapter.IndexGroupUtils.getSkillTrained(c) != 0);
		return buildSkill(sectionId, database, name, url, description,
				attribute, armorCheckPenalty, trainedOnly);
	}

	public SkillListItem buildSkill(Integer sectionId, String database,
			String name, String url, String description, String attribute,
			boolean armorCheckPenalty, boolean trainedOnly) {
		SkillListItem sla = new SkillListItem();
		sla.setSectionId(sectionId);
		sla.setDatabase(database);
		sla.setName(name);
		sla.setUrl(url);
		sla.setDescription(description);
		sla.setAttribute(attribute);
		sla.setArmorCheckPenalty(armorCheckPenalty);
		sla.setTrainedOnly(trainedOnly);
		return sla;
	}
}
