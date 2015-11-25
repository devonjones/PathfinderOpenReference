package org.evilsoft.pathfinder.reference.render.json;

import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.book.ResourceAdapter;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class ResourceRenderer extends JsonRenderer {
	private BookDbAdapter bookDbAdapter;

	public ResourceRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	public JSONObject render(JSONObject section, Integer sectionId)
			throws JSONException {
		Cursor cursor = bookDbAdapter.getResourceAdapter().getResourceDetails(
				sectionId);
		try {
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				addField(section, "bebefit",
						ResourceAdapter.ResourceUtils.getBenefit(cursor));
				addField(section, "create",
						ResourceAdapter.ResourceUtils.getCreate(cursor));
				addField(section, "earnings",
						ResourceAdapter.ResourceUtils.getEarnings(cursor));
				addField(section, "rooms",
						ResourceAdapter.ResourceUtils.getRooms(cursor));
				addField(section, "size",
						ResourceAdapter.ResourceUtils.getSize(cursor));
				addField(section, "skills",
						ResourceAdapter.ResourceUtils.getSkills(cursor));
				addField(section, "teams",
						ResourceAdapter.ResourceUtils.getTeams(cursor));
				addField(section, "time",
						ResourceAdapter.ResourceUtils.getTime(cursor));
				addField(section, "upgrade_from",
						ResourceAdapter.ResourceUtils.getUpgradeFrom(cursor));
				addField(section, "upgrade_to",
						ResourceAdapter.ResourceUtils.getUpgradeTo(cursor));
				addField(section, "wage",
						ResourceAdapter.ResourceUtils.getWage(cursor));
			}
		} finally {
			cursor.close();
		}
		return section;
	}
}
