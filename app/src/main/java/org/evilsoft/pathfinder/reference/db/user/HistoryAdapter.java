package org.evilsoft.pathfinder.reference.db.user;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.content.ContentValues;
import android.database.Cursor;

public class HistoryAdapter {
	private UserDbAdapter userDbAdapter;

	public HistoryAdapter(UserDbAdapter userDbAdapter) {
		this.userDbAdapter = userDbAdapter;
	}

	public boolean addHistory(String name, String url) {
		Integer historyId = selectHistoryId(url);
		if (historyId != null) {
			delHistory(historyId);
		}
		ContentValues cv = new ContentValues();
		cv.put("name", name);
		cv.put("url", url);
		return userDbAdapter.database.insert("history", null, cv) > -1;
	}

	public int delHistory(Integer historyId) {
		List<String> args = new ArrayList<String>();
		args.add(historyId.toString());
		return userDbAdapter.database.delete("history", "history_id = ?",
				BaseDbHelper.toStringArray(args));
	}

	public Integer selectHistoryId(String url) {
		List<String> args = new ArrayList<String>();
		args.add(url);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT history_id");
		sb.append(" FROM history");
		sb.append(" WHERE url = ?");
		String sql = sb.toString();
		Cursor c = userDbAdapter.database.rawQuery(sql,
				BaseDbHelper.toStringArray(args));
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

	public Cursor fetchHistory() {
		List<String> args = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT history_id as _id, name, url");
		sql.append(" FROM history");
		sql.append(" ORDER BY history_id DESC");
		sql.append(" LIMIT 50");
		return userDbAdapter.database.rawQuery(sql.toString(),
				BaseDbHelper.toStringArray(args));
	}

	public static class HistoryUtils {
		private HistoryUtils() {
		}

		public static Integer getHistoryId(Cursor cursor) {
			return cursor.getInt(0);
		}

		public static String getName(Cursor cursor) {
			return cursor.getString(1);
		}

		public static String getUrl(Cursor cursor) {
			return cursor.getString(2);
		}
	}
}
