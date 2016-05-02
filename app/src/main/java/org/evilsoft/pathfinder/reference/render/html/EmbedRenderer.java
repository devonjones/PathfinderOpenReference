package org.evilsoft.pathfinder.reference.render.html;

import org.evilsoft.pathfinder.reference.db.DbWrangler;
import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.index.SpellListAdapter;

import android.database.Cursor;

public class EmbedRenderer extends HtmlRenderer {
	private BookDbAdapter bookDbAdapter;
	private DbWrangler dbWrangler;

	public EmbedRenderer(DbWrangler dbWrangler, BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
		this.dbWrangler = dbWrangler;
	}

	@Override
	public String renderTitle() {
		return renderTitle(name, abbrev, newUri, depth, top);
	}

	@Override
	public String renderFooter() {
		return "";
	}

	@Override
	public String renderHeader() {
		return "";
	}

	@Override
	public String renderDescription() {
		return "";
	}

	@Override
	public String renderDetails() {
		return super.renderBody();
	}

	@Override
	public String renderBody() {
		StringBuilder sb = new StringBuilder();
		if ("spell_list".equals(this.subtype)) {
			sb.append(renderSpellList());
		}
		return sb.toString();
	}

	public String renderSpellList() {
		StringBuilder sb = new StringBuilder();
		SpellListAdapter sla = dbWrangler.getIndexDbAdapter()
				.getSpellListAdapter();
		Cursor cursor = sla.fetchClassSpells(capitalizeString(this.desc));
		try {
			boolean has_next = cursor.moveToFirst();
			int level = -1;
			String comma = "";
			while (has_next) {
				String name = SpellListAdapter.SpellListUtils
						.getSpellName(cursor);
				String url = SpellListAdapter.SpellListUtils.getUrl(cursor);
				int spell_level = SpellListAdapter.SpellListUtils
						.getSpellLevel(cursor);
				if (spell_level != level) {
					level = spell_level;
					if (!comma.equals("")) {
						sb.append("</p>\n");
					}
					sb.append("<strong>");
					sb.append(level);
					if (level == 1) {
						sb.append("st");
					} else if (level == 2) {
						sb.append("nd");
					} else if (level == 3) {
						sb.append("rd");
					} else {
						sb.append("th");
					}
					sb.append("-Level ");
					sb.append(capitalizeString(this.desc));
					sb.append(" Spells</strong><br>\n");
					comma = "";
					sb.append("<p>");
				}
				sb.append(comma);
				sb.append("<a href=\"");
				sb.append(url);
				sb.append("\">");
				sb.append(name);
				sb.append("</a>");
				comma = ", ";
				has_next = cursor.moveToNext();
			}
		} finally {
			cursor.close();
		}
		return sb.toString();
	}
}
