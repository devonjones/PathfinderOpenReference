package org.evilsoft.pathfinder.reference.db.book;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class FeatAdapter {
	public SQLiteDatabase database;
	public String dbName;

	public FeatAdapter(SQLiteDatabase database, String dbName) {
		this.database = database;
		this.dbName = dbName;
	}

	public Cursor fetchFeatTypeDescriptionForSection(Integer sectionId) {
		List<String> args = new ArrayList<String>();
		args.add(sectionId.toString());
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT feat_type_description");
		sb.append(" FROM feat_type_descriptions");
		sb.append(" WHERE section_id = ?");
		sb.append(" ORDER BY feat_type_description");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public String renderFeatTypeDescription(Integer sectionId) {
		Cursor curs = fetchFeatTypeDescriptionForSection(sectionId);
		try {
			StringBuffer sb = new StringBuffer();
			boolean has_next = curs.moveToFirst();
			while (has_next) {
				sb.append(curs.getString(0));
				has_next = curs.moveToNext();
			}
			return sb.toString();
		} finally {
			curs.close();
		}
	}

	public Cursor getFeatTypes(Integer sectionId) {
		List<String> args = new ArrayList<String>();
		args.add(sectionId.toString());
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT feat_type");
		sb.append(" FROM feat_types");
		sb.append(" WHERE section_id = ?");
		sb.append(" ORDER BY feat_type");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public static class FeatTypeUtils {
		public static String getFeatType(Cursor cursor) {
			return cursor.getString(0);
		}
	}
}
