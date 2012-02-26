package org.evilsoft.pathfinder.reference.render;

import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;

import android.database.Cursor;

public class TrapRenderer extends StatBlockRenderer {
	private PsrdDbAdapter dbAdapter;

	public TrapRenderer(PsrdDbAdapter dbAdapter) {
		this.dbAdapter = dbAdapter;
	}

	@Override
	public String renderTitle() {
		Cursor curs = dbAdapter.getTrapDetails(sectionId);
		boolean has_next = curs.moveToFirst();
		String title = name;
		if (has_next) {
			String cr = curs.getString(0);
			if (cr != null) {
				title = title + " (CR " + cr + ")";
			}
		}
		return renderStatBlockTitle(title, newUri, top);
	}
	
	@Override
	public String renderDetails() {
		StringBuffer sb = new StringBuffer();
		Cursor curs = dbAdapter.getTrapDetails(sectionId);
		//0: cr, 1: trap_type, 2: perception, 3: disable_device, 4: duration, 5: effect,
		//6: trigger, 7: reset
		boolean has_next = curs.moveToFirst();
		if (has_next) {
			String cr = curs.getString(0);
			if (top) {
				sb.append("<b>CR ");
				sb.append(cr);
				sb.append("</b><br>\n");
			}
			sb.append(addField("Type", curs.getString(1), false));
			sb.append(addField("Perception", curs.getString(2), false));
			sb.append(addField("Disable Device", curs.getString(3)));
			sb.append(renderStatBlockBreaker("Effects"));
			sb.append(addField("Trigger", curs.getString(6), false));
			sb.append(addField("Duration", curs.getString(4), false));
			sb.append(addField("Reset", curs.getString(7)));
			sb.append(addField("Effect", curs.getString(5)));
		}
		return sb.toString();
	}
}
