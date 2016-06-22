package org.evilsoft.pathfinder.reference;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.evilsoft.pathfinder.reference.db.BookNotFoundException;
import org.evilsoft.pathfinder.reference.db.DbWrangler;
import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.book.SectionAdapter;
import org.evilsoft.pathfinder.reference.db.user.CollectionAdapter;
import org.evilsoft.pathfinder.reference.db.user.HistoryAdapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

public class DetailsWebViewClient extends WebViewClient {
	public static final String PREFS_NAME = "psrd.prefs";
	private static final String TAG = "DetailsWebViewClient";
	private FragmentActivity act;
	private DbWrangler dbWrangler;
	private TextView title;
	private ImageButton star;
	private String url;
	private String oldUrl;
	private boolean isTablet;
	private long currentCollection;
	private Float progressToRestore;
	private WebView mWebView;
	ArrayList<HashMap<String, String>> path;

	public DetailsWebViewClient(Activity act, TextView title, ImageButton star) {
		this.act = (FragmentActivity) act;
		this.title = title;
		this.star = star;
		this.isTablet = PathfinderOpenReferenceActivity.isTabletLayout(act);
		openDb();
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		if (progressToRestore != null && mWebView != null) {

			view.postDelayed(new Runnable() {
				@Override
				public void run() {
					float webviewsize = mWebView.getContentHeight()
							- mWebView.getTop();
					float positionInWV = webviewsize * progressToRestore;
					int positionY = Math.round(mWebView.getTop() + positionInWV);
					mWebView.scrollTo(0, positionY);
					progressToRestore = null;
				}
				// Delay the scrollTo to make it work
			}, 200);
		}
		super.onPageFinished(view, url);
	}

	public String mungeUrl(String newUrl) {
		Log.i(TAG, newUrl);
		if (newUrl == null) {
			return null;
		}
		if (newUrl.startsWith("http://")) {
			newUrl = newUrl.replace("http://pfsrd://", "pfsrd://"); // Gingerbread-
			newUrl = newUrl.replace("http://pfsrd//", "pfsrd://"); // Honeycomb+
			try {
				newUrl = URLDecoder.decode(newUrl, "UTF-8");
			} catch (UnsupportedEncodingException uee) {
				Log.e(TAG, "Unable to decode url: " + newUrl);
			}
		}
		String[] parts = newUrl.split("\\/");
		if (parts[2].equals("Search") && parts.length < 5) {
			return null;
		}
		return newUrl;
	}

	public boolean render(WebView view, String newUrl, String contextUrl) {
		mWebView = view;
		newUrl = mungeUrl(newUrl);
		if (newUrl != null) {
			String[] parts = newUrl.split("\\/");
			if (parts[0].toLowerCase().equals("pfsrd:")) {
				this.url = newUrl;
				Log.d(TAG, parts[parts.length - 1]);
				try {
					path = dbWrangler.getBookDbAdapterByUrl(newUrl)
							.getPathByUrl(newUrl);
					return renderPfsrd(view, newUrl);
				} catch (BookNotFoundException bnfe) {
					Log.e(TAG, "Book not found: " + bnfe.getMessage());
				}
			}
		}
		return false;
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String newUrl) {
		newUrl = mungeUrl(newUrl);
		if (newUrl != null) {
			String[] parts = newUrl.split("\\/");
			if (parts[0].toLowerCase().equals("pfsrd:")) {
				Intent showContent = new Intent(act.getApplicationContext(),
						DetailsActivity.class);

				showContent.setData(Uri.parse(newUrl));
				act.startActivity(showContent);
				return true;
			}
		}
		return false;
	}

