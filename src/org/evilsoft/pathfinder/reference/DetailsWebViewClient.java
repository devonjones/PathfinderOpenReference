package org.evilsoft.pathfinder.reference;

import java.util.ArrayList;
import java.util.HashMap;

import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

public class DetailsWebViewClient extends WebViewClient {
	private static final String TAG = "DetailsWebViewClient";
	private PsrdDbAdapter dbAdapter;
	private FragmentActivity act;
	private AssetManager assets;
	private TextView title;
	private ImageButton star;
	private ImageButton back;
	private String url;
	private String oldUrl;
	ArrayList<HashMap<String, String>> path;

	public DetailsWebViewClient(Activity act, TextView title, ImageButton back, ImageButton star) {
		dbAdapter = new PsrdDbAdapter(act.getApplicationContext());
		dbAdapter.open();
		this.act = (FragmentActivity) act;
		this.title = title;
		this.back = back;
		this.star = star;
		assets = act.getApplicationContext().getAssets();
	}

	public void back(WebView view) {
		if (this.path.size() > 3) {
			HashMap<String, String> parent = path.get(1);
			String newUrl = SectionRenderer.swapUrl(this.url, parent.get("name"), parent.get("id"));
			String[] parts = newUrl.split("\\/");
			if (parts[2].equals("Search")) {
				parts[2] = "Rules";
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < parts.length; i++) {
					if (i != 0) {
						sb.append("/");
					}
					sb.append(parts[i]);
				}
				newUrl = sb.toString();
			}
			shouldOverrideUrlLoading(view, newUrl);
		}
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String newUrl) {
		Log.e(TAG, newUrl);
		String[] parts = newUrl.split("\\/");
		if (parts[2].equals("Search") && parts.length < 5) {
			return false;
		}
		if (parts[0].toLowerCase().equals("pfsrd:")) {
			this.url = newUrl;
			Log.e(TAG, parts[parts.length - 1]);
			this.path = dbAdapter.getPath(parts[parts.length - 1]);
			if (path.size() <= 3) {
				this.back.setVisibility(View.INVISIBLE);
			} else {
				this.back.setVisibility(View.VISIBLE);
				reloadList(newUrl);
			}
			return renderPfsrd(view, newUrl);
		}
		return false;
	}

	public void reloadList(String newUrl) {
		// [{id=10751, name=Ability Scores}, {id=10701, name=Getting Started},
		// {id=10700, name=Rules: Core Rulebook}, {id=1, name=PFSRD}]
		Log.e(TAG, newUrl);
		String[] parts = newUrl.split("\\/");
		if (parts[2].startsWith("Rules")) {
			DetailsListFragment list = (DetailsListFragment) act.getSupportFragmentManager().findFragmentById(
					R.id.details_list_fragment);
			HashMap<String, String> parent = path.get(1);
			String updateUrl = SectionRenderer.swapUrl(this.url, parent.get("name"), parent.get("id"));
			list.updateUrl(updateUrl);
		}
	}

	public boolean renderPfsrd(WebView view, String newUrl) {
		String[] parts = newUrl.split("\\/");
		String html;
		SectionRenderer sa = new SectionRenderer(dbAdapter, assets, title);
		if (parts[2].equals("Classes")) {
			html = sa.render(parts[parts.length - 1], newUrl);
		} else if (parts[2].equals("Feats")) {
			html = sa.render(parts[parts.length - 1], newUrl);
		} else if (parts[2].equals("Monsters")) {
			html = sa.render(parts[parts.length - 1], newUrl);
		} else if (parts[2].startsWith("Rules")) {
			html = sa.render(parts[parts.length - 1], newUrl);
		} else if (parts[2].equals("Races")) {
			html = sa.render(parts[parts.length - 1], newUrl);
		} else if (parts[2].equals("Search")) {
			html = sa.render(parts[parts.length - 1], newUrl);
		} else if (parts[2].equals("Skills")) {
			html = sa.render(parts[parts.length - 1], newUrl);
		} else if (parts[2].equals("Spells")) {
			html = sa.render(parts[parts.length - 1], newUrl);
		} else {
			html = "<H1>" + newUrl + "</H1>";
		}
		if (Build.VERSION.SDK_INT <= 10) {
			view.loadDataWithBaseURL(newUrl, html, "text/html", "UTF-8", this.oldUrl);
			view.setWebViewClient(this);
			view.scrollTo(0, 0);
		} else {
			view.loadData(html, "text/html", "UTF-8");
			view.scrollTo(0, 0);
			view.reload();
			view.scrollTo(0, 0);
		}

		this.oldUrl = newUrl;
		return true;
	}

	public void onDestroy() {
		if (dbAdapter != null) {
			dbAdapter.close();
		}
	}
}
