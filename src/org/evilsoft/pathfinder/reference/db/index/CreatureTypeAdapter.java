package org.evilsoft.pathfinder.reference.db.index;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CreatureTypeAdapter {
	public SQLiteDatabase database;

	public CreatureTypeAdapter(SQLiteDatabase database) {
		this.database = database;
	}

	public Cursor fetchCreatureTypes() {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT DISTINCT creature_type");
		sb.append(" FROM central_index");
		sb.append("  WHERE creature_type IS NOT NULL");
		sb.append(" ORDER BY creature_type");
		String sql = sb.toString();
		String[] selectionArgs = new String[0];
		return database.rawQuery(sql, selectionArgs);
	}
	
	public static class CreatureTypeUtils {
		public static String getCreatureType(Cursor cursor) {
			return cursor.getString(0);
		}
	}
}

