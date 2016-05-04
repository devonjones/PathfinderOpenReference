package org.evilsoft.pathfinder.reference.render.html;

import org.evilsoft.pathfinder.reference.db.book.AbilityAdapter;
import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;

import android.database.Cursor;

public class AbilityRenderer extends HtmlRenderer {
	private BookDbAdapter bookDbAdapter;

	public AbilityRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	@Override
	public String renderTitle() {
		return renderTitle(abilityName(name, sectionId), abbrev, newUri, depth, top);
	}

	@Override
	public String renderDetails() {
		return "";
	}

	public String abilityName(String name, Integer sectionId) {
		Cursor cursor = bookDbAdapter.getAbilityAdapter().getAbilityTypes(sectionId);
		try {
			StringBuilder sb = new StringBuilder();
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
	public String renderFooter() {
		return "";
	}

	@Override
	public String renderHeader() {
		return "";
	}
}
