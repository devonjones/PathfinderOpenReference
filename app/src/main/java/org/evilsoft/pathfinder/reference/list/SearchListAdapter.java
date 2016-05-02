package org.evilsoft.pathfinder.reference.list;

import org.evilsoft.pathfinder.reference.DisplayListAdapter;
import org.evilsoft.pathfinder.reference.R;
import org.evilsoft.pathfinder.reference.db.index.SearchAdapter;

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
		title.setText(SearchAdapter.SearchUtils.getName(c));
		TextView parent_name = (TextView) V.findViewById(R.id.search_list_parent);
		parent_name.setText("From: " + SearchAdapter.SearchUtils.getParentName(c));
		TextView type = (TextView) V.findViewById(R.id.search_list_misc);
		StringBuilder sb = new StringBuilder();
		String sectionType = SearchAdapter.SearchUtils.getType(c);
		String sectionSubtype = SearchAdapter.SearchUtils.getSubtype(c);
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

	@Override
	public Object buildItem(Cursor c) {
		Integer sectionId = SearchAdapter.SearchUtils.getSectionId(c);
		String database = SearchAdapter.SearchUtils.getDatabase(c);
		String name = SearchAdapter.SearchUtils.getName(c);
		String url = SearchAdapter.SearchUtils.getUrl(c);
		String type = SearchAdapter.SearchUtils.getType(c);
		String subtype = SearchAdapter.SearchUtils.getSubtype(c);
		String parentName = SearchAdapter.SearchUtils.getParentName(c);
		return buildSearchItem(sectionId, database, name, url, type, subtype, parentName);
	}

	public SearchListItem buildSearchItem(Integer sectionId, String database, String name, String url,
			String type, String subtype, String parentName) {
		SearchListItem sla = new SearchListItem();
		sla.setSectionId(sectionId);
		sla.setDatabase(database);
		sla.setName(name);
		sla.setUrl(url);
		sla.setSectionType(type);
		sla.setSectionSubtype(subtype);
		sla.setParent(parentName);
		return sla;
	}
}
