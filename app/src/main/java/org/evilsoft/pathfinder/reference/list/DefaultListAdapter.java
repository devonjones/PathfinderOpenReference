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

public class DefaultListAdapter extends DisplayListAdapter {

	public DefaultListAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		moveCursor(index);
		View V = convertView;

		if (V == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			V = vi.inflate(R.layout.default_list_item, null);
		}

		TextView title = (TextView) V.findViewById(R.id.default_list_name);
		title.setText(IndexGroupAdapter.IndexGroupUtils.getName(c));
		return V;
	}

	public Object buildItem(Cursor c) {
		Integer sectionId = IndexGroupAdapter.IndexGroupUtils.getSectionId(c);
		String database = IndexGroupAdapter.IndexGroupUtils.getDatabase(c);
		String name = IndexGroupAdapter.IndexGroupUtils.getName(c);
		String url = IndexGroupAdapter.IndexGroupUtils.getUrl(c);
		return buildBaseListItem(sectionId, database, name, url);
	}

	public BaseListItem buildBaseListItem(Integer sectionId, String database,
			String name, String url) {
		BaseListItem bla = new BaseListItem();
		bla.setSectionId(sectionId);
		bla.setDatabase(database);
		bla.setName(name);
		bla.setUrl(url);
		return bla;
	}
}
