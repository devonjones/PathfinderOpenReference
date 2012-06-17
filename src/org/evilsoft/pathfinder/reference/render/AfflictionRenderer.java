package org.evilsoft.pathfinder.reference.render;

import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;

import android.database.Cursor;

public class AfflictionRenderer extends StatBlockRenderer {
	private PsrdDbAdapter dbAdapter;

	public AfflictionRenderer(PsrdDbAdapter dbAdapter) {
		this.dbAdapter = dbAdapter;
	}

	@Override
	public String renderTitle() {
		return renderStatBlockTitle(name, newUri, top);
	}

	@Override
	public String renderDetails() {
		Cursor curs = dbAdapter.getAfflictionDetails(sectionId);
		// 0:contracted, 1:save, 2:onset, 3:frequency, 4:effect,
		// 5:initial_effect, 6:secondary_effect, 7:cure
		try {
			StringBuffer sb = new StringBuffer();
			boolean has_next = curs.moveToFirst();
			if (has_next) {
				String contracted = curs.getString(0);
				if (contracted != null) {
					contracted = subtype + ", " + contracted;
				} else {
					contracted = subtype;
				}
				sb.append(addField("Type", contracted, false));
				sb.append(addField("Save", curs.getString(1)));
				sb.append(addField("Onset", curs.getString(2), false));
				sb.append(addField("Frequency", curs.getString(3)));
				sb.append(addField("Effect", curs.getString(4)));
				sb.append(addField("Initial Effect", curs.getString(5)));
				sb.append(addField("Secondary Effect", curs.getString(6)));
				sb.append(addField("Cure", curs.getString(7)));
			}
			return sb.toString();
		} finally {
			curs.close();
		}
	}

	@Override
	public String renderFooter() {
		return "";
	}

	@Override
	public String renderHeader() {
		return "";
	}
}
