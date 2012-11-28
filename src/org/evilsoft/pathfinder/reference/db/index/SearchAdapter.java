package org.evilsoft.pathfinder.reference.db.index;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.app.SearchManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SearchAdapter {
	public SQLiteDatabase database;

	public SearchAdapter(SQLiteDatabase database) {
		this.database = database;
	}

	public Integer countSearchArticles(String constraint) {
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT count(*)");
		sb.append(" FROM central_index");
		if (constraint != null) {
			sb.append(" WHERE search_name like ?");
			args.add('%' + constraint + '%');
		}
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
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT si.section_id as _id,");
		sb.append("  si.search_name AS " + SearchManager.SUGGEST_COLUMN_TEXT_1
				+ ",");
		sb.append("  si.search_name AS " + SearchManager.SUGGEST_COLUMN_QUERY);
		sb.append(" FROM central_index si");
		sb.append(" INNER JOIN section_sort ss");
		sb.append("  ON si.type = ss.type");
		if (constraint != null) {
			sb.append(" WHERE si.search_name like ?");
			args.add('%' + constraint + '%');
		}
		sb.append(" GROUP BY si.search_name");
		sb.append(" ORDER BY ss.section_sort_id, si.search_name");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public Cursor getSingleSearchArticle(String constraint) {
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT i.section_id, i.database, i.name, i.type, i.subtype, i.url, i.parent_id, i.parent_name");
		sb.append(" FROM central_index i");
		sb.append(" WHERE i.search_name like ?");
		sb.append(" LIMIT 1");
		args.add('%' + constraint + '%');
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public Cursor search(String constraint) {
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT i.section_id, i.database, i.name, i.type, i.subtype, i.url, i.parent_id, i.parent_name");
		sb.append(" FROM central_index i");
		sb.append("  INNER JOIN section_sort ss");
		sb.append("   ON i.type = ss.type");
		if (constraint != null) {
			sb.append(" WHERE i.search_name like ?");
			args.add('%' + constraint + '%');
		}
		sb.append(" ORDER BY ss.section_sort_id, i.search_name, i.section_id");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public static class SearchUtils {
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
