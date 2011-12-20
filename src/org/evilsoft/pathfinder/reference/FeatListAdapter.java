package org.evilsoft.pathfinder.reference;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FeatListAdapter extends DisplayListAdapter {
	private boolean mainList = false;

	public FeatListAdapter(Context context, Cursor c) {
		super(context, c);
	}

	public FeatListAdapter(Context context, Cursor c, boolean mainList) {
		super(context, c);
		this.mainList = mainList;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		moveCursor(index);
		View V = convertView;

		int layout = R.layout.feat_list_item;
		if (mainList) {
			layout = R.layout.feat_main_list_item;
		}
		if (V == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			V = vi.inflate(layout, null);
		}

		TextView title = (TextView) V.findViewById(R.id.feat_list_name);
		title.setText(c.getString(1));
		TextView featTypes = (TextView) V.findViewById(R.id.feat_list_type);
		featTypes.setText(c.getString(4));
		if (mainList) {
			TextView description = (TextView) V.findViewById(R.id.feat_list_description);
			description.setText(c.getString(2));
			String p = c.getString(3);
			if (p != null) {
				TextView prereqs = (TextView) V.findViewById(R.id.feat_list_prereqs);
				prereqs.setText(p);
				TextView prereqsTitle = (TextView) V.findViewById(R.id.feat_list_prereqs_title);
				prereqsTitle.setText("Prerequisites: ");
			}
		}
		return V;
	}

	public Object buildItem(Cursor c) {
		int section_id = c.getInt(0);
		String name = c.getString(1);
		String description = c.getString(2);
		String prereqs = c.getString(3);
		String featTypes = c.getString(4);
		return buildFeat(section_id, name, description, prereqs, featTypes);
	}

	public FeatListItem buildFeat(int section_id, String name, String description, String prereqs, String featTypes) {
		FeatListItem fla = new FeatListItem();
		fla.setSectionId(section_id);
		fla.setName(name);
		fla.setDescription(description);
		fla.setPrereqs(prereqs);
		fla.setFeatTypes(featTypes);
		return fla;
	}
}
