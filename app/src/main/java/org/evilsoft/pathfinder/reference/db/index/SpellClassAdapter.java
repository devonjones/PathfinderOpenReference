package org.evilsoft.pathfinder.reference.db.index;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;
import org.evilsoft.pathfinder.reference.preference.FilterPreferenceManager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SpellClassAdapter {
	public SQLiteDatabase database;
	public Context context;

	public SpellClassAdapter(SQLiteDatabase database, Context context) {
		this.database = database;
		this.context = context;
	}

	public Cursor fetchSpellClasses() {
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT DISTINCT sli.name");
		sb.append(" FROM spell_list_index sli");
		sb.append("  INNER JOIN central_index ci");
		sb.append("   ON sli.index_id = ci.index_id");
		sb.append(" WHERE sli.type = 'class'");
		sb.append(FilterPreferenceManager.getSourceFilter(context, args, "AND", "ci"));
		sb.append(" ORDER BY sli.name");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}
	
	public static class SpellListUtils {
		private SpellListUtils() {
		}

		public static String getName(Cursor cursor) {
			return cursor.getString(0);
		}
	}
}

