package org.evilsoft.pathfinder.reference;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

public class DetailsViewFragment extends Fragment {
	private WebView viewer;
	private DetailsWebViewClient client;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.details_view, container, false);
		TextView title = (TextView) v.findViewById(R.id.display_title);
		ImageButton back = (ImageButton) v.findViewById(R.id.display_back);
		ImageButton star = (ImageButton) v.findViewById(R.id.display_star);
		viewer = (WebView) v.findViewById(R.id.display_webview);
		client = new DetailsWebViewClient(getActivity(), title, back, star);
		viewer.setWebViewClient(client);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				client.back(viewer);
			}
		});
		return v;
	}

	public void updateUrl(String newUrl) {
		client.shouldOverrideUrlLoading(viewer, newUrl);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		client.onDestroy();
	}

    public void setCharacter(long itemId) {
        client.setCharacter(itemId);
    }
}
