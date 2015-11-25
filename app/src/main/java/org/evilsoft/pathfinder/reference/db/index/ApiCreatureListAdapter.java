package org.evilsoft.pathfinder.reference.db.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ApiCreatureListAdapter {
	public SQLiteDatabase database;
	public Context context;

	public ApiCreatureListAdapter(SQLiteDatabase database, Context context) {
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
		columns.add("creature_type");
		columns.add("creature_subtype");
		columns.add("super_race");
		columns.add("cr");
		columns.add("xp");
		columns.add("size");
		columns.add("alignment");
		translation.put("_id", "index_id as _id");
		translation.put("content_url", "url as content_url");
		translation.put("skill_attribute", "attribute as skill_attribute");
		translation.put("super_race", "super_race as creature_super_race");
		translation.put("cr", "cr as creature_cr");
		translation.put("xp", "xp as creature_xp");
		translation.put("size", "size as creature_size");
		translation.put("alignment", "alignment as creature_alignment");
	}

	public Cursor getCreatures(String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ");
		sb.append(BaseDbHelper.implementProjection(columns, projection,
				translation));
		sb.append(" FROM central_index");
		sb.append(" WHERE type = 'skill'");
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
