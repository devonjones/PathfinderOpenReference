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

public class RuleListAdapter extends DisplayListAdapter {

	public RuleListAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		moveCursor(index);
		View V = convertView;

		if (V == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			V = vi.inflate(R.layout.rule_list_item, null);
		}

		TextView title = (TextView) V.findViewById(R.id.rule_list_name);
		title.setText(c.getString(1));
		return V;
	}

	public Object buildItem(Cursor c) {
		int section_id = c.getInt(0);
		String name = c.getString(1);
		String description = c.getString(2);
		return buildRule(section_id, name, description);
	}

	public RuleListItem buildRule(int section_id, String name, String description) {
		RuleListItem rla = new RuleListItem();
		rla.setSectionId(section_id);
		rla.setName(name);
		rla.setDescription(description);
		return rla;
	}
}
