package org.evilsoft.pathfinder.reference.render.json;

import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.book.TrapAdapter;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class TrapRenderer extends JsonRenderer {
	private BookDbAdapter bookDbAdapter;

	public TrapRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	public JSONObject render(JSONObject section, Integer sectionId)
			throws JSONException {
		Cursor cursor = bookDbAdapter.getTrapAdapter()
				.getTrapDetails(sectionId);
		try {
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				addField(section, "cr", TrapAdapter.TrapUtils.getCr(cursor));
				addField(section, "disable_device",
						TrapAdapter.TrapUtils.getDisableDevice(cursor));
				addField(section, "duration",
						TrapAdapter.TrapUtils.getDuration(cursor));
				addField(section, "effect",
						TrapAdapter.TrapUtils.getEffect(cursor));
				addField(section, "perception",
						TrapAdapter.TrapUtils.getPerception(cursor));
				addField(section, "reset",
						TrapAdapter.TrapUtils.getReset(cursor));
				addField(section, "trap_type",
						TrapAdapter.TrapUtils.getTrapType(cursor));
				addField(section, "trigger",
						TrapAdapter.TrapUtils.getTrigger(cursor));
			}
		} finally {
			cursor.close();
		}
		return section;
	}
}
