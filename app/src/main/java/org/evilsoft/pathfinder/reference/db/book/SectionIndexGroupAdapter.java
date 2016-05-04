package org.evilsoft.pathfinder.reference.db.book;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SectionIndexGroupAdapter {
	public SQLiteDatabase database;
	public String dbName;

	public SectionIndexGroupAdapter(SQLiteDatabase database, String dbName) {
		this.database = database;
		this.dbName = dbName;
	}

	public Cursor fetchSectionByParentUrl(String parentUrl) {
		List<String> args = new ArrayList<String>();
		args.add(parentUrl);
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT s.section_id, '" + dbName + "' as database, ");
		sb.append("  s.name, s.parent_id, p.name as parent_name, s.source,");
		sb.append("  s.type, s.subtype, s.description, s.url");
		sb.append(" FROM sections s");
		sb.append("  INNER JOIN sections p");
		sb.append("   ON s.parent_id = p.section_id");
		sb.append(" WHERE p.url = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public static class SectionGroupIndexUtils {
		public static Integer getSectionId(Cursor cursor) {
			return cursor.getInt(0);
		}

		public static String getDatabase(Cursor cursor) {
			return cursor.getString(1);
		}

		public static String getName(Cursor cursor) {
			return cursor.getString(2);
		}

		public static Integer getParentId(Cursor cursor) {
			return cursor.getInt(3);
		}

		public static String getParentName(Cursor cursor) {
			return cursor.getString(4);
		}

		public static String getSource(Cursor cursor) {
			return cursor.getString(5);
		}

		public static String getType(Cursor cursor) {
			return cursor.getString(6);
		}

		public static String getSubtype(Cursor cursor) {
			return cursor.getString(7);
		}

		public static String getDescription(Cursor cursor) {
			return cursor.getString(8);
		}

		public static String getUrl(Cursor cursor) {
			return cursor.getString(9);
		}
	}
}
