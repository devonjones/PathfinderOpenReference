package org.evilsoft.pathfinder.reference.db.psrd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.evilsoft.pathfinder.reference.preference.FilterPreferenceManager;

import android.database.Cursor;

public class RaceAdapter {
	private PsrdDbAdapter dbAdapter;

	public RaceAdapter(PsrdDbAdapter dbAdapter) {
		this.dbAdapter = dbAdapter;
	}

	public Cursor fetchRaceTypes() {
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT DISTINCT subtype");
		sb.append(" FROM sections");
		sb.append(" WHERE type = 'race'");
		sb.append(FilterPreferenceManager.getSourceFilter(args, "AND"));
		sb.append(" ORDER BY subtype");
		String sql = sb.toString();
		return dbAdapter.database.rawQuery(sql,
				PsrdDbAdapter.toStringArray(args));
	}

	public Cursor fetchRaceList(String raceType) {
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT s.section_id, s.name, s.subtype");
		sb.append(" FROM sections s");
		sb.append(" WHERE s.type = 'race'");
		sb.append(FilterPreferenceManager.getSourceFilter(args, "AND"));
		if (raceType != null) {
			String subtype = raceType.toLowerCase() + "_race";
			sb.append("   AND subtype = ?");
			args.add(subtype);
		}
		sb.append(" ORDER BY s.name");
		String sql = sb.toString();
		return dbAdapter.database.rawQuery(sql,
				PsrdDbAdapter.toStringArray(args));
	}

	public ArrayList<HashMap<String, Object>> createRaceTypeList() {
		Cursor curs = fetchRaceTypes();
		try {
			ArrayList<HashMap<String, Object>> secList = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> child = new HashMap<String, Object>();
			boolean has_next = curs.moveToFirst();
			while (has_next) {
				String rawRaceType = curs.getString(0);
				String raceType = rawRaceType.split("_")[0];
				String classTitle = raceType.substring(0, 1).toUpperCase()
						+ raceType.substring(1) + " Races";
				child = new HashMap<String, Object>();
				child.put("specificName", classTitle);
				child.put("id", raceType);
				secList.add(child);
				has_next = curs.moveToNext();
			}
			return secList;
		} finally {
			curs.close();
		}
	}

}
