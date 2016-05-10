package org.evilsoft.pathfinder.reference.db.book;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class LinkAdapter {
	public SQLiteDatabase database;
	public String dbName;

	public LinkAdapter(SQLiteDatabase database, String dbName) {
		this.database = database;
		this.dbName = dbName;
	}

	public Cursor getLinkDetails(Integer sectionId) {
		List<String> args = new ArrayList<String>();
		args.add(sectionId.toString());
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT section_id, url, display");
		sb.append(" FROM link_details");
		sb.append(" WHERE section_id = ?");
		sb.append(" LIMIT 1");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public static class LinkUtils {
		private LinkUtils() {
		}

		public static Integer getSectionId(Cursor cursor) {
			return cursor.getInt(0);
		}
		public static String getUrl(Cursor cursor) {
			return cursor.getString(1);
		}
		public static Integer getDisplay(Cursor cursor) {
			return cursor.getInt(2);
		}
	}
}
