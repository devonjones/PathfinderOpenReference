package org.evilsoft.pathfinder.reference.render.html;

import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.book.SkillAdapter;

import android.database.Cursor;

public class SkillRenderer extends HtmlRenderer {
	private BookDbAdapter bookDbAdapter;

	public SkillRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	@Override
	public String renderTitle() {
		return renderTitle(name, abbrev, newUri, 0, top);
	}

	@Override
	public String renderDetails() {
		StringBuilder sb = new StringBuilder();
		sb.append(renderSkillDetails(sectionId));
		sb.append("<B>Source: </B>");
		sb.append(source);
		sb.append("<BR>");
		return sb.toString();
	}

	public String renderSkillDetails(Integer sectionId) {
		Cursor cursor = bookDbAdapter.getSkillAdapter().fetchSkillAttr(sectionId);
		try {
			StringBuilder sb = new StringBuilder();
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				sb.append("<H2>(");
				sb.append(SkillAdapter.SkillUtils.getAttribute(cursor));
				boolean armorCheckPenalty = (SkillAdapter.SkillUtils.getArmorCheckPenalty(cursor) != 0);
				if (armorCheckPenalty) {
					sb.append("; Armor Check Penalty");
				}
				boolean trainedOnly = (SkillAdapter.SkillUtils.getTrainedOnly(cursor) != 0);
				if (trainedOnly) {
					sb.append("; Trained Only");
				}
				sb.append(")</H2>\n");
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
