package org.evilsoft.pathfinder.reference.render.html;

import org.evilsoft.pathfinder.reference.db.book.AbilityAdapter;
import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.book.TrapAdapter;

import android.database.Cursor;

public class TrapRenderer extends StatBlockRenderer {
	private BookDbAdapter bookDbAdapter;

	public TrapRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	@Override
	public String renderTitle() {
		Cursor cursor = bookDbAdapter.getTrapAdapter().getTrapDetails(sectionId);
		try {
			boolean has_next = cursor.moveToFirst();
			String title = abilityName(name, sectionId);
			if (has_next) {
				String cr = TrapAdapter.TrapUtils.getCr(cursor);
				if (cr != null) {
					title = title + " (CR " + cr + ")";
				}
			}
			return renderStatBlockTitle(title, newUri, top);
		} finally {
			cursor.close();
		}
	}

	public String abilityName(String name, Integer sectionId) {
		Cursor cursor = bookDbAdapter.getAbilityAdapter().getAbilityTypes(sectionId);
		try {
			StringBuffer sb = new StringBuffer();
			sb.append(name);
			boolean fields = false;
			boolean has_next = cursor.moveToFirst();
			String comma = "";
			while (has_next) {
				sb.append(comma);
				if (fields != true) {
					sb.append(" (");
					comma = ", ";
					fields = true;
				}
				String type = AbilityAdapter.AbilityUtils.getAbilityType(cursor);
				if (type.equals("Extraordinary")) {
					sb.append("Ex");
				} else if (type.equals("Supernatural")) {
					sb.append("Su");
				} else if (type.equals("Spell-Like")) {
					sb.append("Sp");
				}
				has_next = cursor.moveToNext();
			}
			if (fields) {
				sb.append(")");
			}
			return sb.toString();
		} finally {
			cursor.close();
		}
	}

	@Override
	public String renderDetails() {
		Cursor cursor = bookDbAdapter.getTrapAdapter().getTrapDetails(sectionId);
		try {
			StringBuffer sb = new StringBuffer();
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				String cr = TrapAdapter.TrapUtils.getCr(cursor);
				if (top) {
					sb.append("<b>CR ");
					sb.append(cr);
					sb.append("</b><br>\n");
				}
				sb.append(addField("Type", TrapAdapter.TrapUtils.getTrapType(cursor), false));
				sb.append(addField("Perception", TrapAdapter.TrapUtils.getPerception(cursor), false));
				sb.append(addField("Disable Device", TrapAdapter.TrapUtils.getDisableDevice(cursor)));
				sb.append(renderStatBlockBreaker("Effects"));
				sb.append(addField("Trigger", TrapAdapter.TrapUtils.getTrigger(cursor), false));
				sb.append(addField("Duration", TrapAdapter.TrapUtils.getDuration(cursor), false));
				sb.append(addField("Reset", TrapAdapter.TrapUtils.getReset(cursor)));
				sb.append(addField("Effect", TrapAdapter.TrapUtils.getEffect(cursor)));
			}
			return sb.toString();
		} finally {
			cursor.close();
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
