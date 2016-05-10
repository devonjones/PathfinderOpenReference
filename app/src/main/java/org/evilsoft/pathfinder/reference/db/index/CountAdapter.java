package org.evilsoft.pathfinder.reference.db.index;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;
import org.evilsoft.pathfinder.reference.preference.FilterPreferenceManager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CountAdapter {
	public SQLiteDatabase database;
	public Context context;

	public CountAdapter(SQLiteDatabase database, Context context) {
		this.database = database;
		this.context = context;
	}

	public Cursor countByType(String type, String subtype) {
		if(type == null || type.equals("*")) {
			type = null;
		}
		List<String> args = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT count(*) AS cnt");
		sb.append(" FROM central_index i");
		String where = "WHERE";
		if (type != null) {
			sb.append(" " + where + " i.type = ?");
			where = "AND";
			args.add(type);
		}
		if (subtype != null) {
			sb.append("  " + where + " i.subtype = ?");
			where = "AND";
			args.add(subtype);
		}
		sb.append(FilterPreferenceManager.getSourceFilter(context, args, where, "i"));
		sb.append(" ORDER BY i.name");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public Cursor fetchByUrl(String url) {
		List<String> args = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT count(*) AS cnt");
		sb.append(" FROM central_index i");
		sb.append(" WHERE i.url = ?");
		args.add(url);
		sb.append(FilterPreferenceManager.getSourceFilter(context, args, "AND", "i"));
		sb.append(" ORDER BY i.name");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public static class CountUtils {
		private CountUtils() {
		}

		public static Integer getCount(Cursor cursor) {
			return cursor.getInt(0);
		}
	}
}
