package org.evilsoft.pathfinder.reference.db.index;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;
import org.evilsoft.pathfinder.reference.preference.FilterPreferenceManager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class FeatTypeAdapter {
	public SQLiteDatabase database;
	public Context context;

	public FeatTypeAdapter(SQLiteDatabase database, Context context) {
		this.database = database;
		this.context = context;
	}

	public Cursor fetchFeatTypes() {
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT DISTINCT ft.feat_type");
		sb.append(" FROM feat_type_index ft");
		sb.append("  INNER JOIN central_index ci");
		sb.append("   ON ft.index_id = ci.index_id");
		sb.append(FilterPreferenceManager.getSourceFilter(context, args, "WHERE", "ci"));
		sb.append(" ORDER BY ft.feat_type");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}
	
	public static class FeatTypeUtils {
		private FeatTypeUtils() {
		}

		public static String getFeatType(Cursor cursor) {
			return cursor.getString(0);
		}
	}
}

