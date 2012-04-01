package org.evilsoft.pathfinder.reference;

import java.util.ArrayList;

import org.evilsoft.pathfinder.reference.db.psrd.CharacterAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.ClassAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.FeatAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.MonsterAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.RaceAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.RuleAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.SkillAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.SpellAdapter;
import org.evilsoft.pathfinder.reference.db.user.PsrdUserDbAdapter;
import org.evilsoft.pathfinder.reference.list.CharacterListAdapter;
import org.evilsoft.pathfinder.reference.list.CharacterListItem;
import org.evilsoft.pathfinder.reference.list.ClassListAdapter;
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
	private String currentUrl;
	private BaseAdapter currentListAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.top_titles,
				R.layout.list_item));
		openDb();
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
		if (dbAdapter != null) {
			dbAdapter.close();
		}
		if (userDbAdapter != null) {
			userDbAdapter.close();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent showContent = new Intent(getActivity().getApplicationContext(), DetailsActivity.class);
		String uri = currentUrl + "/" + currentListAdapter.getItemId(position);
		DisplayListAdapter a = (DisplayListAdapter) parent.getAdapter();
		if (a.getClass().equals(CharacterListAdapter.class)) {
			CharacterListItem item = (CharacterListItem) a.getItem(position);
			uri = item.getUrl();
			showContent.putExtra("currentCharacter", item.getCharacterId());
		}
		Log.e(TAG, uri);
		showContent.setData(Uri.parse(uri));
		startActivity(showContent);
	}

	public void updateUrl(String newUrl) {
		Log.e(TAG, newUrl);
		this.getListView().setOnItemClickListener(this);
		this.getListView().setCacheColorHint(Color.WHITE);
		currentUrl = newUrl;
		String[] parts = newUrl.split("\\/");
		if (parts[2].equals("Classes")) {
			ClassAdapter ca = new ClassAdapter(dbAdapter);
			String id = parts[parts.length - 1];
			Cursor curs = ca.fetchClassList(id);
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
				currentListAdapter = new FeatListAdapter(getActivity().getApplicationContext(), curs, true);
			}
		} else if (parts[2].equals("Races")) {
			RaceAdapter ra = new RaceAdapter(dbAdapter);
			Cursor curs = ra.fetchRaceList();
			currentListAdapter = new RaceListAdapter(getActivity().getApplicationContext(), curs);
		} else if (parts[2].startsWith("Rules")) {
			if (parts.length > 4) {
				RuleAdapter ra = new RuleAdapter(dbAdapter);
				String ruleId = parts[parts.length - 1];
				Cursor curs = ra.fetchRuleList(ruleId);
				currentListAdapter = new RuleListAdapter(getActivity().getApplicationContext(), curs);
			}
		} else if (parts[2].equals("Skills")) {
			SkillAdapter sa = new SkillAdapter(dbAdapter);
			Cursor curs = sa.fetchSkillList();
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
					CharacterAdapter ca = new CharacterAdapter(userDbAdapter);
					Cursor curs = ca.fetchCharacterEntries(parts[parts.length - 1]);
					currentListAdapter = new CharacterListAdapter(getActivity(), curs, parts[3]);
				}
			}
		} else {
			ArrayList<String> list = new ArrayList<String>();
			for (int i = 0; i < 6; i++) {
				list.add(newUrl);
			}
			currentListAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.list_item,
					list);
		}
		setListAdapter(currentListAdapter);
	}

	private void showNewCollectionDialog() {
		AlertDialog.Builder alert =
				android.os.Build.VERSION.SDK_INT < 11 ?
						new AlertDialog.Builder(getActivity()) :
							new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);

		final EditText edit = new EditText(this.getActivity().getApplicationContext());
		edit.setSingleLine(true);

		alert.setTitle(R.string.collection_entry_title)
			.setMessage(R.string.collection_entry_text)
			.setView(edit)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if (userDbAdapter.addCollection(edit.getText().toString())) {
						Toast.makeText(getActivity(), R.string.collection_entry_success, Toast.LENGTH_SHORT).show();
						SectionListFragment list = (SectionListFragment) getActivity().getSupportFragmentManager().findFragmentById(
								R.id.section_list_fragment);
						list.refresh(dbAdapter, userDbAdapter);
					} else {
						Toast.makeText(getActivity(), R.string.collection_entry_failure, Toast.LENGTH_SHORT).show();
					}
				}
			}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {}
			}).show();
	}

	private void showDelCollectionDialog() {
		AlertDialog.Builder builder =
				android.os.Build.VERSION.SDK_INT < 11 ?
						new AlertDialog.Builder(getActivity()) :
							new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);

		Cursor curs = userDbAdapter.fetchCharacterList();
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
				if (userDbAdapter.delCollection(characterList.get(which)) > 0) {
					Toast.makeText(getActivity(), R.string.del_collection_entry_success, Toast.LENGTH_SHORT).show();
					SectionListFragment list = (SectionListFragment) getActivity().getSupportFragmentManager().findFragmentById(
							R.id.section_list_fragment);
					list.refresh(dbAdapter, userDbAdapter);
				} else {
					Toast.makeText(getActivity(), R.string.del_collection_entry_failure, Toast.LENGTH_SHORT).show();
				}
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
}
