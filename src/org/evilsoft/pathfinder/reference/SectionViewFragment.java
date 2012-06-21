package org.evilsoft.pathfinder.reference;

import java.util.ArrayList;
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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
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

public class SectionViewFragment extends SherlockListFragment implements OnItemClickListener {
	private static final String TAG = "SectionViewFragment";
	private PsrdDbAdapter dbAdapter;
	private PsrdUserDbAdapter userDbAdapter;
	private List<Cursor> cursorList = new ArrayList<Cursor>();
	private String currentUrl;
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
		setListAdapter(ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.top_titles,
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
			userDbAdapter = new PsrdUserDbAdapter(this.getActivity().getApplicationContext());
		}
		if (userDbAdapter.isClosed()) {
			userDbAdapter.open();
		}
		if (dbAdapter == null) {
			dbAdapter = new PsrdDbAdapter(this.getActivity().getApplicationContext());
		}
		if (dbAdapter.isClosed()) {
			dbAdapter.open();
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
		if (dbAdapter != null) {
			dbAdapter.close();
		}
		if (userDbAdapter != null) {
			userDbAdapter.close();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		StringBuffer sb = new StringBuffer();
		sb.append("SectionViewFragment.onItemClick: position:");
		sb.append(position);
		sb.append(", id:");
		sb.append(id);
		ErrorReporter e = ErrorReporter.getInstance();
		e.putCustomData("LastClick", sb.toString());
		if(empty) {
			return;
		}
		Intent showContent = new Intent(getActivity().getApplicationContext(), DetailsActivity.class);
		String uri = currentUrl + "/" + currentListAdapter.getItemId(position);
		Log.d(TAG, uri);
		showContent.setData(Uri.parse(uri));
		startActivity(showContent);
	}

	public void updateUrl(String newUrl) {
		Log.i(TAG, newUrl);
		ErrorReporter e = ErrorReporter.getInstance();
		e.putCustomData("LastSectionViewUrl", newUrl);
		this.getListView().setOnItemClickListener(this);
		this.getListView().setCacheColorHint(Color.WHITE);
		currentUrl = newUrl;
		String[] parts = newUrl.split("\\/");
		if (parts[2].equals("Classes")) {
			ClassAdapter ca = new ClassAdapter(dbAdapter);
			String id = parts[parts.length - 1];
			Cursor curs = ca.fetchClassList(id);
			cursorList.add(curs);
			currentListAdapter = new ClassListAdapter(getActivity().getApplicationContext(), curs);
		} else if (parts[2].equals("Feats")) {
			if (parts.length > 4) {
				FeatAdapter fa = new FeatAdapter(dbAdapter);
				String featType = parts[parts.length - 1];
				Cursor curs;
				if (featType.equals("All Feats")) {
					curs = fa.fetchFeatList();
				} else {
					curs = fa.fetchFeatList(featType);
				}
				cursorList.add(curs);
				currentListAdapter = new FeatListAdapter(getActivity().getApplicationContext(), curs, true);
			}
		} else if (parts[2].equals("Races")) {
			RaceAdapter ra = new RaceAdapter(dbAdapter);
			Cursor curs = ra.fetchRaceList();
			cursorList.add(curs);
			currentListAdapter = new RaceListAdapter(getActivity().getApplicationContext(), curs);
		} else if (parts[2].startsWith("Rules")) {
			if (parts.length > 4) {
				RuleAdapter ra = new RuleAdapter(dbAdapter);
				String ruleId = parts[parts.length - 1];
				Cursor curs = ra.fetchRuleList(ruleId);
				cursorList.add(curs);
				currentListAdapter = new RuleListAdapter(getActivity().getApplicationContext(), curs);
			}
		} else if (parts[2].equals("Skills")) {
			SkillAdapter sa = new SkillAdapter(dbAdapter);
			Cursor curs = sa.fetchSkillList();
			cursorList.add(curs);
			currentListAdapter = new SkillListAdapter(getActivity().getApplicationContext(), curs, true);
		} else if (parts[2].equals("Spells")) {
			SpellAdapter sa = new SpellAdapter(dbAdapter);
			String spellClass = parts[parts.length - 1];
			Cursor curs;
			if (spellClass.equals("All")) {
				curs = sa.fetchSpellList();
			} else {
				curs = sa.fetchSpellList(spellClass);
			}
			cursorList.add(curs);
			currentListAdapter = new SpellListAdapter(getActivity().getApplicationContext(), curs, true);
		} else if (parts[2].equals("Monsters")) {
			if (parts.length > 4) {
				MonsterAdapter ma = new MonsterAdapter(dbAdapter);
				String monsterId = parts[parts.length - 1];
				Cursor curs;
				if (monsterId.equals("All Monsters")) {
					monsterId = parts[parts.length - 2];
					curs = ma.fetchMonsterList(monsterId);
				} else {
					curs = ma.fetchMonstersByType(monsterId);
				}
				cursorList.add(curs);
				currentListAdapter = new MonsterListAdapter(getActivity().getApplicationContext(), curs, true);
			}
		} else if (parts[2].equals("Bookmarks")) {
			if (parts.length > 4) {
				// I believe it's safe to test against the name because the keyboard doesn't allow typing an ellipsis character
				if (parts[parts.length - 1].equals(getString(R.string.add_collection))) {
					showNewCollectionDialog();
				} else if (parts[parts.length - 1].equals(getString(R.string.del_collection))) {
					showDelCollectionDialog();
				} else {
					// We have a collection name and can search on it
					CollectionAdapter ca = new CollectionAdapter(userDbAdapter);
					Cursor curs = ca.fetchCollectionEntries(parts[parts.length - 1]);
					cursorList.add(curs);
					currentListAdapter = new CollectionItemListAdapter(getActivity(), curs);
				}
			}
		} else {
			ArrayList<String> list = new ArrayList<String>();
			list.add(newUrl);
			currentListAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.list_item,
					list);
		}
		if (currentListAdapter != null && currentListAdapter.isEmpty()) {
			empty = true;
			ArrayList<String> list = new ArrayList<String>();
			list.add("(Empty List)");
			currentListAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.list_item,
					list);
		}
		else {
			empty = false;
		}
		setListAdapter(currentListAdapter);
	}

	private void showNewCollectionDialog() {
		AlertDialog.Builder alert =
				android.os.Build.VERSION.SDK_INT < 11 ?
						new AlertDialog.Builder(getActivity()) :
							new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_DARK);

		final EditText edit = new EditText(this.getActivity().getApplicationContext());
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
					CollectionAdapter ca = new CollectionAdapter(userDbAdapter);
					if (ca.addCollection(edit.getText().toString())) {
						Toast.makeText(getActivity(), R.string.collection_entry_success, Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getActivity(), R.string.collection_entry_failure, Toast.LENGTH_SHORT).show();
					}
					refreshCollection();
				}
			}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					ErrorReporter e = ErrorReporter.getInstance();
					e.putCustomData("LastClick", "SectionViewFragment.showNewCollectionDialog.onClick: Cancel");
					refreshCollection();
				}
			}).show();
	}

	private void showDelCollectionDialog() {
		CollectionAdapter ca = new CollectionAdapter(userDbAdapter);
		Cursor curs = ca.fetchCollectionList();
		try {
			AlertDialog.Builder builder =
					android.os.Build.VERSION.SDK_INT < 11 ?
							new AlertDialog.Builder(getActivity()) :
								new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_DARK);

			final ArrayList<String> characterList = new ArrayList<String>();
			boolean hasNext = curs.moveToFirst();
			while (hasNext) {
				characterList.add(curs.getString(1));
				hasNext = curs.moveToNext();
			}
			String[] items = characterList.toArray(new String[characterList.size()]);
			builder.setTitle(R.string.del_collection_entry_title);
			builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					CollectionAdapter ca = new CollectionAdapter(userDbAdapter);
					if (ca.delCollection(characterList.get(which)) > 0) {
						Toast.makeText(getActivity(), R.string.del_collection_entry_success, Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getActivity(), R.string.del_collection_entry_failure, Toast.LENGTH_SHORT).show();
					}
					refreshCollection();
				}
			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
		if(PathfinderOpenReferenceActivity.isTabletLayout(getActivity())) {
			SectionListFragment list = (SectionListFragment) getActivity().getSupportFragmentManager().findFragmentById(
					R.id.section_list_fragment);
			list.refresh(dbAdapter, userDbAdapter);
		} else {
			Intent showContent = new Intent(getActivity().getApplicationContext(), PathfinderOpenReferenceActivity.class);
			startActivity(showContent);
		}
	}
}
