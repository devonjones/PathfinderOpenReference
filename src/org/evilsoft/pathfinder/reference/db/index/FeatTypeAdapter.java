package org.evilsoft.pathfinder.reference.db.index;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class FeatTypeAdapter {
	public SQLiteDatabase database;

	public FeatTypeAdapter(SQLiteDatabase database) {
		this.database = database;
	}

	public Cursor fetchFeatTypes() {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT DISTINCT feat_type");
		sb.append(" FROM feat_type_index ft");
		sb.append(" ORDER BY feat_type");
		String sql = sb.toString();
		String[] selectionArgs = new String[0];
		return database.rawQuery(sql, selectionArgs);
	}
	
	public static class FeatTypeUtils {
		public static String getFeatType(Cursor cursor) {
			return cursor.getString(0);
		}
	}
}

