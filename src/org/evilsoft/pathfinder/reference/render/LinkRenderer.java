package org.evilsoft.pathfinder.reference.render;

import java.util.HashMap;

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
		StringBuffer sb = new StringBuffer();
		if (render) {
			sb.append("<a href='");
			sb.append(link_url);
			sb.append("'>");
			sb.append(super.renderBody());
			sb.append("</a>");
		} else {
			HashMap<Integer, Integer> depthMap = new HashMap<Integer, Integer>();
			int localdepth = depth;
			boolean showTitle = true;
			String sectionId = getLinkSectionId();
			if (sectionId != null) {
				Cursor curs = this.dbAdapter.fetchFullSection(sectionId);
				try {
					boolean has_next = curs.moveToFirst();
					while (has_next) {
						String type = curs.getString(4);
						int secId = curs.getInt(0);
						int parentId = curs.getInt(3);
						Renderer renderer = RenderFarm.getRenderer(
								type, dbAdapter);
						localdepth = RenderFarm.getDepth(depthMap, secId, parentId, depth);
						sb.append(renderer.render(
								curs, link_url, localdepth, top, showTitle, isTablet));
						has_next = curs.moveToNext();
						showTitle = false;
					}
				} finally {
					curs.close();
				}
			}
		}
		return sb.toString();
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
