package org.evilsoft.pathfinder.reference;

import org.acra.ErrorReporter;

import android.annotation.SuppressLint;
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

	@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.details_view, container, false);
		TextView title = (TextView) v.findViewById(R.id.display_title);
		ImageButton back = (ImageButton) v.findViewById(R.id.display_back);
		ImageButton star = (ImageButton) v.findViewById(R.id.display_star);
		ImageButton contentError = (ImageButton) v
				.findViewById(R.id.content_error);
		viewer = (WebView) v.findViewById(R.id.display_webview);
		viewer.getSettings().setJavaScriptEnabled(true);
		if (Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT < 16) {
			// Wierd rendering bug that causes the viewport of a webview to be
			// offset from
			// the actual webview.
			viewer.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		client = new DetailsWebViewClient(getActivity(), title, back, star,
				contentError);
		viewer.setWebViewClient(client);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ErrorReporter e = ErrorReporter.getInstance();
				e.putCustomData("LastClick",
						"DetailsViewFragment.onCreateView.onClick");
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

	public WebView getWebView() {
		return viewer;
	}

	public void onViewCreated(View view, Bundle savedInstanceState) {
	}

	public void updateUrl(String newUrl, String contextUrl) {
		client.render(viewer, newUrl, contextUrl);
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
