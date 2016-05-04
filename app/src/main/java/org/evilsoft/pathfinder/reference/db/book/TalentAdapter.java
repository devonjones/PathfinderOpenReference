package org.evilsoft.pathfinder.reference.db.book;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import java.util.ArrayList;
import java.util.List;

public class TalentAdapter {
	public SQLiteDatabase database;
	public String dbName;

	public TalentAdapter(SQLiteDatabase database, String dbName) {
		this.database = database;
		this.dbName = dbName;
	}

	public Cursor getTalentDetails(Integer sectionId) {
		List<String> args = new ArrayList<String>();
		args.add(sectionId.toString());
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT element, talent_type, blast_type, level, burn, damage, prerequisite, ");
		sb.append("  associated_blasts, saving_throw, spell_resistance");
		sb.append(" FROM talent_details");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public static class TalentUtils {
		public static String getElement(Cursor cursor) { return cursor.getString(0); }
		public static String getTalentType(Cursor cursor) { return cursor.getString(1); }
		public static String getBlastType(Cursor cursor) { return cursor.getString(2); }
		public static String getLevel(Cursor cursor) { return cursor.getString(3); }
		public static String getBurn(Cursor cursor) { return cursor.getString(4); }
		public static String getDamage(Cursor cursor) {
			return cursor.getString(5);
		}
		public static String getPrerequisite(Cursor cursor) { return cursor.getString(6); }
		public static String getAssociatedBlasts(Cursor cursor) {
			return cursor.getString(7);
		}
		public static String getSavingThrow(Cursor cursor) { return cursor.getString(8); }
		public static String getSpellResistance(Cursor cursor) { return cursor.getString(9); }
	}
}
