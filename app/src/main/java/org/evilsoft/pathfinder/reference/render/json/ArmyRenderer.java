package org.evilsoft.pathfinder.reference.render.json;

import org.evilsoft.pathfinder.reference.db.book.ArmyAdapter;
import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class ArmyRenderer extends JsonRenderer {
	private BookDbAdapter bookDbAdapter;

	public ArmyRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	public JSONObject render(JSONObject section, Integer sectionId)
			throws JSONException {
		Cursor cursor = bookDbAdapter.getArmyAdapter()
				.getArmyDetails(sectionId);
		try {
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				addField(section, "acr", ArmyAdapter.ArmyUtils.getAcr(cursor));
				addField(section, "alignment",
						ArmyAdapter.ArmyUtils.getAlignment(cursor));
				addField(section, "consumption",
						ArmyAdapter.ArmyUtils.getConsumption(cursor));
				addField(section, "creature_type",
						ArmyAdapter.ArmyUtils.getCreatureType(cursor));
				addField(section, "dv", ArmyAdapter.ArmyUtils.getDv(cursor));
				addField(section, "hp", ArmyAdapter.ArmyUtils.getHp(cursor));
				addField(section, "note", ArmyAdapter.ArmyUtils.getNote(cursor));
				addField(section, "om", ArmyAdapter.ArmyUtils.getOm(cursor));
				addField(section, "resources",
						ArmyAdapter.ArmyUtils.getResources(cursor));
				addField(section, "size", ArmyAdapter.ArmyUtils.getSize(cursor));
				addField(section, "special",
						ArmyAdapter.ArmyUtils.getSpecial(cursor));
				addField(section, "speed",
						ArmyAdapter.ArmyUtils.getSpeed(cursor));
				addField(section, "tactics",
						ArmyAdapter.ArmyUtils.getTactics(cursor));
				addField(section, "xp", ArmyAdapter.ArmyUtils.getXp(cursor));
			}
		} finally {
			cursor.close();
		}
		return section;
	}
}
