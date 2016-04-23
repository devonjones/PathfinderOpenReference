package org.evilsoft.pathfinder.reference.db.user;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;
import org.evilsoft.pathfinder.reference.db.DbWrangler;
import org.evilsoft.pathfinder.reference.utils.UrlAliaser;

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

	public void updateBookmarks(DbWrangler dbWrangler) {
		Cursor curs = fetchAllBookmarks();
		try {
			boolean hasNext = curs.moveToFirst();
			while (hasNext) {
				String id = curs.getString(0);
				String url = curs.getString(2);
				if (url.indexOf("?") > -1) {
					url = TextUtils.split(url, "\\?")[0];
				}
				String aliasedUrl = UrlAliaser.aliasUrl(dbWrangler, url);
				Cursor cursor = dbWrangler.getIndexDbAdapter()
						.getIndexGroupAdapter().fetchByUrl(aliasedUrl);
				try {
					if (cursor.getCount() == 0) {
					} else if (!aliasedUrl.equals(url)) {
						updateBookmarkUrl(id, aliasedUrl);
					}
				} finally {
					cursor.close();
				}
				hasNext = curs.moveToNext();
			}
		} finally {
			curs.close();
		}
	}

	private int updateBookmarkUrl(String id, String newUrl) {
		ContentValues args = new ContentValues();
		args.put("url", newUrl);
		return database.update("collection_values", args,
				"collection_entry_id = " + id, null);
	}

	private Cursor fetchAllBookmarks() {
		List<String> args = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT collection_entry_id, name, url");
		sql.append(" FROM collection_values");
		return database.rawQuery(sql.toString(),
				BaseDbHelper.toStringArray(args));
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
