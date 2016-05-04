package org.evilsoft.pathfinder.reference.db.book;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SpellListAdapter {
	public SQLiteDatabase database;
	public String dbName;

	public SpellListAdapter(SQLiteDatabase database, String dbName) {
		this.database = database;
		this.dbName = dbName;
	}

	public Cursor getSpellLists(Integer sectionId) {
		List<String> args = new ArrayList<String>();
		args.add(sectionId.toString());
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT level, type, name, notes, magic_type");
		sb.append(" FROM spell_lists");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public static class SpellListUtils {
		public static Integer getLevel(Cursor cursor) {
			return cursor.getInt(0);
		}

		public static String getType(Cursor cursor) {
			return cursor.getString(1);
		}

		public static String getName(Cursor cursor) {
			return cursor.getString(2);
		}

		public static String getNotes(Cursor cursor) {
			return cursor.getString(3);
		}

		public static String getMagicType(Cursor cursor) {
			return cursor.getString(4);
		}
	}
}
