package org.evilsoft.pathfinder.reference.render.json;

import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.book.HauntAdapter;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class HauntRenderer extends JsonRenderer {
	private BookDbAdapter bookDbAdapter;

	public HauntRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	public JSONObject render(JSONObject section, Integer sectionId)
			throws JSONException {
		Cursor cursor = bookDbAdapter.getHauntAdapter().getHauntDetails(
				sectionId);
		try {
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				addField(section, "alignment",
						HauntAdapter.HauntUtils.getAlignment(cursor));
				addField(section, "area",
						HauntAdapter.HauntUtils.getArea(cursor));
				addField(section, "caster_level",
						HauntAdapter.HauntUtils.getCasterLevel(cursor));
				addField(section, "cr", HauntAdapter.HauntUtils.getCr(cursor));
				addField(section, "destruction",
						HauntAdapter.HauntUtils.getDestruction(cursor));
				addField(section, "effect",
						HauntAdapter.HauntUtils.getEffect(cursor));
				addField(section, "haunt_type",
						HauntAdapter.HauntUtils.getHauntType(cursor));
				addField(section, "hp", HauntAdapter.HauntUtils.getHp(cursor));
				addField(section, "notice",
						HauntAdapter.HauntUtils.getNotice(cursor));
				addField(section, "reset",
						HauntAdapter.HauntUtils.getReset(cursor));
				addField(section, "trigger",
						HauntAdapter.HauntUtils.getTrigger(cursor));
				addField(section, "xp", HauntAdapter.HauntUtils.getXp(cursor));
			}
		} finally {
			cursor.close();
		}
		return section;
	}
}
