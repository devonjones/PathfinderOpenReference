package org.evilsoft.pathfinder.reference.db.book;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class KingdomResourceAdapter {
	public SQLiteDatabase database;
	public String dbName;

	public KingdomResourceAdapter(SQLiteDatabase database, String dbName) {
		this.database = database;
		this.dbName = dbName;
	}

	public Cursor getKingdomResourceDetails(Integer sectionId) {
		List<String> args = new ArrayList<String>();
		args.add(sectionId.toString());
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT bp, lot, kingdom, discount, magic_items,");
		sb.append("  settlement, special, resource_limit, upgrade_from,");
		sb.append("  upgrade_to");
		sb.append(" FROM kingdom_resource_details");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public static class KingdomResourceUtils {
		public static String getBp(Cursor cursor) {
			return cursor.getString(0);
		}

		public static String getLot(Cursor cursor) {
			return cursor.getString(1);
		}

		public static String getKingdom(Cursor cursor) {
			return cursor.getString(2);
		}

		public static String getDiscount(Cursor cursor) {
			return cursor.getString(3);
		}

		public static String getMagicItems(Cursor cursor) {
			return cursor.getString(4);
		}

		public static String getSettlement(Cursor cursor) {
			return cursor.getString(5);
		}

		public static String getSpecial(Cursor cursor) {
			return cursor.getString(6);
		}

		public static String getLimit(Cursor cursor) {
			return cursor.getString(7);
		}

		public static String getUpgradeFrom(Cursor cursor) {
			return cursor.getString(8);
		}

		public static String getUpgradeTo(Cursor cursor) {
			return cursor.getString(9);
		}
	}
}
