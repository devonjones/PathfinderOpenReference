package org.evilsoft.pathfinder.reference.db.book;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class HauntAdapter {
	public SQLiteDatabase database;
	public String dbName;

	public HauntAdapter(SQLiteDatabase database, String dbName) {
		this.database = database;
		this.dbName = dbName;
	}

	public Cursor getHauntDetails(Integer sectionId) {
		List<String> args = new ArrayList<String>();
		args.add(sectionId.toString());
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT cr, xp, haunt_type, notice, area, hp, destruction, alignment, caster_level, effect, trigger, reset");
		sb.append(" FROM haunt_details");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public static class HauntUtils {
		private HauntUtils() {
		}

		public static String getCr(Cursor cursor) {
			return cursor.getString(0);
		}
		public static String getXp(Cursor cursor) {
			return cursor.getString(1);
		}
		public static String getHauntType(Cursor cursor) {
			return cursor.getString(2);
		}
		public static String getNotice(Cursor cursor) {
			return cursor.getString(3);
		}
		public static String getArea(Cursor cursor) {
			return cursor.getString(4);
		}
		public static String getHp(Cursor cursor) {
			return cursor.getString(5);
		}
		public static String getDestruction(Cursor cursor) {
			return cursor.getString(6);
		}
		public static String getAlignment(Cursor cursor) {
			return cursor.getString(7);
		}
		public static String getCasterLevel(Cursor cursor) {
			return cursor.getString(8);
		}
		public static String getEffect(Cursor cursor) {
			return cursor.getString(9);
		}
		public static String getTrigger(Cursor cursor) {
			return cursor.getString(10);
		}
		public static String getReset(Cursor cursor) {
			return cursor.getString(11);
		}
	}
}
