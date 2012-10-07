package org.evilsoft.pathfinder.reference.render;

import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.SpellAdapter;

import android.database.Cursor;

public class SpellRenderer extends Renderer {
	private SpellAdapter spellAdapter;
	private PsrdDbAdapter dbAdapter;

	public SpellRenderer(PsrdDbAdapter dbAdapter) {
		this.dbAdapter = dbAdapter;
	}

	private SpellAdapter getSpellAdapter() {
		if (this.spellAdapter == null) {
			this.spellAdapter = new SpellAdapter(this.dbAdapter);
		}
		return this.spellAdapter;
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

	public String renderSpellDetails(String sectionId) {
		Cursor curs = this.getSpellAdapter().fetchSpellDetails(sectionId);
		// 0:school, 1:subschool, 2:descriptor_text, 3:level_text,
		// 4:casting_time, 5:preparation_time, 6:range, 7:duration,
		// 8:saving_throw, 9:spell_resistance, 10:as_spell_id
		try {
			StringBuffer sb = new StringBuffer();
			boolean has_next = curs.moveToFirst();
			if (has_next) {
				String school = curs.getString(0);
				String subschool = curs.getString(1);
				String descriptor = curs.getString(2);
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
				String level = curs.getString(3);
				sb.append(addField("Level", level));
				String casting_time = curs.getString(4);
				sb.append(addField("Casting Time", casting_time));
				String preparation_time = curs.getString(5);
				sb.append(addField("Preparation Time", preparation_time));
				sb.append(renderComponents(sectionId));
				String range = curs.getString(6);
				sb.append(addField("Range", range));
				sb.append(renderEffects(sectionId));
				String duration = curs.getString(7);
				sb.append(addField("Duration", duration));
				String saving_throw = curs.getString(8);
				sb.append(addField("Saving Throw", saving_throw));
				String spell_resistance = curs.getString(9);
				sb.append(addField("Spell Resistance", spell_resistance));
			}
			return sb.toString();
		} finally {
			curs.close();
		}
	}

	public String renderComponents(String sectionId) {
		Cursor curs = this.getSpellAdapter().fetchSpellComponents(sectionId);
		try {
			StringBuffer sb = new StringBuffer();
			boolean has_next = curs.moveToFirst();
			boolean has_field = false;
			if (has_next) {
				sb.append(fieldTitle("Components"));
				has_field = true;
			}
			String comma = "";
			while (has_next) {
				String type = curs.getString(0);
				String desc = curs.getString(1);
				if (type != null) {
					sb.append(comma);
					sb.append(curs.getString(0));
					if (desc != null) {
						sb.append(" (");
						sb.append(desc);
						sb.append(")");
					}
				} else {
					sb.append(desc);
				}
				comma = ", ";
				has_next = curs.moveToNext();
			}
			if (has_field) {
				sb.append("<br>\n");
			}
			return sb.toString();
		} finally {
			curs.close();
		}
	}

	public String renderEffects(String sectionId) {
		Cursor curs = this.getSpellAdapter().fetchSpellEffects(sectionId);
		try {
			StringBuffer sb = new StringBuffer();
			boolean has_next = curs.moveToFirst();
			while (has_next) {
				sb.append(addField(curs.getString(0), curs.getString(1)));
				has_next = curs.moveToNext();
			}
			return sb.toString();
		} finally {
			curs.close();
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
