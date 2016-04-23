package org.evilsoft.pathfinder.reference.render.json;

import org.evilsoft.pathfinder.reference.db.book.AbilityAdapter;
import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class AbilityRenderer extends JsonRenderer {
	private BookDbAdapter bookDbAdapter;

	public AbilityRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	public JSONObject render(JSONObject section, Integer sectionId)
			throws JSONException {
		Cursor cursor = bookDbAdapter.getAbilityAdapter().getAbilityTypes(
				sectionId);
		try {
			boolean has_next = cursor.moveToFirst();
			JSONArray ja = new JSONArray();
			while (has_next) {
				ja.put(AbilityAdapter.AbilityUtils.getAbilityType(cursor));
				has_next = cursor.moveToNext();
			}
			if (ja.length() > 0) {
				section.put("ability_types", ja);
			}
		} finally {
			cursor.close();
		}
		return section;
	}
}
