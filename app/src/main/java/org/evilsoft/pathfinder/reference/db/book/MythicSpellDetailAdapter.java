package org.evilsoft.pathfinder.reference.db.book;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MythicSpellDetailAdapter {
	public SQLiteDatabase database;
	public String dbName;

	public MythicSpellDetailAdapter(SQLiteDatabase database, String dbName) {
		this.database = database;
		this.dbName = dbName;
	}

	public Cursor fetchMythicSpellDetails(Integer sectionId) {
		List<String> args = new ArrayList<String>();
		args.add(sectionId.toString());
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT section_id, spell_source");
		sb.append(" FROM mythic_spell_details");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public Cursor fetchMythicSpellDetailsByName(String spellName) {
		List<String> args = new ArrayList<String>();
		args.add(spellName);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT section_id, spell_source");
		sb.append(" FROM mythic_spell_details");
		sb.append(" WHERE spell_source = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public static class MythicSpellDetailUtils {
		private MythicSpellDetailUtils() {
		}

		public static Integer getSectionId(Cursor cursor) {
			return cursor.getInt(0);
		}

		public static String getSpellSource(Cursor cursor) {
			return cursor.getString(1);
		}
	}
}
