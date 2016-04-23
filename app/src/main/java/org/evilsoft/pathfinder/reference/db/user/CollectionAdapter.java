package org.evilsoft.pathfinder.reference.db.user;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.MenuItem;
import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.util.Log;

public class CollectionAdapter {
	private UserDbAdapter userDbAdapter;

	public CollectionAdapter(UserDbAdapter userDbAdapter) {
		this.userDbAdapter = userDbAdapter;
	}

	/* Collections calls */

	public ArrayList<MenuItem> createCollectionList() {
		Cursor curs = fetchCollectionList();
		try {
			ArrayList<MenuItem> charList = new ArrayList<MenuItem>();
			MenuItem child;

			boolean hasNext = curs.moveToFirst();
			while (hasNext) {
				child = new MenuItem();
				child.setId(curs.getInt(0));
				child.setName(curs.getString(1));
				charList.add(child);
				hasNext = curs.moveToNext();
			}
			return charList;
		} finally {
			curs.close();
		}
	}

	public Integer fetchFirstCollectionId() {
		Cursor c = fetchFirstCollection();
		try {
			c.moveToFirst();
			return c.getInt(0);
		} catch (CursorIndexOutOfBoundsException cioobe) {
			return null;
		} finally {
			c.close();
		}
	}

	public Boolean collectionExists(String collectionId) {
		Cursor c = fetchCollection(collectionId);
		try {
			if (c.getCount() > 0) {
				return true;
			}
			return false;
		} finally {
			c.close();
		}
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
		userDbAdapter.database.delete("collection_values",
				"collection_id = ?", BaseDbHelper.toStringArray(args));
		args = new ArrayList<String>();
		args.add(name);
		return userDbAdapter.database.delete("collections", "name = ?",
				BaseDbHelper.toStringArray(args));
	}

	public Integer selectCollectionId(String name) {
		List<String> args = new ArrayList<String>();
		args.add(name);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT collection_id");
		sb.append(" FROM collections");
		sb.append(" WHERE name = ?");
		String sql = sb.toString();
		Cursor c = userDbAdapter.database.rawQuery(sql,
				BaseDbHelper.toStringArray(args));
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

	public Cursor fetchCollectionList() {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT collection_id AS _id, name");
		sb.append(" FROM collections");
		String sql = sb.toString();
		return userDbAdapter.database.rawQuery(sql, new String[0]);
	}

	public Cursor fetchCollection(String collectionId) {
		List<String> args = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT collections.*");
		sql.append(" FROM collections");
		sql.append(" WHERE collections.collection_id = ?");
		args.add(collectionId);
		return userDbAdapter.database.rawQuery(sql.toString(),
				BaseDbHelper.toStringArray(args));
	}

	public Cursor fetchFirstCollection() {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT collections.*");
		sql.append(" FROM collections");
		sql.append(" ORDER BY collection_id");
		sql.append(" LIMIT 1");
		return userDbAdapter.database.rawQuery(sql.toString(), new String[0]);
	}

	/* Collection Values calls */

	public boolean entryIsStarred(long collectionId, String url) {
		List<String> args = new ArrayList<String>();
		args.add(Long.toString(collectionId));
		args.add(url);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT 1 FROM collection_values");
		sql.append(" WHERE collection_id = ?");
		sql.append("  AND url = ?");
		Cursor curs = userDbAdapter.database.rawQuery(sql.toString(),
				BaseDbHelper.toStringArray(args));
		try {
			return curs.moveToFirst();
		} finally {
			curs.close();
		}
	}

	public void toggleEntryStar(long collectionId, String url, String title) {
		if (entryIsStarred(collectionId, url)) {
			unstar(collectionId, url);
		} else {
			star(collectionId, url, title);
		}
	}

	public void star(long collectionId, String url, String name)
			throws SQLException {
		Log.i("starring", String.format(
				"collection_id = %s, url = %s, name = %s",
				Long.toString(collectionId), url, name));
		ContentValues cv = new ContentValues();
		cv.put("collection_id", collectionId);
		cv.put("url", url);
		cv.put("name", name);
		userDbAdapter.database.insertOrThrow("collection_values", null, cv);
	}

	public void unstar(long collectionId, String url)
			throws SQLException {
		Log.i("unstarring", String.format(
				"collection_id = %s, url = %s",
				Long.toString(collectionId), url));
		List<String> args = new ArrayList<String>();
		args.add(Long.toString(collectionId));
		args.add(url);
		int result = userDbAdapter.database.delete(
				"collection_values",
				"collection_id = ? AND url = ?",
				BaseDbHelper.toStringArray(args));

		if (result < 1) {
			throw new SQLException(String.format(
					"Failed to delete key: character_id = %s, url = %s",
					Long.toString(collectionId), url));
		}
	}

	public Cursor fetchCollectionValues(String collectionName) {
		List<String> args = new ArrayList<String>();
		args.add(collectionName);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT cv.collection_entry_id, cv.name, cv.url");
		sql.append(" FROM collection_values cv");
		sql.append("  INNER JOIN collections");
		sql.append("   ON collections.collection_id = cv.collection_id");
		sql.append(" WHERE collections.name = ?");
		return userDbAdapter.database.rawQuery(sql.toString(),
				BaseDbHelper.toStringArray(args));
	}

	public Cursor fetchCollectionValue(String collectionValueId) {
		List<String> args = new ArrayList<String>();
		args.add(collectionValueId);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT collection_entry_id, name, url");
		sql.append(" FROM collection_values cv");
		sql.append(" WHERE collection_entry_id = ?");
		return userDbAdapter.database.rawQuery(sql.toString(),
				BaseDbHelper.toStringArray(args));
	}
}
