package org.evilsoft.pathfinder.reference.render.html;

public class SectionRenderer extends HtmlRenderer {
	public SectionRenderer() {
	}

	@Override
	public String renderTitle() {
		return renderTitle(name, abbrev, newUri, depth, top);
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
