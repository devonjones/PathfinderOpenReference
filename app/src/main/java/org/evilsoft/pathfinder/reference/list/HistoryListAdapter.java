package org.evilsoft.pathfinder.reference.list;

import org.evilsoft.pathfinder.reference.DisplayListAdapter;
import org.evilsoft.pathfinder.reference.R;
import org.evilsoft.pathfinder.reference.db.user.HistoryAdapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HistoryListAdapter extends DisplayListAdapter {

	public HistoryListAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		moveCursor(index);
		View V = convertView;

		int layout = R.layout.default_list_item;
		if (V == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			V = vi.inflate(layout, null);
		}

		TextView title = (TextView) V.findViewById(R.id.default_list_name);
		title.setText(HistoryAdapter.HistoryUtils.getName(c));
		return V;
	}

	@Override
	public Object buildItem(Cursor c) {
		Integer historyId = HistoryAdapter.HistoryUtils.getHistoryId(c);
		String name = HistoryAdapter.HistoryUtils.getName(c);
		String url = HistoryAdapter.HistoryUtils.getUrl(c);
		return buildHistory(historyId, name, url);
	}

	public HistoryListItem buildHistory(Integer historyId, String name,
			String url) {
		HistoryListItem hla = new HistoryListItem();
		hla.setHistoryId(historyId);
		hla.setName(name);
		hla.setUrl(url);
		return hla;
	}
}
