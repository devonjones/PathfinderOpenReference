package org.evilsoft.pathfinder.reference.render.json;

import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.book.SkillAdapter;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class SkillRenderer extends JsonRenderer {
	private BookDbAdapter bookDbAdapter;

	public SkillRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	public JSONObject render(JSONObject section, Integer sectionId)
			throws JSONException {
		Cursor cursor = bookDbAdapter.getSkillAdapter().fetchSkillAttr(
				sectionId);
		try {
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				addField(section, "armor_check_penalty",
						SkillAdapter.SkillUtils.getArmorCheckPenalty(cursor));
				addField(section, "attribute",
						SkillAdapter.SkillUtils.getAttribute(cursor));
				addField(section, "trained_only",
						SkillAdapter.SkillUtils.getTrainedOnly(cursor));
			}
		} finally {
			cursor.close();
		}
		return section;
	}
}
