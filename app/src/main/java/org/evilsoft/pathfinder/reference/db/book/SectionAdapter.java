package org.evilsoft.pathfinder.reference.db.book;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SectionAdapter {
	public SQLiteDatabase database;
	public String dbName;

	public SectionAdapter(SQLiteDatabase database, String dbName) {
		this.database = database;
		this.dbName = dbName;
	}

	public Cursor fetchSectionBySectionId(Integer sectionId) {
		List<String> args = new ArrayList<String>();
		args.add(sectionId.toString());
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT section_id, parent_id, name, type, subtype, url,");
		sb.append("  abbrev, source, description, body, image, alt");
		sb.append(" FROM sections");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public Cursor fetchSectionByParentId(Integer parentId) {
		List<String> args = new ArrayList<String>();
		args.add(parentId.toString());
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT section_id, parent_id, name, type, subtype, url,");
		sb.append("  abbrev, source, description, body, image, alt");
		sb.append(" FROM sections");
		sb.append(" WHERE parent_id = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public Cursor fetchSectionByParentIdAndName(String parentId, String name) {
		List<String> args = new ArrayList<String>();
		args.add(parentId);
		args.add(name);
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT section_id, parent_id, name, type, subtype, url,");
		sb.append("  abbrev, source, description, body, image, alt");
		sb.append(" FROM sections");
		sb.append(" WHERE parent_id = ?");
		sb.append("  AND name = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public Cursor fetchParentBySectionId(Integer sectionId) {
		List<String> args = new ArrayList<String>();
		args.add(sectionId.toString());
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT p.section_id, p.parent_id, p.name, p.type, p.subtype, p.url,");
		sb.append("  p.abbrev, p.source, p.description, p.body, p.image, p.alt");
		sb.append(" FROM sections s");
		sb.append("  INNER JOIN sections p");
		sb.append("   ON s.parent_id = p.section_id");
		sb.append(" WHERE s.section_id = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public Cursor fetchSectionByParentUrl(String parentUrl) {
		List<String> args = new ArrayList<String>();
		args.add(parentUrl);
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT s.section_id, s.parent_id, s.name, s.type, s.subtype, s.url,");
		sb.append("  s.abbrev, s.source, s.description, s.body, s.image, s.alt");
		sb.append(" FROM sections s");
		sb.append("  INNER JOIN sections p");
		sb.append("   ON s.parent_id = p.section_id");
		sb.append(" WHERE p.url = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public Cursor fetchSectionByUrl(String url) {
		List<String> args = new ArrayList<String>();
		args.add(url);
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT section_id, parent_id, name, type, subtype, url,");
		sb.append("  abbrev, source, description, body, image, alt");
		sb.append(" FROM sections");
		sb.append(" WHERE url = ?");
		String sql = sb.toString();
		Cursor curs = database.rawQuery(sql, BaseDbHelper.toStringArray(args));
		if (curs.getCount() == 0) {
			curs.close();
			sb = new StringBuilder();
			sb.append("SELECT s.section_id, s.parent_id, s.name, s.type, s.subtype, s.url,");
			sb.append("  s.abbrev, s.source, s.description, s.body, s.image, s.alt");
			sb.append(" FROM sections s");
			sb.append("  INNER JOIN url_references u");
			sb.append("   ON s.section_id = u.section_id");
			sb.append(" WHERE u.url = ?");
			sql = sb.toString();
			curs = database.rawQuery(sql, BaseDbHelper.toStringArray(args));
		}
		return curs;
	}

	public static class SectionUtils {
		public static Integer getSectionId(Cursor cursor) {
			return cursor.getInt(0);
		}

		public static Integer getParentId(Cursor cursor) {
			return cursor.getInt(1);
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

		public static String getAbbrev(Cursor cursor) {
			return cursor.getString(6);
		}

		public static String getSource(Cursor cursor) {
			return cursor.getString(7);
		}

		public static String getDescription(Cursor cursor) {
			return cursor.getString(8);
		}

		public static String getBody(Cursor cursor) {
			return cursor.getString(9);
		}

		public static String getImage(Cursor cursor) {
			return cursor.getString(10);
		}

		public static String getAlt(Cursor cursor) {
			return cursor.getString(11);
		}
	}
}