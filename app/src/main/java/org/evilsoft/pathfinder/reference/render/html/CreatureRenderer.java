package org.evilsoft.pathfinder.reference.render.html;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.book.CreatureAdapter;

import android.database.Cursor;

public class CreatureRenderer extends StatBlockRenderer {
	private BookDbAdapter bookDbAdapter;

	public CreatureRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	@Override
	public String renderTitle() {
		Cursor curs = bookDbAdapter.getCreatureAdapter().getCreatureDetails(
				sectionId);
		try {
			boolean has_next = curs.moveToFirst();
			String title = name;
			if (has_next) {
				String cr = curs.getString(3);
				if (cr != null) {
					title = title + " (CR " + cr + ")";
				}
			}
			return renderStatBlockTitle(title, newUri, top);
		} finally {
			curs.close();
		}
	}

	@Override
	public String renderDetails() {
		Cursor curs = bookDbAdapter.getCreatureAdapter().getCreatureDetails(
				sectionId);
		try {
			StringBuffer sb = new StringBuffer();
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
		} finally {
			curs.close();
		}
	}

	public String renderDescription() {
		return "";
	}

	private String renderCreatureHeader(Cursor cursor, String desc,
			String source, String newUri, boolean top) {
		StringBuffer sb = new StringBuffer();
		if (desc != null) {
			sb.append("<p>");
			sb.append(desc);
			sb.append("</p>");
		}
		if (top) {
			sb.append("<b>CR ");
			sb.append(CreatureAdapter.CreatureUtils.getCR(cursor));
			sb.append("</b><br>\n");
		}
		String xp = CreatureAdapter.CreatureUtils.getXP(cursor);
		if (xp != null) {
			sb.append("<b>XP ");
			sb.append(xp);
			sb.append("</b><br>\n");
		}
		List<String> elems = new ArrayList<String>();
		elems.add(CreatureAdapter.CreatureUtils.getSex(cursor));
		elems.add(CreatureAdapter.CreatureUtils.getSuperRace(cursor));
		elems.add(CreatureAdapter.CreatureUtils.getLevel(cursor));
		sb.append(displayLine(elems));
		elems.clear();
		elems.add(CreatureAdapter.CreatureUtils.getAlignment(cursor));
		elems.add(CreatureAdapter.CreatureUtils.getSize(cursor));
		elems.add(CreatureAdapter.CreatureUtils.getCreatureType(cursor));
		String subtype = CreatureAdapter.CreatureUtils
				.getCreatureSubtype(cursor);
		if (subtype != null) {
			elems.add("(" + subtype + ")");
		}
		sb.append(displayLine(elems));
		sb.append(addField("Init",
				CreatureAdapter.CreatureUtils.getInit(cursor), false));
		sb.append(addField("Senses",
				CreatureAdapter.CreatureUtils.getSenses(cursor)));
		sb.append(addField("Aura",
				CreatureAdapter.CreatureUtils.getAura(cursor)));
		sb.append(addField("Source", source));
		return sb.toString();
	}

	private String renderCreatureDefense(Cursor cursor) {
		StringBuffer sb = new StringBuffer();
		sb.append(renderStatBlockBreaker("Defense"));
		sb.append(addField("Natural Armor",
				CreatureAdapter.CreatureUtils.getNaturalArmor(cursor), false));
		sb.append(addField("AC", CreatureAdapter.CreatureUtils.getAC(cursor)));
		sb.append(addField("Hit Dice",
				CreatureAdapter.CreatureUtils.getHitDice(cursor), false));
		sb.append(addField("HP", CreatureAdapter.CreatureUtils.getHP(cursor)));
		sb.append(addField("Fort",
				CreatureAdapter.CreatureUtils.getFortitude(cursor), false));
		sb.append(addField("Ref",
				CreatureAdapter.CreatureUtils.getReflex(cursor), false));
		sb.append(addField("Will",
				CreatureAdapter.CreatureUtils.getWill(cursor)));
		sb.append(addField("Defensive Abilities",
				CreatureAdapter.CreatureUtils.getDefensiveAbilities(cursor),
				false));
		sb.append(addField("DR", CreatureAdapter.CreatureUtils.getDR(cursor),
				false));
		sb.append(addField("Resist",
				CreatureAdapter.CreatureUtils.getResist(cursor), false));
		sb.append(addField("Immune",
				CreatureAdapter.CreatureUtils.getImmune(cursor), false));
		sb.append(addField("SR", CreatureAdapter.CreatureUtils.getSR(cursor),
				false));
		sb.append("<br>\n");
		sb.append(addField("Weakness",
				CreatureAdapter.CreatureUtils.getWeaknesses(cursor)));
		String retval = sb.toString();
		if (retval.equals(renderStatBlockBreaker("Defense"))) {
			return "";
		}
		return retval;
	}

