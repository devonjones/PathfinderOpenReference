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

public class NpcListAdapter extends DisplayListAdapter {
	private boolean mainList = false;

	public NpcListAdapter(Context context, Cursor c) {
		super(context, c);
	}

	public NpcListAdapter(Context context, Cursor c, boolean mainList) {
		super(context, c);
		this.mainList = mainList;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		moveCursor(index);
		View V = convertView;

		int layout = R.layout.monster_list_item;
		if (mainList) {
			layout = R.layout.monster_main_list_item;
		}
		if (V == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			V = vi.inflate(layout, null);
		}
		TextView title = (TextView) V.findViewById(R.id.monster_list_name);
		title.setText(IndexGroupAdapter.IndexGroupUtils.getName(c));
		TextView desc = (TextView) V
				.findViewById(R.id.monster_list_description);
		desc.setText(createTypeLine(c));
		return V;
	}

	private String createTypeLine(Cursor curs) {
		// cd.size cd.alignment cd.creature_type (cd.creature_subtype)
		StringBuilder sb = new StringBuilder();
		String space = "";
		String size = IndexGroupAdapter.IndexGroupUtils.getCreatureSize(c);
		if (size != null) {
			sb.append(space);
			sb.append(size);
			space = " ";
		}
		String alignment = IndexGroupAdapter.IndexGroupUtils
				.getCreatureAlign(c);
		if (alignment != null) {
			sb.append(space);
			sb.append(alignment);
			space = " ";
		}
		String superRace = IndexGroupAdapter.IndexGroupUtils
				.getCreatureSuperRace(c);
		if (superRace != null) {
			sb.append(space);
			sb.append(superRace);
			space = " ";
		}
		return sb.toString();
	}

	@Override
	public Object buildItem(Cursor c) {
		Integer sectionId = IndexGroupAdapter.IndexGroupUtils.getSectionId(c);
		String database = IndexGroupAdapter.IndexGroupUtils.getDatabase(c);
		String name = IndexGroupAdapter.IndexGroupUtils.getName(c);
		String url = IndexGroupAdapter.IndexGroupUtils.getUrl(c);
		String description = IndexGroupAdapter.IndexGroupUtils
				.getDescription(c);
		String superRace = IndexGroupAdapter.IndexGroupUtils
				.getCreatureSuperRace(c);
		String size = IndexGroupAdapter.IndexGroupUtils.getCreatureSize(c);
		String alignment = IndexGroupAdapter.IndexGroupUtils
				.getCreatureAlign(c);
		return buildNpc(sectionId, database, name, url, description, superRace,
				size, alignment);
	}

	public NpcListItem buildNpc(Integer sectionId, String database,
			String name, String url, String description, String superRace,
			String size, String alignment) {
		NpcListItem mla = new NpcListItem();
		mla.setSectionId(sectionId);
		mla.setDatabase(database);
		mla.setName(name);
		mla.setUrl(url);
		mla.setDescription(description);
		mla.setSuperRace(superRace);
		mla.setSize(size);
		mla.setAlignment(alignment);
		return mla;
	}
}
