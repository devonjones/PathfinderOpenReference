package org.evilsoft.pathfinder.reference.render.html;

import java.util.HashMap;

import org.evilsoft.pathfinder.reference.HtmlRenderFarm;
import org.evilsoft.pathfinder.reference.db.BookNotFoundException;
import org.evilsoft.pathfinder.reference.db.DbWrangler;
import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.book.FullSectionAdapter;
import org.evilsoft.pathfinder.reference.db.book.SpellDetailAdapter;
import org.evilsoft.pathfinder.reference.db.book.SpellEffectAdapter;
import org.evilsoft.pathfinder.reference.db.book.SpellListAdapter;
import org.evilsoft.pathfinder.reference.db.index.IndexGroupAdapter;

import android.database.Cursor;
import android.util.Log;

public class SpellRenderer extends HtmlRenderer {
	public static final String TAG = "SpellRenderer";
	private BookDbAdapter bookDbAdapter;
	private DbWrangler dbWrangler;

	public SpellRenderer(DbWrangler dbWrangler, BookDbAdapter bookDbAdapter) {
		this.dbWrangler = dbWrangler;
		this.bookDbAdapter = bookDbAdapter;
	}

	@Override
	public String renderTitle() {
		return renderTitle(name, abbrev, newUri, 0, top);
	}

	@Override
	public String renderDetails() {
		StringBuilder sb = new StringBuilder();
		sb.append(renderSpellDetails(sectionId));
		sb.append("<B>Source: </B>");
		sb.append(source);
		sb.append("<BR>\n");
		if (desc != null) {
			sb.append("<B>Summary: </B>");
			sb.append(desc);
		}
		sb.append("<BR>\n");
		return sb.toString();
	}

	private String renderSpellLevels(Integer sectionId) {
		Cursor cursor = bookDbAdapter.getSpellListAdapter().getSpellLists(
				sectionId);
		StringBuilder sb = new StringBuilder();
		try {
			boolean has_next = cursor.moveToFirst();
			String semi = "";
			while (has_next) {
				sb.append(semi);
				sb.append(SpellListAdapter.SpellListUtils.getName(cursor));
				sb.append(": ");
				sb.append(SpellListAdapter.SpellListUtils.getLevel(cursor));
				semi = "; ";
				has_next = cursor.moveToNext();
			}
		} finally {
			cursor.close();
		}
		return sb.toString();
	}


