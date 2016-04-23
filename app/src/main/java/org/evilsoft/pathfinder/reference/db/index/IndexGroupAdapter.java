package org.evilsoft.pathfinder.reference.db.index;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;
import org.evilsoft.pathfinder.reference.preference.FilterPreferenceManager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class IndexGroupAdapter {
	public SQLiteDatabase database;
	public Context context;

	public IndexGroupAdapter(SQLiteDatabase database, Context context) {
		this.database = database;
		this.context = context;
	}

	private String selectStatement() {
		return selectStatement("");
	}

	private String selectStatement(String addedColumns) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT i.index_id, i.section_id, i.parent_id, i.parent_name,");
		sb.append("  i.database, i.source, i.type, i.subtype, i.name, i.search_name,");
		sb.append("  i.description, i.url,");
		sb.append("  i.feat_type_description, i.feat_prerequisites,");
		sb.append("  i.skill_attribute, i.skill_armor_check_penalty, i.skill_trained_only,");
		sb.append("  i.spell_school, i.spell_subschool_text, i.spell_descriptor_text,");
		sb.append("  i.spell_list_text, i.spell_component_text, i.spell_source,");
		sb.append("  i.creature_type, i.creature_subtype, i.creature_super_race,");
		sb.append("  i.creature_cr, i.creature_xp, i.creature_size, i.creature_alignment");
		sb.append(addedColumns);
		sb.append(" FROM central_index i");
		return sb.toString();
	}

	public Cursor fetchById(Integer id) {
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append(selectStatement());
		sb.append(" WHERE i.index_id = ?");
		args.add(id.toString());
		sb.append(FilterPreferenceManager.getSourceFilter(context, args, "AND",
				"i"));
		sb.append(" ORDER BY i.name");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public Cursor fetchByUrl(String url) {
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append(selectStatement());
		sb.append(" WHERE i.url = ?");
		args.add(url);
		sb.append(FilterPreferenceManager.getSourceFilter(context, args, "AND",
				"i"));
		sb.append(" ORDER BY i.name");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public Cursor fetchByMatchUrl(String url) {
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append(selectStatement());
		sb.append(" WHERE i.url LIKE ?");
		args.add(url);
		sb.append(FilterPreferenceManager.getSourceFilter(context, args, "AND",
				"i"));
		sb.append(" ORDER BY i.name");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public Cursor fetchByType(String type, String subtype) {
		if (type != null && type.equals("*")) {
			type = null;
		}
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append(selectStatement());
		String where = "WHERE";
		if (type != null) {
			sb.append(" " + where + " i.type = ?");
			where = "AND";
			args.add(type);
		}
		if (subtype != null) {
			sb.append("  " + where + " i.subtype = ?");
			where = "AND";
			args.add(subtype);
		}
		sb.append(FilterPreferenceManager.getSourceFilter(context, args, where,
				"i"));
		sb.append(" ORDER BY i.name");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public Cursor fetchByTypeAndName(String name, String type, String subtype) {
		if (type.equals("*")) {
			type = null;
		}
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append(selectStatement());
		String where = "WHERE";
		if (name != null) {
			sb.append(" " + where + " i.name = ?");
			where = "AND";
			args.add(name);
		}
		if (type != null) {
			sb.append(" " + where + " i.type = ?");
			where = "AND";
			args.add(type);
		}
		if (subtype != null) {
			sb.append("  " + where + " i.subtype = ?");
			where = "AND";
			args.add(subtype);
		}
		sb.append(FilterPreferenceManager.getSourceFilter(context, args, where,
				"i"));
		sb.append(" ORDER BY i.name");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public Cursor fetchByCreatureType(String creatureType) {
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append(selectStatement());
		sb.append(" WHERE i.type = 'creature'");
		sb.append("  AND (i.subtype != 'npc'");
		sb.append("   OR i.subtype IS NULL)");
		if (creatureType != null) {
			sb.append("  AND i.creature_type = ?");
			args.add(creatureType);
		}
		sb.append(FilterPreferenceManager.getSourceFilter(context, args, "AND",
				"i"));
		sb.append(" ORDER BY i.name");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public Cursor fetchByFeatType(String featType) {
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append(selectStatement());
		if (featType != null) {
			sb.append("  INNER JOIN feat_type_index fti");
			sb.append("   ON i.index_id = fti.index_id");
			sb.append("    AND fti.feat_type = ?");
			args.add(featType);
		}
		sb.append(" WHERE i.type = 'feat'");
		sb.append(FilterPreferenceManager.getSourceFilter(context, args, "AND",
				"i"));
		sb.append(" ORDER BY i.name");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public Cursor fetchBySpellClass(String spellClass) {
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append(selectStatement(", sl.level, sl.name"));
		sb.append("  INNER JOIN spell_list_index sl");
		sb.append("   ON i.index_id = sl.index_id");
		sb.append("    AND sl.name = ?");
		args.add(spellClass);
		sb.append(" WHERE i.type = 'spell'");
		sb.append(FilterPreferenceManager.getSourceFilter(context, args, "AND",
				"i"));
		sb.append(" ORDER BY sl.level, i.name");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public Cursor fetchBySpellSource(String spellSource) {
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append(selectStatement());
		sb.append(" WHERE i.spell_source = ?");
		sb.append("  AND i.type = 'mythic_spell'");
		args.add(spellSource);
		sb.append(FilterPreferenceManager.getSourceFilter(context, args, "AND",
				"i"));
		sb.append(" ORDER BY i.name");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public Cursor fetchByParentUrl(String parentUrl) {
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append(selectStatement());
		sb.append("  INNER JOIN central_index p");
		sb.append("   ON i.parent_id = p.section_id");
		sb.append("    AND i.database = p.database");
		sb.append(" WHERE p.url = ?");
		args.add(parentUrl);
		sb.append(FilterPreferenceManager.getSourceFilter(context, args, "AND",
				"i"));
		sb.append(" ORDER BY i.name");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public static class IndexGroupUtils {
		public static Integer getIndexId(Cursor cursor) {
			return cursor.getInt(0);
		}

		public static Integer getSectionId(Cursor cursor) {
			return cursor.getInt(1);
		}

		public static Integer getParentId(Cursor cursor) {
			return cursor.getInt(2);
		}

		public static String getParentName(Cursor cursor) {
			return cursor.getString(3);
		}

		public static String getDatabase(Cursor cursor) {
			return cursor.getString(4);
		}

		public static String getSource(Cursor cursor) {
			return cursor.getString(5);
		}

		public static String getType(Cursor cursor) {
			return cursor.getString(6);
		}

		public static String getSubtype(Cursor cursor) {
			return cursor.getString(7);
		}

		public static String getName(Cursor cursor) {
			return cursor.getString(8);
		}

		public static String getSearchName(Cursor cursor) {
			return cursor.getString(9);
		}

		public static String getDescription(Cursor cursor) {
			return cursor.getString(10);
		}

		public static String getUrl(Cursor cursor) {
			return cursor.getString(11);
		}

		public static String getFeatTypes(Cursor cursor) {
			return cursor.getString(12);
		}

		public static String getFeatPrereqs(Cursor cursor) {
			return cursor.getString(13);
		}

		public static String getSkillAttr(Cursor cursor) {
			return cursor.getString(14);
		}

		public static Integer getSkillArmor(Cursor cursor) {
			return cursor.getInt(15);
		}

		public static Integer getSkillTrained(Cursor cursor) {
			return cursor.getInt(16);
		}

		public static String getSpellSchool(Cursor cursor) {
			return cursor.getString(17);
		}

		public static String getSpellSubschool(Cursor cursor) {
			return cursor.getString(18);
		}

		public static String getSpellDescriptor(Cursor cursor) {
			return cursor.getString(19);
		}

		public static String getSpellLists(Cursor cursor) {
			return cursor.getString(20);
		}

		public static String getSpellComponents(Cursor cursor) {
			return cursor.getString(21);
		}

		public static String getSpellSource(Cursor cursor) {
			return cursor.getString(22);
		}

		public static String getCreatureType(Cursor cursor) {
			return cursor.getString(23);
		}

		public static String getCreatureSubtype(Cursor cursor) {
			return cursor.getString(24);
		}

		public static String getCreatureSuperRace(Cursor cursor) {
			return cursor.getString(25);
		}

		public static String getCreatureCr(Cursor cursor) {
			return cursor.getString(26);
		}

		public static String getCreatureXp(Cursor cursor) {
			return cursor.getString(27);
		}

		public static String getCreatureSize(Cursor cursor) {
			return cursor.getString(28);
		}

		public static String getCreatureAlign(Cursor cursor) {
			return cursor.getString(29);
		}

		public static boolean hasLevel(Cursor cursor) {
			if (cursor.getColumnCount() > 30) {
				return true;
			}
			return false;

		}

		public static Integer getSpellLevel(Cursor cursor) {
			return cursor.getInt(30);
		}
	}
}
