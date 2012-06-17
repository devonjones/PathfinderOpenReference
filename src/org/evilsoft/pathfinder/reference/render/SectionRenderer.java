package org.evilsoft.pathfinder.reference.render;

import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;

public class SectionRenderer extends Renderer {
	public SectionRenderer(PsrdDbAdapter dbAdapter) {
	}

	@Override
	public String renderTitle() {
		return renderTitle(name, newUri, depth, top);
	}

	@Override
	public String renderDetails() {
		return "";
	}

	@Override
	public String renderFooter() {
		return "";
	}

	@Override
	public String renderHeader() {
		return "";
	}
}
