package org.evilsoft.pathfinder.reference.render;

import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.book.HauntAdapter;

import android.database.Cursor;

public class HauntRenderer extends StatBlockRenderer {
	private BookDbAdapter bookDbAdapter;

	public HauntRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	@Override
	public String renderTitle() {
		Cursor cursor = bookDbAdapter.getHauntAdapter().getHauntDetails(sectionId);
		try {
			boolean has_next = cursor.moveToFirst();
			String title = name;
			if (has_next) {
				String cr = HauntAdapter.HauntUtils.getCr(cursor);
				if (cr != null) {
					title = title + " (CR " + cr + ")";
				}
			}
			return renderStatBlockTitle(title, newUri, top);
		} finally {
			cursor.close();
		}
	}

	@Override
	public String renderDetails() {
		Cursor cursor = bookDbAdapter.getHauntAdapter().getHauntDetails(sectionId);
		try {
			StringBuffer sb = new StringBuffer();
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				String cr = HauntAdapter.HauntUtils.getCr(cursor);
				if (top) {
					sb.append("<b>CR ");
					sb.append(cr);
					sb.append("</b><br>\n");
				}
				sb.append(HauntAdapter.HauntUtils.getAlignment(cursor));
				sb.append(" ");
				sb.append(HauntAdapter.HauntUtils.getHauntType(cursor));
				sb.append(" (");
				sb.append(HauntAdapter.HauntUtils.getArea(cursor));
				sb.append(")<br>\n");
				sb.append(addField("Notice", HauntAdapter.HauntUtils.getNotice(cursor)));
				sb.append(addField("hp", HauntAdapter.HauntUtils.getHp(cursor), false));
				sb.append(addField("Trigger", HauntAdapter.HauntUtils.getTrigger(cursor), false));
				sb.append(addField("Reset", HauntAdapter.HauntUtils.getReset(cursor)));
				sb.append(addField("Effect", HauntAdapter.HauntUtils.getEffect(cursor)));
				sb.append(addField("Destruction", HauntAdapter.HauntUtils.getDestruction(cursor)));
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
