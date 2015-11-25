package org.evilsoft.pathfinder.reference.db.index;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class IndexDbHelper extends BaseDbHelper {
	private static String DB_NAME = "index.db";

	public IndexDbHelper(Context context) {
		super(context, DB_NAME);
	}

	public boolean testDb(SQLiteDatabase database) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT count(*)");
		sb.append(" FROM central_index");
		String sql = sb.toString();
		Cursor curs = database.rawQuery(sql, new String[0]);
		try {
			curs.moveToFirst();
			curs.getInt(0);
		} finally {
			curs.close();
		}
		return true;
	}
}
