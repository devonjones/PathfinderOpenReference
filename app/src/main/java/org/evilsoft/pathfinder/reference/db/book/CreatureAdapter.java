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
		sb.append("  ac, hp, fortitude, reflex, will, defensive_abilities, dr, resist, immune, concentration, sr, weaknesses,");
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

	public static class CreatureUtils {
		private CreatureUtils() {
		}

		public static String getSex(Cursor cursor) {
			return cursor.getString(0);
		}

		public static String getSuperRace(Cursor cursor) {
			return cursor.getString(1);
		}

		public static String getLevel(Cursor cursor) {
			return cursor.getString(2);
		}

		public static String getCR(Cursor cursor) {
			return cursor.getString(3);
		}

		public static String getXP(Cursor cursor) {
			return cursor.getString(4);
		}

		public static String getAlignment(Cursor cursor) {
			return cursor.getString(5);
		}

		public static String getSize(Cursor cursor) {
			return cursor.getString(6);
		}

		public static String getCreatureType(Cursor cursor) {
			return cursor.getString(7);
		}

		public static String getCreatureSubtype(Cursor cursor) {
			return cursor.getString(8);
		}

		public static String getInit(Cursor cursor) {
			return cursor.getString(9);
		}

		public static String getSenses(Cursor cursor) {
			return cursor.getString(10);
		}

		public static String getAura(Cursor cursor) {
			return cursor.getString(11);
		}

		public static String getAC(Cursor cursor) {
			return cursor.getString(12);
		}

		public static String getHP(Cursor cursor) {
			return cursor.getString(13);
		}

		public static String getFortitude(Cursor cursor) {
			return cursor.getString(14);
		}

		public static String getReflex(Cursor cursor) {
			return cursor.getString(15);
		}

		public static String getWill(Cursor cursor) {
			return cursor.getString(16);
		}

		public static String getDefensiveAbilities(Cursor cursor) {
			return cursor.getString(17);
		}

		public static String getDR(Cursor cursor) {
			return cursor.getString(18);
		}

		public static String getResist(Cursor cursor) {
			return cursor.getString(19);
		}

		public static String getImmune(Cursor cursor) {
			return cursor.getString(20);
		}

		public static String getConcentration(Cursor cursor) {
			return cursor.getString(21);
		}

		public static String getSR(Cursor cursor) {
			return cursor.getString(22);
		}

		public static String getWeaknesses(Cursor cursor) {
			return cursor.getString(23);
		}

		public static String getSpeed(Cursor cursor) {
			return cursor.getString(24);
		}

		public static String getMelee(Cursor cursor) {
			return cursor.getString(25);
		}

		public static String getRanged(Cursor cursor) {
			return cursor.getString(26);
		}

		public static String getSpace(Cursor cursor) {
			return cursor.getString(27);
		}

		public static String getReach(Cursor cursor) {
			return cursor.getString(28);
		}

		public static String getSpecialAttacks(Cursor cursor) {
			return cursor.getString(29);
		}

		public static String getStrength(Cursor cursor) {
			return cursor.getString(30);
		}

		public static String getDexterity(Cursor cursor) {
			return cursor.getString(31);
		}

		public static String getConstitution(Cursor cursor) {
			return cursor.getString(32);
		}

		public static String getIntelligence(Cursor cursor) {
			return cursor.getString(33);
		}

		public static String getWisdom(Cursor cursor) {
			return cursor.getString(34);
		}

		public static String getCharisma(Cursor cursor) {
			return cursor.getString(35);
		}

		public static String getBaseAttack(Cursor cursor) {
			return cursor.getString(36);
		}

		public static String getCMB(Cursor cursor) {
			return cursor.getString(37);
		}

		public static String getCMD(Cursor cursor) {
			return cursor.getString(38);
		}

		public static String getFeats(Cursor cursor) {
			return cursor.getString(39);
		}

		public static String getSkills(Cursor cursor) {
			return cursor.getString(40);
		}

		public static String getRacialModifiers(Cursor cursor) {
			return cursor.getString(41);
		}

		public static String getLanguages(Cursor cursor) {
			return cursor.getString(42);
		}

		public static String getSpecialQualities(Cursor cursor) {
			return cursor.getString(43);
		}

		public static String getGear(Cursor cursor) {
			return cursor.getString(44);
		}

		public static String getCombatGear(Cursor cursor) {
			return cursor.getString(45);
		}

		public static String getOtherGear(Cursor cursor) {
			return cursor.getString(46);
		}

		public static String getBoon(Cursor cursor) {
			return cursor.getString(47);
		}

		public static String getEnvironment(Cursor cursor) {
			return cursor.getString(48);
		}

		public static String getOrganization(Cursor cursor) {
			return cursor.getString(49);
		}

		public static String getTreasure(Cursor cursor) {
			return cursor.getString(50);
		}

		public static String getHitDice(Cursor cursor) {
			return cursor.getString(51);
		}

		public static String getNaturalArmor(Cursor cursor) {
			return cursor.getString(52);
		}

		public static String getBreathWeapon(Cursor cursor) {
			return cursor.getString(52);
		}
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

	public static class CreatureSpellsUtils {
		private CreatureSpellsUtils() {
		}

		public static String getName(Cursor cursor) {
			return cursor.getString(0);
		}

		public static String getBody(Cursor cursor) {
			return cursor.getString(1);
		}
	}
}
