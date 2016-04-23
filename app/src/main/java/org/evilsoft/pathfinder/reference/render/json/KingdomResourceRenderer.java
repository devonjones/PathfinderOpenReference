package org.evilsoft.pathfinder.reference.render.json;

import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.book.KingdomResourceAdapter;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class KingdomResourceRenderer extends JsonRenderer {
	private BookDbAdapter bookDbAdapter;

	public KingdomResourceRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	public JSONObject render(JSONObject section, Integer sectionId)
			throws JSONException {
		Cursor cursor = bookDbAdapter.getKingdomResourceAdapter()
				.getKingdomResourceDetails(sectionId);
		try {
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				addField(section, "bp",
						KingdomResourceAdapter.KingdomResourceUtils
								.getBp(cursor));
				addField(section, "discount",
						KingdomResourceAdapter.KingdomResourceUtils
								.getDiscount(cursor));
				addField(section, "kingdom",
						KingdomResourceAdapter.KingdomResourceUtils
								.getKingdom(cursor));
				addField(section, "limit",
						KingdomResourceAdapter.KingdomResourceUtils
								.getLimit(cursor));
				addField(section, "lot",
						KingdomResourceAdapter.KingdomResourceUtils
								.getLot(cursor));
				addField(section, "magic_items",
						KingdomResourceAdapter.KingdomResourceUtils
								.getMagicItems(cursor));
				addField(section, "settlement",
						KingdomResourceAdapter.KingdomResourceUtils
								.getSettlement(cursor));
				addField(section, "special",
						KingdomResourceAdapter.KingdomResourceUtils
								.getSpecial(cursor));
				addField(section, "upgrade_from",
						KingdomResourceAdapter.KingdomResourceUtils
								.getUpgradeFrom(cursor));
				addField(section, "upgrade_to",
						KingdomResourceAdapter.KingdomResourceUtils
								.getUpgradeTo(cursor));
			}
		} finally {
			cursor.close();
		}
		return section;
	}
}
