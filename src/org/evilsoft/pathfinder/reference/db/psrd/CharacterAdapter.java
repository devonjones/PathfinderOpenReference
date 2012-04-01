package org.evilsoft.pathfinder.reference.db.psrd;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import org.evilsoft.pathfinder.reference.db.user.PsrdUserDbAdapter;

public class CharacterAdapter {
	private PsrdUserDbAdapter userDbAdapter;

	public CharacterAdapter(PsrdUserDbAdapter userDbAdapter) {
		this.userDbAdapter = userDbAdapter;
	}

	public ArrayList<HashMap<String, Object>> createCharacterList() {
		ArrayList<HashMap<String, Object>> charList = new ArrayList<HashMap<String, Object>>();
		Cursor curs = userDbAdapter.fetchCharacterList();
		HashMap<String, Object> child;

		boolean hasNext = curs.moveToFirst();
		while (hasNext) {
			child = new HashMap<String, Object>();
			child.put("id", curs.getString(0));
			child.put("specificName", curs.getString(1));
			Log.e(child.get("id").toString(), child.get("specificName").toString());
			charList.add(child);
			hasNext = curs.moveToNext();
		}
		return charList;
	}

	public Cursor fetchCharacterEntries(String characterId) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT collection_entries.*");
		sql.append(" FROM collection_entries");
		sql.append("  INNER JOIN collections ON collections.collection_id = collection_entries.collection_id");
		sql.append(" WHERE collections.name = ?");
		// assume that DB is open because it's coming from SectionViewFragment
		return userDbAdapter.database.rawQuery(sql.toString(), new String[] { characterId });
	}

	public static void toggleEntryStar(Context context, long characterId, ArrayList<HashMap<String, String>> path, String title, String url) {
		if (CharacterAdapter.entryIsStarred(context, characterId, path, title)) {
			CharacterAdapter.unstar(context, characterId, path, title, url);
		} else {
			CharacterAdapter.star(context, characterId, path, title, url);
		}
	}

	public static boolean entryIsStarred(Context context, long characterId, ArrayList<HashMap<String, String>> path, String title) {
		PsrdUserDbAdapter userDbAdapter = new PsrdUserDbAdapter(context);

		try {
			userDbAdapter.open();

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT 1 FROM collection_entries");
			sql.append(" WHERE collection_id = ?");
			sql.append("   AND section_id = ?");
			sql.append("   AND name = ?");

			Cursor curs = userDbAdapter.database.rawQuery(sql.toString(), new String[] { Long.toString(characterId),
				path.get(0).get("id"), title });

			return curs.moveToFirst();
		} finally {
			userDbAdapter.close();
		}
	}

	private static void star(Context context, long characterId, ArrayList<HashMap<String, String>> path, String title, String url) {
		PsrdUserDbAdapter userDbAdapter = new PsrdUserDbAdapter(context);

		try {
			userDbAdapter.open();
			userDbAdapter.star(characterId, path.get(0).get("id"), title, url);
		} finally {
			userDbAdapter.close();
		}
	}

	private static void unstar(Context context, long characterId, ArrayList<HashMap<String, String>> path, String title, String url) {
		PsrdUserDbAdapter userDbAdapter = new PsrdUserDbAdapter(context);

		try {
			userDbAdapter.open();
			userDbAdapter.unstar(characterId, path.get(0).get("id"), title, url);
		} finally {
			userDbAdapter.close();
		}
	}

}
