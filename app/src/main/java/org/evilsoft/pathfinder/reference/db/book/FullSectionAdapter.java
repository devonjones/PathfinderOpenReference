package org.evilsoft.pathfinder.reference.db.book;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class FullSectionAdapter {
	public SQLiteDatabase database;
	public String dbName;

	public FullSectionAdapter(SQLiteDatabase database, String dbName) {
		this.database = database;
		this.dbName = dbName;
	}

	public Cursor fetchFullSection(String sectionId) {
		List<String> args = new ArrayList<String>();
		args.add(sectionId);
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT node.section_id, node.lft, node.rgt, node.parent_id, node.type,");
		sb.append("  node.subtype, node.name, node.abbrev, node.source, node.description, node.body,");
		sb.append("  node.image, node.alt, node.create_index, node.url");
		sb.append(" FROM sections AS node, sections AS parent");
		sb.append(" WHERE node.lft BETWEEN parent.lft AND parent.rgt");
		sb.append("  AND parent.section_id = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public static class SectionUtils {
		public static Integer getSectionId(Cursor cursor) {
			return cursor.getInt(0);
		}
		public static Integer getLft(Cursor cursor) {
			return cursor.getInt(1);
		}
		public static Integer getRgt(Cursor cursor) {
			return cursor.getInt(2);
		}
		public static Integer getParentId(Cursor cursor) {
			return cursor.getInt(3);
		}
		public static String getType(Cursor cursor) {
			return cursor.getString(4);
		}
		public static String getSubtype(Cursor cursor) {
			return cursor.getString(5);
		}
		public static String getName(Cursor cursor) {
			return cursor.getString(6);
		}
		public static String getAbbrev(Cursor cursor) {
			return cursor.getString(7);
		}
		public static String getSource(Cursor cursor) {
			return cursor.getString(8);
		}
		public static String getDescription(Cursor cursor) {
			return cursor.getString(9);
		}
		public static String getBody(Cursor cursor) {
			return cursor.getString(10);
		}
		public static String getImage(Cursor cursor) {
			return cursor.getString(11);
		}
		public static String getAlt(Cursor cursor) {
			return cursor.getString(12);
		}
		public static Integer getCreateIndex(Cursor cursor) {
			return cursor.getInt(13);
		}
		public static String getUrl(Cursor cursor) {
			return cursor.getString(14);
		}
	}

}
