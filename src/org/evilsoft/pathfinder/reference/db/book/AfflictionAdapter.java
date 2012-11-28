package org.evilsoft.pathfinder.reference.db.book;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AfflictionAdapter {
	public SQLiteDatabase database;
	public String dbName;

	public AfflictionAdapter(SQLiteDatabase database, String dbName) {
		this.database = database;
		this.dbName = dbName;
	}

	public Cursor getAfflictionDetails(Integer sectionId) {
		List<String> args = new ArrayList<String>();
		args.add(sectionId.toString());
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT contracted, save, onset, frequency, effect, initial_effect, ");
		sb.append("  secondary_effect, cure");
		sb.append(" FROM affliction_details");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public static class AfflictionUtils {
		public static String getContracted(Cursor cursor) {
			return cursor.getString(0);
		}
		public static String getSave(Cursor cursor) {
			return cursor.getString(1);
		}
		public static String getOnset(Cursor cursor) {
			return cursor.getString(2);
		}
		public static String getFrequency(Cursor cursor) {
			return cursor.getString(3);
		}
		public static String getEffect(Cursor cursor) {
			return cursor.getString(4);
		}
		public static String getInitialEffect(Cursor cursor) {
			return cursor.getString(5);
		}
		public static String getSecondaryEffect(Cursor cursor) {
			return cursor.getString(6);
		}
		public static String getCure(Cursor cursor) {
			return cursor.getString(7);
		}
	}
}
