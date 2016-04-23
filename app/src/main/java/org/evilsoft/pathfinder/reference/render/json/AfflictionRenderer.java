package org.evilsoft.pathfinder.reference.render.json;

import org.evilsoft.pathfinder.reference.db.book.AfflictionAdapter;
import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class AfflictionRenderer extends JsonRenderer {
	private BookDbAdapter bookDbAdapter;

	public AfflictionRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	public JSONObject render(JSONObject section, Integer sectionId)
			throws JSONException {
		Cursor cursor = bookDbAdapter.getAfflictionAdapter()
				.getAfflictionDetails(sectionId);
		try {
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				addField(section, "contracted",
						AfflictionAdapter.AfflictionUtils.getContracted(cursor));
				addField(section, "addiction",
						AfflictionAdapter.AfflictionUtils.getAddiction(cursor));
				addField(section, "save",
						AfflictionAdapter.AfflictionUtils.getSave(cursor));
				addField(section, "onset",
						AfflictionAdapter.AfflictionUtils.getOnset(cursor));
				addField(section, "frequency",
						AfflictionAdapter.AfflictionUtils.getFrequency(cursor));
				addField(section, "effect",
						AfflictionAdapter.AfflictionUtils.getEffect(cursor));
				addField(section, "initial_effect",
						AfflictionAdapter.AfflictionUtils
								.getInitialEffect(cursor));
				addField(section, "secondary_effect",
						AfflictionAdapter.AfflictionUtils
								.getSecondaryEffect(cursor));
				addField(section, "damage", AfflictionAdapter.AfflictionUtils.getDamage(cursor));
				addField(section, "cure",
						AfflictionAdapter.AfflictionUtils.getCure(cursor));
				addField(section, "cost",
						AfflictionAdapter.AfflictionUtils.getCost(cursor));
			}
		} finally {
			cursor.close();
		}
		return section;
	}
}