	private String renderCreatureOffense(Cursor cursor) {
		StringBuffer sb = new StringBuffer();
		sb.append(renderStatBlockBreaker("Offense"));
		sb.append(addField("Speed",
				CreatureAdapter.CreatureUtils.getSpeed(cursor)));
		sb.append(addField("Melee",
				CreatureAdapter.CreatureUtils.getMelee(cursor)));
		sb.append(addField("Ranged",
				CreatureAdapter.CreatureUtils.getRanged(cursor)));
		sb.append(addField("Space",
				CreatureAdapter.CreatureUtils.getSpace(cursor)));
		sb.append(addField("Reach",
				CreatureAdapter.CreatureUtils.getReach(cursor)));
		sb.append(addField("Breath Weapon",
				CreatureAdapter.CreatureUtils.getBreathWeapon(cursor), false));
		sb.append(addField("Special Attacks",
				CreatureAdapter.CreatureUtils.getSpecialAttacks(cursor)));
		String retval = sb.toString();
		if (retval.equals(renderStatBlockBreaker("Offense"))) {
			return "";
		}
		return retval;
	}

	private String renderCreatureSpells(Integer sectionId) {
		Cursor cursor = bookDbAdapter.getCreatureAdapter().getCreatureSpells(
				sectionId);
		// 0:name, 1:body
		try {
			StringBuffer sb = new StringBuffer();
			boolean has_next = cursor.moveToFirst();
			while (has_next) {
				String name = capitalizeString(CreatureAdapter.CreatureSpellsUtils
						.getName(cursor));
				sb.append(addField(name,
						CreatureAdapter.CreatureSpellsUtils.getBody(cursor)));
				has_next = cursor.moveToNext();
			}
			return sb.toString();
		} finally {
			cursor.close();
		}
	}

	private String renderCreatureStatistics(Cursor cursor) {
		StringBuffer sb = new StringBuffer();
		sb.append(renderStatBlockBreaker("Statistics"));
		sb.append(addField("Str",
				CreatureAdapter.CreatureUtils.getStrength(cursor), false));
		sb.append(addField("Dex",
				CreatureAdapter.CreatureUtils.getDexterity(cursor), false));
		sb.append(addField("Con",
				CreatureAdapter.CreatureUtils.getConstitution(cursor), false));
		sb.append(addField("Int",
				CreatureAdapter.CreatureUtils.getIntelligence(cursor), false));
		sb.append(addField("Wis",
				CreatureAdapter.CreatureUtils.getWisdom(cursor), false));
		sb.append(addField("Cha",
				CreatureAdapter.CreatureUtils.getCharisma(cursor)));
		sb.append(addField("Base Atk",
				CreatureAdapter.CreatureUtils.getBaseAttack(cursor), false));
		sb.append(addField("CMB", CreatureAdapter.CreatureUtils.getCMB(cursor),
				false));
		sb.append(addField("CMD", CreatureAdapter.CreatureUtils.getCMD(cursor)));
		sb.append(addField("Feats",
				CreatureAdapter.CreatureUtils.getFeats(cursor)));
		boolean rmExists = CreatureAdapter.CreatureUtils
				.getRacialModifiers(cursor) == null;
		sb.append(addField("Skills",
				CreatureAdapter.CreatureUtils.getSkills(cursor), rmExists));
		sb.append(addField("Racial Modifiers",
				CreatureAdapter.CreatureUtils.getRacialModifiers(cursor)));
		sb.append(addField("Languages",
				CreatureAdapter.CreatureUtils.getLanguages(cursor)));
		sb.append(addField("Special Qualities",
				CreatureAdapter.CreatureUtils.getSpecialQualities(cursor)));
		sb.append(addField("Gear",
				CreatureAdapter.CreatureUtils.getGear(cursor)));
		sb.append(addField("Combat Gear",
				CreatureAdapter.CreatureUtils.getCombatGear(cursor)));
		sb.append(addField("Other Gear",
				CreatureAdapter.CreatureUtils.getOtherGear(cursor)));
		sb.append(addField("Boon",
				CreatureAdapter.CreatureUtils.getBoon(cursor)));
		String retval = sb.toString();
		if (retval.equals(renderStatBlockBreaker("Statistics"))) {
			return "";
		}
		return retval;
	}

	private String renderCreatureEcology(Cursor cursor) {
		StringBuffer sb = new StringBuffer();
		// 47:environment, 48:organization, 49:treasure
		sb.append(renderStatBlockBreaker("Ecology"));
		sb.append(addField("Environment",
				CreatureAdapter.CreatureUtils.getEnvironment(cursor)));
		sb.append(addField("Organization",
				CreatureAdapter.CreatureUtils.getOrganization(cursor)));
		sb.append(addField("Treasure",
				CreatureAdapter.CreatureUtils.getTreasure(cursor)));
		String retval = sb.toString();
		if (retval.equals(renderStatBlockBreaker("Ecology"))) {
			return "";
		}
		return retval;
	}

	@Override
	public String renderFooter() {
		return "";
	}

	@Override
	public String renderHeader() {
		return "";
	}
}
