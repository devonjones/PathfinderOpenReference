package org.evilsoft.pathfinder.reference.list;

import org.evilsoft.pathfinder.reference.DisplayListAdapter;
import org.evilsoft.pathfinder.reference.R;
import org.evilsoft.pathfinder.reference.db.book.SectionIndexGroupAdapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SectionListAdapter extends DisplayListAdapter {

	public SectionListAdapter(Context context, Cursor c) {
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
		title.setText(SectionIndexGroupAdapter.SectionGroupIndexUtils.getName(c));
		return V;
	}

	public Object buildItem(Cursor c) {
		Integer sectionId = SectionIndexGroupAdapter.SectionGroupIndexUtils.getSectionId(c);
		String database = SectionIndexGroupAdapter.SectionGroupIndexUtils.getDatabase(c);
		String name = SectionIndexGroupAdapter.SectionGroupIndexUtils.getName(c);
		String url = SectionIndexGroupAdapter.SectionGroupIndexUtils.getUrl(c);
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
