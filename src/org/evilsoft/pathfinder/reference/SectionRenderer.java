package org.evilsoft.pathfinder.reference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.psrd.FeatAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.MonsterAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.SkillAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.SpellAdapter;

import android.content.res.AssetManager;
import android.database.Cursor;
import android.util.Log;
import android.widget.TextView;

public class SectionRenderer {
	private static final String TAG = "SectionRenderer";
	private PsrdDbAdapter dbAdapter;
	private AssetManager assets;
	private FeatAdapter featAdapter;
	private MonsterAdapter monsterAdapter;
	private SkillAdapter skillAdapter;
	private SpellAdapter spellAdapter;
	private static String css;
	private TextView title;

	public SectionRenderer(PsrdDbAdapter dbAdapter, AssetManager assets, TextView title) {
		this.dbAdapter = dbAdapter;
		this.assets = assets;
		this.title = title;
	}

	private FeatAdapter getFeatAdapter() {
		if (this.featAdapter == null) {
			this.featAdapter = new FeatAdapter(this.dbAdapter);
		}
		return this.featAdapter;
	}

	private MonsterAdapter getMonsterAdapter() {
		if (this.monsterAdapter == null) {
			this.monsterAdapter = new MonsterAdapter(this.dbAdapter);
		}
		return this.monsterAdapter;
	}

	private SkillAdapter getSkillAdapter() {
		if (this.skillAdapter == null) {
			this.skillAdapter = new SkillAdapter(this.dbAdapter);
		}
		return this.skillAdapter;
	}

	private SpellAdapter getSpellAdapter() {
		if (this.spellAdapter == null) {
			this.spellAdapter = new SpellAdapter(this.dbAdapter);
		}
		return this.spellAdapter;
	}

