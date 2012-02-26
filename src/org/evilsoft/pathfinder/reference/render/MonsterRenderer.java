package org.evilsoft.pathfinder.reference.render;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.psrd.MonsterAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;

import android.database.Cursor;

public class MonsterRenderer extends StatBlockRenderer {
	private MonsterAdapter monsterAdapter;
	private PsrdDbAdapter dbAdapter;

	public MonsterRenderer(PsrdDbAdapter dbAdapter) {
		this.dbAdapter = dbAdapter;
	}

	private MonsterAdapter getMonsterAdapter() {
		if (this.monsterAdapter == null) {
			this.monsterAdapter = new MonsterAdapter(this.dbAdapter);
		}
		return this.monsterAdapter;
	}

	@Override
	public String renderTitle() {
		Cursor curs = this.getMonsterAdapter().getCreatureDetails(sectionId);
		boolean has_next = curs.moveToFirst();
		String title = name;
		if (has_next) {
			String cr = curs.getString(3);
			if (cr != null) {
				title = title + " (CR " + cr + ")";
			}
		}
		return renderStatBlockTitle(title, newUri, top);
	}

	@Override
	public String renderDetails() {
		StringBuffer sb = new StringBuffer();
		Cursor curs = this.getMonsterAdapter().getCreatureDetails(sectionId);
		boolean has_next = curs.moveToFirst();
		if (has_next) {
			sb.append(renderCreatureHeader(curs, desc, source, newUri, top));
			sb.append(renderCreatureDefense(curs));
			sb.append(renderCreatureOffense(curs));
			sb.append(renderCreatureSpells(sectionId));
			sb.append(renderCreatureStatistics(curs));
			sb.append(renderCreatureEcology(curs));
		}
		return sb.toString();
	}

	public String renderDescription() {
		return "";
	}

	private String renderCreatureHeader(Cursor curs, String desc, String source, String newUri, boolean top) {
		// 0:sex, 1:super_race, 2:level, 3:cr, 4:xp, 5:alignment, 6:size,
		// 7:creature_type, 8:creature_subtype, 9:init, 10:senses, 11:aura,
		StringBuffer sb = new StringBuffer();
		String cr = curs.getString(3);
		if (desc != null) {
			sb.append("<p>");
			sb.append(desc);
			sb.append("</p>");
		}
		if (top) {
			sb.append("<b>CR ");
			sb.append(cr);
			sb.append("</b><br>\n");
		}
		String xp = curs.getString(4);
		if (xp != null) {
			sb.append("<b>XP ");
			sb.append(xp);
			sb.append("</b><br>\n");
		}
		List<String> elems = new ArrayList<String>();
		elems.add(curs.getString(0));
		elems.add(curs.getString(1));
		elems.add(curs.getString(2));
		sb.append(displayLine(elems));
		elems.clear();
		elems.add(curs.getString(5));
		elems.add(curs.getString(6));
		elems.add(curs.getString(7));
		String subtype = curs.getString(8);
		if (subtype != null) {
			elems.add("(" + subtype + ")");
		}
		sb.append(displayLine(elems));
		sb.append(addField("Init", curs.getString(9), false));
		sb.append(addField("Senses", curs.getString(10)));
		sb.append(addField("Aura", curs.getString(11)));
		sb.append(addField("Source", source));
		return sb.toString();
	}

	private String renderCreatureDefense(Cursor curs) {
		StringBuffer sb = new StringBuffer();
		// 12:ac, 13:hp, 14:fortitude, 15:reflex, 16:will,
		// 17:defensive_abilities, 18:dr, 19:resist, 20:immune,
		// 21:sr, 22:weakness,
		sb.append(renderStatBlockBreaker("Defense"));
		sb.append(addField("AC", curs.getString(12)));
		sb.append(addField("HP", curs.getString(13)));
		sb.append(addField("Fort", curs.getString(14), false));
		sb.append(addField("Ref", curs.getString(15), false));
		sb.append(addField("Will", curs.getString(16)));
		sb.append(addField("Defensive Abilities", curs.getString(17), false));
		sb.append(addField("DR", curs.getString(18), false));
		sb.append(addField("Resist", curs.getString(19), false));
		sb.append(addField("Immune", curs.getString(20), false));
		sb.append(addField("SR", curs.getString(21), false));
		sb.append("<br>\n");
		sb.append(addField("Weakness", curs.getString(22)));
		return sb.toString();
	}

	private String renderCreatureOffense(Cursor curs) {
		StringBuffer sb = new StringBuffer();
		// 23:speed, 24:melee, 25:ranged, 26:space, 27:reach,
		// 28:special_attacks,
		sb.append(renderStatBlockBreaker("Offense"));
		sb.append(addField("Speed", curs.getString(23)));
		sb.append(addField("Melee", curs.getString(24)));
		sb.append(addField("Ranged", curs.getString(25)));
		sb.append(addField("Space", curs.getString(26)));
		sb.append(addField("Reach", curs.getString(27)));
		sb.append(addField("Special Attacks", curs.getString(28)));
		return sb.toString();
	}

	private String renderCreatureSpells(String sectionId) {
		StringBuffer sb = new StringBuffer();
		Cursor curs = this.getMonsterAdapter().getCreatureSpells(sectionId);
		// 0:name, 1:body
		boolean has_next = curs.moveToFirst();
		while (has_next) {
			sb.append(addField(curs.getString(0), curs.getString(1)));
			has_next = curs.moveToNext();
		}
		return sb.toString();
	}

	private String renderCreatureStatistics(Cursor curs) {
		StringBuffer sb = new StringBuffer();
		// 29:strength, 30:dexterity, 31:constitution, 32:intelligence,
		// 33:wisdom, 34:charisma,
		// 35:base_attack, 36:cmb, 37:cmd, 38:feats, 39:skills,
		// 40:racial_modifiers, 41:languages, 42:special_abilities, 43:gear,
		sb.append(renderStatBlockBreaker("Statistics"));
		sb.append(addField("Str", curs.getString(29), false));
		sb.append(addField("Dex", curs.getString(30), false));
		sb.append(addField("Con", curs.getString(31), false));
		sb.append(addField("Int", curs.getString(32), false));
		sb.append(addField("Wis", curs.getString(33), false));
		sb.append(addField("Cha", curs.getString(34)));
		sb.append(addField("Base Atk", curs.getString(35), false));
		sb.append(addField("CMB", curs.getString(36), false));
		sb.append(addField("CMD", curs.getString(37)));
		sb.append(addField("Feats", curs.getString(38)));
		boolean rmExists = curs.getString(40) == null;
		sb.append(addField("Skills", curs.getString(39), rmExists));
		sb.append(addField("Racial Modifiers", curs.getString(40)));
		sb.append(addField("Languages", curs.getString(41)));
		sb.append(addField("Special Abilities", curs.getString(42)));
		sb.append(addField("Gear", curs.getString(43)));
		return sb.toString();
	}

	private String renderCreatureEcology(Cursor curs) {
		StringBuffer sb = new StringBuffer();
		// 44:environment, 45:organization, 46:treasure
		sb.append(renderStatBlockBreaker("Ecology"));
		sb.append(addField("Environment", curs.getString(44)));
		sb.append(addField("Organization", curs.getString(45)));
		sb.append(addField("Treasure", curs.getString(46)));
		return sb.toString();
	}
}
