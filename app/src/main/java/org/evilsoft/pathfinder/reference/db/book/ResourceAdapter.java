package org.evilsoft.pathfinder.reference.db.book;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ResourceAdapter {
	public SQLiteDatabase database;
	public String dbName;

	public ResourceAdapter(SQLiteDatabase database, String dbName) {
		this.database = database;
		this.dbName = dbName;
	}

	public Cursor getResourceDetails(Integer sectionId) {
		List<String> args = new ArrayList<String>();
		args.add(sectionId.toString());
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT benefit, resource_create, earnings, rooms, size, ");
		sb.append("  skills, teams, time, upgrade_from, upgrade_to, wage");
		sb.append(" FROM resource_details");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public static class ResourceUtils {
		private ResourceUtils() {
		}

		public static String getBenefit(Cursor cursor) {
			return cursor.getString(0);
		}

		public static String getCreate(Cursor cursor) {
			return cursor.getString(1);
		}

		public static String getEarnings(Cursor cursor) {
			return cursor.getString(2);
		}

		public static String getRooms(Cursor cursor) {
			return cursor.getString(3);
		}

		public static String getSize(Cursor cursor) {
			return cursor.getString(4);
		}

		public static String getSkills(Cursor cursor) {
			return cursor.getString(5);
		}

		public static String getTeams(Cursor cursor) {
			return cursor.getString(6);
		}

		public static String getTime(Cursor cursor) {
			return cursor.getString(7);
		}

		public static String getUpgradeFrom(Cursor cursor) {
			return cursor.getString(8);
		}

		public static String getUpgradeTo(Cursor cursor) {
			return cursor.getString(9);
		}

		public static String getWage(Cursor cursor) {
			return cursor.getString(10);
		}
	}
}
