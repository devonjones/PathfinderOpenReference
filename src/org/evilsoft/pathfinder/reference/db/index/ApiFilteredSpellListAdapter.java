package org.evilsoft.pathfinder.reference.db.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ApiFilteredSpellListAdapter {
	public SQLiteDatabase database;
	public Context context;

	public ApiFilteredSpellListAdapter(SQLiteDatabase database, Context context) {
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
		columns.add("school");
		columns.add("subschool");
		columns.add("descriptor");
		columns.add("classes");
		columns.add("components");
		translation.put("_id", "i.index_id as _id");
		translation.put("content_url", "i.url as content_url");
		translation.put("school", "i.spell_school as school");
		translation.put("subschool", "i.spell_subschool_text as subschool");
		translation.put("descriptor", "i.spell_descriptor_text as descriptor");
		translation.put("classes", "i.spell_list_text as classes");
		translation.put("components", "i.spell_component_text as components");
	}

	public Cursor getFilteredSpells(String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT DISTINCT ");
		sb.append(BaseDbHelper.implementProjection(columns, projection,
				translation));
		sb.append(" FROM central_index as i");
		sb.append("  INNER JOIN spell_component_index as spell_components");
		sb.append("   ON spell_components.index_id = i.index_id");
		sb.append("  INNER JOIN spell_descriptor_index as spell_descriptors");
		sb.append("   ON spell_descriptors.index_id = i.index_id");
		sb.append("  INNER JOIN spell_list_index as spell_lists");
		sb.append("   ON spell_lists.index_id = i.index_id");
		sb.append("  INNER JOIN spell_subschool_index as spell_subschools");
		sb.append("   ON spell_subschools.index_id = i.index_id");
		sb.append(" WHERE type = 'spell'");
		if (selection != null) {
			sb.append("  AND ");
			sb.append(selection);
		}
		if (sortOrder != null) {
			sb.append(" ORDER BY ");
			sb.append(sortOrder);
		} else {
			sb.append(" ORDER BY name");
		}
		String sql = sb.toString();
		return database.rawQuery(sql, selectionArgs);
	}
}
