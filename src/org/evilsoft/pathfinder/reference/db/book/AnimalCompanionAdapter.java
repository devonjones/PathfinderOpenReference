package org.evilsoft.pathfinder.reference.db.book;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AnimalCompanionAdapter {
	public SQLiteDatabase database;
	public String dbName;

	public AnimalCompanionAdapter(SQLiteDatabase database, String dbName) {
		this.database = database;
		this.dbName = dbName;
	}

	public Cursor getAnimalCompanionDetails(Integer sectionId) {
		List<String> args = new ArrayList<String>();
		args.add(sectionId.toString());
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ac, attack, ability_scores, special_qualities, special_attacks,");
		sb.append("  size, speed, level");
		sb.append(" FROM animal_companion_details");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public static class AnimalCompanionUtils {
		public static String getAc(Cursor cursor) {
			return cursor.getString(0);
		}
		public static String getAttack(Cursor cursor) {
			return cursor.getString(1);
		}
		public static String getAbilityScores(Cursor cursor) {
			return cursor.getString(2);
		}
		public static String getSpecialQualities(Cursor cursor) {
			return cursor.getString(3);
		}
		public static String getSpecialAttacks(Cursor cursor) {
			return cursor.getString(4);
		}
		public static String getSize(Cursor cursor) {
			return cursor.getString(5);
		}
		public static String getSpeed(Cursor cursor) {
			return cursor.getString(6);
		}
		public static String getLevel(Cursor cursor) {
			return cursor.getString(7);
		}
	}
}
