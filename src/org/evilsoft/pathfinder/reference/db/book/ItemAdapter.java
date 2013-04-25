package org.evilsoft.pathfinder.reference.db.book;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ItemAdapter {
	public SQLiteDatabase database;
	public String dbName;

	public ItemAdapter(SQLiteDatabase database, String dbName) {
		this.database = database;
		this.dbName = dbName;
	}

	public Cursor getItemDetails(Integer sectionId) {
		List<String> args = new ArrayList<String>();
		args.add(sectionId.toString());
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT aura, slot, cl, price, weight, requirements, skill, ");
		sb.append("  cr_increase, cost");
		sb.append(" FROM item_details");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public static class ItemUtils {
		public static String getAura(Cursor cursor) {
			return cursor.getString(0);
		}

		public static String getSlot(Cursor cursor) {
			return cursor.getString(1);
		}

		public static String getCl(Cursor cursor) {
			return cursor.getString(2);
		}

		public static String getPrice(Cursor cursor) {
			return cursor.getString(3);
		}

		public static String getWeight(Cursor cursor) {
			return cursor.getString(4);
		}

		public static String getRequirements(Cursor cursor) {
			return cursor.getString(5);
		}

		public static String getSkill(Cursor cursor) {
			return cursor.getString(6);
		}

		public static String getCrIncrease(Cursor cursor) {
			return cursor.getString(7);
		}

		public static String getCost(Cursor cursor) {
			return cursor.getString(8);
		}
	}

	public Cursor getItemMisc(Integer sectionId) {
		List<String> args = new ArrayList<String>();
		args.add(sectionId.toString());
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT field, subsection, value");
		sb.append(" FROM item_misc");
		sb.append(" WHERE section_id = ?");
		sb.append(" ORDER BY subsection");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public static class ItemMiscUtils {
		public static String getField(Cursor cursor) {
			return cursor.getString(0);
		}

		public static String getSubsection(Cursor cursor) {
			return cursor.getString(1);
		}

		public static String getValue(Cursor cursor) {
			return cursor.getString(2);
		}
	}
}
