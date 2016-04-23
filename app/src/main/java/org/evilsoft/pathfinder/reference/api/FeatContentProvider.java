package org.evilsoft.pathfinder.reference.api;

import org.evilsoft.pathfinder.reference.api.contracts.FeatContract;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

public class FeatContentProvider extends AbstractContentProvider {
	public FeatContentProvider() {
		uriMatcher.addURI(FeatContract.AUTHORITY, "feats", 1);
		uriMatcher.addURI(FeatContract.AUTHORITY, "feats/#", 1000);
	}

	public Cursor getFeatList(String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return dbWrangler.getIndexDbAdapter().getApiFeatListAdapter()
				.getFeats(projection, selection, selectionArgs, sortOrder);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		if (initializeDatabase()) {
			switch (uriMatcher.match(stripExtension(uri))) {
			case 1:
				return getFeatList(projection, selection, selectionArgs,
						sortOrder);

			case 1000:
				throw new IllegalArgumentException("URI " + uri.toString()
						+ " can only be opened as a file");

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
			return FeatContract.FEAT_LIST_CONTENT_TYPE;
		case 1000:
			if (uri.getLastPathSegment().endsWith(".html")) {
				return "text/html";
			} else if (uri.getLastPathSegment().endsWith(".json")) {
				return "application/json";
			}
		default:
			return null;
		}
	}

	@Override
	public String getFileId(Uri uri) {
		if (uriMatcher.match(stripExtension(uri)) >= 1000) {
			String last = uri.getLastPathSegment();
			return last.substring(0, last.indexOf("."));
		}
		return "-1";
	}
}
