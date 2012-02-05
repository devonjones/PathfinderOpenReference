package org.evilsoft.pathfinder.reference.db.psrd;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

public class SkillAdapter {
	private PsrdDbAdapter dbAdapter;

	public SkillAdapter(PsrdDbAdapter dbAdapter) {
		this.dbAdapter = dbAdapter;
	}

	public Cursor fetchSkillAttr(String section_id) {
		List<String> args = new ArrayList<String>();
		args.add(section_id);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT attribute, armor_check_penalty, trained_only");
		sb.append(" FROM skill_attributes");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return dbAdapter.database.rawQuery(sql, dbAdapter.toStringArray(args));
	}

	public Cursor fetchSkillList() {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT s.section_id as _id, s.name, s.description, sa.attribute, sa.armor_check_penalty, sa.trained_only");
		sb.append(" FROM sections s");
		sb.append("  INNER JOIN skill_attributes sa");
		sb.append("   ON s.section_id = sa.section_id");
		sb.append(" WHERE s.type = 'skill'");
		sb.append(" ORDER BY s.name");
		String sql = sb.toString();
		return dbAdapter.database.rawQuery(sql, new String[0]);
	}
}
