package org.evilsoft.pathfinder.reference.render.html;

public abstract class StatBlockRenderer extends HtmlRenderer {
	public String renderStatBlockTitle(String title, String newUri, boolean top) {
		if (top) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		if (title != null) {
			sb.append("\n<p class='stat-block-title'><a href='");
			sb.append(newUri);
			sb.append("'><font color='white'>");
			sb.append(title);
			sb.append("</font></a></p>\n");
		}
		return sb.toString();
	}

	public String renderStatBlockBreaker(String title) {
		StringBuilder sb = new StringBuilder();
		if (title != null) {
			sb.append("\n<p class='stat-block-breaker'>");
			sb.append(title);
			sb.append("</p>\n");
		}
		return sb.toString();
	}
}
