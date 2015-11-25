package org.evilsoft.pathfinder.reference.render.json;

import org.json.JSONException;
import org.json.JSONObject;

public class NoOpRenderer extends JsonRenderer {

	@Override
	public JSONObject render(JSONObject section, Integer sectionId)
			throws JSONException {
		return section;
	}

}