	public String renderSpellDetails(Integer sectionId) {
		Cursor cursor = bookDbAdapter.getSpellDetailAdapter()
				.fetchSpellDetails(sectionId);
		try {
			StringBuilder sb = new StringBuilder();
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				String school = SpellDetailAdapter.SpellDetailUtils
						.getSchool(cursor);
				String subschool = SpellDetailAdapter.SpellDetailUtils
						.getSubschoolText(cursor);
				String descriptor = SpellDetailAdapter.SpellDetailUtils
						.getDescriptorText(cursor);
				sb.append(fieldTitle("School"));
				sb.append(school);
				if (subschool != null) {
					sb.append(" (");
					sb.append(subschool);
					sb.append(")");
				}
				if (descriptor != null) {
					sb.append(" [");
					sb.append(descriptor);
					sb.append("]");
				}
				sb.append("<br>\n");
				String level = renderSpellLevels(sectionId);
				sb.append(addField("Level", level));
				String casting_time = SpellDetailAdapter.SpellDetailUtils
						.getCastingTime(cursor);
				sb.append(addField("Casting Time", casting_time));
				String preparation_time = SpellDetailAdapter.SpellDetailUtils
						.getPreparationTime(cursor);
				sb.append(addField("Preparation Time", preparation_time));
				sb.append(addField("Components",
						SpellDetailAdapter.SpellDetailUtils
								.getComponentText(cursor)));
				String range = SpellDetailAdapter.SpellDetailUtils
						.getRange(cursor);
				sb.append(addField("Range", range));
				sb.append(renderEffects(sectionId));
				String duration = SpellDetailAdapter.SpellDetailUtils
						.getDuration(cursor);
				sb.append(addField("Duration", duration));
				String saving_throw = SpellDetailAdapter.SpellDetailUtils
						.getSavingThrow(cursor);
				sb.append(addField("Saving Throw", saving_throw));
				String spell_resistance = SpellDetailAdapter.SpellDetailUtils
						.getSpellResistance(cursor);
				sb.append(addField("Spell Resistance", spell_resistance));
				if ("mythic_spell".equals(subtype)) {
					sb.append(addField("Mythic", "Mythic only"));
				} else {
					Cursor mcurs = dbWrangler.getIndexDbAdapter()
							.getIndexGroupAdapter().fetchBySpellSource(name);
					try {
						if (mcurs.moveToFirst()) {
							sb.append(addField("Mythic", "Has Mythic version"));
						}
					} finally {
						mcurs.close();
					}
				}
			}
			return sb.toString();
		} finally {
			cursor.close();
		}
	}

	public String renderEffects(Integer sectionId) {
		Cursor cursor = bookDbAdapter.getSpellEffectAdapter()
				.fetchSpellEffects(sectionId);
		try {
			StringBuilder sb = new StringBuilder();
			boolean has_next = cursor.moveToFirst();
			while (has_next) {
				String name = SpellEffectAdapter.SpellEffectUtils
						.getName(cursor);
				String desc = SpellEffectAdapter.SpellEffectUtils
						.getDescription(cursor);
				sb.append(addField(name, desc));
				has_next = cursor.moveToNext();
			}
			return sb.toString();
		} finally {
			cursor.close();
		}
	}

	@Override
	public String renderDescription() {
		return "";
	}

	@Override
	public String renderFooter() {
		StringBuilder sb = new StringBuilder();
		Cursor cursor = dbWrangler.getIndexDbAdapter().getIndexGroupAdapter()
				.fetchBySpellSource(name);
		try {
			if (cursor.moveToFirst()) {
				Integer msId = IndexGroupAdapter.IndexGroupUtils
						.getSectionId(cursor);
				Integer parentId = IndexGroupAdapter.IndexGroupUtils
						.getParentId(cursor);
				String msUrl = IndexGroupAdapter.IndexGroupUtils.getUrl(cursor);
				BookDbAdapter mythicSpellDbAdapter;
				try {
					mythicSpellDbAdapter = dbWrangler
							.getBookDbAdapterByUrl(msUrl);
					HashMap<Integer, Integer> depthMap = new HashMap<Integer, Integer>();
					int localdepth = HtmlRenderFarm.getDepth(depthMap, msId,
							parentId, depth) + 1;
					HtmlRenderer renderer = HtmlRenderFarm.getRenderer(
							"section", dbWrangler, mythicSpellDbAdapter);
					boolean first = true;
					Cursor msCurs = mythicSpellDbAdapter
							.getFullSectionAdapter().fetchFullSection(
									msId.toString());
					try {
						boolean has_next = msCurs.moveToFirst();
						while (has_next) {
							String type = FullSectionAdapter.SectionUtils
									.getType(msCurs);
							Integer secId = FullSectionAdapter.SectionUtils
									.getSectionId(msCurs);
							parentId = FullSectionAdapter.SectionUtils
									.getParentId(msCurs);
							if (first) {
								first = false;
							} else {
								renderer = HtmlRenderFarm.getRenderer(type,
										dbWrangler, mythicSpellDbAdapter);
							}
							localdepth = HtmlRenderFarm.getDepth(depthMap,
									secId, parentId, depth) + 1;
							sb.append(renderer.render(msCurs, msUrl,
									localdepth, false, first, isTablet));
							has_next = msCurs.moveToNext();
						}
					} finally {
						msCurs.close();
					}
				} catch (BookNotFoundException bnfe) {
					Log.e(TAG, "Book not found: " + bnfe.getMessage());
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
}
