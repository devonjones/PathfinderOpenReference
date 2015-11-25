package org.evilsoft.pathfinder.reference.render.json;

import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.book.MythicSpellDetailAdapter;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class MythicSpellRenderer extends JsonRenderer {
	private BookDbAdapter bookDbAdapter;

	public MythicSpellRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	public JSONObject render(JSONObject section, Integer sectionId)
			throws JSONException {
		Cursor cursor = bookDbAdapter.getMythicSpellDetailAdapter()
				.fetchMythicSpellDetails(sectionId);
		try {
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				addField(section, "spell_source",
						MythicSpellDetailAdapter.MythicSpellDetailUtils
								.getSpellSource(cursor));
			}
		} finally {
			cursor.close();
		}
		return section;
	}
}
