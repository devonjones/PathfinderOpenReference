package org.evilsoft.pathfinder.reference.db.book;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TrapAdapter {
	public SQLiteDatabase database;
	public String dbName;

	public TrapAdapter(SQLiteDatabase database, String dbName) {
		this.database = database;
		this.dbName = dbName;
	}

	public Cursor getTrapDetails(Integer sectionId) {
		List<String> args = new ArrayList<String>();
		args.add(sectionId.toString());
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT cr, trap_type, perception, disable_device, duration, effect, trigger, reset");
		sb.append(" FROM trap_details");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public static class TrapUtils {
		public static String getCr(Cursor cursor) {
			return cursor.getString(0);
		}
		public static String getTrapType(Cursor cursor) {
			return cursor.getString(1);
		}
		public static String getPerception(Cursor cursor) {
			return cursor.getString(2);
		}
		public static String getDisableDevice(Cursor cursor) {
			return cursor.getString(3);
		}
		public static String getDuration(Cursor cursor) {
			return cursor.getString(4);
		}
		public static String getEffect(Cursor cursor) {
			return cursor.getString(5);
		}
		public static String getTrigger(Cursor cursor) {
			return cursor.getString(6);
		}
		public static String getReset(Cursor cursor) {
			return cursor.getString(7);
		}
	}
}
