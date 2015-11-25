package org.evilsoft.pathfinder.reference.render.json;

import org.evilsoft.pathfinder.reference.db.book.AbilityAdapter;
import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.book.FeatAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class FeatRenderer extends JsonRenderer {
	private BookDbAdapter bookDbAdapter;

	public FeatRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	public JSONObject render(JSONObject section, Integer sectionId)
			throws JSONException {
		Cursor cursor = bookDbAdapter.getFeatAdapter().getFeatTypes(sectionId);
		try {
			boolean has_next = cursor.moveToFirst();
			JSONArray ft = new JSONArray();
			while (has_next) {
				addField(section, "acr",
						FeatAdapter.FeatTypeUtils.getFeatType(cursor));
				ft.put(AbilityAdapter.AbilityUtils.getAbilityType(cursor));
				has_next = cursor.moveToNext();
			}
			if (ft.length() > 0) {
				section.put("feat_types", ft);
			}
		} finally {
			cursor.close();
		}
		return section;
	}
}
