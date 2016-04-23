package org.evilsoft.pathfinder.reference.db.book;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SpellSubschoolAdapter {
	public SQLiteDatabase database;
	public String dbName;

	public SpellSubschoolAdapter(SQLiteDatabase database, String dbName) {
		this.database = database;
		this.dbName = dbName;
	}

	public Cursor getSpellSubschools(Integer sectionId) {
		List<String> args = new ArrayList<String>();
		args.add(sectionId.toString());
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT subschool");
		sb.append(" FROM spell_subschools");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public static class SpellSubschoolUtils {
		public static String getSubschool(Cursor cursor) {
			return cursor.getString(0);
		}
	}
}
