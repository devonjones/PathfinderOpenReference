package org.evilsoft.pathfinder.reference.db.psrd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.evilsoft.pathfinder.reference.preference.FilterPreferenceManager;

import android.database.Cursor;

public class ClassAdapter {
	private PsrdDbAdapter dbAdapter;

	public ClassAdapter(PsrdDbAdapter dbAdapter) {
		this.dbAdapter = dbAdapter;
	}

	public Cursor fetchClassDetails(String section_id) {
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT alignment, hit_die");
		sb.append(" FROM class_details");
		sb.append(" WHERE section_id = ?");
		args.add(section_id);
		String sql = sb.toString();
		return dbAdapter.database.rawQuery(sql,
				PsrdDbAdapter.toStringArray(args));
	}

	public Cursor fetchClassTypes() {
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT DISTINCT subtype");
		sb.append(" FROM sections");
		sb.append(" WHERE type = 'class'");
		sb.append(FilterPreferenceManager.getSourceFilter(args, "AND"));
		sb.append(" ORDER BY subtype");
		String sql = sb.toString();
		return dbAdapter.database.rawQuery(sql, PsrdDbAdapter.toStringArray(args));
	}

	public Cursor fetchClassList(String classType) {
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT section_id, name");
		sb.append(" FROM sections");
		sb.append("  WHERE type = 'class'");
		sb.append(FilterPreferenceManager.getSourceFilter(args, "AND"));
		if (classType != null) {
			sb.append("   AND subtype = ?");
			args.add(classType);
		}
		sb.append(" ORDER BY name");
		String sql = sb.toString();
		return dbAdapter.database.rawQuery(sql,
				PsrdDbAdapter.toStringArray(args));
	}

	public ArrayList<HashMap<String, Object>> createClassTypeList() {
		Cursor curs = fetchClassTypes();
		try {
			ArrayList<HashMap<String, Object>> secList = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> child = new HashMap<String, Object>();
			boolean has_next = curs.moveToFirst();
			while (has_next) {
				String classType = curs.getString(0);
				String classTitle = classType.substring(0, 1).toUpperCase()
						+ classType.substring(1) + " Classes";
				child = new HashMap<String, Object>();
				child.put("specificName", classTitle);
				child.put("id", classType);
				secList.add(child);
				has_next = curs.moveToNext();
			}
			return secList;
		} finally {
			curs.close();
		}
	}

	public static String classTypeFromTitle(String title) {
		String first = title.substring(0, title.indexOf(' '));
		return first.toLowerCase();
	}
}
