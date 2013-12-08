package org.evilsoft.pathfinder.reference.api;

import java.util.List;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

public class SpellContentProvider extends AbstractContentProvider {
	public SpellContentProvider() {
		uriMatcher.addURI("org.evilsoft.pathfinder.reference.api.spell",
				"spells", 1);
		uriMatcher.addURI("org.evilsoft.pathfinder.reference.api.spell",
				"classes/#/spells", 2);
		uriMatcher.addURI("org.evilsoft.pathfinder.reference.api.spell",
				"spells/#", 1000);
	}

	public Cursor getSpellList(String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return dbWrangler.getIndexDbAdapter().getApiSpellListAdapter()
				.getSpells(projection, selection, selectionArgs, sortOrder);
	}

	public Cursor getClassSpellList(String classId, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {
		return dbWrangler
				.getIndexDbAdapter()
				.getApiClassSpellListAdapter()
				.getClassSpells(classId, projection, selection, selectionArgs,
						sortOrder);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		if (initializeDatabase()) {
			switch (uriMatcher.match(stripExtension(uri))) {
			case 1:
				return getSpellList(projection, selection, selectionArgs,
						sortOrder);

			case 2:
				List<String> segments = uri.getPathSegments();
				String id = segments.get(segments.size() - 2);
				return getClassSpellList(id, projection, selection,
						selectionArgs, sortOrder);

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
			return "vnd.android.cursor.dir/org.evilsoft.pathfinder.reference.api.spell.list";

		case 2:
			return "vnd.android.cursor.dir/org.evilsoft.pathfinder.reference.api.class.spell.list";

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
