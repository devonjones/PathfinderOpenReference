package org.evilsoft.pathfinder.reference.render;

import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;

import android.database.Cursor;

public class AbilityRenderer extends Renderer {
	private PsrdDbAdapter dbAdapter;

	public AbilityRenderer(PsrdDbAdapter dbAdapter) {
		this.dbAdapter = dbAdapter;
	}

	@Override
	public String renderTitle() {
		return renderTitle(abilityName(name, sectionId), newUri, depth, top);
	}

	@Override
	public String renderDetails() {
		return "";
	}

	public String abilityName(String name, String sectionId) {
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		boolean fields = false;
		Cursor curs = dbAdapter.getAbilityTypes(sectionId);
		boolean has_next = curs.moveToFirst();
		String comma = "";
		while (has_next) {
			sb.append(comma);
			if (fields != true) {
				sb.append(" (");
				comma = ", ";
				fields = true;
			}
			String type = curs.getString(0);
			if (type.equals("Extraordinary")) {
				sb.append("Ex");
			} else if (type.equals("Supernatural")) {
				sb.append("Su");
			} else if (type.equals("Spell-Like")) {
				sb.append("Sp");
			}
			has_next = curs.moveToNext();
		}
		if (fields) {
			sb.append(")");
		}
		return sb.toString();
	}
}
