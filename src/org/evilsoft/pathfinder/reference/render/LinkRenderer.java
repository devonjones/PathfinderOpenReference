package org.evilsoft.pathfinder.reference.render;

import org.evilsoft.pathfinder.reference.RenderFarm;
import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;

import android.database.Cursor;

public class LinkRenderer extends Renderer {
	private PsrdDbAdapter dbAdapter;
	private boolean render = true;
	private String link_url;

	public LinkRenderer(PsrdDbAdapter dbAdapter) {
		this.dbAdapter = dbAdapter;
	}

	@Override
	public void localSetValues() {
		Cursor lcurs = dbAdapter.getLinkDetails(sectionId);
		try {
			boolean has_next = lcurs.moveToFirst();
			if (has_next) {
				link_url = lcurs.getString(1);
				render = lcurs.getInt(2) == 0;
			}
		} finally {
			lcurs.close();
		}
	}

	@Override
	public String renderTitle() {
		return renderTitle(name, newUri, depth, top);
	}

	@Override
	public String renderDescription() {
		if (render) {
			return super.renderDescription();
		}
		return "";
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

	@Override
	public String renderBody() {
		if (render) {
			StringBuffer sb = new StringBuffer();
			sb.append("<a href='");
			sb.append(link_url);
			sb.append("'>");
			sb.append(super.renderBody());
			sb.append("</a>");
			return sb.toString();
		} else {
			String sectionId = getLinkSectionId();
			if (sectionId != null) {
				Cursor curs = this.dbAdapter.fetchFullSection(sectionId);
				try {
					boolean has_next = curs.moveToFirst();
					if (has_next) {
						String type = curs.getString(4);
						Renderer renderer = RenderFarm.getRenderer(type,
								dbAdapter);
						return renderer
								.render(curs, link_url, depth, top, true,
										isTablet);
					}
				} finally {
					curs.close();
				}
			}
		}
		return "";
	}

	private String getLinkSectionId() {
		Cursor curs = dbAdapter.fetchSectionByUrl(link_url);
		try {
			boolean has_next = curs.moveToFirst();
			if (has_next) {
				return curs.getString(0);
			}
		} finally {
			curs.close();
		}
		return null;
	}
}
