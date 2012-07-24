package org.evilsoft.pathfinder.reference.db.psrd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.database.Cursor;

public class MonsterAdapter {
	private PsrdDbAdapter dbAdapter;

	public MonsterAdapter(PsrdDbAdapter dbAdapter) {
		this.dbAdapter = dbAdapter;
	}

	public Cursor fetchMonsterList() {
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT s.section_id, s.name, s.description, cd.creature_type, cd.creature_subtype,");
		sb.append("  cd.cr, cd.xp, cd.size, cd.alignment");
		sb.append(" FROM sections s");
		sb.append("  INNER JOIN creature_details cd");
		sb.append("   ON s.section_id = cd.section_id");
		sb.append(" ORDER BY s.name");
		String sql = sb.toString();
		return dbAdapter.database.rawQuery(sql,
				PsrdDbAdapter.toStringArray(args));
	}

	public Cursor fetchMonstersByType(String creatureType) {
		List<String> args = new ArrayList<String>();
		args.add(creatureType);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT s.section_id, s.name, s.description, cd.creature_type, cd.creature_subtype,");
		sb.append("  cd.cr, cd.xp, cd.size, cd.alignment");
		sb.append(" FROM sections s, creature_details cd");
		sb.append(" WHERE s.section_id = cd.section_id");
		sb.append("  AND cd.creature_type = ?");
		sb.append(" ORDER BY s.name");
		String sql = sb.toString();
		return dbAdapter.database.rawQuery(sql,
				PsrdDbAdapter.toStringArray(args));
	}

	public Cursor fetchMonsterTypes() {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT DISTINCT creature_type");
		sb.append(" FROM creature_details dt");
		sb.append(" WHERE creature_type IS NOT NULL");
		sb.append(" ORDER BY creature_type");
		String sql = sb.toString();
		String[] selectionArgs = new String[0];
		return dbAdapter.database.rawQuery(sql, selectionArgs);
	}

	public Cursor getCreatureDetails(String sectionId) {
		List<String> args = new ArrayList<String>();
		args.add(sectionId);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT sex, super_race, level, cr, xp, alignment, size, creature_type, creature_subtype, init,");
		sb.append("  senses, aura,");
		sb.append("  ac, hp, fortitude, reflex, will, defensive_abilities, dr, resist, immune, sr, weaknesses,");
		sb.append("  speed, melee, ranged, space, reach, special_attacks,");
		sb.append("  strength, dexterity, constitution, intelligence, wisdom, charisma,");
		sb.append("  base_attack, cmb, cmd, feats, skills, racial_modifiers, languages, special_qualities, gear,");
		sb.append("  environment, organization, treasure,");
		sb.append("  hit_dice, natural_armor, breath_weapon");
		sb.append(" FROM creature_details");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return dbAdapter.database.rawQuery(sql,
				PsrdDbAdapter.toStringArray(args));
	}

	public Cursor getCreatureSpells(String sectionId) {
		List<String> args = new ArrayList<String>();
		args.add(sectionId);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT name, body");
		sb.append(" FROM creature_spells");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return dbAdapter.database.rawQuery(sql,
				PsrdDbAdapter.toStringArray(args));
	}

	public ArrayList<HashMap<String, Object>> createMonsterTypeList() {
		Cursor curs = fetchMonsterTypes();
		try {
			ArrayList<HashMap<String, Object>> secList = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> child = new HashMap<String, Object>();
			child.put("specificName", "All Monsters");
			child.put("id", null);
			secList.add(child);
			boolean has_next = curs.moveToFirst();
			while (has_next) {
				String creatureType = curs.getString(0);
				child = new HashMap<String, Object>();
				child.put("specificName", titleCase(creatureType));
				child.put("id", creatureType);
				secList.add(child);
				has_next = curs.moveToNext();
			}
			return secList;
		} finally {
			curs.close();
		}
	}

	private String titleCase(String name) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < name.length(); i++) {
			if (i == 0) {
				sb.append(Character.toUpperCase(name.charAt(i)));
			} else if (name.charAt(i - 1) == ' ' && name.charAt(i) != ' ') {
				sb.append(Character.toUpperCase(name.charAt(i)));
			} else {
				sb.append(name.charAt(i));
			}
		}
		return sb.toString();
	}
}
