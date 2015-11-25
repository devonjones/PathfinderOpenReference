package org.evilsoft.pathfinder.reference.render.json;

import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.book.SettlementAdapter;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class SettlementRenderer extends JsonRenderer {
	private BookDbAdapter bookDbAdapter;

	public SettlementRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	public JSONObject render(JSONObject section, Integer sectionId)
			throws JSONException {
		Cursor cursor = bookDbAdapter.getSettlementAdapter()
				.getSettlementDetails(sectionId);
		try {
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				addField(section, "alignment",
						SettlementAdapter.SettlementUtils.getAlignment(cursor));
				addField(section, "base_value",
						SettlementAdapter.SettlementUtils.getBaseValue(cursor));
				addField(section, "corruption",
						SettlementAdapter.SettlementUtils.getCorruption(cursor));
				addField(section, "crime",
						SettlementAdapter.SettlementUtils.getCrime(cursor));
				addField(section, "danger",
						SettlementAdapter.SettlementUtils.getDanger(cursor));
				addField(section, "disadvantages",
						SettlementAdapter.SettlementUtils
								.getDisadvantages(cursor));
				addField(section, "economy",
						SettlementAdapter.SettlementUtils.getEconomy(cursor));
				addField(section, "government",
						SettlementAdapter.SettlementUtils.getGovernment(cursor));
				addField(section, "law",
						SettlementAdapter.SettlementUtils.getLaw(cursor));
				addField(section, "lore",
						SettlementAdapter.SettlementUtils.getLore(cursor));
				addField(section, "major_items",
						SettlementAdapter.SettlementUtils.getMajorItems(cursor));
				addField(section, "medium_items",
						SettlementAdapter.SettlementUtils
								.getMediumItems(cursor));
				addField(section, "minor_items",
						SettlementAdapter.SettlementUtils.getMinorItems(cursor));
				addField(section, "population",
						SettlementAdapter.SettlementUtils.getPopulation(cursor));
				addField(section, "purchase_limit",
						SettlementAdapter.SettlementUtils
								.getPurchaseLimit(cursor));
				addField(section, "qualities",
						SettlementAdapter.SettlementUtils.getQualities(cursor));
				addField(section, "settlement_type",
						SettlementAdapter.SettlementUtils
								.getSettlementType(cursor));
				addField(section, "size",
						SettlementAdapter.SettlementUtils.getSize(cursor));
				addField(section, "society",
						SettlementAdapter.SettlementUtils.getSociety(cursor));
				addField(section, "spellcasting",
						SettlementAdapter.SettlementUtils
								.getSpellcasting(cursor));
			}
		} finally {
			cursor.close();
		}
		return section;
	}
}
