package org.evilsoft.pathfinder.reference.render;

import org.evilsoft.pathfinder.reference.db.psrd.FeatAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;

public class FeatRenderer extends Renderer {
	private FeatAdapter featAdapter;
	private PsrdDbAdapter dbAdapter;

	public FeatRenderer(PsrdDbAdapter dbAdapter) {
		this.dbAdapter = dbAdapter;
	}

	private FeatAdapter getFeatAdapter() {
		if (this.featAdapter == null) {
			this.featAdapter = new FeatAdapter(this.dbAdapter);
		}
		return this.featAdapter;
	}

	@Override
	public String renderTitle() {
		return renderTitle(name, abbrev, newUri, 0, top);
	}

	@Override
	public String renderDetails() {
		StringBuffer sb = new StringBuffer();
		sb.append("<B>");
		sb.append(this.getFeatAdapter().renderFeatTypeDescription(sectionId));
		sb.append("</B><BR>\n");
		sb.append("<B>Source: </B>");
		sb.append(source);
		sb.append("<BR>");
		return sb.toString();
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
