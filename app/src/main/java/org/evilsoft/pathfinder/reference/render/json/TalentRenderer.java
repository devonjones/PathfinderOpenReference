package org.evilsoft.pathfinder.reference.render.json;

import android.database.Cursor;

import org.evilsoft.pathfinder.reference.db.book.TalentAdapter;
import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.json.JSONException;
import org.json.JSONObject;

public class TalentRenderer extends JsonRenderer {
	private BookDbAdapter bookDbAdapter;

	public TalentRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	public JSONObject render(JSONObject section, Integer sectionId)
			throws JSONException {
		Cursor cursor = bookDbAdapter.getTalentAdapter()
				.getTalentDetails(sectionId);
		try {
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				addField(section, "element",
						TalentAdapter.TalentUtils.getElement(cursor));
				addField(section, "talent_type",
						TalentAdapter.TalentUtils.getTalentType(cursor));
				addField(section, "blast_type",
						TalentAdapter.TalentUtils.getBlastType(cursor));
				addField(section, "level",
						TalentAdapter.TalentUtils.getLevel(cursor));
				addField(section, "burn",
						TalentAdapter.TalentUtils.getBurn(cursor));
				addField(section, "damage",
						TalentAdapter.TalentUtils.getDamage(cursor));
				addField(section, "prerequisite",
						TalentAdapter.TalentUtils.getPrerequisite(cursor));
				addField(section, "associated_blasts",
						TalentAdapter.TalentUtils.getAssociatedBlasts(cursor));
				addField(section, "saving_throw",
						TalentAdapter.TalentUtils.getSavingThrow(cursor));
				addField(section, "spell_resistance",
						TalentAdapter.TalentUtils.getSpellResistance(cursor));
			}
		} finally {
			cursor.close();
		}
		return section;
	}
}
