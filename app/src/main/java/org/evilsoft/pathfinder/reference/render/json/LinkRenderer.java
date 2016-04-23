package org.evilsoft.pathfinder.reference.render.json;

import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.book.LinkAdapter;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class LinkRenderer extends JsonRenderer {
	private BookDbAdapter bookDbAdapter;

	public LinkRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	public JSONObject render(JSONObject section, Integer sectionId)
			throws JSONException {
		Cursor cursor = bookDbAdapter.getLinkAdapter()
				.getLinkDetails(sectionId);
		try {
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				addField(section, "display",
						LinkAdapter.LinkUtils.getDisplay(cursor));
				addField(section, "link_url",
						LinkAdapter.LinkUtils.getUrl(cursor));
			}
		} finally {
			cursor.close();
		}
		return section;
	}
}
