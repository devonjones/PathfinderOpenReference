package org.evilsoft.pathfinder.reference.render;

import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.book.ClassAdapter;

import android.database.Cursor;

public class ClassRenderer extends Renderer {
	private BookDbAdapter bookDbAdapter;

	public ClassRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	@Override
	public String renderTitle() {
		return renderTitle(name, abbrev, newUri, depth, top);
	}

	@Override
	public String renderFooter() {
		StringBuffer sb = new StringBuffer();
		Cursor cursor = bookDbAdapter.getClassAdapter().fetchClassDetails(sectionId);
		try {
			sb.append("<B>Source: </B>");
			sb.append(source);
			sb.append("<BR>");
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				String align = ClassAdapter.ClassUtils.getAlignment(cursor);
				if (align != null) {
					sb.append("<B>Alignment: </B>");
					sb.append(align);
					sb.append("<BR>\n");
				}
				String hd = ClassAdapter.ClassUtils.getHitDie(cursor);
				if (hd != null) {
					sb.append("<B>Hit Die: </B>");
					sb.append(hd);
					sb.append("<BR>\n");
				}
			}
		} finally {
			cursor.close();
		}
		return sb.toString();
	}

	@Override
	public String renderHeader() {
		return "";
	}

	@Override
	public String renderDetails() {
		return "";
	}
}
