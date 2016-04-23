package org.evilsoft.pathfinder.reference.db.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ApiCasterListAdapter {
	public SQLiteDatabase database;
	public Context context;
	private static final String TAG = "ApiCasterListAdapter";

	public ApiCasterListAdapter(SQLiteDatabase database, Context context) {
		this.database = database;
		this.context = context;
	}

	protected static List<String> columns = new ArrayList<String>();
	protected static Map<String, String> translation = new HashMap<String, String>();

	static {
		columns.add("_id");
		columns.add("class");
		columns.add("level");
		columns.add("magic_type");
		translation.put("_id", "i.index_id as _id");
	}

	public Cursor getCasters(String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ");
		sb.append(BaseDbHelper.implementProjection(columns, projection,
				translation));
		sb.append(" FROM spell_list_index AS sli");
		sb.append("  INNER JOIN central_index AS i");
		sb.append("   ON i.name = sli.name");
		sb.append("    AND i.type = 'class'");
		if (selection != null) {
			sb.append(" WHERE");
			sb.append(selection);
		}
		sb.append(" GROUP BY name, level");
		if (sortOrder != null) {
			sb.append(" ORDER BY ");
			sb.append(sortOrder);
		} else {
			sb.append(" ORDER BY name, level");
		}
		String sql = sb.toString();
		Log.d(TAG, database == null ? "null" : database.toString());
		return database.rawQuery(sql, selectionArgs);
	}
}
