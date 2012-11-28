package org.evilsoft.pathfinder.reference.db.book;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SettlementAdapter {
	public SQLiteDatabase database;
	public String dbName;

	public SettlementAdapter(SQLiteDatabase database, String dbName) {
		this.database = database;
		this.dbName = dbName;
	}

	public Cursor getSettlementDetails(Integer sectionId) {
		List<String> args = new ArrayList<String>();
		args.add(sectionId.toString());
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT alignment, settlement_type, size, corruption, crime, economy, law,");
		sb.append("  lore, society, qualities, danger, disadvantages, government,");
		sb.append("  population, base_value, purchase_limit, spellcasting,");
		sb.append("  minor_items, medium_items, major_items");
		sb.append(" FROM settlement_details");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public static class SettlementUtils {
		public static String getAlignment(Cursor cursor) {
			return cursor.getString(0);
		}
		public static String getSettlementType(Cursor cursor) {
			return cursor.getString(1);
		}
		public static String getSize(Cursor cursor) {
			return cursor.getString(2);
		}
		public static String getCorruption(Cursor cursor) {
			return cursor.getString(3);
		}
		public static String getCrime(Cursor cursor) {
			return cursor.getString(4);
		}
		public static String getEconomy(Cursor cursor) {
			return cursor.getString(5);
		}
		public static String getLaw(Cursor cursor) {
			return cursor.getString(6);
		}
		public static String getLore(Cursor cursor) {
			return cursor.getString(7);
		}
		public static String getSociety(Cursor cursor) {
			return cursor.getString(8);
		}
		public static String getQualities(Cursor cursor) {
			return cursor.getString(9);
		}
		public static String getDanger(Cursor cursor) {
			return cursor.getString(10);
		}
		public static String getDisadvantages(Cursor cursor) {
			return cursor.getString(11);
		}
		public static String getGovernment(Cursor cursor) {
			return cursor.getString(12);
		}
		public static String getPopulation(Cursor cursor) {
			return cursor.getString(13);
		}
		public static String getBaseValue(Cursor cursor) {
			return cursor.getString(14);
		}
		public static String getPurchaseLimit(Cursor cursor) {
			return cursor.getString(15);
		}
		public static String getSpellcasting(Cursor cursor) {
			return cursor.getString(16);
		}
		public static String getMinorItems(Cursor cursor) {
			return cursor.getString(17);
		}
		public static String getMediumItems(Cursor cursor) {
			return cursor.getString(18);
		}
		public static String getMajorItems(Cursor cursor) {
			return cursor.getString(19);
		}
	}
}
