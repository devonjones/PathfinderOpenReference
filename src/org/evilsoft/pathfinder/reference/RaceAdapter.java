package org.evilsoft.pathfinder.reference;

import android.database.Cursor;

public class RaceAdapter {
	private PsrdDbAdapter dbAdapter;

	public RaceAdapter(PsrdDbAdapter dbAdapter) {
		this.dbAdapter = dbAdapter;
	}

	public Cursor fetchRaceList() {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT s.section_id, s.name, s.subtype");
		sb.append(" FROM sections s");
		sb.append(" WHERE s.type = 'race'");
		sb.append(" ORDER BY s.subtype DESC, s.name");
		String sql = sb.toString();
		return dbAdapter.database.rawQuery(sql, new String[0]);
	}
}
