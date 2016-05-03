package org.evilsoft.pathfinder.reference;

import java.io.IOException;

import org.evilsoft.pathfinder.reference.db.DbWrangler;
import org.evilsoft.pathfinder.reference.utils.LimitedSpaceException;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class SearchProvider extends ContentProvider {
	private DbWrangler dbWrangler;

	@Override
	public boolean onCreate() {
		return true;
	}

	public boolean initializeDatabase() {
		if (dbWrangler != null && !dbWrangler.isClosed()) {
			return true;
		}
		boolean cont = true;
		try {
			DbWrangler dbw = new DbWrangler(getContext());
			dbw.checkDatabases();
		} catch (IOException e) {
			cont = false;
		} catch (LimitedSpaceException e) {
			// ignoring the big warning message that would normally accompany an
			// out of space issue with database creation. This is due to the
			// fact that this code is called from global search and the use case
			// for that error is a bit wonky
			cont = false;
		} finally {
		}
		if (cont) {
			openDb();
		}
		return cont;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor c = null;
		if (initializeDatabase()) {
			c = dbWrangler.getIndexDbAdapter().getSearchAdapter()
					.autocomplete(uri.getLastPathSegment().trim());
		}

		return c;
	}

	private void openDb() {
		if (dbWrangler == null) {
			dbWrangler = new DbWrangler(getContext());
		}
		if (dbWrangler.isClosed()) {
			dbWrangler.open();
		}
	}

	@Override
	public void shutdown() {
		if (dbWrangler != null) {
			dbWrangler.close();
		}
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		return 0;
	}
}
