package org.evilsoft.pathfinder.reference;

import java.io.IOException;

import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbHelper;
import org.evilsoft.pathfinder.reference.db.user.PsrdUserDbAdapter;
import org.evilsoft.pathfinder.reference.utils.LimitedSpaceException;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class SearchProvider extends ContentProvider {
	private PsrdDbAdapter dbAdapter;

	@Override
	public boolean onCreate() {
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		PsrdDbHelper dbh = new PsrdDbHelper(getContext());
		PsrdUserDbAdapter userDbAdapter = new PsrdUserDbAdapter(getContext());
		userDbAdapter.open();
		boolean cont = true;
		try {
			dbh.createDatabase(userDbAdapter);
		} catch (IOException e) {
			cont = false;
		} catch (LimitedSpaceException e) {
			// ignoring the big warning message that would normally accompany an
			// out of space issue with database creation. This is due to the
			// fact that this code is called from global search and the use case
			// for that error is a bit wonky
			cont = false;
		} finally {
			userDbAdapter.close();
		}

		Cursor c = null;

		if (cont) {
			if (dbAdapter == null) {
				dbAdapter = new PsrdDbAdapter(getContext());
				dbAdapter.open();
			}

			c = dbAdapter.autocomplete(uri.getLastPathSegment());
		}

		return c;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
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
