package org.evilsoft.pathfinder.reference.db.user;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class UserDbAdapter {
	private Context context;
	public SQLiteDatabase database;
	private UserDbHelper dbHelper;
	private boolean closed = true;

	public UserDbAdapter(Context context) {
		this.context = context;
	}

	public UserDbAdapter open() throws SQLException {
		dbHelper = new UserDbHelper(context);
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
}
