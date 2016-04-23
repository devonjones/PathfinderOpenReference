package org.evilsoft.pathfinder.reference.render.json;

import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.book.CreatureAdapter;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class CreatureRenderer extends JsonRenderer {
	private BookDbAdapter bookDbAdapter;

	public CreatureRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	public JSONObject render(JSONObject section, Integer sectionId)
			throws JSONException {
		Cursor cursor = bookDbAdapter.getCreatureAdapter().getCreatureDetails(
				sectionId);
		try {
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				addField(section, "ac",
						CreatureAdapter.CreatureUtils.getAC(cursor));
				addField(section, "alignment",
						CreatureAdapter.CreatureUtils.getAlignment(cursor));
				addField(section, "aura",
						CreatureAdapter.CreatureUtils.getAura(cursor));
				addField(section, "base_attack",
						CreatureAdapter.CreatureUtils.getBaseAttack(cursor));
				addField(section, "boon",
						CreatureAdapter.CreatureUtils.getBoon(cursor));
				addField(section, "breath_weapon",
						CreatureAdapter.CreatureUtils.getBreathWeapon(cursor));
				addField(section, "charisma",
						CreatureAdapter.CreatureUtils.getCharisma(cursor));
				addField(section, "cmb",
						CreatureAdapter.CreatureUtils.getCMB(cursor));
				addField(section, "cmd",
						CreatureAdapter.CreatureUtils.getCMD(cursor));
				addField(section, "combat_gear",
						CreatureAdapter.CreatureUtils.getCombatGear(cursor));
				addField(section, "constitution",
						CreatureAdapter.CreatureUtils.getConstitution(cursor));
				addField(section, "cr",
						CreatureAdapter.CreatureUtils.getCR(cursor));
				addField(section, "creature_subtype",
						CreatureAdapter.CreatureUtils
								.getCreatureSubtype(cursor));
				addField(section, "creature_type",
						CreatureAdapter.CreatureUtils.getCreatureType(cursor));
				addField(section, "defensive_abilities",
						CreatureAdapter.CreatureUtils
								.getDefensiveAbilities(cursor));
				addField(section, "dexterity",
						CreatureAdapter.CreatureUtils.getDexterity(cursor));
				addField(section, "dr",
						CreatureAdapter.CreatureUtils.getDR(cursor));
				addField(section, "environment",
						CreatureAdapter.CreatureUtils.getEnvironment(cursor));
				addField(section, "feats",
						CreatureAdapter.CreatureUtils.getFeats(cursor));
				addField(section, "fortitude",
						CreatureAdapter.CreatureUtils.getFortitude(cursor));
				addField(section, "gear",
						CreatureAdapter.CreatureUtils.getGear(cursor));
				addField(section, "hite_dice",
						CreatureAdapter.CreatureUtils.getHitDice(cursor));
				addField(section, "hp",
						CreatureAdapter.CreatureUtils.getHP(cursor));
				addField(section, "immune",
						CreatureAdapter.CreatureUtils.getImmune(cursor));
				addField(section, "concentration",
						CreatureAdapter.CreatureUtils.getConcentration(cursor));
				addField(section, "init",
						CreatureAdapter.CreatureUtils.getInit(cursor));
				addField(section, "intelligence",
						CreatureAdapter.CreatureUtils.getIntelligence(cursor));
				addField(section, "languages",
						CreatureAdapter.CreatureUtils.getLanguages(cursor));
				addField(section, "level",
						CreatureAdapter.CreatureUtils.getLevel(cursor));
				addField(section, "melee",
						CreatureAdapter.CreatureUtils.getMelee(cursor));
				addField(section, "natural_armor",
						CreatureAdapter.CreatureUtils.getNaturalArmor(cursor));
				addField(section, "organization",
						CreatureAdapter.CreatureUtils.getOrganization(cursor));
				addField(section, "other_gear",
						CreatureAdapter.CreatureUtils.getOtherGear(cursor));
				addField(section, "racial_modifiers",
						CreatureAdapter.CreatureUtils
								.getRacialModifiers(cursor));
				addField(section, "ranged",
						CreatureAdapter.CreatureUtils.getRanged(cursor));
				addField(section, "reach",
						CreatureAdapter.CreatureUtils.getReach(cursor));
				addField(section, "reflex",
						CreatureAdapter.CreatureUtils.getReflex(cursor));
				addField(section, "resist",
						CreatureAdapter.CreatureUtils.getResist(cursor));
				addField(section, "senses",
						CreatureAdapter.CreatureUtils.getSenses(cursor));
				addField(section, "sex",
						CreatureAdapter.CreatureUtils.getSex(cursor));
				addField(section, "size",
						CreatureAdapter.CreatureUtils.getSize(cursor));
				addField(section, "skills",
						CreatureAdapter.CreatureUtils.getSkills(cursor));
				addField(section, "space",
						CreatureAdapter.CreatureUtils.getSpace(cursor));
				addField(section, "special_attacks",
						CreatureAdapter.CreatureUtils.getSpecialAttacks(cursor));
				addField(section, "special_qualities",
						CreatureAdapter.CreatureUtils
								.getSpecialQualities(cursor));
				addField(section, "speed",
						CreatureAdapter.CreatureUtils.getSpeed(cursor));
				addField(section, "sr",
						CreatureAdapter.CreatureUtils.getSR(cursor));
				addField(section, "strength",
						CreatureAdapter.CreatureUtils.getStrength(cursor));
				addField(section, "super_race",
						CreatureAdapter.CreatureUtils.getSuperRace(cursor));
				addField(section, "treasure",
						CreatureAdapter.CreatureUtils.getTreasure(cursor));
				addField(section, "weaknesses",
						CreatureAdapter.CreatureUtils.getWeaknesses(cursor));
				addField(section, "will",
						CreatureAdapter.CreatureUtils.getWill(cursor));
				addField(section, "wisdom",
						CreatureAdapter.CreatureUtils.getWisdom(cursor));
				addField(section, "xp",
						CreatureAdapter.CreatureUtils.getXP(cursor));
				renderCreatureSpells(section, sectionId);
			}
		} finally {
			cursor.close();
		}
		return section;
	}

	private void renderCreatureSpells(JSONObject section, Integer sectionId)
			throws JSONException {
		Cursor cursor = bookDbAdapter.getCreatureAdapter().getCreatureSpells(
				sectionId);
		try {
			boolean has_next = cursor.moveToFirst();
			JSONObject spells = new JSONObject();
			while (has_next) {
				spells.put(CreatureAdapter.CreatureSpellsUtils.getName(cursor),
						CreatureAdapter.CreatureSpellsUtils.getBody(cursor));
				has_next = cursor.moveToNext();
			}
			if (spells.length() > 0) {
				section.put("spells", spells);
			}
		} finally {
			cursor.close();
		}
	}
}
