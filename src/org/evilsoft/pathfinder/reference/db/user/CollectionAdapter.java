package org.evilsoft.pathfinder.reference.db.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

public class CollectionAdapter {
	private PsrdUserDbAdapter userDbAdapter;

	public CollectionAdapter(PsrdUserDbAdapter userDbAdapter) {
		this.userDbAdapter = userDbAdapter;
	}

	public ArrayList<HashMap<String, Object>> createCharacterList() {
		ArrayList<HashMap<String, Object>> charList = new ArrayList<HashMap<String, Object>>();
		Cursor curs = fetchCollectionList();
		HashMap<String, Object> child;

		boolean hasNext = curs.moveToFirst();
		while (hasNext) {
			child = new HashMap<String, Object>();
			child.put("id", curs.getString(0));
			child.put("specificName", curs.getString(1));
			charList.add(child);
			hasNext = curs.moveToNext();
		}
		return charList;
	}

	public Integer fetchFirstCollectionId() {
		Cursor c = fetchFirstCollection();
		c.moveToFirst();
		return c.getInt(0);
	}

	public Boolean collectionExists(String collectionId) {
		Cursor c = fetchCollection(collectionId);
		if (c.getCount() > 0) {
			return true;
		}
		return false;
	}

	public boolean addCollection(String name) {
		Integer colId = selectCollectionId(name);
		if (colId != null) {
			return false;
		}
		ContentValues cv = new ContentValues();
		cv.put("name", name);
		return userDbAdapter.database.insert("collections", null, cv) > -1;
	}

	public int delCollection(String name) {
		List<String> args = new ArrayList<String>();
		Integer colId = selectCollectionId(name);
		args.add(colId.toString());
		userDbAdapter.database.delete("collection_entries", "collection_id = ?", PsrdDbAdapter.toStringArray(args));
		args = new ArrayList<String>();
		args.add(name);
		return userDbAdapter.database.delete("collections", "name = ?", PsrdDbAdapter.toStringArray(args));
	}

	public Integer selectCollectionId(String name) {
		List<String> args = new ArrayList<String>();
		args.add(name);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT collection_id");
		sb.append(" FROM collections");
		sb.append(" WHERE name = ?");
		String sql = sb.toString();
		Cursor c = userDbAdapter.database.rawQuery(sql, PsrdDbAdapter.toStringArray(args));
		try {
			c.moveToFirst();
			if (c.getCount() < 1) {
				return null;
			}
			return c.getInt(0);
		} finally {
			c.close();
		}
	}

	public void toggleEntryStar(long characterId, ArrayList<HashMap<String, String>> path, String title, String url) {
		if (entryIsStarred(characterId, path, title)) {
			unstar(characterId, path.get(0).get("id"), title, url);
		} else {
			star(characterId, path.get(0).get("id"), title, url);
		}
	}
	
	public boolean entryIsStarred(long characterId, ArrayList<HashMap<String, String>> path, String title) {
		List<String> args = new ArrayList<String>();
		args.add(Long.toString(characterId));
		args.add(path.get(0).get("id"));
		args.add(title);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT 1 FROM collection_entries");
		sql.append(" WHERE collection_id = ?");
		sql.append("   AND section_id = ?");
		sql.append("   AND name = ?");
		Cursor curs = userDbAdapter.database.rawQuery(sql.toString(), PsrdDbAdapter.toStringArray(args));
		return curs.moveToFirst();
	}

	public void star(long characterId, String sectionId, String name, String url) throws SQLException {
		Log.i("starring",
				String.format("character_id = %s, section_id = %s, name = %s", Long.toString(characterId), sectionId, name));
		ContentValues cv = new ContentValues();
		cv.put("collection_id", characterId);
		cv.put("section_id", sectionId);
		cv.put("name", name);
		cv.put("path", url);
		userDbAdapter.database.insertOrThrow("collection_entries", null, cv);
	}

	public void unstar(long characterId, String sectionId, String name, String url) throws SQLException {
		Log.i("unstarring",
				String.format("character_id = %s, section_id = %s, name = %s", Long.toString(characterId), sectionId, name));
		List<String> args = new ArrayList<String>();
		args.add(Long.toString(characterId));
		args.add(sectionId);
		args.add(name);
		int result = userDbAdapter.database.delete(
				"collection_entries", "collection_id = ? AND section_id = ? AND name = ?",
				PsrdDbAdapter.toStringArray(args));

		if (result < 1) {
			// delete was called on something that doesn't exist!
			throw new SQLException(
				String.format("Failed to delete key: character_id = %s, section_id = %s, name = %s",
					Long.toString(characterId), sectionId, name));
		}
	}

	public Cursor fetchCollectionList() {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT collection_id AS _id, name");
		sb.append(" FROM collections");
		String sql = sb.toString();
		return userDbAdapter.database.rawQuery(sql, new String[0]);
	}

	public Cursor fetchCollectionEntries(String collectionName) {
		List<String> args = new ArrayList<String>();
		args.add(collectionName);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ce.section_id, ce.name");
		sql.append(" FROM collection_entries ce");
		sql.append("  INNER JOIN collections ON collections.collection_id = ce.collection_id");
		sql.append(" WHERE collections.name = ?");
		return userDbAdapter.database.rawQuery(sql.toString(), PsrdDbAdapter.toStringArray(args));
	}

	public Cursor fetchFirstCollection() {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT collections.*");
		sql.append(" FROM collections");
		sql.append(" ORDER BY collection_id");
		sql.append(" LIMIT 1");
		return userDbAdapter.database.rawQuery(sql.toString(), new String[0]);
	}

	public Cursor fetchCollection(String collectionId) {
		List<String> args = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT collections.*");
		sql.append(" FROM collections");
		sql.append(" WHERE collections.collection_id = ?");
		args.add(collectionId);
		return userDbAdapter.database.rawQuery(sql.toString(), PsrdDbAdapter.toStringArray(args));
	}
}