	private String up(String uri) {
		String subtype = Uri.parse(uri).getQueryParameter("subtype");
		String localUrl = uri;
		if (uri.indexOf("?") > -1) {
			localUrl = TextUtils.split(uri, "\\?")[0];
		}
		if (path == null) {
			return localUrl;
		}

		String newUrl = null;
		int index = 1;
		while (newUrl == null && index < path.size() - 1) {
			newUrl = path.get(index).get("url");
			index++;
		}
		if (newUrl == null) {
			return uri;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(newUrl);
		if (subtype != null) {
			sb.append("?subtype=");
			sb.append(subtype);
		}
		return sb.toString();
	}

	public void back(WebView view) {
		try {
			if (path.size() > 1 && !path.get(1).get("type").equals("list")) {
				String newUrl = up(url);					
				shouldOverrideUrlLoading(view, newUrl);
			}
		} catch (Exception e) {
			Log.e(TAG, "Back button failed");
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
			String[] parts = newUrl.split("\\/");
			if (parts[2].equals("Menu")) {
				list.updateUrl(newUrl);
			} else {
				String updateUrl = up(newUrl);
				list.updateUrl(updateUrl);
			}
		}
	}

	public String renderByUrl(HtmlRenderFarm sa, String newUrl) {
		Cursor cursor;
		try {
			cursor = dbWrangler.getBookDbAdapterByUrl(newUrl)
					.getSectionAdapter().fetchSectionByUrl(newUrl);
			String html = null;
			StringBuilder htmlparts = new StringBuilder();
			try {
				boolean hasNext = cursor.moveToFirst();
				while (hasNext) {
					HistoryAdapter ha = new HistoryAdapter(
							dbWrangler.getUserDbAdapter());
					ha.addHistory(SectionAdapter.SectionUtils.getName(cursor),
							newUrl);
					htmlparts.append(sa.render(SectionAdapter.SectionUtils
							.getSectionId(cursor).toString(), newUrl));
					hasNext = cursor.moveToNext();
				}
			} finally {
				html = htmlparts.toString();
				if (html.equals("")) {
					html = null;
				}
				cursor.close();
			}
			return html;
		} catch (BookNotFoundException bnfe) {
			Log.e(TAG, "Book not found: " + bnfe.getMessage());
		}
		return "";
	}

	public boolean renderPfsrd(WebView view, String newUrl) {
		if (newUrl.indexOf("?") > -1) {
			newUrl = TextUtils.split(newUrl, "\\?")[0];
		}
		String[] parts = newUrl.split("\\/");
		String html;
		SharedPreferences settings = act.getSharedPreferences(PREFS_NAME, 0);
		boolean showToc = settings.getBoolean("showToc", true);
		BookDbAdapter bookDbAdapter;
		try {
			bookDbAdapter = dbWrangler.getBookDbAdapterByUrl(newUrl);
			HtmlRenderFarm sa = new HtmlRenderFarm(dbWrangler, bookDbAdapter,
					title, isTablet, showToc);
			html = renderByUrl(sa, newUrl);
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
			view.loadDataWithBaseURL(encodeUrl(newUrl), html, "text/html",
					"UTF-8", this.oldUrl);
			view.setWebViewClient(this);
			view.scrollTo(0, 0);

			refreshStarButtonState();
			star.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					CollectionAdapter ca = new CollectionAdapter(dbWrangler
							.getUserDbAdapter());
					ca.toggleEntryStar(currentCollection, url, title.getText()
							.toString());
					refreshStarButtonState();
				}
			});
			this.oldUrl = newUrl;
			return true;
		} catch (BookNotFoundException bnfe) {
			Log.e(TAG, "Book not found: " + bnfe.getMessage());
		}
		return false;
	}

	private String encodeUrl(String url) {
		String[] parts = url.split("\\/");
		StringBuilder sb = new StringBuilder();
		sb.append(parts[0]);
		for (int i = 1; i < parts.length; i++) {
			sb.append("/");
			sb.append(Uri.encode(parts[i]));
		}
		return sb.toString();
	}

	private String urlFilter(String html) {
		return html.replace("pfsrd://", "http://pfsrd://");
	}

	private void refreshStarButtonState() {
		if (url != null) {
			CollectionAdapter ca = new CollectionAdapter(
					dbWrangler.getUserDbAdapter());
			boolean starred = ca.entryIsStarred(currentCollection, url);
			star.setPressed(starred);
			star.setImageResource(starred ? android.R.drawable.btn_star_big_on
					: android.R.drawable.btn_star_big_off);
		}
	}

	private void openDb() {
		if (dbWrangler == null) {
			dbWrangler = new DbWrangler(act.getApplicationContext());
		}
		if (dbWrangler.isClosed()) {
			dbWrangler.open();
		}
	}

	public void onDestroy() {
		if (dbWrangler != null) {
			dbWrangler.close();
		}
	}

	public void setCharacter(long itemId) {
		currentCollection = itemId;
		refreshStarButtonState();
	}

	public void setProgressToRestore(float progressToRestore) {
		this.progressToRestore = progressToRestore;
	}

	public void contextMenu(ContextMenu menu, ContextMenuInfo menuInfo) {
		try {
			if (path.size() > 0) {
				menu.setHeaderTitle("Article Context");
				String start = "";
				String end = "";
				int reverse = 0;
				for (int i = path.size() - 1; i >= 0; i--) {
					if (i == 0) {
						end = " (current)";
					}
					if (path.get(i).get("url") != null) {
						menu.add(i, i, 0, start + path.get(i).get("name") + end);
						start += "\u2022 ";
						if (reverse < 2
								|| path.get(i).get("type").equals("list")) {
							menu.getItem(reverse).setEnabled(false);
						}
						reverse += 1;
					}
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "Back button failed");
		}
	}

	public boolean contextMenuSelected(android.view.MenuItem item) {
		if (path.size() > item.getGroupId()) {
			shouldOverrideUrlLoading(mWebView,
					path.get(item.getGroupId()).get("url"));
			return true;
		}
		return false;
	}
}
