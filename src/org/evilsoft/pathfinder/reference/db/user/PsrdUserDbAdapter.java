package org.evilsoft.pathfinder.reference.db.user;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PsrdUserDbAdapter {
	private Context context;
	public SQLiteDatabase database;
	private PsrdUserDbHelper dbHelper;
	private boolean closed = true;

	public PsrdUserDbAdapter(Context context) {
		this.context = context;
	}

	public PsrdUserDbAdapter open() throws SQLException {
		dbHelper = new PsrdUserDbHelper(context);
		database = dbHelper.getWritableDatabase();
		closed = false;
		return this;
	}

	public void close() {
		dbHelper.close();
		dbHelper = null;
		database.close();
		database = null;
		closed = true;
	}

	public boolean isClosed() {
		return closed;
	}

	public Integer getPsrdDbVersion() {
		Integer ver = selectPsrdDbVersion();
		if (ver == null) {
			insertPsrdDbVersion();
		}
		return selectPsrdDbVersion();
	}

	public Integer selectPsrdDbVersion() {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT version");
		sb.append(" FROM psrd_db_version");
		sb.append(" LIMIT 1");
		String sql = sb.toString();
		String[] selectionArgs = new String[0];
		Cursor c = database.rawQuery(sql, selectionArgs);
		try {
			c.moveToFirst();
			if (c.getCount() < 1) {
				return null;
			}
			return c.getInt(0);
		} finally {
			c.close();
		}
	}

	public void insertPsrdDbVersion() {
		ContentValues values = new ContentValues();
		values.put("version", 0);
		database.insert("psrd_db_version", null, values);
	}

	public void updatePsrdDbVersion(Integer version) {
		ContentValues values = new ContentValues();
		values.put("version", version);
		database.update("psrd_db_version", values, null, null);
	}
}
