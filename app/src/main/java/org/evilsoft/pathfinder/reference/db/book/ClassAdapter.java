package org.evilsoft.pathfinder.reference.db.book;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ClassAdapter {
	public SQLiteDatabase database;
	public String dbName;

	public ClassAdapter(SQLiteDatabase database, String dbName) {
		this.database = database;
		this.dbName = dbName;
	}

	public Cursor fetchClassDetails(Integer section_id) {
		List<String> args = new ArrayList<String>();
		args.add(section_id.toString());
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT alignment, hit_die");
		sb.append(" FROM class_details");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public static class ClassUtils {
		public static String getAlignment(Cursor cursor) {
			return cursor.getString(0);
		}
		public static String getHitDie(Cursor cursor) {
			return cursor.getString(1);
		}
	}
}
