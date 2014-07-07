package org.evilsoft.pathfinder.reference.render.json;

import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.book.ClassAdapter;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class ClassRenderer extends JsonRenderer {
	private BookDbAdapter bookDbAdapter;

	public ClassRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	public JSONObject render(JSONObject section, Integer sectionId)
			throws JSONException {
		Cursor cursor = bookDbAdapter.getClassAdapter().fetchClassDetails(
				sectionId);
		try {
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				addField(section, "alignment",
						ClassAdapter.ClassUtils.getAlignment(cursor));
				addField(section, "hit_die",
						ClassAdapter.ClassUtils.getHitDie(cursor));
			}
		} finally {
			cursor.close();
		}
		return section;
	}
}
