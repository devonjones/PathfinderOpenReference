package org.evilsoft.pathfinder.reference.db.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.acra.ErrorReporter;
import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

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

	public void updateBookmarks(SQLiteDatabase psrdDb) {
		Cursor curs = fetchAllBookmarks();
		boolean hasNext = curs.moveToFirst();
		while(hasNext) {
			String id = curs.getString(0);
			String url = curs.getString(2);
			if(url.indexOf("?") > -1) {
				url = TextUtils.split(url, "\\?")[0];
			}
			if (!urlExists(psrdDb, url)) {
				LinkedList<String> parts = new LinkedList<String>(Arrays.asList(url.split("\\/")));
				String removed = parts.remove(parts.size() - 1);
				String testUrl = TextUtils.join("/", parts);
				String candidateUrl = getCandidate(psrdDb, removed, testUrl);
				if (candidateUrl != null) {
					updateBookmarkUrl(id, candidateUrl);
				} else {
					ErrorReporter.getInstance().putCustomData(
							"Situation", "URL does not exist: " + url);
					ErrorReporter.getInstance().handleException(null);
					deleteBookmark(id);
				}
			}
			hasNext = curs.moveToNext();
		}
	}

	private int deleteBookmark(String id) {
		List<String> args = new ArrayList<String>();
		args.add(id);
		return database.delete(
				"collection_values",
				"collection_id = ?",
				BaseDbHelper.toStringArray(args));
	}

	private int updateBookmarkUrl(String id, String newUrl) {
		ContentValues args = new ContentValues();
		args.put("url", newUrl);
		return database.update("collection_values", args, "collection_entry_id = " + id, null);
	}

	private String getCandidate(SQLiteDatabase psrdDb, String removed,
			String testUrl) {
		List<String> args = new ArrayList<String>();
		args.add(testUrl);
		args.add(removed);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT node.url");
		sql.append(" FROM sections AS node, sections AS parent");
		sql.append(" WHERE node.lft BETWEEN parent.lft AND parent.rgt");
		sql.append("  AND parent.url = ?");
		sql.append("  AND node.name = ?");
		Cursor curs = psrdDb.rawQuery(sql.toString(),
				BaseDbHelper.toStringArray(args));
		try {
			if (curs.getCount() == 1) {
				curs.moveToFirst();
				return curs.getString(0);
			}
		} finally {
			curs.close();
		}
		return null;
	}

	private boolean urlExists(SQLiteDatabase psrdDb, String url) {
		List<String> args = new ArrayList<String>();
		args.add(url);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT *");
		sql.append(" FROM sections");
		sql.append(" WHERE url = ?");
		Cursor curs = psrdDb.rawQuery(sql.toString(),
				BaseDbHelper.toStringArray(args));
		try {
			boolean result = curs.moveToFirst();
			if (result) {
				return result;
			}
			else {
				return urlReferenceExists(psrdDb, url);
			}
		} finally {
			curs.close();
		}
	}

	private boolean urlReferenceExists(SQLiteDatabase psrdDb, String url) {
		List<String> args = new ArrayList<String>();
		args.add(url);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT *");
		sql.append(" FROM url_references");
		sql.append(" WHERE url = ?");
		Cursor curs = psrdDb.rawQuery(sql.toString(),
				BaseDbHelper.toStringArray(args));
		try {
			return curs.moveToFirst();
		} finally {
			curs.close();
		}
	}

	private Cursor fetchAllBookmarks() {
		List<String> args = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT collection_entry_id, name, url");
		sql.append(" FROM collection_values");
		return database.rawQuery(sql.toString(),
				BaseDbHelper.toStringArray(args));
	}
}
