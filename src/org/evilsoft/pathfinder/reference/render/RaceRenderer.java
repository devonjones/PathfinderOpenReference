package org.evilsoft.pathfinder.reference.render;

import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;

public class RaceRenderer extends Renderer {
	public RaceRenderer(PsrdDbAdapter dbAdapter) {
	}

	@Override
	public String renderTitle() {
		String title = name;
		if (!top) {
			title = title + " Characters";
		}
		return renderTitle(title, newUri, depth, top);
	}

	@Override
	public String renderDetails() {
		return "";
	}
}
