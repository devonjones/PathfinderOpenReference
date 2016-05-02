package org.evilsoft.pathfinder.reference.db.book;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BookDbHelper extends BaseDbHelper {

	public BookDbHelper(Context context, String db_name) {
		super(context, db_name);
	}

	public boolean testDb(SQLiteDatabase database) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT count(*)");
		sb.append(" FROM section_index");
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
