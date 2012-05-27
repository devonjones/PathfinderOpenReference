package org.evilsoft.pathfinder.reference;

import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
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
		if(dbAdapter == null) {
			dbAdapter = new PsrdDbAdapter(getContext());
			dbAdapter.open();
		}
		
		Cursor c = null;
		try {
			c = dbAdapter.autocomplete(uri.getLastPathSegment());
		} catch(NullPointerException e) {
			
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
