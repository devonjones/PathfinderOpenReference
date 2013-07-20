package org.evilsoft.pathfinder.reference.db.book;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ArmyAdapter {
	public SQLiteDatabase database;
	public String dbName;

	public ArmyAdapter(SQLiteDatabase database, String dbName) {
		this.database = database;
		this.dbName = dbName;
	}

	public Cursor getArmyDetails(Integer sectionId) {
		List<String> args = new ArrayList<String>();
		args.add(sectionId.toString());
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT xp, creature_type, alignment, size, hp, acr, dv,");
		sb.append("  om, special, speed, consumption, tactics,");
		sb.append("  resources, note");
		sb.append(" FROM army_details");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public static class ArmyUtils {
		public static String getXp(Cursor cursor) {
			return cursor.getString(0);
		}

		public static String getCreatureType(Cursor cursor) {
			return cursor.getString(1);
		}

		public static String getAlignment(Cursor cursor) {
			return cursor.getString(2);
		}

		public static String getSize(Cursor cursor) {
			return cursor.getString(3);
		}

		public static String getHp(Cursor cursor) {
			return cursor.getString(4);
		}

		public static String getAcr(Cursor cursor) {
			return cursor.getString(5);
		}

		public static String getDv(Cursor cursor) {
			return cursor.getString(6);
		}

		public static String getOm(Cursor cursor) {
			return cursor.getString(7);
		}

		public static String getSpecial(Cursor cursor) {
			return cursor.getString(8);
		}

		public static String getSpeed(Cursor cursor) {
			return cursor.getString(9);
		}

		public static String getConsumption(Cursor cursor) {
			return cursor.getString(10);
		}

		public static String getTactics(Cursor cursor) {
			return cursor.getString(11);
		}

		public static String getResources(Cursor cursor) {
			return cursor.getString(12);
		}

		public static String getNote(Cursor cursor) {
			return cursor.getString(13);
		}
	}
}
