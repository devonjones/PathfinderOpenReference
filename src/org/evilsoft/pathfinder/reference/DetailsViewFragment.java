package org.evilsoft.pathfinder.reference;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class DetailsViewFragment extends SherlockFragment {
	private WebView viewer;
	private DetailsWebViewClient client;
	private String startUrl;

	public DetailsViewFragment() {
		super();
	}
	
	public DetailsViewFragment(String newUrl) {
		super();
		this.startUrl = newUrl;
	}

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
		viewer.getSettings().setSupportZoom(true);
		viewer.getSettings().setBuiltInZoomControls(true);
		if (Build.VERSION.SDK_INT >= 11) {
			viewer.getSettings().setDisplayZoomControls(false);
		}
		return v;
	}

	public void onViewCreated(View view, Bundle savedInstanceState) {
		if (this.startUrl != null) {
			updateUrl(this.startUrl);
		}
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
