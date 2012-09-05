package org.evilsoft.pathfinder.reference.list;

import org.evilsoft.pathfinder.reference.DisplayListAdapter;
import org.evilsoft.pathfinder.reference.R;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RaceListAdapter extends DisplayListAdapter {

	public RaceListAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		moveCursor(index);
		View V = convertView;

		if (V == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			V = vi.inflate(R.layout.race_list_item, null);
		}

		TextView title = (TextView) V.findViewById(R.id.race_list_name);
		title.setText(c.getString(1));
		TextView type = (TextView) V.findViewById(R.id.race_list_misc);
		String raceType = c.getString(2);
		return V;
	}

	public Object buildItem(Cursor c) {
		int section_id = c.getInt(0);
		String name = c.getString(1);
		String raceType = c.getString(2);
		return buildRace(section_id, name, raceType);
	}

	public RaceListItem buildRace(int section_id, String name, String raceType) {
		RaceListItem rla = new RaceListItem();
		rla.setSectionId(section_id);
		rla.setName(name);
		rla.setRaceType(raceType);
		return rla;
	}
}