	public static String swapUrl(String uri, String title, String id) {
		String[] parts = uri.split("\\/");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < parts.length - 2; i++) {
			String part = parts[i];
			if (i > 0) {
				part = part.replace(':', '_');
			}
			sb.append(part);
			sb.append('/');
		}
		sb.append(title);
		sb.append("/");
		sb.append(id);
		return sb.toString();
	}

	public String render(String sectionId, String uri) {
		Cursor curs = this.dbAdapter.fetchFullSection(sectionId);
		return renderSection(curs, uri);
	}

	public String renderSection(Cursor curs, String uri) {
		HashMap<Integer, Integer> depthMap = new HashMap<Integer, Integer>();
		HashMap<Integer, String> titleMap = new HashMap<Integer, String>();
		int depth = 0;
		StringBuffer sb = new StringBuffer();
		boolean has_next = curs.moveToFirst();
		boolean top = true;
		String topTitle = curs.getString(6);
		// 0:section_id, 1:lft, 2:rgt, 3:parent_id, 4:type, 5:subtype, 6:name,
		// 7:abbrev,
		// 8:source, 9:description, 10:body
		sb.append(renderCss());
		this.title.setText(topTitle);
		while (has_next) {
			int sectionId = curs.getInt(0);
			int parentId = curs.getInt(3);
			String name = curs.getString(6);
			depth = getDepth(depthMap, sectionId, parentId, depth);
			titleMap.put(sectionId, name);
			String title = name;
			if (titleMap.containsKey(parentId)) {
				title = titleMap.get(parentId);
			}
			sb.append(renderSectionText(curs, title, depth, uri, top));
			has_next = curs.moveToNext();
			top = false;
		}
		return sb.toString();
	}

	public String renderCss() {
		StringBuffer sb = new StringBuffer();
		sb.append("<head><style type='text/css'>");
		if (css == null) {
			try {
				InputStream in = assets.open("display.css");
				css = readFile(in);
			} catch (IOException e) {
				Log.e(TAG, "Failed to loaded display.css");
			}
		}
		sb.append("\n");
		sb.append(css);
		sb.append("</style></head>");
		sb.append("\n");
		return sb.toString();
	}

	public int getDepth(HashMap<Integer, Integer> depthMap, int section_id, int parent_id, int depth) {
		if (depthMap.containsKey(parent_id)) {
			depth = depthMap.get(parent_id) + 1;
			depthMap.put(section_id, depth);
		} else {
			depthMap.put(section_id, depth);
		}
		return depth;
	}

	public String renderSectionText(Cursor curs, String title, int depth, String uri, boolean top) {
		StringBuffer sb = new StringBuffer();
		String id = curs.getString(0);
		String type = curs.getString(4);
		String newUri = swapUrl(uri, title, id);
		String name = curs.getString(6);
		String source = curs.getString(8);
		String desc = curs.getString(9);

		if (type.equals("ability")) {
			sb.append(renderTitle(abilityName(name, id), newUri, depth, top));
		} else if (type.equals("affliction")) {
			sb.append(renderStatBlockTitle(name, newUri, top));
			sb.append(renderAfflictionText(curs.getString(0), curs.getString(5), newUri));
		} else if (type.equals("animal_companion")) {
			sb.append(renderAnimalCompanionText(curs.getString(0), name, newUri, top));
		} else if (type.equals("creature")) {
			sb.append(renderCreature(curs.getString(0), name, desc, source, newUri, top));
			desc = null;
		} else if (type.equals("feat")) {
			sb.append(renderFeatText(curs, newUri, top));
			// } else if (type.equals("haunt")) {
			// } else if (type.equals("item")) {
		} else if (type.equals("race")) {
			if (!top) {
				name = name + " Characters";
			}
			sb.append(renderTitle(name, newUri, depth, top));
			// } else if (type.equals("settlement")) {
		} else if (type.equals("skill")) {
			sb.append(renderSkillText(curs, newUri, top));
		} else if (type.equals("spell")) {
			sb.append(renderSpellText(curs, newUri, top));
		} else if (type.equals("table")) {
			// } else if (type.equals("trap")) {
			// } else if (type.equals("vehicle")) {
		} else {
			sb.append(renderTitle(name, newUri, depth, top));
		}
		if (desc != null) {
			sb.append("<p>");
			sb.append(desc);
			sb.append("</p>\n");
		}
		String body = curs.getString(10);
		if (body != null) {
			sb.append(body);
		}
		if (depth >= 3) {
			sb.append("<br>\n");
		}
		return sb.toString();
	}

	public String abilityName(String name, String sectionId) {
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		boolean fields = false;
		Cursor curs = dbAdapter.getAbilityTypes(sectionId);
		boolean has_next = curs.moveToFirst();
		String comma = "";
		while (has_next) {
			sb.append(comma);
			if (fields != true) {
				sb.append(" (");
				comma = ", ";
				fields = true;
			}
			String type = curs.getString(0);
			if (type.equals("Extraordinary")) {
				sb.append("Ex");
			} else if (type.equals("Supernatural")) {
				sb.append("Su");
			} else if (type.equals("Spell-Like")) {
				sb.append("Sp");
			}
			has_next = curs.moveToNext();
		}
		if (fields) {
			sb.append(")");
		}
		return sb.toString();
	}

	public String renderFeatText(Cursor curs, String newUri, boolean top) {
		StringBuffer sb = new StringBuffer();
		sb.append(renderTitle(curs.getString(6), newUri, 0, top));
		sb.append("<B>");
		sb.append(this.getFeatAdapter().renderFeatTypeDescription(curs.getString(0)));
		sb.append("</B><BR>\n");
		sb.append("<B>Source: </B>");
		sb.append(curs.getString(8));
		sb.append("<BR>");
		return sb.toString();
	}

	public String renderSpellText(Cursor curs, String newUri, boolean top) {
		StringBuffer sb = new StringBuffer();
		sb.append(renderTitle(curs.getString(6), newUri, 0, top));
		sb.append(renderSpellDetails(curs.getString(0)));
		sb.append("<B>Source: </B>");
		sb.append(curs.getString(8));
		sb.append("<BR>");
		return sb.toString();
	}

	public String renderAfflictionText(String sectionId, String subtype, String newUri) {
		StringBuffer sb = new StringBuffer();
		Cursor curs = dbAdapter.getAfflictionDetails(sectionId);
		// 0:contracted, 1:save, 2:onset, 3:frequency, 4:effect,
		// 5:initial_effect, 6:secondary_effect, 7:cure
		boolean has_next = curs.moveToFirst();
		if (has_next) {
			String contracted = curs.getString(0);
			if (contracted != null) {
				contracted = subtype + ", " + contracted;
			} else {
				contracted = subtype;
			}
			sb.append(addField("Type", contracted, false));
			sb.append(addField("Save", curs.getString(1)));
			sb.append(addField("Onset", curs.getString(2), false));
			sb.append(addField("Frequency", curs.getString(3)));
			sb.append(addField("Effect", curs.getString(4)));
			sb.append(addField("Initial Effect", curs.getString(5)));
			sb.append(addField("Secondary Effect", curs.getString(6)));
			sb.append(addField("Cure", curs.getString(7)));
		}
		return sb.toString();
	}

	private Object renderAnimalCompanionText(String sectionId, String name, String newUri, boolean top) {
		StringBuffer sb = new StringBuffer();
		Cursor curs = dbAdapter.getAnimalCompanionDetails(sectionId);
		// 0:ac, 1:attack, 2:ability_scores, 3:special_qualities,
		// 4:special_attacks, 5:size, 6:speed, 7:level
		boolean has_next = curs.moveToFirst();
		if (has_next) {
			String level = curs.getString(7);
			if (level != null) {
				sb.append(renderStatBlockBreaker(level));
			} else {
				sb.append(renderStatBlockTitle(name, newUri, top));
				sb.append(renderStatBlockBreaker("Starting Statistics"));
			}
			sb.append(addField("Size", curs.getString(5), false));
			sb.append(addField("Speed", curs.getString(6)));
			sb.append(addField("AC", curs.getString(0)));
			sb.append(addField("Attack", curs.getString(1)));
			sb.append(addField("Ability Scores", curs.getString(2)));
			sb.append(addField("Special Qualities", curs.getString(3)));
			sb.append(addField("Special Attacks", curs.getString(4)));
		}
		return sb.toString();
	}

	public String renderCreature(String sectionId, String name, String desc, String source, String newUri, boolean top) {
		StringBuffer sb = new StringBuffer();
		Cursor curs = this.getMonsterAdapter().getCreatureDetails(sectionId);
		boolean has_next = curs.moveToFirst();
		if (has_next) {
			sb.append(renderCreatureHeader(curs, name, desc, source, newUri, top));
			sb.append(renderCreatureDefense(curs));
			sb.append(renderCreatureOffense(curs));
			sb.append(renderCreatureSpells(sectionId));
			sb.append(renderCreatureStatistics(curs));
			sb.append(renderCreatureEcology(curs));
		}
		return sb.toString();
	}

	private String renderCreatureHeader(Cursor curs, String name, String desc, String source, String newUri, boolean top) {
		// 0:sex, 1:super_race, 2:level, 3:cr, 4:xp, 5:alignment, 6:size,
		// 7:creature_type, 8:creature_subtype, 9:init, 10:senses, 11:aura,
		StringBuffer sb = new StringBuffer();
		String cr = curs.getString(3);
		if (cr != null) {
			name = name + " (CR " + cr + ")";
		}
		sb.append(renderStatBlockTitle(name, newUri, top));
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

	private String displayLine(List<String> elements) {
		StringBuffer sb = new StringBuffer();
		boolean space = false;
		for (int i = 0; i < elements.size(); i++) {
			String elem = elements.get(i);
			if (elem != null) {
				if (space) {
					sb.append(" ");
				}
				sb.append(elem);
				space = true;
			}
		}
		if (sb.length() > 0) {
			sb.append("<br>\n");
		}
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

	public String renderSpellDetails(String sectionId) {
		Cursor curs = this.getSpellAdapter().fetchSpellDetails(sectionId);
		StringBuffer sb = new StringBuffer();
		// 0:school, 1:subschool, 2:descriptor_text, 3:level_text,
		// 4:casting_time, 5:preparation_time, 6:range, 7:duration,
		// 8:saving_throw, 9:spell_resistance, 10:as_spell_id
		boolean has_next = curs.moveToFirst();
		if (has_next) {
			String school = curs.getString(0);
			String subschool = curs.getString(1);
			String descriptor = curs.getString(2);
			sb.append(fieldTitle("School"));
			sb.append(school);
			if (subschool != null) {
				sb.append(" (");
				sb.append(subschool);
				sb.append(")");
			}
			if (descriptor != null) {
				sb.append(" [");
				sb.append(descriptor);
				sb.append("]");
			}
			sb.append("<br>\n");
			String level = curs.getString(3);
			sb.append(addField("Level", level));
			String casting_time = curs.getString(4);
			sb.append(addField("Casting Time", casting_time));
			String preparation_time = curs.getString(5);
			sb.append(addField("Preparation Time", preparation_time));
			sb.append(renderComponents(sectionId));
			String range = curs.getString(6);
			sb.append(addField("Range", range));
			sb.append(renderEffects(sectionId));
			String duration = curs.getString(7);
			sb.append(addField("Duration", duration));
			String saving_throw = curs.getString(8);
			sb.append(addField("Saving Throw", saving_throw));
			String spell_resistance = curs.getString(9);
			sb.append(addField("Spell Resistance", spell_resistance));
		}
		return sb.toString();
	}

	public String renderComponents(String sectionId) {
		Cursor curs = this.getSpellAdapter().fetchSpellComponents(sectionId);
		StringBuffer sb = new StringBuffer();
		boolean has_next = curs.moveToFirst();
		boolean has_field = false;
		if (has_next) {
			sb.append(fieldTitle("Components"));
			has_field = true;
		}
		String comma = "";
		while (has_next) {
			sb.append(comma);
			sb.append(curs.getString(0));
			String desc = curs.getString(1);
			if (desc != null) {
				sb.append(" (");
				sb.append(desc);
				sb.append(")");
			}
			comma = ", ";
			has_next = curs.moveToNext();
		}
		if (has_field) {
			sb.append("<br>\n");
		}
		return sb.toString();
	}

	public String renderEffects(String sectionId) {
		Cursor curs = this.getSpellAdapter().fetchSpellEffects(sectionId);
		StringBuffer sb = new StringBuffer();
		boolean has_next = curs.moveToFirst();
		while (has_next) {
			sb.append(addField(curs.getString(0), curs.getString(1)));
			has_next = curs.moveToNext();
		}
		return sb.toString();
	}

	public String renderSkillText(Cursor curs, String newUri, boolean top) {
		StringBuffer sb = new StringBuffer();
		sb.append(renderTitle(curs.getString(6), newUri, 0, top));
		sb.append(renderSpellDetails(curs.getString(0)));
		sb.append("<B>Source: </B>");
		sb.append(curs.getString(8));
		sb.append("<BR>");
		return sb.toString();
	}

	public String renderSkillDetails(String sectionId) {
		Cursor curs = this.getSkillAdapter().fetchSkillAttr(sectionId);
		StringBuffer sb = new StringBuffer();
		boolean has_next = curs.moveToFirst();
		if (has_next) {
			sb.append("<H2>(");
			sb.append(curs.getString(0));
			boolean armorCheckPenalty = (curs.getInt(1) != 0);
			if (armorCheckPenalty) {
				sb.append("; Armor Check Penalty");
			}
			boolean trainedOnly = (curs.getInt(2) != 0);
			if (trainedOnly) {
				sb.append("; Trained Only");
			}
			sb.append(")</H2>\n");
		}
		return sb.toString();
	}

	public String addField(String field, String value) {
		return addField(field, value, true);
	}

	public String addField(String field, String value, boolean lineEnd) {
		StringBuffer sb = new StringBuffer();
		if (value != null) {
			sb.append(fieldTitle(field));
			sb.append(value);
			if (lineEnd) {
				sb.append("<br>\n");
			} else {
				sb.append("; ");
			}
		}
		return sb.toString();
	}

	public String fieldTitle(String field) {
		return "<B>" + field + ":</B> ";
	}

	public String renderTitle(String title, String newUri, int depth, boolean top) {
		if (top) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		String[] tags = getDepthTag(depth);
		if (title != null) {
			sb.append("\n");
			sb.append(tags[0]);
			if (depth >= 1) {
				sb.append("<a href=\"");
				sb.append(newUri);
				sb.append("\">");
			}
			sb.append(title);
			if (depth >= 1) {
				sb.append("</a>");
			}
			sb.append(tags[1]);
			sb.append("\n");
		}
		return sb.toString();
	}

	public String renderStatBlockTitle(String title, String newUri, boolean top) {
		if (top) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		if (title != null) {
			sb.append("\n<p class='stat-block-title'><a href='");
			sb.append(newUri);
			sb.append("'><font color='white'>");
			sb.append(title);
			sb.append("</font></a></p>\n");
		}
		return sb.toString();
	}

	public String renderStatBlockBreaker(String title) {
		StringBuffer sb = new StringBuffer();
		if (title != null) {
			sb.append("\n<p class='stat-block-breaker'>");
			sb.append(title);
			sb.append("</p>\n");
		}
		return sb.toString();
	}

	public String[] getDepthTag(int depth) {
		String[] tags = new String[2];
		if (depth == 0) {
			tags[0] = "<H1>";
			tags[1] = "</H1>";
		} else if (depth == 1) {
			tags[0] = "<H2>";
			tags[1] = "</H2>";
		} else if (depth == 2) {
			tags[0] = "<H3>";
			tags[1] = "</H3>";
		} else if (depth == 3) {
			tags[0] = "<B>";
			tags[1] = ":</B>";
		} else {
			tags[0] = "<I>";
			tags[1] = ":</I>";
		}
		return tags;
	}

	public String readFile(InputStream is) {
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		try {
			for (int readnum; (readnum = is.read(buffer)) != -1;) {
				// is.read(buffer);
				bo.write(buffer, 0, readnum);
			}
			bo.close();
			is.close();
		} catch (IOException e) {

		}
		return bo.toString();
	}

}
