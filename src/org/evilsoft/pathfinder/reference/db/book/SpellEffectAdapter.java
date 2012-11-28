package org.evilsoft.pathfinder.reference.db.book;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SpellEffectAdapter {
	public SQLiteDatabase database;
	public String dbName;

	public SpellEffectAdapter(SQLiteDatabase database, String dbName) {
		this.database = database;
		this.dbName = dbName;
	}

	public Cursor fetchSpellEffects(Integer section_id) {
		List<String> args = new ArrayList<String>();
		args.add(section_id.toString());
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT name, description");
		sb.append(" FROM spell_effects");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public static class SpellEffectUtils {
		public static String getName(Cursor cursor) {
			return cursor.getString(0);
		}
		public static String getDescription(Cursor cursor) {
			return cursor.getString(1);
		}
	}
}
