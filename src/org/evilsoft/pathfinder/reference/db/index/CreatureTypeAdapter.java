package org.evilsoft.pathfinder.reference.db.index;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;
import org.evilsoft.pathfinder.reference.preference.FilterPreferenceManager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CreatureTypeAdapter {
	public SQLiteDatabase database;

	public CreatureTypeAdapter(SQLiteDatabase database) {
		this.database = database;
	}

	public Cursor fetchCreatureTypes() {
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT DISTINCT creature_type");
		sb.append(" FROM central_index");
		sb.append("  WHERE creature_type IS NOT NULL");
		sb.append(FilterPreferenceManager.getSourceFilter(args, "AND"));
		sb.append(" ORDER BY creature_type");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}
	
	public static class CreatureTypeUtils {
		public static String getCreatureType(Cursor cursor) {
			return cursor.getString(0);
		}
	}
}

