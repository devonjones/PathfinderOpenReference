package org.evilsoft.pathfinder.reference.render.json;

import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.book.SpellComponentAdapter;
import org.evilsoft.pathfinder.reference.db.book.SpellDescriptorAdapter;
import org.evilsoft.pathfinder.reference.db.book.SpellDetailAdapter;
import org.evilsoft.pathfinder.reference.db.book.SpellEffectAdapter;
import org.evilsoft.pathfinder.reference.db.book.SpellListAdapter;
import org.evilsoft.pathfinder.reference.db.book.SpellSubschoolAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class SpellRenderer extends JsonRenderer {
	private BookDbAdapter bookDbAdapter;

	public SpellRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	public JSONObject render(JSONObject section, Integer sectionId)
			throws JSONException {
		Cursor cursor = bookDbAdapter.getSpellDetailAdapter()
				.fetchSpellDetails(sectionId);
		try {
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				// TODO: Handle "as spell"
				// addField(section, "as_spell_url",
				// SpellDetailAdapter.SpellDetailUtils.getAsSpellId(cursor));
				addField(section, "casting_time",
						SpellDetailAdapter.SpellDetailUtils
								.getCastingTime(cursor));
				addField(section, "component_text",
						SpellDetailAdapter.SpellDetailUtils
								.getComponentText(cursor));
				addField(section, "descriptor_text",
						SpellDetailAdapter.SpellDetailUtils
								.getDescriptorText(cursor));
				addField(section, "duration",
						SpellDetailAdapter.SpellDetailUtils.getDuration(cursor));
				addField(section, "level_text",
						SpellDetailAdapter.SpellDetailUtils
								.getLevelText(cursor));
				addField(section, "preparation_time",
						SpellDetailAdapter.SpellDetailUtils
								.getPreparationTime(cursor));
				addField(section, "range",
						SpellDetailAdapter.SpellDetailUtils.getRange(cursor));
				addField(section, "saving_throw",
						SpellDetailAdapter.SpellDetailUtils
								.getSavingThrow(cursor));
				addField(section, "school",
						SpellDetailAdapter.SpellDetailUtils.getSchool(cursor));
				addField(section, "spell_resistance",
						SpellDetailAdapter.SpellDetailUtils
								.getSpellResistance(cursor));
				addField(section, "subschool_text",
						SpellDetailAdapter.SpellDetailUtils
								.getSubschoolText(cursor));
				renderSpellComponents(section, sectionId);
				renderSpellDescriptors(section, sectionId);
				renderSpellEffects(section, sectionId);
				renderSpellLevels(section, sectionId);
				renderSpellSubschools(section, sectionId);
			}
		} finally {
			cursor.close();
		}
		return section;
	}

	private void renderSpellComponents(JSONObject section, Integer sectionId)
			throws JSONException {
		Cursor cursor = bookDbAdapter.getSpellComponentAdapter()
				.fetchSpellComponents(sectionId);
		try {
			boolean has_next = cursor.moveToFirst();
			JSONArray components = new JSONArray();
			while (has_next) {
				JSONObject c = new JSONObject();
				addField(c, "type",
						SpellComponentAdapter.SpellComponentUtils
								.getComponentType(cursor));
				addField(c, "description",
						SpellComponentAdapter.SpellComponentUtils
								.getDescription(cursor));
				components.put(c);
				has_next = cursor.moveToNext();
			}
			if (components.length() > 0) {
				section.put("components", components);
			}
		} finally {
			cursor.close();
		}
	}

	private void renderSpellDescriptors(JSONObject section, Integer sectionId)
			throws JSONException {
		Cursor cursor = bookDbAdapter.getSpellDescriptorAdapter()
				.getSpellDescriptors(sectionId);
		try {
			boolean has_next = cursor.moveToFirst();
			JSONArray descriptors = new JSONArray();
			while (has_next) {
				descriptors.put(SpellDescriptorAdapter.SpellDescriptorUtils
						.getDescriptor(cursor));
				has_next = cursor.moveToNext();
			}
			if (descriptors.length() > 0) {
				section.put("descriptors", descriptors);
			}
		} finally {
			cursor.close();
		}
	}

	private void renderSpellEffects(JSONObject section, Integer sectionId)
			throws JSONException {
		Cursor cursor = bookDbAdapter.getSpellEffectAdapter()
				.fetchSpellEffects(sectionId);
		try {
			boolean has_next = cursor.moveToFirst();
			JSONObject effects = new JSONObject();
			while (has_next) {
				effects.put(
						SpellEffectAdapter.SpellEffectUtils.getName(cursor),
						SpellEffectAdapter.SpellEffectUtils
								.getDescription(cursor));
				has_next = cursor.moveToNext();
			}
			if (effects.length() > 0) {
				section.put("effects", effects);
			}
		} finally {
			cursor.close();
		}
	}

	private void renderSpellLevels(JSONObject section, Integer sectionId)
			throws JSONException {
		Cursor cursor = bookDbAdapter.getSpellListAdapter().getSpellLists(
				sectionId);
		try {
			boolean has_next = cursor.moveToFirst();
			JSONArray level = new JSONArray();
			while (has_next) {
				JSONObject l = new JSONObject();
				addField(l, "class",
						SpellListAdapter.SpellListUtils.getClass(cursor));
				addField(l, "level",
						SpellListAdapter.SpellListUtils.getLevel(cursor));
				addField(l, "magic_type",
						SpellListAdapter.SpellListUtils.getMagicType(cursor));
				level.put(l);
				has_next = cursor.moveToNext();
			}
			if (level.length() > 0) {
				section.put("level", level);
			}
		} finally {
			cursor.close();
		}
	}

	private void renderSpellSubschools(JSONObject section, Integer sectionId)
			throws JSONException {
		Cursor cursor = bookDbAdapter.getSpellSubschoolAdapter()
				.getSpellSubschools(sectionId);
		try {
			boolean has_next = cursor.moveToFirst();
			JSONArray subschools = new JSONArray();
			while (has_next) {
				subschools.put(SpellSubschoolAdapter.SpellSubschoolUtils
						.getSubschool(cursor));
				has_next = cursor.moveToNext();
			}
			if (subschools.length() > 0) {
				section.put("subschools", subschools);
			}
		} finally {
			cursor.close();
		}
	}
}
