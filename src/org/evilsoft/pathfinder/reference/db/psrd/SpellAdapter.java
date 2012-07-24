package org.evilsoft.pathfinder.reference.db.psrd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.database.Cursor;

public class SpellAdapter {
	private PsrdDbAdapter dbAdapter;

	public SpellAdapter(PsrdDbAdapter dbAdapter) {
		this.dbAdapter = dbAdapter;
	}

	public Cursor fetchSpellComponents(String section_id) {
		List<String> args = new ArrayList<String>();
		args.add(section_id);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT component_type, description");
		sb.append(" FROM spell_components");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return dbAdapter.database.rawQuery(sql, PsrdDbAdapter.toStringArray(args));
	}

	public Cursor fetchSpellDetails(String section_id) {
		List<String> args = new ArrayList<String>();
		args.add(section_id);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT school, subschool, descriptor_text, level_text, casting_time, preparation_time, range, duration, saving_throw, spell_resistance, as_spell_id");
		sb.append(" FROM spell_details");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return dbAdapter.database.rawQuery(sql, PsrdDbAdapter.toStringArray(args));
	}

	public Cursor fetchSpellEffects(String section_id) {
		List<String> args = new ArrayList<String>();
		args.add(section_id);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT name, description");
		sb.append(" FROM spell_effects");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return dbAdapter.database.rawQuery(sql, PsrdDbAdapter.toStringArray(args));
	}

	public Cursor fetchSpellClasses() {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT DISTINCT class");
		sb.append(" FROM spell_lists");
		sb.append(" ORDER BY class");
		String sql = sb.toString();
		String[] selectionArgs = new String[0];
		return dbAdapter.database.rawQuery(sql, selectionArgs);
	}

	public Cursor fetchSpellList() {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT s.section_id, s.name, s.description, sd.school, sd.subschool");
		sb.append(" FROM sections s");
		sb.append("  INNER JOIN spell_details sd");
		sb.append("   ON s.section_id = sd.section_id");
		sb.append(" WHERE s.type = 'spell'");
		sb.append(" ORDER BY s.name");
		String sql = sb.toString();
		return dbAdapter.database.rawQuery(sql, new String[0]);
	}

	public Cursor fetchSpellList(String spellClass) {
		List<String> args = new ArrayList<String>();
		args.add(spellClass);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT s.section_id, s.name, s.description, sd.school, sd.subschool, sl_filter.level");
		sb.append(" FROM sections s");
		sb.append("  INNER JOIN spell_details sd");
		sb.append("   ON s.section_id = sd.section_id");
		sb.append("  INNER JOIN spell_lists sl_filter");
		sb.append("   ON s.section_id = sl_filter.section_id");
		sb.append("    AND sl_filter.class = ?");
		sb.append(" WHERE s.type = 'spell'");
		sb.append(" ORDER BY sl_filter.level, s.name");
		String sql = sb.toString();
		return dbAdapter.database.rawQuery(sql, PsrdDbAdapter.toStringArray(args));
	}

	public ArrayList<HashMap<String, Object>> createSpellClassList() {
		Cursor curs = fetchSpellClasses();
		try {
			ArrayList<HashMap<String, Object>> secList = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> child = new HashMap<String, Object>();
			child.put("specificName", "All");
			child.put("id", null);
			secList.add(child);
			boolean has_next = curs.moveToFirst();
			while (has_next) {
				String spellClass = curs.getString(0);
				child = new HashMap<String, Object>();
				child.put("specificName", spellClass);
				child.put("id", spellClass);
				secList.add(child);
				has_next = curs.moveToNext();
			}
			return secList;
		} finally {
			curs.close();
		}
	}
}
