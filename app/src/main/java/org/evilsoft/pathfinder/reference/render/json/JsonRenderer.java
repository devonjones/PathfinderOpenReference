package org.evilsoft.pathfinder.reference.render.json;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class JsonRenderer {
	public abstract JSONObject render(JSONObject section, Integer sectionId)
			throws JSONException;

	public void addField(JSONObject section, String name, Object value)
			throws JSONException {
		if (value != null) {
			section.put(name, value);
		}
	}
}
