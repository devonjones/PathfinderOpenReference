package org.evilsoft.pathfinder.reference.db.psrd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
		sb.append("SELECT section_id, name");
		sb.append(" FROM sections");
		sb.append(" WHERE parent_id = ?");
		sb.append(" ORDER BY section_id");
		String sql = sb.toString();
		return dbAdapter.database.rawQuery(sql, PsrdDbAdapter.toStringArray(args));
	}

	public ArrayList<HashMap<String, Object>> createRuleList(String parentId) {
		ArrayList<HashMap<String, Object>> secList = new ArrayList<HashMap<String, Object>>();
		Cursor curs = fetchRuleList(parentId);
		HashMap<String, Object> child = new HashMap<String, Object>();
		boolean has_next = curs.moveToFirst();
		while (has_next) {
			String ruleId = curs.getString(0);
			String ruleName = curs.getString(1);
			child = new HashMap<String, Object>();
			child.put("specificName", ruleName);
			child.put("id", ruleId);
			secList.add(child);
			has_next = curs.moveToNext();
		}
		return secList;
	}
}
