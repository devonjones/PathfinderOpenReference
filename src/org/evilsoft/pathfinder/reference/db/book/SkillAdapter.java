package org.evilsoft.pathfinder.reference.db.book;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SkillAdapter {
	public SQLiteDatabase database;
	public String dbName;

	public SkillAdapter(SQLiteDatabase database, String dbName) {
		this.database = database;
		this.dbName = dbName;
	}

	public Cursor fetchSkillAttr(Integer section_id) {
		List<String> args = new ArrayList<String>();
		args.add(section_id.toString());
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT attribute, armor_check_penalty, trained_only");
		sb.append(" FROM skill_attributes");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public static class SkillUtils {
		public static String getAttribute(Cursor cursor) {
			return cursor.getString(0);
		}
		public static Integer getArmorCheckPenalty(Cursor cursor) {
			return cursor.getInt(1);
		}
		public static Integer getTrainedOnly(Cursor cursor) {
			return cursor.getInt(2);
		}
	}
}
