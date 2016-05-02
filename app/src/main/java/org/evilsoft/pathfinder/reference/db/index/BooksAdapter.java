package org.evilsoft.pathfinder.reference.db.index;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;
import org.evilsoft.pathfinder.reference.preference.FilterPreferenceManager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BooksAdapter {
	public SQLiteDatabase database;
	public Context context;

	public BooksAdapter(SQLiteDatabase database, Context context) {
		this.database = database;
		this.context = context;
	}

	public Cursor fetchBook(String source) {
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT book_id, source, db");
		sb.append(" FROM books");
		sb.append(" WHERE source = ?");
		args.add(source);
		sb.append(FilterPreferenceManager.getSourceFilter(context, args, "AND"));
		sb.append(" LIMIT 1");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public static class BookUtils {
		private BookUtils() {
		}

		public static String getBookDb(Cursor cursor) {
			boolean hasnext = cursor.moveToFirst();
			if(hasnext) {
				return getDb(cursor);
			}
			return null;
		}

		public static Integer getBookId(Cursor cursor) {
			return cursor.getInt(0);
		}

		public static String getSource(Cursor cursor) {
			return cursor.getString(1);
		}

		public static String getDb(Cursor cursor) {
			return cursor.getString(2);
		}
	}
}
