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
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT level, class, magic_type");
		sb.append(" FROM spell_lists");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public static class SpellListUtils {
		public static Integer getLevel(Cursor cursor) {
			return cursor.getInt(0);
		}

		public static String getClass(Cursor cursor) {
			return cursor.getString(1);
		}

		public static String getMagicType(Cursor cursor) {
			return cursor.getString(2);
		}
	}
}
