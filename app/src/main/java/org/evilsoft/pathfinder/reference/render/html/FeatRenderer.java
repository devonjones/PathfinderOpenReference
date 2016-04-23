package org.evilsoft.pathfinder.reference.render.html;

import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;

public class FeatRenderer extends HtmlRenderer {
	private BookDbAdapter bookDbAdapter;

	public FeatRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	@Override
	public String renderTitle() {
		return renderTitle(name, abbrev, newUri, 0, top);
	}

	@Override
	public String renderDetails() {
		StringBuffer sb = new StringBuffer();
		sb.append("<B>");
		sb.append(bookDbAdapter.getFeatAdapter().renderFeatTypeDescription(sectionId));
		sb.append("</B><BR>\n");
		sb.append("<B>Source: </B>");
		sb.append(source);
		sb.append("<BR>");
		return sb.toString();
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
