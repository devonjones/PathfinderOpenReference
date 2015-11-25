package org.evilsoft.pathfinder.reference;

import org.acra.ErrorReporter;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
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
	protected DetailsWebViewClient client;

	@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.details_view, container, false);
		TextView title = (TextView) v.findViewById(R.id.display_title);
		title.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		registerForContextMenu(title);
		title.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ErrorReporter e = ErrorReporter.getInstance();
				e.putCustomData("LastClick",
						"DetailsViewFragment.onCreateView.onClick");
				DetailsViewFragment.this.getActivity().openContextMenu(v);
			}
		});
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
		client = new DetailsWebViewClient(getActivity(), title, star,
				contentError);
		if (savedInstanceState != null
				&& savedInstanceState.containsKey("progression")) {
			client.setProgressToRestore(savedInstanceState
					.getFloat("progression"));
		}
		viewer.setWebViewClient(client);
		viewer.getSettings().setSupportZoom(true);
		viewer.getSettings().setBuiltInZoomControls(true);
		if (Build.VERSION.SDK_INT >= 11) {
			viewer.getSettings().setDisplayZoomControls(false);
		}
		return v;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		client.contextMenu(menu, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		return client.contextMenuSelected(item);
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
	public void onSaveInstanceState(Bundle outState) {
		outState.putFloat("progression", calculateProgression());
	}

	private float calculateProgression() {
		float positionTopView = viewer.getTop();
		float contentHeight = viewer.getContentHeight();
		float currentScrollPosition = viewer.getScrollY();
		float percentWebview = (currentScrollPosition - positionTopView)
				/ contentHeight;
		return percentWebview;
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
