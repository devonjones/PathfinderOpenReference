package org.evilsoft.pathfinder.reference.db.index;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;
import org.evilsoft.pathfinder.reference.preference.FilterPreferenceManager;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SearchAdapter {
	public SQLiteDatabase database;
	public Context context;

	public SearchAdapter(SQLiteDatabase database, Context context) {
		this.database = database;
		this.context = context;
	}

	public Integer countSearchArticles(String constraint) {
		constraint = constraint.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT count(DISTINCT i.index_id)");
		sb.append(" FROM central_index i");
		if (constraint != null) {
			sb.append("  INNER JOIN search_alternatives sa");
			sb.append("   ON i.index_id = sa.index_id");
			sb.append(" WHERE sa.alternative like ?");
			args.add('%' + constraint + '%');
		}
		sb.append(FilterPreferenceManager.getSourceFilter(context, args, "AND"));
		String sql = sb.toString();
		Cursor c = database.rawQuery(sql, BaseDbHelper.toStringArray(args));
		try {
			c.moveToFirst();
			return c.getInt(0);
		} finally {
			c.close();
		}
	}

	public Cursor autocomplete(String constraint) {
		constraint = constraint.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT i.section_id as _id,");
		sb.append("  i.search_name AS " + SearchManager.SUGGEST_COLUMN_TEXT_1
				+ ",");
		sb.append("  i.search_name AS " + SearchManager.SUGGEST_COLUMN_QUERY);
		sb.append(" FROM central_index i");
		sb.append(" INNER JOIN section_sort ss");
		sb.append("  ON i.type = ss.type");
		if (constraint != null) {
			sb.append("  INNER JOIN search_alternatives sa");
			sb.append("   ON i.index_id = sa.index_id");
			sb.append(" WHERE sa.alternative like ?");
			args.add('%' + constraint + '%');
		}
		sb.append(FilterPreferenceManager.getSourceFilter(context, args, "AND",
				"si"));
		sb.append(" GROUP BY i.search_name");
		sb.append(" ORDER BY ss.section_sort_id, i.search_name");
		sb.append(" LIMIT 50");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public Cursor getSingleSearchArticle(String constraint) {
		constraint = constraint.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT DISTINCT i.section_id, i.database, i.name, i.type, i.subtype, i.url, i.parent_id, i.parent_name");
		sb.append(" FROM central_index i");
		sb.append("  INNER JOIN search_alternatives sa");
		sb.append("   ON i.index_id = sa.index_id");
		sb.append(" WHERE sa.alternative like ?");
		args.add('%' + constraint + '%');
		sb.append(FilterPreferenceManager.getSourceFilter(context, args, "AND",
				"i"));
		sb.append(" LIMIT 1");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public Cursor search(String constraint) {
		constraint = constraint.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT DISTINCT i.section_id, i.database, i.name, i.type, i.subtype, i.url, i.parent_id, i.parent_name");
		sb.append(" FROM central_index i");
		sb.append("  INNER JOIN section_sort ss");
		sb.append("   ON i.type = ss.type");
		if (constraint != null) {
			sb.append("  INNER JOIN search_alternatives sa");
			sb.append("   ON i.index_id = sa.index_id");
			sb.append(" WHERE sa.alternative like ?");
			args.add('%' + constraint + '%');
		}
		sb.append(FilterPreferenceManager.getSourceFilter(context, args, "AND",
				"i"));
		sb.append(" ORDER BY ss.section_sort_id, i.search_name, i.section_id");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public static class SearchUtils {
		private SearchUtils() {
		}

		public static Integer getSectionId(Cursor cursor) {
			return cursor.getInt(0);
		}

		public static String getDatabase(Cursor cursor) {
			return cursor.getString(1);
		}

		public static String getName(Cursor cursor) {
			return cursor.getString(2);
		}

		public static String getType(Cursor cursor) {
			return cursor.getString(3);
		}

		public static String getSubtype(Cursor cursor) {
			return cursor.getString(4);
		}

		public static String getUrl(Cursor cursor) {
			return cursor.getString(5);
		}

		public static Integer getParentId(Cursor cursor) {
			return cursor.getInt(6);
		}

		public static String getParentName(Cursor cursor) {
			return cursor.getString(7);
		}
	}
}
