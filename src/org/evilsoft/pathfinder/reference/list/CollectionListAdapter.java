package org.evilsoft.pathfinder.reference.list;

import org.evilsoft.pathfinder.reference.DisplayListAdapter;
import org.evilsoft.pathfinder.reference.R;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CollectionListAdapter extends DisplayListAdapter {

	public CollectionListAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		moveCursor(position);
		View V = convertView;

		if (V == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			V = vi.inflate(R.layout.character_list_item, null);
		}

		TextView title = (TextView) V.findViewById(R.id.character_list_name);
		title.setText(c.getString(3));
		return V;
	}

	@Override
	public Object buildItem(Cursor c) {
		CollectionListItem cla = new CollectionListItem();
		cla.setCharacterId(c.getString(1));
		cla.setName(c.getString(3));
		cla.setUrl(c.getString(4));
		return cla;
	}

}
