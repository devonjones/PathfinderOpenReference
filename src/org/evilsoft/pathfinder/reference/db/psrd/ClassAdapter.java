package org.evilsoft.pathfinder.reference.db.psrd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.database.Cursor;

public class ClassAdapter {
	private PsrdDbAdapter dbAdapter;

	public ClassAdapter(PsrdDbAdapter dbAdapter) {
		this.dbAdapter = dbAdapter;
	}

	public Cursor fetchClassTypes() {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT DISTINCT subtype");
		sb.append(" FROM sections");
		sb.append(" WHERE type = 'class'");
		sb.append(" ORDER BY subtype");
		String sql = sb.toString();
		String[] selectionArgs = new String[0];
		return dbAdapter.database.rawQuery(sql, selectionArgs);
	}

	public Cursor fetchClassList(String classType) {
		List<String> args = new ArrayList<String>();
		args.add(classType);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT section_id, name");
		sb.append(" FROM sections");
		sb.append("  WHERE type = 'class'");
		sb.append("   AND subtype = ?");
		sb.append(" ORDER BY name");
		String sql = sb.toString();
		return dbAdapter.database.rawQuery(sql, PsrdDbAdapter.toStringArray(args));
	}

	public ArrayList<HashMap<String, Object>> createClassTypeList() {
		ArrayList<HashMap<String, Object>> secList = new ArrayList<HashMap<String, Object>>();
		Cursor curs = fetchClassTypes();
		HashMap<String, Object> child = new HashMap<String, Object>();
		boolean has_next = curs.moveToFirst();
		while (has_next) {
			String classType = curs.getString(0);
			String classTitle = classType.substring(0, 1).toUpperCase() + classType.substring(1) + " Classes";
			child = new HashMap<String, Object>();
			child.put("specificName", classTitle);
			child.put("id", classType);
			secList.add(child);
			has_next = curs.moveToNext();
		}
		return secList;
	}

	public static String classTypeFromTitle(String title) {
		String first = title.substring(0, title.indexOf(' '));
		return first.toLowerCase();
	}
}
