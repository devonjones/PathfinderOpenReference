package org.evilsoft.pathfinder.reference.db.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ApiFilteredClassSpellListAdapter {
	public SQLiteDatabase database;
	public Context context;

	public ApiFilteredClassSpellListAdapter(SQLiteDatabase database,
			Context context) {
		this.database = database;
		this.context = context;
	}

	protected static List<String> columns = new ArrayList<String>();
	protected static Map<String, String> translation = new HashMap<String, String>();

	static {
		columns.add("_id");
		columns.add("source");
		columns.add("type");
		columns.add("subtype");
		columns.add("name");
		columns.add("description");
		columns.add("content_url");
		columns.add("class");
		columns.add("level");
		columns.add("magic_type");
		columns.add("school");
		columns.add("subschool");
		columns.add("descriptor");
		columns.add("components");
		translation.put("_id", "i.index_id as _id");
		translation.put("content_url", "i.url as content_url");
		translation.put("school", "spell_school as school");
		translation.put("subschool", "spell_subschool_text as subschool");
		translation.put("descriptor", "spell_descriptor_text as descriptor");
		translation.put("components", "spell_component_text as components");
	}

	public String getClassName(String classId) {
		List<String> args = new ArrayList<String>();
		args.add(classId);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT name");
		sb.append(" FROM central_index");
		sb.append(" WHERE type = 'class'");
		sb.append("  AND index_id = ?");
		String sql = sb.toString();
		Cursor curs = database.rawQuery(sql, BaseDbHelper.toStringArray(args));
		boolean hasRecord = curs.moveToFirst();
		if (hasRecord) {
			return curs.getString(0);
		}
		return null;
	}

	public Cursor getFilteredClassSpells(String classId, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ");
		sb.append(BaseDbHelper.implementProjection(columns, projection,
				translation));
		sb.append(" FROM central_index i");
		sb.append("  INNER JOIN spell_list_index as spell_lists");
		sb.append("   ON spell_lists.index_id = i.index_id");
		sb.append("  INNER JOIN spell_component_index as spell_components");
		sb.append("   ON spell_components.index_id = i.index_id");
		sb.append("  INNER JOIN spell_descriptor_index as spell_descriptors");
		sb.append("   ON spell_descriptors.index_id = i.index_id");
		sb.append("  INNER JOIN spell_subschool_index as spell_subschools");
		sb.append("   ON spell_subschools.index_id = i.index_id");
		sb.append(" WHERE type = 'spell'");
		if (classId != null) {
			String className = getClassName(classId);
			if (className != null) {
				sb.append("  AND spell_lists.class = ?");
				args.add(className);
			}
		}
		if (selection != null) {
			sb.append("  AND ");
			sb.append(selection);
			if (selectionArgs != null) {
				args.addAll(Arrays.asList(selectionArgs));
			}
		}
		if (sortOrder != null) {
			sb.append(" ORDER BY ");
			sb.append(sortOrder);
		} else {
			sb.append(" ORDER BY level, name");
		}
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}
}
