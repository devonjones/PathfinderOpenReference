package org.evilsoft.pathfinder.reference.list;

import org.evilsoft.pathfinder.reference.DisplayListAdapter;
import org.evilsoft.pathfinder.reference.R;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ClassListAdapter extends DisplayListAdapter {

	public ClassListAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		moveCursor(index);
		View V = convertView;

		int layout = R.layout.class_list_item;
		if (V == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			V = vi.inflate(layout, null);
		}

		TextView title = (TextView) V.findViewById(R.id.class_list_name);
		title.setText(c.getString(1));
		return V;
	}

	public Object buildItem(Cursor c) {
		int section_id = c.getInt(0);
		String name = c.getString(1);
		String description = c.getString(2);
		return buildClass(section_id, name, description);
	}

	public ClassListItem buildClass(int section_id, String name, String description) {
		ClassListItem cla = new ClassListItem();
		cla.setSectionId(section_id);
		cla.setName(name);
		cla.setDescription(description);
		return cla;
	}
}
