package org.evilsoft.pathfinder.reference.api;

import org.evilsoft.pathfinder.reference.api.contracts.CasterContract;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

public class CasterContentProvider extends AbstractContentProvider {
	public CasterContentProvider() {
		uriMatcher.addURI(CasterContract.AUTHORITY, "/casters", 1);
	}

	public Cursor getCasterList(String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return dbWrangler.getIndexDbAdapter().getApiCasterListAdapter()
				.getCasters(projection, selection, selectionArgs, sortOrder);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		if (initializeDatabase()) {
			switch (uriMatcher.match(stripExtension(uri))) {
			case 1:
				return getCasterList(projection, selection, selectionArgs,
						sortOrder);
			default:
				throw new IllegalArgumentException("URI " + uri.toString()
						+ " Not supported");
			}
		}
		return new MatrixCursor(selectionArgs, 0);
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(stripExtension(uri))) {
		case 1:
			return CasterContract.CASTER_LIST_CONTENT_TYPE;

		default:
			return null;
		}
	}

	@Override
	public String getFileId(Uri uri) {
		return "-1";
	}
}
