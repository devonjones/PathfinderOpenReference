package org.evilsoft.pathfinder.reference;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.acra.ErrorReporter;
import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;
import org.evilsoft.pathfinder.reference.db.user.CollectionAdapter;
import org.evilsoft.pathfinder.reference.db.user.PsrdUserDbAdapter;

import android.app.Activity;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

public class DetailsWebViewClient extends WebViewClient {
	private static final String TAG = "DetailsWebViewClient";
	private PsrdDbAdapter dbAdapter;
	private PsrdUserDbAdapter userDbAdapter;
	private FragmentActivity act;
	private AssetManager assets;
	private TextView title;
	private ImageButton contentError;
	private ImageButton star;
	private ImageButton back;
	private String url;
	private String oldUrl;
	private boolean isTablet;
	private long currentCollection;
	ArrayList<HashMap<String, String>> path;

	public DetailsWebViewClient(Activity act, TextView title, ImageButton back,
			ImageButton star, ImageButton contentError) {
		this.act = (FragmentActivity) act;
		this.title = title;
		this.back = back;
		this.star = star;
		this.contentError = contentError;
		this.isTablet = PathfinderOpenReferenceActivity.isTabletLayout(act);
		assets = act.getApplicationContext().getAssets();
		openDb();
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String newUrl) {
		Log.i(TAG, newUrl);
		ErrorReporter e = ErrorReporter.getInstance();
		if (newUrl == null) {
			return false;
		}
		e.putCustomData("LastWebViewUrl", newUrl);
		if (newUrl.startsWith("http://")) {
			newUrl = newUrl.replace("http://pfsrd://", "pfsrd://"); // Gingerbread-
			newUrl = newUrl.replace("http://pfsrd//", "pfsrd://"); // Honeycomb+
			try {
				newUrl = URLDecoder.decode(newUrl, "UTF-8");
			} catch (UnsupportedEncodingException uee) {
				ErrorReporter.getInstance().putCustomData(
						"Situation", "Unable to decode url: " + newUrl);
				ErrorReporter.getInstance().handleException(uee);
			}
		}
		String[] parts = newUrl.split("\\/");
		if (parts[2].equals("Search") && parts.length < 5) {
			return false;
		}
		if (parts[0].toLowerCase().equals("pfsrd:")) {
			this.url = newUrl;
			Log.d(TAG, parts[parts.length - 1]);
			path = dbAdapter.getPathByUrl(newUrl);
			setBackVisibility(newUrl);
			return renderPfsrd(view, newUrl);
		}
		return false;
	}

	public void setBackVisibility(String newUrl) {
		if (path != null && path.size() > 1) {
			reloadList(newUrl);
			if (!path.get(1).get("type").equals("list")) {
				this.back.setVisibility(View.VISIBLE);
				return;
			}
		}
		this.back.setVisibility(View.INVISIBLE);
	}

	private String up(String uri) {
		String subtype = Uri.parse(uri).getQueryParameter("subtype");
		String localUrl = uri;
		if(uri.indexOf("?") > -1) {
			localUrl = TextUtils.split(uri, "\\?")[0];
		}
		if (path == null) {
			return localUrl;
		}

		String newUrl = null;
		int index = 1;
		while (newUrl == null && index < path.size() -1) {
			newUrl = path.get(index).get("url");
			index++;
		}
		StringBuffer sb = new StringBuffer();
		sb.append(newUrl);
		if (subtype != null) {
			sb.append("?subtype=");
			sb.append(subtype);
		}
		return sb.toString();
	}

	public void back(WebView view) {
		try {
			if (path.size() > 1) {
				if (!path.get(1).get("type").equals("list")) {
					String newUrl = up(url);
					shouldOverrideUrlLoading(view, newUrl);
				}
			}
		} catch (Exception e) {
			ErrorReporter.getInstance().putCustomData(
					"Situation", "Back button failed");
			ErrorReporter.getInstance().handleException(e);
		}
	}

