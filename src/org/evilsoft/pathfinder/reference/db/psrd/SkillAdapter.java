package org.evilsoft.pathfinder.reference.db.psrd;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.preference.FilterPreferenceManager;

import android.database.Cursor;

public class SkillAdapter {
	private PsrdDbAdapter dbAdapter;

	public SkillAdapter(PsrdDbAdapter dbAdapter) {
		this.dbAdapter = dbAdapter;
	}

	public Cursor fetchSkillAttr(String section_id) {
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT attribute, armor_check_penalty, trained_only");
		sb.append(" FROM skill_attributes");
		sb.append(" WHERE section_id = ?");
		args.add(section_id);
		String sql = sb.toString();
		return dbAdapter.database.rawQuery(sql, PsrdDbAdapter.toStringArray(args));
	}

	public Cursor fetchSkillList() {
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT s.section_id as _id, s.name, s.description, sa.attribute, sa.armor_check_penalty, sa.trained_only");
		sb.append(" FROM sections s");
		sb.append("  INNER JOIN skill_attributes sa");
		sb.append("   ON s.section_id = sa.section_id");
		sb.append(" WHERE s.type = 'skill'");
		sb.append(FilterPreferenceManager.getSourceFilter(args, "AND"));
		sb.append(" ORDER BY s.name");
		String sql = sb.toString();
		return dbAdapter.database.rawQuery(sql,  PsrdDbAdapter.toStringArray(args));
	}
}
