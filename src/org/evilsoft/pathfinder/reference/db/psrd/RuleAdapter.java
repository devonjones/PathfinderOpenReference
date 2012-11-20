package org.evilsoft.pathfinder.reference.db.psrd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.evilsoft.pathfinder.reference.preference.FilterPreferenceManager;

import android.database.Cursor;

public class RuleAdapter {
	private PsrdDbAdapter dbAdapter;

	public RuleAdapter(PsrdDbAdapter dbAdapter) {
		this.dbAdapter = dbAdapter;
	}

	public Cursor fetchRuleList(String parentId) {
		List<String> args = new ArrayList<String>();
		args.add(parentId);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT section_id, name, url");
		sb.append(" FROM sections");
		sb.append(" WHERE parent_id = ?");
		sb.append(FilterPreferenceManager.getSourceFilter("AND"));
		sb.append(" ORDER BY section_id");
		String sql = sb.toString();
		return dbAdapter.database.rawQuery(sql,
				PsrdDbAdapter.toStringArray(args));
	}

	public Cursor fetchRuleListByUrl(String url) {
		List<String> args = new ArrayList<String>();
		args.add(url);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT s.section_id, s.name");
		sb.append(" FROM sections s");
		sb.append("  INNER JOIN sections p");
		sb.append("   ON s.parent_id = p.section_id");
		sb.append(" WHERE p.url = ?");
		sb.append(" ORDER BY s.section_id");
		String sql = sb.toString();
		return dbAdapter.database.rawQuery(sql,
				PsrdDbAdapter.toStringArray(args));
	}

	public ArrayList<HashMap<String, Object>> createRuleList(String parentId) {
		Cursor curs = fetchRuleList(parentId);
		try {
			ArrayList<HashMap<String, Object>> secList = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> child = new HashMap<String, Object>();
			boolean has_next = curs.moveToFirst();
			while (has_next) {
				String ruleId = curs.getString(0);
				String ruleName = curs.getString(1);
				String url = curs.getString(2);
				child = new HashMap<String, Object>();
				child.put("specificName", ruleName);
				child.put("id", ruleId);
				child.put("url", url);
				secList.add(child);
				has_next = curs.moveToNext();
			}
			return secList;
		} finally {
			curs.close();
		}
	}
}
