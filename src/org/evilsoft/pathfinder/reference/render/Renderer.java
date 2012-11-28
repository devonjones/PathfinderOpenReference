package org.evilsoft.pathfinder.reference.render;

import java.util.List;

import org.evilsoft.pathfinder.reference.db.book.FullSectionAdapter;

import android.database.Cursor;

public abstract class Renderer {
	public Integer sectionId;
	public String type;
	public String subtype;
	public String name;
	public String abbrev;
	public String source;
	public String desc;
	public String body;
	public String image;
	public String alt;
	public String newUri;
	public int depth;
	public boolean top;
	public boolean isTablet;
	public boolean suppressNextTitle;

	public abstract String renderTitle();

	public abstract String renderDetails();

	public abstract String renderFooter();

	public abstract String renderHeader();

	public String render(Cursor cursor, String newUri, int depth, boolean top,
			boolean suppressTitle, boolean isTablet) {
		this.newUri = newUri;
		this.depth = depth;
		this.top = top;
		this.isTablet = isTablet;
		this.sectionId = FullSectionAdapter.SectionUtils.getSectionId(cursor);
		this.type = FullSectionAdapter.SectionUtils.getType(cursor);
		this.subtype = FullSectionAdapter.SectionUtils.getSubtype(cursor);
		this.name = FullSectionAdapter.SectionUtils.getName(cursor);
		this.abbrev = FullSectionAdapter.SectionUtils.getAbbrev(cursor);
		this.source = FullSectionAdapter.SectionUtils.getSource(cursor);
		this.desc = FullSectionAdapter.SectionUtils.getDescription(cursor);
		this.body = FullSectionAdapter.SectionUtils.getBody(cursor);
		this.image = FullSectionAdapter.SectionUtils.getImage(cursor);
		this.alt = FullSectionAdapter.SectionUtils.getAlt(cursor);
		localSetValues();
		StringBuffer sb = new StringBuffer();
		if (suppressTitle == false) {
			sb.append(renderTitle());
		}
		sb.append(renderImage());
		sb.append(renderHeader());
		sb.append(renderDetails());
		sb.append(renderDescription());
		sb.append(renderBody());
		sb.append(renderFooter());
		return sb.toString();
	}

	public void localSetValues() {
	}

	public String renderImage() {
		StringBuffer sb = new StringBuffer();
		if (image != null) {
			if (isTablet) {
				sb.append("<img style='float: left' src='file:///android_asset/");
				sb.append(image);
				sb.append("' alt='");
				sb.append(alt);
				sb.append("'>");
			} else {
				sb.append("<img src='file:///android_asset/");
				sb.append(image);
				sb.append("' alt='");
				sb.append(alt);
				sb.append("'>");
			}
		}
		return sb.toString();
	}

	public String renderDescription() {
		StringBuffer sb = new StringBuffer();
		if (desc != null) {
			sb.append("<p>");
			sb.append(desc);
			sb.append("</p>\n");
		}
		return sb.toString();
	}

	public String renderBody() {
		StringBuffer sb = new StringBuffer();
		if (body != null) {
			int index = body.indexOf("img src=");
			if (index > 0) {
				String start = body.substring(0, index);
				String end = body.substring(index + 7);
				int quote = end.indexOf('"');
				end = end.substring(quote + 1);
				quote = end.indexOf('"');
				String link = end.substring(0, quote);
				String[] parts = link.split("/");
				end = end.substring(quote);
				body = start + "img src=" + '"' + "file:///android_asset/"
						+ parts[parts.length - 1] + end;
			}
			sb.append(body);
		}
		if (depth >= 3) {
			sb.append("<br>\n");
		}
		return sb.toString();
	}

	protected String displayLine(List<String> elements) {
		StringBuffer sb = new StringBuffer();
		boolean space = false;
		for (int i = 0; i < elements.size(); i++) {
			String elem = elements.get(i);
			if (elem != null) {
				if (space) {
					sb.append(" ");
				}
				sb.append(elem);
				space = true;
			}
		}
		if (sb.length() > 0) {
			sb.append("<br>\n");
		}
		return sb.toString();
	}

	public String addField(String field, String value) {
		return addField(field, value, true);
	}

	public String addField(String field, String value, boolean lineEnd) {
		StringBuffer sb = new StringBuffer();
		if (value != null) {
			sb.append(fieldTitle(field));
			sb.append(value);
			if (lineEnd) {
				sb.append("<br>\n");
			} else {
				sb.append("; ");
			}
		}
		return sb.toString();
	}

	public String renderTitle(String title, String abbrev, String newUri, int depth,
			boolean top) {
		if (top) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		String[] tags = getDepthTag(depth);
		if (title != null) {
			sb.append("\n");
			sb.append(tags[0]);
			if (depth >= 1 && newUri != null) {
				sb.append("<a href=\"");
				sb.append(newUri);
				sb.append("\">");
			}
			sb.append(title);
			if (depth >= 1 && newUri != null) {
				sb.append("</a>");
			}
			if (abbrev != null) {
				sb.append(" (");
				sb.append(abbrev);
				sb.append(")");
			}
			sb.append(tags[1]);
			sb.append("\n");
		} else {
			sb.append("<br>\n");
		}
		return sb.toString();
	}

	public String fieldTitle(String field) {
		return "<B>" + field + ":</B> ";
	}

	public String[] getDepthTag(int depth) {
		String[] tags = new String[2];
		if (depth == 0) {
			tags[0] = "<H1>";
			tags[1] = "</H1>";
		} else if (depth == 1) {
			tags[0] = "<H2>";
			tags[1] = "</H2>";
		} else if (depth == 2) {
			tags[0] = "<H3>";
			tags[1] = "</H3>";
		} else if (depth == 3) {
			tags[0] = "<B>";
			tags[1] = ":</B> ";
		} else {
			tags[0] = "<I>";
			tags[1] = ":</I> ";
		}
		return tags;
	}

	public static String capitalizeString(String string) {
		char[] chars = string.toLowerCase().toCharArray();
		boolean found = false;
		for (int i = 0; i < chars.length; i++) {
			if (!found && Character.isLetter(chars[i])) {
				chars[i] = Character.toUpperCase(chars[i]);
				found = true;
			} else if (Character.isWhitespace(chars[i])
					|| chars[i] == '.' || chars[i] == '\'' || chars[i] == '-') {
				// You can add other chars here
				found = false;
			}
		}
		return String.valueOf(chars);
	}
}
