package org.evilsoft.pathfinder.reference.render.json;

import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.book.ItemAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class ItemRenderer extends JsonRenderer {
	private BookDbAdapter bookDbAdapter;

	public ItemRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	public JSONObject render(JSONObject section, Integer sectionId)
			throws JSONException {
		Cursor cursor = bookDbAdapter.getItemAdapter()
				.getItemDetails(sectionId);
		try {
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				addField(section, "aura", ItemAdapter.ItemUtils.getAura(cursor));
				addField(section, "cl", ItemAdapter.ItemUtils.getCl(cursor));
				addField(section, "price",
						ItemAdapter.ItemUtils.getPrice(cursor));
				addField(section, "slot", ItemAdapter.ItemUtils.getSlot(cursor));
				addField(section, "weight",
						ItemAdapter.ItemUtils.getWeight(cursor));
				renderItemMisc(section, sectionId);
			}
		} finally {
			cursor.close();
		}
		return section;
	}

	private void renderItemMisc(JSONObject section, Integer sectionId)
			throws JSONException {
		Cursor cursor = bookDbAdapter.getItemAdapter().getItemMisc(sectionId);
		try {
			boolean has_next = cursor.moveToFirst();
			JSONArray misc = new JSONArray();
			while (has_next) {
				JSONObject m = new JSONObject();
				addField(m, "field", ItemAdapter.ItemMiscUtils.getField(cursor));
				addField(m, "subsection",
						ItemAdapter.ItemMiscUtils.getSubsection(cursor));
				addField(m, "value", ItemAdapter.ItemMiscUtils.getValue(cursor));
				misc.put(m);
				has_next = cursor.moveToNext();
			}
			if (misc.length() > 0) {
				section.put("misc", misc);
			}
		} finally {
			cursor.close();
		}
	}
}
