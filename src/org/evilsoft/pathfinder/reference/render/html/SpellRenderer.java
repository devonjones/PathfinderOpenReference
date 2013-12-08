package org.evilsoft.pathfinder.reference.render.html;

import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.book.SpellComponentAdapter;
import org.evilsoft.pathfinder.reference.db.book.SpellDetailAdapter;
import org.evilsoft.pathfinder.reference.db.book.SpellEffectAdapter;

import android.database.Cursor;

public class SpellRenderer extends HtmlRenderer {
	private BookDbAdapter bookDbAdapter;

	public SpellRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	@Override
	public String renderTitle() {
		return renderTitle(name, abbrev, newUri, 0, top);
	}

	@Override
	public String renderDetails() {
		StringBuffer sb = new StringBuffer();
		sb.append(renderSpellDetails(sectionId));
		sb.append("<B>Source: </B>");
		sb.append(source);
		sb.append("<BR>\n");
		sb.append("<B>Summary: </B>");
		sb.append(desc);
		sb.append("<BR>\n");
		return sb.toString();
	}

	public String renderSpellDetails(Integer sectionId) {
		Cursor cursor = bookDbAdapter.getSpellDetailAdapter().fetchSpellDetails(sectionId);
		try {
			StringBuffer sb = new StringBuffer();
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				String school = SpellDetailAdapter.SpellDetailUtils.getSchool(cursor);
				String subschool = SpellDetailAdapter.SpellDetailUtils.getSubschool(cursor);
				String descriptor = SpellDetailAdapter.SpellDetailUtils.getDescriptorText(cursor);
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
				String level = SpellDetailAdapter.SpellDetailUtils.getLevelText(cursor);
				sb.append(addField("Level", level));
				String casting_time = SpellDetailAdapter.SpellDetailUtils.getCastingTime(cursor);
				sb.append(addField("Casting Time", casting_time));
				String preparation_time = SpellDetailAdapter.SpellDetailUtils.getPreparationTime(cursor);
				sb.append(addField("Preparation Time", preparation_time));
				sb.append(renderComponents(sectionId));
				String range = SpellDetailAdapter.SpellDetailUtils.getRange(cursor);
				sb.append(addField("Range", range));
				sb.append(renderEffects(sectionId));
				String duration = SpellDetailAdapter.SpellDetailUtils.getDuration(cursor);
				sb.append(addField("Duration", duration));
				String saving_throw = SpellDetailAdapter.SpellDetailUtils.getSavingThrow(cursor);
				sb.append(addField("Saving Throw", saving_throw));
				String spell_resistance = SpellDetailAdapter.SpellDetailUtils.getSpellResistance(cursor);
				sb.append(addField("Spell Resistance", spell_resistance));
			}
			return sb.toString();
		} finally {
			cursor.close();
		}
	}

	public String renderComponents(Integer sectionId) {
		Cursor cursor = bookDbAdapter.getSpellComponentAdapter().fetchSpellComponents(sectionId);
		try {
			StringBuffer sb = new StringBuffer();
			boolean has_next = cursor.moveToFirst();
			boolean has_field = false;
			if (has_next) {
				sb.append(fieldTitle("Components"));
				has_field = true;
			}
			String comma = "";
			while (has_next) {
				String type = SpellComponentAdapter.SpellComponentUtils.getComponentType(cursor);
				String desc = SpellComponentAdapter.SpellComponentUtils.getDescription(cursor);
				if (type != null) {
					sb.append(comma);
					sb.append(type);
					if (desc != null) {
						sb.append(" (");
						sb.append(desc);
						sb.append(")");
					}
				} else {
					sb.append(desc);
				}
				comma = ", ";
				has_next = cursor.moveToNext();
			}
			if (has_field) {
				sb.append("<br>\n");
			}
			return sb.toString();
		} finally {
			cursor.close();
		}
	}

	public String renderEffects(Integer sectionId) {
		Cursor cursor = bookDbAdapter.getSpellEffectAdapter().fetchSpellEffects(sectionId);
		try {
			StringBuffer sb = new StringBuffer();
			boolean has_next = cursor.moveToFirst();
			while (has_next) {
				String name = SpellEffectAdapter.SpellEffectUtils.getName(cursor);
				String desc = SpellEffectAdapter.SpellEffectUtils.getDescription(cursor);
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
		return "";
	}

	@Override
	public String renderHeader() {
		return "";
	}
}
