package org.evilsoft.pathfinder.reference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.acra.ErrorReporter;
import org.evilsoft.pathfinder.reference.db.psrd.ClassAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.FeatAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.MonsterAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.RaceAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.RuleAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.SkillAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.SpellAdapter;
import org.evilsoft.pathfinder.reference.db.user.CollectionAdapter;
import org.evilsoft.pathfinder.reference.db.user.PsrdUserDbAdapter;
import org.evilsoft.pathfinder.reference.list.ClassListAdapter;
import org.evilsoft.pathfinder.reference.list.CollectionItemListAdapter;
import org.evilsoft.pathfinder.reference.list.FeatListAdapter;
import org.evilsoft.pathfinder.reference.list.MonsterListAdapter;
import org.evilsoft.pathfinder.reference.list.RaceListAdapter;
import org.evilsoft.pathfinder.reference.list.RuleListAdapter;
import org.evilsoft.pathfinder.reference.list.SkillListAdapter;
import org.evilsoft.pathfinder.reference.list.SpellListAdapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;

public class SectionViewFragment extends SherlockListFragment implements
		OnItemClickListener {
	private static final String TAG = "SectionViewFragment";
	private PsrdDbAdapter dbAdapter;
	private PsrdUserDbAdapter userDbAdapter;
	private List<Cursor> cursorList = new ArrayList<Cursor>();
	private String currentUrl;
	private String currentType;
	private String subtype;
	private BaseAdapter currentListAdapter;
	private String startUrl;
	private boolean empty = false;

	public SectionViewFragment() {
		super();
	}

	public SectionViewFragment(String startUrl) {
		super();
		this.startUrl = startUrl;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(ArrayAdapter.createFromResource(getActivity()
				.getApplicationContext(), R.array.top_titles,
				R.layout.list_item));
		openDb();
	}

	public void onViewCreated(View view, Bundle savedInstanceState) {
		if (this.startUrl != null) {
			updateUrl(startUrl);
		}
	}

	private void openDb() {
		if (userDbAdapter == null) {
			userDbAdapter = new PsrdUserDbAdapter(this.getActivity()
					.getApplicationContext());
		}
		if (userDbAdapter.isClosed()) {
			userDbAdapter.open();
		}
		if (dbAdapter == null) {
			dbAdapter = new PsrdDbAdapter(this.getActivity()
					.getApplicationContext());
		}
		if (dbAdapter.isClosed()) {
			dbAdapter.open();
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
		if (dbAdapter != null) {
			dbAdapter.close();
		}
		if (userDbAdapter != null) {
			userDbAdapter.close();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		StringBuffer sb = new StringBuffer();
		sb.append("SectionViewFragment.onItemClick: position:");
		sb.append(position);
		sb.append(", id:");
		sb.append(id);
		ErrorReporter e = ErrorReporter.getInstance();
		e.putCustomData("LastClick", sb.toString());
		if (empty) {
			return;
		}
		Intent showContent = new Intent(getActivity().getApplicationContext(),
				DetailsActivity.class);
		String uri = getNextUrl(id);
		if (subtype != null) {
			uri = uri + "?subtype=" + subtype;
		}
		Log.d(TAG, uri);
		showContent.setData(Uri.parse(uri));
		startActivity(showContent);
	}

	private String getNextUrl(Long id) {
		String uri = null;
		if ("Bookmarks".equals(currentType)) {
			CollectionAdapter ca = new CollectionAdapter(userDbAdapter);
			Cursor curs = ca.fetchCollectionValue(id.toString());
			boolean has_next = curs.moveToNext();
			if (has_next) {
				uri = curs.getString(2);
			}
		} else {
			Cursor curs = dbAdapter.fetchSection(id.toString());
			try {
				boolean has_next = curs.moveToNext();
				if (has_next) {
					uri = curs.getString(3);
				}
			} finally {
				curs.close();
			}
		}
		if (uri == null) {
			uri = currentUrl + "/" + id;
		}
		return uri;
	}

	public void updateUrl(String newUrl) {
		currentType = null;
		Log.i(TAG, newUrl);
		ErrorReporter e = ErrorReporter.getInstance();
		e.putCustomData("LastSectionViewUrl", newUrl);
		this.getListView().setOnItemClickListener(this);
		this.getListView().setCacheColorHint(Color.WHITE);
		currentUrl = newUrl;
		Uri uri = Uri.parse(currentUrl);
		subtype = uri.getQueryParameter("subtype");
		String localUrl = currentUrl;
		if(currentUrl.indexOf("?") > -1) {
			localUrl = TextUtils.split(currentUrl, "\\?")[0];
		}
		Cursor curs = dbAdapter.fetchSectionByUrl(localUrl);
		try {
			boolean has_item = curs.moveToFirst();
			String[] parts = newUrl.split("\\/");
			if (has_item) {
				String name = curs.getString(2);
				String type = curs.getString(3);
				getListAdapter(name, type, subtype);
			} else if (parts[2].equals("Bookmarks")) {
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
						CollectionAdapter ca = new CollectionAdapter(userDbAdapter);
						Cursor curs2 = ca
								.fetchCollectionValues(parts[parts.length - 1]);
						cursorList.add(curs2);
						currentListAdapter = new CollectionItemListAdapter(
								getActivity(), curs2);
					}
				}
			} else {
				empty = false;
			}
			setListAdapter(currentListAdapter);
		} finally {
			curs.close();
		}
	}

	public void getListAdapter(String name, String type, String subtype) {
		ArrayList<HashMap<String, String>> path = dbAdapter.getPathByUrl(currentUrl);
		if (path != null && path.size() > 1 && path.get(path.size() - 2).get("name").startsWith("Rules")) {
			currentType = "Rules";
			RuleAdapter ra = new RuleAdapter(dbAdapter);
			Cursor curs = ra.fetchRuleListByUrl(currentUrl);
			cursorList.add(curs);
			currentListAdapter = new RuleListAdapter(getActivity()
					.getApplicationContext(), curs);
		} else if (name.equals("Classes")) {
			currentType = "Classes";
			ClassAdapter ca = new ClassAdapter(dbAdapter);
			Cursor curs = ca.fetchClassList(subtype);
			cursorList.add(curs);
			currentListAdapter = new ClassListAdapter(getActivity()
					.getApplicationContext(), curs);
		} else if (name.equals("Feats")) {
			currentType = "Feats";
			FeatAdapter fa = new FeatAdapter(dbAdapter);
			Cursor curs;
			if (subtype != null) {
				curs = fa.fetchFeatList(subtype);
			} else {
				curs = fa.fetchFeatList();
			}
			cursorList.add(curs);
			currentListAdapter = new FeatListAdapter(getActivity()
					.getApplicationContext(), curs, true);
		} else if (name.equals("Monsters")) {
			currentType = "Monsters";
			MonsterAdapter ma = new MonsterAdapter(dbAdapter);
			Cursor curs;
			if (subtype != null) {
				curs = ma.fetchMonstersByType(subtype);
			} else {
				curs = ma.fetchMonsterList();
			}
			cursorList.add(curs);
			currentListAdapter = new MonsterListAdapter(getActivity()
					.getApplicationContext(), curs, true);
		} else if (name.equals("Races")) {
			currentType = "Races";
			RaceAdapter ra = new RaceAdapter(dbAdapter);
			Cursor curs = ra.fetchRaceList(subtype);
			cursorList.add(curs);
			currentListAdapter = new RaceListAdapter(getActivity()
					.getApplicationContext(), curs);
		} else if (name.equals("Skills")) {
			currentType = "Skills";
			SkillAdapter sa = new SkillAdapter(dbAdapter);
			Cursor curs = sa.fetchSkillList();
			cursorList.add(curs);
			currentListAdapter = new SkillListAdapter(getActivity()
					.getApplicationContext(), curs, true);
		} else if (name.equals("Spells")) {
			currentType = "Spells";
			SpellAdapter sa = new SpellAdapter(dbAdapter);
			Cursor curs;
			if (subtype != null) {
				curs = sa.fetchSpellList(subtype);
			} else {
				curs = sa.fetchSpellList();
			}
			cursorList.add(curs);
			currentListAdapter = new SpellListAdapter(getActivity()
					.getApplicationContext(), curs, true);
		}
	}

	@SuppressLint("NewApi")
	private void showNewCollectionDialog() {
		AlertDialog.Builder alert =
				android.os.Build.VERSION.SDK_INT < 11 ?
						new AlertDialog.Builder(getActivity()) :
						new AlertDialog.Builder(getActivity(),
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
						CollectionAdapter ca = new CollectionAdapter(
								userDbAdapter);
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
		CollectionAdapter ca = new CollectionAdapter(userDbAdapter);
		Cursor curs = ca.fetchCollectionList();
		try {
			AlertDialog.Builder builder =
					android.os.Build.VERSION.SDK_INT < 11 ?
							new AlertDialog.Builder(getActivity()) :
							new AlertDialog.Builder(getActivity(),
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
					CollectionAdapter ca = new CollectionAdapter(userDbAdapter);
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
			list.refresh(dbAdapter, userDbAdapter);
		} else {
			Intent showContent = new Intent(getActivity()
					.getApplicationContext(),
					PathfinderOpenReferenceActivity.class);
			startActivity(showContent);
		}
	}
}
