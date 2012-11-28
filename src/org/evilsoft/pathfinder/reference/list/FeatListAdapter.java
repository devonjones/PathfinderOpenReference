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
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			V = vi.inflate(layout, null);
		}

		TextView title = (TextView) V.findViewById(R.id.feat_list_name);
		title.setText(IndexGroupAdapter.IndexGroupUtils.getName(c));
		TextView featTypes = (TextView) V.findViewById(R.id.feat_list_type);
		featTypes.setText(IndexGroupAdapter.IndexGroupUtils.getFeatTypes(c));
		if (mainList) {
			TextView description = (TextView) V
					.findViewById(R.id.feat_list_description);
			description.setText(IndexGroupAdapter.IndexGroupUtils.getDescription(c));
			TextView prereqs = (TextView) V
					.findViewById(R.id.feat_list_prereqs);
			TextView prereqsTitle = (TextView) V
					.findViewById(R.id.feat_list_prereqs_title);
			String p = IndexGroupAdapter.IndexGroupUtils.getFeatPrereqs(c);
			if (p != null) {
				prereqs.setText(p);
				prereqsTitle.setText("Prerequisites: ");
			} else {
				prereqs.setText("");
				prereqsTitle.setText("");
			}
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
		String prereqs = IndexGroupAdapter.IndexGroupUtils.getFeatPrereqs(c);
		String featTypes = IndexGroupAdapter.IndexGroupUtils.getFeatTypes(c);
		return buildFeat(sectionId, database, name, url, description, prereqs, featTypes);
	}

	public FeatListItem buildFeat(Integer sectionId, String database,
			String name, String url, String description, String prereqs, String featTypes) {
		FeatListItem fla = new FeatListItem();
		fla.setSectionId(sectionId);
		fla.setDatabase(database);
		fla.setName(name);
		fla.setUrl(url);
		fla.setDescription(description);
		fla.setPrereqs(prereqs);
		fla.setFeatTypes(featTypes);
		return fla;
	}
}