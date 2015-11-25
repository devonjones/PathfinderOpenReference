package org.evilsoft.pathfinder.reference.db.book;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SpellComponentAdapter {
	public SQLiteDatabase database;
	public String dbName;

	public SpellComponentAdapter(SQLiteDatabase database, String dbName) {
		this.database = database;
		this.dbName = dbName;
	}

	public Cursor fetchSpellComponents(Integer section_id) {
		List<String> args = new ArrayList<String>();
		args.add(section_id.toString());
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT component_type, description");
		sb.append(" FROM spell_components");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public static class SpellComponentUtils {
		public static String getComponentType(Cursor cursor) {
			return cursor.getString(0);
		}
		public static String getDescription(Cursor cursor) {
			return cursor.getString(1);
		}
	}
}
