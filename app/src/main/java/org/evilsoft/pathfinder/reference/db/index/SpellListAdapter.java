package org.evilsoft.pathfinder.reference.db.index;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;
import org.evilsoft.pathfinder.reference.preference.FilterPreferenceManager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SpellListAdapter {
	public SQLiteDatabase database;
	public Context context;

	public SpellListAdapter(SQLiteDatabase database, Context context) {
		this.database = database;
		this.context = context;
	}

	public Cursor fetchClassSpells(String class_name) {
		List<String> args = new ArrayList<String>();
		args.add(class_name);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT i.name, i.url, sl.name, sl.level");
		sb.append(" FROM spell_list_index sl");
		sb.append("  INNER JOIN central_index i");
		sb.append("   ON sl.index_id = i.index_id");
		sb.append(" WHERE sl.name = ?");
		sb.append(FilterPreferenceManager.getSourceFilter(context, args, "AND"));
		sb.append(" ORDER BY sl.level, i.name");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public static class SpellListUtils {
		private SpellListUtils() {
		}

		public static String getSpellName(Cursor cursor) {
			return cursor.getString(0);
		}

		public static String getUrl(Cursor cursor) {
			return cursor.getString(1);
		}

		public static String getSpellClass(Cursor cursor) {
			return cursor.getString(2);
		}

		public static Integer getSpellLevel(Cursor cursor) {
			return cursor.getInt(3);
		}
	}
}
