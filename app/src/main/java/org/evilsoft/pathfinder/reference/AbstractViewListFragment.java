package org.evilsoft.pathfinder.reference;

import java.util.ArrayList;
import java.util.List;

import org.acra.ErrorReporter;
import org.evilsoft.pathfinder.reference.db.BookNotFoundException;
import org.evilsoft.pathfinder.reference.db.DbWrangler;
import org.evilsoft.pathfinder.reference.db.user.CollectionAdapter;
import org.evilsoft.pathfinder.reference.list.CollectionItemListAdapter;
import org.evilsoft.pathfinder.reference.list.CreatureListAdapter;
import org.evilsoft.pathfinder.reference.list.DefaultListAdapter;
import org.evilsoft.pathfinder.reference.list.FeatListAdapter;
import org.evilsoft.pathfinder.reference.list.NpcListAdapter;
import org.evilsoft.pathfinder.reference.list.SearchListAdapter;
import org.evilsoft.pathfinder.reference.list.SectionListAdapter;
import org.evilsoft.pathfinder.reference.list.SkillListAdapter;
import org.evilsoft.pathfinder.reference.list.SpellListAdapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;

public abstract class AbstractViewListFragment extends SherlockListFragment
		implements OnItemClickListener {
	private static final String TAG = "AbstractViewListFragment";
	protected DbWrangler dbWrangler;
	protected List<Cursor> cursorList = new ArrayList<Cursor>();
	protected String currentUrl;
	protected String currentType;
	protected BaseAdapter currentListAdapter;
	protected String startUrl;
	protected boolean empty = false;
	protected boolean thin = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(ArrayAdapter.createFromResource(getActivity()
				.getApplicationContext(), R.array.top_titles,
				R.layout.list_item));
		openDb();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		if (bundle != null) {
			if (bundle.containsKey("url")) {
				startUrl = bundle.getString("url");
			}
		}
		if (startUrl != null) {
			updateUrl(startUrl);
		}
	}

	private void openDb() {
		if (dbWrangler == null) {
			dbWrangler = new DbWrangler(this.getActivity()
					.getApplicationContext());
		}
		if (dbWrangler.isClosed()) {
			dbWrangler.open();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		for (Cursor curs : cursorList) {
			if (!curs.isClosed()) {
				curs.close();
			}
		}
		if (dbWrangler != null) {
			dbWrangler.close();
		}
	}

	@Override
	public abstract void onItemClick(AdapterView<?> parent, View view,
			int position, long id);

	public boolean checkUrlEqual(String newUrl) {
		if (currentUrl == null) {
			return false;
		}
		return newUrl.equals(currentUrl);
	}

	public void updateUrl(String newUrl) {
		currentType = null;
		if (newUrl == null || checkUrlEqual(newUrl)) {
			return;
		}
		Log.i(TAG, newUrl);
		ErrorReporter e = ErrorReporter.getInstance();
		e.putCustomData("LastSectionViewUrl", newUrl);
		this.getListView().setOnItemClickListener(this);
		this.getListView().setCacheColorHint(Color.WHITE);
		currentUrl = newUrl;
		String[] parts = newUrl.split("\\/");
		if (parts.length > 2 && "Search".equals(parts[2])) {
			if (parts.length == 4) {
				Cursor searchcurs = dbWrangler.getIndexDbAdapter()
						.getSearchAdapter().search(parts[3].trim());
				cursorList.add(searchcurs);
				currentListAdapter = new SearchListAdapter(getActivity()
						.getApplicationContext(), searchcurs);
				if (currentListAdapter.isEmpty()) {
					empty = true;
					ArrayList<String> list = new ArrayList<String>();
					list.add("(No Results)");
					currentListAdapter = new ArrayAdapter<String>(getActivity()
							.getApplicationContext(), R.layout.list_item, list);
				}
			}
		} else if (parts.length > 3 && "Bookmarks".equals(parts[3])) {
			currentType = "Bookmarks";
			if (parts.length > 3) {
				// I believe it's safe to test against the name because the
				// keyboard doesn't allow typing an ellipsis character
				if (parts[parts.length - 1]
						.equals(getString(R.string.add_collection))) {
					showNewCollectionDialog();
				} else if (parts[parts.length - 1]
						.equals(getString(R.string.del_collection))) {
					showDelCollectionDialog();
				} else {
					// We have a collection name and can search on it
					CollectionAdapter ca = new CollectionAdapter(
							dbWrangler.getUserDbAdapter());
					Cursor curs2 = ca
							.fetchCollectionValues(parts[parts.length - 1]);
					cursorList.add(curs2);
					currentListAdapter = new CollectionItemListAdapter(
							getActivity(), curs2);
				}
			}
		} else if (parts.length > 2 && "Menu".equals(parts[2])) {
			Log.i(TAG, parts[2]);
			String name = parts[3];
			String type = parts[4];
			String subtype = null;
			if (parts.length > 5) {
				subtype = parts[5];
			}
			getListAdapter(name, type, subtype);
		} else {
			Log.i(TAG, parts[2]);
			String source = parts[2];
			String name = parts[3];
			getContentListAdapter(source, name, currentUrl);
		}
		setListAdapter(currentListAdapter);
	}

	public void getContentListAdapter(String source, String name, String url) {
		currentType = name;
		try {
			Cursor curs = dbWrangler.getBookDbAdapterByName(source)
					.getSectionIndexGroupAdapter().fetchSectionByParentUrl(url);
			cursorList.add(curs);
			currentListAdapter = new SectionListAdapter(getActivity()
					.getApplicationContext(), curs);
		} catch (BookNotFoundException bnfe) {
			Log.e(TAG, "Book not found: " + bnfe.getMessage());
			ErrorReporter e = ErrorReporter.getInstance();
			ErrorReporter.getInstance().putCustomData("FailedURI", url);
			ErrorReporter.getInstance().handleException(bnfe);
			e.handleException(null);
		}
	}

	public void getListAdapter(String name, String type, String subtype) {
		currentType = name;
		if ("Feats".equals(name)) {
			Cursor curs = dbWrangler.getIndexDbAdapter().getIndexGroupAdapter()
					.fetchByFeatType(subtype);
			cursorList.add(curs);
			if (thin) {
				currentListAdapter = new DefaultListAdapter(getActivity()
						.getApplicationContext(), curs);

			} else {
				currentListAdapter = new FeatListAdapter(getActivity()
						.getApplicationContext(), curs, true);
			}
		} else if ("Creatures".equals(name) && "creature".equals(type)) {
			if ("npc".equals(subtype) || "mythic".equals(subtype)) {
				Cursor curs = dbWrangler.getIndexDbAdapter()
						.getIndexGroupAdapter().fetchByType(type, subtype);
				cursorList.add(curs);
				if (thin) {
					currentListAdapter = new DefaultListAdapter(getActivity()
							.getApplicationContext(), curs);

				} else {
					currentListAdapter = new NpcListAdapter(getActivity()
							.getApplicationContext(), curs, true);
				}
			} else {
				Cursor curs = dbWrangler.getIndexDbAdapter()
						.getIndexGroupAdapter().fetchByCreatureType(subtype);
				cursorList.add(curs);
				if (thin) {
					currentListAdapter = new DefaultListAdapter(getActivity()
							.getApplicationContext(), curs);
				} else {
					currentListAdapter = new CreatureListAdapter(getActivity()
							.getApplicationContext(), curs, true);
				}
			}
		} else if ("Skills".equals(name)) {
			Cursor curs = dbWrangler.getIndexDbAdapter().getIndexGroupAdapter()
					.fetchByType(type, subtype);
			cursorList.add(curs);
			if (thin) {
				currentListAdapter = new DefaultListAdapter(getActivity()
						.getApplicationContext(), curs);

			} else {
				currentListAdapter = new SkillListAdapter(getActivity()
						.getApplicationContext(), curs, true);
			}
		} else if ("Spells".equals(name)) {
			Cursor curs;
			if ("*".equals(type) && subtype != null) {
				curs = dbWrangler.getIndexDbAdapter().getIndexGroupAdapter()
						.fetchByType(type, subtype);
				thin = true;
			} else if (subtype != null) {
				curs = dbWrangler.getIndexDbAdapter().getIndexGroupAdapter()
						.fetchBySpellClass(subtype);
			} else {
				curs = dbWrangler.getIndexDbAdapter().getIndexGroupAdapter()
						.fetchByType(type, subtype);
			}
			cursorList.add(curs);
			if (thin) {
				currentListAdapter = new DefaultListAdapter(getActivity()
						.getApplicationContext(), curs);

			} else {
				currentListAdapter = new SpellListAdapter(getActivity()
						.getApplicationContext(), curs, true);
			}
		} else {
			Cursor curs = dbWrangler.getIndexDbAdapter().getIndexGroupAdapter()
					.fetchByType(type, subtype);
			cursorList.add(curs);
			currentListAdapter = new DefaultListAdapter(getActivity()
					.getApplicationContext(), curs);
		}
	}

	@SuppressLint("NewApi")
	private void showNewCollectionDialog() {
		AlertDialog.Builder alert = android.os.Build.VERSION.SDK_INT < 11 ? new AlertDialog.Builder(
				getActivity()) : new AlertDialog.Builder(getActivity(),
				AlertDialog.THEME_HOLO_DARK);

		final EditText edit = new EditText(this.getActivity()
				.getApplicationContext());
		edit.setSingleLine(true);

		alert.setTitle(R.string.collection_entry_title)
				.setMessage(R.string.collection_entry_text)
				.setView(edit)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						StringBuffer sb = new StringBuffer();
						sb.append("SectionViewFragment.showNewCollectionDialog.onClick: OK: which:");
						sb.append(which);
						ErrorReporter e = ErrorReporter.getInstance();
						e.putCustomData("LastClick", sb.toString());
						CollectionAdapter ca = new CollectionAdapter(dbWrangler
								.getUserDbAdapter());
						if (ca.addCollection(edit.getText().toString())) {
							Toast.makeText(getActivity(),
									R.string.collection_entry_success,
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getActivity(),
									R.string.collection_entry_failure,
									Toast.LENGTH_SHORT).show();
						}
						refreshCollection();
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								ErrorReporter e = ErrorReporter.getInstance();
								e.putCustomData("LastClick",
										"SectionViewFragment.showNewCollectionDialog.onClick: Cancel");
								refreshCollection();
							}
						}).show();
	}

	@SuppressLint("NewApi")
	private void showDelCollectionDialog() {
		CollectionAdapter ca = new CollectionAdapter(
				dbWrangler.getUserDbAdapter());
		Cursor curs = ca.fetchCollectionList();
		try {
			AlertDialog.Builder builder = android.os.Build.VERSION.SDK_INT < 11 ? new AlertDialog.Builder(
					getActivity()) : new AlertDialog.Builder(getActivity(),
					AlertDialog.THEME_HOLO_DARK);

			final ArrayList<String> characterList = new ArrayList<String>();
			boolean hasNext = curs.moveToFirst();
			while (hasNext) {
				characterList.add(curs.getString(1));
				hasNext = curs.moveToNext();
			}
			String[] items = characterList.toArray(new String[characterList
					.size()]);
			builder.setTitle(R.string.del_collection_entry_title);
			builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					CollectionAdapter ca = new CollectionAdapter(dbWrangler
							.getUserDbAdapter());
					if (ca.delCollection(characterList.get(which)) > 0) {
						Toast.makeText(getActivity(),
								R.string.del_collection_entry_success,
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getActivity(),
								R.string.del_collection_entry_failure,
								Toast.LENGTH_SHORT).show();
					}
					refreshCollection();
				}
			});
			builder.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							refreshCollection();
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
		} finally {
			curs.close();
		}
	}

	private void refreshCollection() {
		if (PathfinderOpenReferenceActivity.isTabletLayout(getActivity())) {
			SectionListFragment list = (SectionListFragment) getActivity()
					.getSupportFragmentManager().findFragmentById(
							R.id.section_list_fragment);
			list.refresh(dbWrangler);
		} else {
			Intent showContent = new Intent(getActivity()
					.getApplicationContext(),
					PathfinderOpenReferenceActivity.class);
			startActivity(showContent);
		}
	}
}
