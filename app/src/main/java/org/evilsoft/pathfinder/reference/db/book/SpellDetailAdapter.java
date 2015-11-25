package org.evilsoft.pathfinder.reference.db.book;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SpellDetailAdapter {
	public SQLiteDatabase database;
	public String dbName;

	public SpellDetailAdapter(SQLiteDatabase database, String dbName) {
		this.database = database;
		this.dbName = dbName;
	}

	public Cursor fetchSpellDetails(Integer section_id) {
		List<String> args = new ArrayList<String>();
		args.add(section_id.toString());
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT school, subschool_text, descriptor_text, level_text, component_text,");
		sb.append("  casting_time, preparation_time, range, duration, saving_throw, spell_resistance, as_spell_id");
		sb.append(" FROM spell_details");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public static class SpellDetailUtils {
		public static String getSchool(Cursor cursor) {
			return cursor.getString(0);
		}

		public static String getSubschoolText(Cursor cursor) {
			return cursor.getString(1);
		}

		public static String getDescriptorText(Cursor cursor) {
			return cursor.getString(2);
		}

		public static String getLevelText(Cursor cursor) {
			return cursor.getString(3);
		}

		public static String getComponentText(Cursor cursor) {
			return cursor.getString(4);
		}

		public static String getCastingTime(Cursor cursor) {
			return cursor.getString(5);
		}

		public static String getPreparationTime(Cursor cursor) {
			return cursor.getString(6);
		}

		public static String getRange(Cursor cursor) {
			return cursor.getString(7);
		}

		public static String getDuration(Cursor cursor) {
			return cursor.getString(8);
		}

		public static String getSavingThrow(Cursor cursor) {
			return cursor.getString(9);
		}

		public static String getSpellResistance(Cursor cursor) {
			return cursor.getString(10);
		}

		public static Integer getAsSpellId(Cursor cursor) {
			return cursor.getInt(11);
		}
	}
}
