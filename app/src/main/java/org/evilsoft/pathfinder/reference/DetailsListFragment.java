package org.evilsoft.pathfinder.reference;

import org.evilsoft.pathfinder.reference.list.UrlListItem;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

public class DetailsListFragment extends AbstractViewListFragment {
	private static final String TAG = "DetailsListFragment";

	public DetailsListFragment() {
		super();
		super.thin = true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (empty) {
			return;
		}
		String uri = getNextUrl(position);
		if (uri != null) {
			Log.d(TAG, uri);
			if (PathfinderOpenReferenceActivity.isTabletLayout(getActivity())) {
				DetailsViewFragment viewer = (DetailsViewFragment) this
						.getActivity().getSupportFragmentManager()
						.findFragmentById(R.id.details_view_fragment);
				viewer.updateUrl(uri, currentUrl);
				DetailsActivity da = (DetailsActivity) getActivity();
				da.historyManager.refreshDrawer();
			} else {
				Intent showContent = new Intent(this.getActivity()
						.getApplicationContext(), DetailsActivity.class);
				showContent.setData(Uri.parse(uri));
				startActivity(showContent);
			}
		}
	}

	private String getNextUrl(Integer position) {
		UrlListItem item = (UrlListItem) currentListAdapter.getItem(position);
		return item.getUrl();
	}
}
