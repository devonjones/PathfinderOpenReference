package org.evilsoft.pathfinder.reference;

import java.util.ArrayList;
import java.util.List;

import org.acra.ErrorReporter;
import org.evilsoft.pathfinder.reference.db.DbWrangler;
import org.evilsoft.pathfinder.reference.db.index.IndexGroupAdapter;
import org.evilsoft.pathfinder.reference.list.BaseListItem;
import org.evilsoft.pathfinder.reference.list.CreatureListAdapter;
import org.evilsoft.pathfinder.reference.list.DefaultListAdapter;
import org.evilsoft.pathfinder.reference.list.FeatListAdapter;
import org.evilsoft.pathfinder.reference.list.SearchListAdapter;
import org.evilsoft.pathfinder.reference.list.SectionListAdapter;
import org.evilsoft.pathfinder.reference.list.SkillListAdapter;
import org.evilsoft.pathfinder.reference.list.SpellListAdapter;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import com.actionbarsherlock.app.SherlockListFragment;

public class DetailsListFragment extends SherlockListFragment implements OnItemClickListener {
	private static final String TAG = "DetailsListFragment";
	private DbWrangler dbWrangler;
	private List<Cursor> cursorList = new ArrayList<Cursor>();
	private String currentUrl;
	private String subtype;
	private BaseAdapter currentListAdapter;
	private boolean empty = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.top_titles,
				R.layout.list_item));
		openDb();
	}

	private void openDb() {
		if (dbWrangler == null) {
			dbWrangler = new DbWrangler(this.getActivity().getApplicationContext());
		}
		if (dbWrangler.isClosed()) {
			dbWrangler.open();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		for (Cursor curs : cursorList) {
			if(!curs.isClosed()) {
				curs.close();
			}
		}
		if (dbWrangler != null) {
			dbWrangler.close();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		StringBuffer sb = new StringBuffer();
		sb.append("DetailsListFragment.onItemClick: position:");
		sb.append(position);
		sb.append(", id:");
		sb.append(id);
		ErrorReporter e = ErrorReporter.getInstance();
		e.putCustomData("LastClick", sb.toString());
		if(empty) {
			return;
		}
		String uri = getNextUrl(position);
		Log.d(TAG, uri);
		if(PathfinderOpenReferenceActivity.isTabletLayout(getActivity())) {
			DetailsViewFragment viewer = (DetailsViewFragment) this.getActivity().getSupportFragmentManager()
				.findFragmentById(R.id.details_view_fragment);
			viewer.updateUrl(uri);
		} else {
			Intent showContent = new Intent(this.getActivity().getApplicationContext(), DetailsActivity.class);

			showContent.setData(Uri.parse(uri));
			startActivity(showContent);
		}
	}

	private String getNextUrl(Integer position) {
		BaseListItem item = (BaseListItem)currentListAdapter.getItem(position);
		return item.getUrl();
	}

	public boolean checkUrlEqual(String newUrl) {
		if(currentUrl == null) {
			return false;
		}
		if(newUrl.indexOf("?") > -1) {
			newUrl = TextUtils.split(newUrl, "\\?")[0];
		}
		String localUrl = currentUrl;
		if(localUrl.indexOf("?") > -1) {
			localUrl = TextUtils.split(localUrl, "\\?")[0];
		}
		return newUrl.equals(localUrl);
	}
	
	public void updateUrl(String newUrl) {
		ErrorReporter e = ErrorReporter.getInstance();
		e.putCustomData("LastDetailsListUrl", newUrl);
		this.getListView().setOnItemClickListener(this);
		this.getListView().setCacheColorHint(Color.WHITE);
		if (newUrl == null || checkUrlEqual(newUrl)) {
			return;
		}
		currentUrl = newUrl;
		
		Cursor cursor = dbWrangler.getIndexDbAdapter().getIndexGroupAdapter().fetchByUrl(currentUrl);
		try {
			boolean has_item = cursor.moveToFirst();
			String[] parts = newUrl.split("\\/");
			if (has_item) {
				String name = IndexGroupAdapter.IndexGroupUtils.getName(cursor);
				String type = IndexGroupAdapter.IndexGroupUtils.getType(cursor);
				getListAdapter(name, type, subtype);
			}
			else if (parts[2].equals("Search")) {
				if (parts.length == 4) {
					Cursor searchcurs = dbWrangler.getIndexDbAdapter().getSearchAdapter().search(parts[3]);
					cursorList.add(searchcurs);
					currentListAdapter = new SearchListAdapter(getActivity().getApplicationContext(), searchcurs);
					if (currentListAdapter.isEmpty()) {
						empty = true;
						ArrayList<String> list = new ArrayList<String>();
						list.add("(No Results)");
						currentListAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.list_item,
							list);
					}
				}
			} else {
				empty = false;
			}
			setListAdapter(currentListAdapter);
		} finally {
			cursor.close();
		}
	}

	public void getContentListAdapter(String source, String name, String url) {
		Cursor curs = dbWrangler.getIndexDbAdapter().getBooksAdapter().fetchBook(source);
		curs = dbWrangler.getBookDbAdapterByName(source).getSectionAdapter().fetchSectionByParentUrl(url);
		cursorList.add(curs);
		currentListAdapter = new SectionListAdapter(getActivity()
				.getApplicationContext(), curs);
	}

	public void getListAdapter(String name, String type, String subtype) {
		if (name.equals("Feats")) {
			Cursor curs = dbWrangler.getIndexDbAdapter().getIndexGroupAdapter().fetchByFeatType(subtype);
			cursorList.add(curs);
			currentListAdapter = new FeatListAdapter(getActivity()
					.getApplicationContext(), curs, true);
		} else if (name.equals("Creatures")) {
			Cursor curs = dbWrangler.getIndexDbAdapter().getIndexGroupAdapter().fetchByCreatureType(subtype);
			cursorList.add(curs);
			currentListAdapter = new CreatureListAdapter(getActivity()
					.getApplicationContext(), curs, true);
		} else if (name.equals("Skills")) {
			Cursor curs = dbWrangler.getIndexDbAdapter().getIndexGroupAdapter().fetchByType(type, subtype);
			cursorList.add(curs);
			currentListAdapter = new SkillListAdapter(getActivity()
					.getApplicationContext(), curs, true);
		} else if (name.equals("Spells")) {
			Cursor curs;
			if(subtype != null) {
				curs = dbWrangler.getIndexDbAdapter().getIndexGroupAdapter().fetchBySpellClass(subtype);
			} else {
				curs = dbWrangler.getIndexDbAdapter().getIndexGroupAdapter().fetchByType(type, subtype);
			}
			cursorList.add(curs);
			currentListAdapter = new SpellListAdapter(getActivity()
					.getApplicationContext(), curs, true);
		} else {
			Cursor curs = dbWrangler.getIndexDbAdapter().getIndexGroupAdapter().fetchByType(type, subtype);
			cursorList.add(curs);
			currentListAdapter = new DefaultListAdapter(getActivity()
					.getApplicationContext(), curs);
		}
	}
}
