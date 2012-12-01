package org.evilsoft.pathfinder.reference.utils;

import org.evilsoft.pathfinder.reference.db.DbWrangler;
import org.evilsoft.pathfinder.reference.db.index.IndexGroupAdapter;

import android.database.Cursor;

public class UrlAliaser {

	public static String aliasUrl(DbWrangler dbWrangler, String url) {
		String[] parts = url.split("\\/");
		if (parts[2].equals("PFSRD")) {
			if (parts[3].startsWith("Rules")) {
				return aliasRulesUrl(parts);
			} else {
				return aliasCategoryUrl(dbWrangler, parts);
			}
		}
		return url;
	}
	
	protected static String aliasRulesUrl(String[] parts) {
		// pfsrd://PFSRD/Rules Ultimate Combat/Class Archetypes/Class Archetypes
		String book = parts[3].substring(6);
		StringBuffer sb = new StringBuffer();
		sb.append("pfsrd://");
		sb.append(book);
		sb.append("/Rules");
		for(int i = 4; i < parts.length; i++) {
			sb.append("/");
			sb.append(parts[i]);
		}
		return sb.toString();
	}

	protected static String aliasCategoryUrl(DbWrangler dbWrangler, String[] parts) {
		// pfsrd://PFSRD/Monsters/Spriggan/Spriggan (Large Size)
		String testUrl = createTestUrl(parts, 0);
		for(int i = 4; i < parts.length; i++) {
			testUrl = createTestUrl(parts, i - 4);
			Cursor cursor = dbWrangler.getIndexDbAdapter().getIndexGroupAdapter().fetchByMatchUrl(testUrl);
			try {
				if(cursor.getCount() > 0) {
					cursor.moveToFirst();
					return IndexGroupAdapter.IndexGroupUtils.getUrl(cursor);
				}
			}
			finally {
				cursor.close();
			}
		}
		return testUrl;
	}
	
	protected static String createTestUrl(String[] parts, int sub) {
		StringBuffer sb = new StringBuffer();
		sb.append("pfsrd://%");
		for(int i = 3; i < parts.length - sub; i++) {
			sb.append("/");
			sb.append(parts[i]);
		}
		return sb.toString();
	}
}
