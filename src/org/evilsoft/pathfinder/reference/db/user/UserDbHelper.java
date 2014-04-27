package org.evilsoft.pathfinder.reference.db.user;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDbHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "psrd_user.db";
	private static final int DATABASE_VERSION = 3;

	public UserDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(createCollectionTable());
		db.execSQL(addDefaultCollection());
		db.execSQL(createPsrdDbVersionTable());
		db.execSQL(createCollectionValuesTable());
		db.execSQL(createHistoryTable());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < 2) {
			db.execSQL(createCollectionValuesTable());
		} else if (oldVersion < 3) {
			db.execSQL(createHistoryTable());
		}
	}

	private String createCollectionTable() {
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE collections(");
		sb.append(" collection_id INTEGER PRIMARY KEY,");
		sb.append(" name TEXT");
		sb.append(")");
		return sb.toString();
	}

	public String addDefaultCollection() {
		StringBuffer sb = new StringBuffer();
		sb.append("INSERT INTO collections");
		sb.append(" (name) VALUES ('default')");
		return sb.toString();
	}

	private String createCollectionValuesTable() {
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE collection_values (");
		sb.append(" collection_entry_id INTEGER PRIMARY KEY,");
		sb.append(" collection_id INTEGER,");
		sb.append(" name TEXT,");
		sb.append(" url TEXT");
		sb.append(")");
		return sb.toString();
	}

	private String createPsrdDbVersionTable() {
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE psrd_db_version (");
		sb.append(" version INTEGER");
		sb.append(")");
		return sb.toString();
	}

	private String createHistoryTable() {
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE history (");
		sb.append(" history_id INTEGER PRIMARY KEY,");
		sb.append(" name TEXT,");
		sb.append(" url TEXT");
		sb.append(")");
		return sb.toString();
	}
}