	public void reloadList(String newUrl) {
		// [{id=10751, name=Ability Scores}, {id=10701, name=Getting Started},
		// {id=10700, name=Rules: Core Rulebook}, {id=1, name=PFSRD}]
		Log.i(TAG, newUrl);
		if (this.isTablet) {
			DetailsListFragment list = (DetailsListFragment) act
					.getSupportFragmentManager().findFragmentById(
							R.id.details_list_fragment);
			String updateUrl = up(newUrl);
			list.updateUrl(updateUrl);
		}
	}

	public String renderByUrl(WebView view, RenderFarm sa, String newUrl) {
		Cursor curs = dbAdapter.fetchSectionByUrl(newUrl);
		String html = null;
		StringBuffer htmlparts = new StringBuffer();
		try {
			boolean has_next = curs.moveToFirst();
			while (has_next) {
				htmlparts.append(sa.render(curs.getString(0), newUrl));
				has_next = curs.moveToNext();
			}
		} finally {
			html = htmlparts.toString();
			if (html.equals("")) {
				html = null;
			}
			curs.close();
		}
		return html;
	}

	public boolean renderPfsrd(WebView view, String newUrl) {
		if(newUrl.indexOf("?") > -1) {
			newUrl = TextUtils.split(newUrl, "\\?")[0];
		}
		String[] parts = newUrl.split("\\/");
		String html;
		RenderFarm sa = new RenderFarm(dbAdapter, assets, title, isTablet);
		html = renderByUrl(view, sa, newUrl);
		if (html == null) {
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
			} else if (parts[2].equals("Bookmarks")) {
				html = sa.render(parts[parts.length - 1], newUrl);
			} else if (parts[2].equals("Search")) {
				html = sa.render(parts[parts.length - 1], newUrl);
			} else if (parts[2].equals("Skills")) {
				html = sa.render(parts[parts.length - 1], newUrl);
			} else if (parts[2].equals("Spells")) {
				html = sa.render(parts[parts.length - 1], newUrl);
			} else if (parts[2].equals("Ogl")) {
				html = sa.render(parts[parts.length - 1], newUrl);
			} else {
				html = "<H1>" + newUrl + "</H1>";
			}
		}
		html = urlFilter(html);
		view.loadDataWithBaseURL(newUrl, html, "text/html", "UTF-8",
				this.oldUrl);
		view.setWebViewClient(this);
		view.scrollTo(0, 0);

		refreshStarButtonState();
		star.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CollectionAdapter ca = new CollectionAdapter(userDbAdapter);
				ca.toggleEntryStar(currentCollection, url,
						title.getText().toString());
				refreshStarButtonState();
			}
		});
		contentError.setOnClickListener(new ContentErrorReporter(this.act,
				path, title.getText().toString()));
		this.oldUrl = newUrl;
		return true;
	}

	private String urlFilter(String html) {
		return html.replace("pfsrd://", "http://pfsrd://");
	}

	private void refreshStarButtonState() {
		if (url != null) {
			CollectionAdapter ca = new
					CollectionAdapter(userDbAdapter);
			boolean starred =
					ca.entryIsStarred(currentCollection, url);
			star.setPressed(starred);
			star.setImageResource(starred ? android.R.drawable.btn_star_big_on :
					android.R.drawable.btn_star_big_off);
		}
	}

	private void openDb() {
		if (userDbAdapter == null) {
			userDbAdapter = new PsrdUserDbAdapter(act.getApplicationContext());
		}
		if (userDbAdapter.isClosed()) {
			userDbAdapter.open();
		}
		if (dbAdapter == null) {
			dbAdapter = new PsrdDbAdapter(act.getApplicationContext());
		}
		if (dbAdapter.isClosed()) {
			dbAdapter.open();
		}
	}

	public void onDestroy() {
		if (dbAdapter != null) {
			dbAdapter.close();
		}
		if (userDbAdapter != null) {
			userDbAdapter.close();
		}
	}

	public void setCharacter(long itemId) {
		currentCollection = itemId;
		refreshStarButtonState();
	}
}
