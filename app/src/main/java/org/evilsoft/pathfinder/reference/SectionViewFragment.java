package org.evilsoft.pathfinder.reference;

import org.evilsoft.pathfinder.reference.db.book.SectionAdapter;
import org.evilsoft.pathfinder.reference.db.user.CollectionAdapter;
import org.evilsoft.pathfinder.reference.list.BaseListItem;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

public class SectionViewFragment extends AbstractViewListFragment {
	private static final String TAG = "SectionViewFragment";

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (empty) {
			return;
		}
		Intent showContent = new Intent(getActivity().getApplicationContext(),
				DetailsActivity.class);
		String uri = getNextUrl(id, position);
		Log.d(TAG, uri);
		showContent.setData(Uri.parse(uri));
		showContent.putExtra("context", currentUrl);
		startActivity(showContent);
	}

	private String getNextUrl(Long id, int position) {
		String uri = null;
		if ("Bookmarks".equals(currentType)) {
			CollectionAdapter ca = new CollectionAdapter(
					dbWrangler.getUserDbAdapter());
			Cursor curs = ca.fetchCollectionValue(id.toString());
			try {
				boolean has_next = curs.moveToNext();
				if (has_next) {
					uri = curs.getString(2);
				}
			} finally {
				curs.close();
			}
			return uri;
		} else {
			BaseListItem item = (BaseListItem) currentListAdapter
					.getItem(position);
			String url = item.getUrl();
			if (url == null) {
				SectionAdapter sa = dbWrangler.getBookDbAdapter(
						item.getDatabase()).getSectionAdapter();
				Cursor cursor = sa.fetchParentBySectionId(item.getSectionId());
				try {
					if (cursor.moveToFirst()) {
						return SectionAdapter.SectionUtils.getUrl(cursor);
					}
				} finally {
					cursor.close();
				}
			}
			return item.getUrl();
		}
	}
}
