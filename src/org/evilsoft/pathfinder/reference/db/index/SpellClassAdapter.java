package org.evilsoft.pathfinder.reference.db.index;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SpellClassAdapter {
	public SQLiteDatabase database;

	public SpellClassAdapter(SQLiteDatabase database) {
		this.database = database;
	}

	public Cursor fetchSpellClasses() {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT DISTINCT class");
		sb.append(" FROM spell_list_index");
		sb.append(" ORDER BY class");
		String sql = sb.toString();
		String[] selectionArgs = new String[0];
		return database.rawQuery(sql, selectionArgs);
	}
	
	public static class SpellListUtils {
		public static String getClass(Cursor cursor) {
			return cursor.getString(0);
		}
	}
}

