package org.evilsoft.pathfinder.reference.list;

import org.evilsoft.pathfinder.reference.DisplayListAdapter;
import org.evilsoft.pathfinder.reference.R;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SearchListAdapter extends DisplayListAdapter {

	public SearchListAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		moveCursor(index);
		View V = convertView;

		if (V == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			V = vi.inflate(R.layout.search_list_item, null);
		}

		TextView title = (TextView) V.findViewById(R.id.search_list_name);
		title.setText(c.getString(1));
		TextView parent_name = (TextView) V.findViewById(R.id.search_list_parent);
		parent_name.setText("From: " + c.getString(4));
		TextView type = (TextView) V.findViewById(R.id.search_list_misc);
		StringBuffer sb = new StringBuffer();
		String sectionType = c.getString(2);
		String sectionSubtype = c.getString(3);
		sb.append("(");
		if(sectionType != null) {
			sb.append(sectionType);
		}
		if(sectionSubtype != null) {
			sb.append("/");
			sb.append(sectionSubtype);
		}
		sb.append(")");
		type.setText(sb.toString());
		return V;
	}

	public Object buildItem(Cursor c) {
		int section_id = c.getInt(0);
		String name = c.getString(1);
		String sectionType = c.getString(2);
		String sectionSubtype = c.getString(2);
		String parent = c.getString(3);
		return buildSearchItem(section_id, name, sectionType, sectionSubtype, parent);
	}

	public SearchListItem buildSearchItem(int section_id, String name, String sectionType, String sectionSubtype, String parent) {
		SearchListItem sla = new SearchListItem();
		sla.setSectionId(section_id);
		sla.setName(name);
		sla.setSectionType(sectionType);
		sla.setSectionSubtype(sectionSubtype);
		sla.setParent(parent);
		return sla;
	}
}
