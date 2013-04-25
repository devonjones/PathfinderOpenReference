package org.evilsoft.pathfinder.reference.db.book;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CreatureAdapter {
	public SQLiteDatabase database;
	public String dbName;

	public CreatureAdapter(SQLiteDatabase database, String dbName) {
		this.database = database;
		this.dbName = dbName;
	}

	public Cursor getCreatureDetails(Integer sectionId) {
		List<String> args = new ArrayList<String>();
		args.add(sectionId.toString());
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT sex, super_race, level, cr, xp, alignment, size, creature_type, creature_subtype, init,");
		sb.append("  senses, aura,");
		sb.append("  ac, hp, fortitude, reflex, will, defensive_abilities, dr, resist, immune, sr, weaknesses,");
		sb.append("  speed, melee, ranged, space, reach, special_attacks,");
		sb.append("  strength, dexterity, constitution, intelligence, wisdom, charisma,");
		sb.append("  base_attack, cmb, cmd, feats, skills, racial_modifiers, languages, special_qualities,");
		sb.append("  gear, combat_gear, other_gear, boon,");
		sb.append("  environment, organization, treasure,");
		sb.append("  hit_dice, natural_armor, breath_weapon");
		sb.append(" FROM creature_details");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public Cursor getCreatureSpells(Integer sectionId) {
		List<String> args = new ArrayList<String>();
		args.add(sectionId.toString());
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT name, body");
		sb.append(" FROM creature_spells");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}
}
