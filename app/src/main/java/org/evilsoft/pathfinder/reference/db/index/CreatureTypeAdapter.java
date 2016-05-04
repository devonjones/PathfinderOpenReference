package org.evilsoft.pathfinder.reference.db.index;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;
import org.evilsoft.pathfinder.reference.preference.FilterPreferenceManager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CreatureTypeAdapter {
	public SQLiteDatabase database;
	public Context context;

	public CreatureTypeAdapter(SQLiteDatabase database, Context context) {
		this.database = database;
		this.context = context;
	}

	public Cursor fetchCreatureTypes() {
		List<String> args = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT DISTINCT creature_type");
		sb.append(" FROM central_index");
		sb.append("  WHERE creature_type IS NOT NULL");
		sb.append(FilterPreferenceManager.getSourceFilter(context, args, "AND"));
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

