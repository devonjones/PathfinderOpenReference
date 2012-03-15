package org.evilsoft.pathfinder.reference.db.user;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PsrdUserDbAdapter {
	private Context context;
	public SQLiteDatabase database;
	private PsrdUserDbHelper dbHelper;

	public PsrdUserDbAdapter(Context context) {
		this.context = context;
	}

	public PsrdUserDbAdapter open() throws SQLException {
		dbHelper = new PsrdUserDbHelper(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
		database.close();
	}

	public boolean addCharacter(String name) {
		ContentValues cv = new ContentValues();
		cv.put("name", name);
		return database.insert("collections", null, cv) > -1;
	}

	public void star(long characterId, String sectionId, String name, String url) throws SQLException {
		Log.i("starring",
		    String.format("character_id = %s, section_id = %s, name = %s", Long.toString(characterId), sectionId, name));
		ContentValues cv = new ContentValues();
		cv.put("collection_id", characterId);
		cv.put("section_id", sectionId);
		cv.put("name", name);
		cv.put("path", url);
		database.insertOrThrow("collection_entries", null, cv);
	}

	public void unstar(long characterId, String sectionId, String name, String url) throws SQLException {
		Log.i("unstarring",
		    String.format("character_id = %s, section_id = %s, name = %s", Long.toString(characterId), sectionId, name));
		int result = database.delete("collection_entries", "collection_id = ? AND section_id = ? AND name = ?",
			new String[] { Long.toString(characterId), sectionId, name });

		if (result < 1) {
			// delete was called on something that doesn't exist!
			throw new SQLException(
				String.format("Failed to delete key: character_id = %s, section_id = %s, name = %s",
					Long.toString(characterId), sectionId, name));
		}
	}

	public Integer getPsrdDbVersion() {
		Integer ver = selectPsrdDbVersion();
		if (ver == null) {
			insertPsrdDbVersion();
		}
		return selectPsrdDbVersion();
	}

	public Integer selectPsrdDbVersion() {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT version");
		sb.append(" FROM psrd_db_version");
		sb.append(" LIMIT 1");
		String sql = sb.toString();
		String[] selectionArgs = new String[0];
		Cursor c = database.rawQuery(sql, selectionArgs);
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

	public void insertPsrdDbVersion() {
		ContentValues values = new ContentValues();
		values.put("version", 0);
		database.insert("psrd_db_version", null, values);
	}

	public void updatePsrdDbVersion(Integer version) {
		ContentValues values = new ContentValues();
		values.put("version", version);
		database.update("psrd_db_version", values, null, null);
	}
}
