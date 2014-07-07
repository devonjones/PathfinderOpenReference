package org.evilsoft.pathfinder.reference.render.json;

import org.evilsoft.pathfinder.reference.db.book.AnimalCompanionAdapter;
import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class AnimalCompanionRenderer extends JsonRenderer {
	private BookDbAdapter bookDbAdapter;

	public AnimalCompanionRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	public JSONObject render(JSONObject section, Integer sectionId)
			throws JSONException {
		Cursor cursor = bookDbAdapter.getAnimalCompanionAdapter()
				.getAnimalCompanionDetails(sectionId);
		try {
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				addField(section, "ability_scores",
						AnimalCompanionAdapter.AnimalCompanionUtils
								.getAbilityScores(cursor));
				addField(section, "ac",
						AnimalCompanionAdapter.AnimalCompanionUtils
								.getAc(cursor));
				addField(section, "attack",
						AnimalCompanionAdapter.AnimalCompanionUtils
								.getAttack(cursor));
				addField(section, "cmd",
						AnimalCompanionAdapter.AnimalCompanionUtils
								.getCmd(cursor));
				addField(section, "level",
						AnimalCompanionAdapter.AnimalCompanionUtils
								.getLevel(cursor));
				addField(section, "size",
						AnimalCompanionAdapter.AnimalCompanionUtils
								.getSize(cursor));
				addField(section, "special_attacks",
						AnimalCompanionAdapter.AnimalCompanionUtils
								.getSpecialAttacks(cursor));
				addField(section, "special_qualities",
						AnimalCompanionAdapter.AnimalCompanionUtils
								.getSpecialQualities(cursor));
				addField(section, "speed",
						AnimalCompanionAdapter.AnimalCompanionUtils
								.getSpeed(cursor));
			}
		} finally {
			cursor.close();
		}
		return section;
	}
}
