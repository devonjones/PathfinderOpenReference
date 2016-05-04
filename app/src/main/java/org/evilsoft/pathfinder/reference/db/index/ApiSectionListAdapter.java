package org.evilsoft.pathfinder.reference.db.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ApiSectionListAdapter {
	public SQLiteDatabase database;
	public Context context;

	public ApiSectionListAdapter(SQLiteDatabase database, Context context) {
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
		translation.put("_id", "index_id as _id");
		translation.put("content_url", "url as content_url");
	}

	public Cursor getClasses(String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		sb.append(BaseDbHelper.implementProjection(columns, projection,
				translation));
		sb.append(" FROM central_index");
		sb.append(" WHERE type = 'class'");
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
