package org.evilsoft.pathfinder.reference.db.psrd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.evilsoft.pathfinder.reference.preference.FilterPreferenceManager;

import android.database.Cursor;

public class FeatAdapter {
	private PsrdDbAdapter dbAdapter;

	public FeatAdapter(PsrdDbAdapter dbAdapter) {
		this.dbAdapter = dbAdapter;
	}

	public Cursor fetchFeatTypes() {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT DISTINCT feat_type");
		sb.append(" FROM feat_types ft");
		sb.append(" ORDER BY feat_type");
		String sql = sb.toString();
		String[] selectionArgs = new String[0];
		return dbAdapter.database.rawQuery(sql, selectionArgs);
	}

	public Cursor fetchFeatTypeDescriptionForSection(String section_id) {
		List<String> args = new ArrayList<String>();
		args.add(section_id);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT feat_type_description");
		sb.append(" FROM feat_type_descriptions");
		sb.append(" WHERE section_id = ?");
		sb.append(" ORDER BY feat_type_description");
		String sql = sb.toString();
		return dbAdapter.database.rawQuery(sql, PsrdDbAdapter.toStringArray(args));
	}

	public Cursor fetchFeatsByType(String feat_type) {
		List<String> args = new ArrayList<String>();
		args.add(feat_type);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT s.section_id as _id, s.*");
		sb.append(" FROM sections s, feat_types ft");
		sb.append(" WHERE s.section_id = ft.section_id");
		sb.append(FilterPreferenceManager.getSourceFilter("AND"));
		sb.append("  AND ft.feat_type = ?");
		sb.append(" ORDER BY s.name");
		String sql = sb.toString();
		return dbAdapter.database.rawQuery(sql, PsrdDbAdapter.toStringArray(args));
	}

	public Cursor fetchFeatList() {
		return fetchFeatList(null);
	}

	public Cursor fetchFeatList(String featType) {
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT s.section_id, s.name, s.description, ");
		sb.append(" (SELECT p.description");
		sb.append("  FROM sections p");
		sb.append("   WHERE p.parent_id = s.section_id");
		sb.append("    AND p.name = 'Prerequisites'");
		sb.append("  LIMIT 1) as preqs,");
		sb.append(" (SELECT ftd.feat_type_description");
		sb.append("  FROM feat_type_descriptions ftd");
		sb.append("   WHERE ftd.section_id = s.section_id");
		sb.append("  LIMIT 1) as feat_types");
		sb.append(" FROM sections s");
		if (featType != null) {
			sb.append("  INNER JOIN feat_types ft_filter");
			sb.append("   ON s.section_id = ft_filter.section_id");
			sb.append("    AND ft_filter.feat_type = ?");
			args.add(featType);
		}
		sb.append(" WHERE s.type = 'feat'");
		sb.append(FilterPreferenceManager.getSourceFilter("AND"));
		sb.append(" ORDER BY s.name");
		String sql = sb.toString();
		return dbAdapter.database.rawQuery(sql, PsrdDbAdapter.toStringArray(args));
	}

	public ArrayList<HashMap<String, Object>> createFeatTypeList() {
		Cursor curs = fetchFeatTypes();
		try {
			ArrayList<HashMap<String, Object>> secList = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> child = new HashMap<String, Object>();
			child.put("specificName", "All Feats");
			child.put("id", null);
			secList.add(child);
			boolean has_next = curs.moveToFirst();
			while (has_next) {
				String featType = curs.getString(0);
				child = new HashMap<String, Object>();
				child.put("specificName", featType);
				child.put("id", featType);
				secList.add(child);
				has_next = curs.moveToNext();
			}
			return secList;
		} finally {
			curs.close();
		}
	}

	public String renderFeatTypeDescription(String section_id) {
		Cursor curs = fetchFeatTypeDescriptionForSection(section_id);
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
}
