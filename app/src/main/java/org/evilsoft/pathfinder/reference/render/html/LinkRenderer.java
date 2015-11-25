package org.evilsoft.pathfinder.reference.render.html;

import java.util.HashMap;

import org.acra.ErrorReporter;
import org.evilsoft.pathfinder.reference.HtmlRenderFarm;
import org.evilsoft.pathfinder.reference.db.BookNotFoundException;
import org.evilsoft.pathfinder.reference.db.DbWrangler;
import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.book.FullSectionAdapter;
import org.evilsoft.pathfinder.reference.db.book.SectionAdapter;
import org.evilsoft.pathfinder.reference.db.index.CountAdapter;

import android.database.Cursor;
import android.util.Log;

public class LinkRenderer extends HtmlRenderer {
	public static final String TAG = "LinkRenderer";
	private BookDbAdapter bookDbAdapter;
	private DbWrangler dbWrangler;
	private boolean render = true;
	private boolean exists = false;
	private String linkUrl;

	public LinkRenderer(DbWrangler dbWrangler, BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
		this.dbWrangler = dbWrangler;
	}

	@Override
	public void localSetValues() {
		Cursor lcurs = bookDbAdapter.getLinkAdapter().getLinkDetails(sectionId);
		try {
			boolean has_next = lcurs.moveToFirst();
			if (has_next) {
				linkUrl = lcurs.getString(1).replaceAll("'", "");
				render = lcurs.getInt(2) == 0;
				Cursor ccurs = dbWrangler.getIndexDbAdapter().getCountAdapter()
						.fetchByUrl(linkUrl);
				ccurs.moveToFirst();
				try {
					if (CountAdapter.CountUtils.getCount(ccurs) > 0) {
						exists = true;
					}
				} finally {
					ccurs.close();
				}
			}
		} finally {
			lcurs.close();
		}
	}

	@Override
	public String renderTitle() {
		if (exists) {
			return renderTitle(name, abbrev, newUri, depth, top);
		}
		return "";
	}

	@Override
	public String renderDescription() {
		if (render && exists) {
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
		if (!exists) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		if (render) {
			sb.append("<a href='");
			sb.append(linkUrl);
			sb.append("'>");
			sb.append(super.renderBody());
			sb.append("</a>");
		} else {
			HashMap<Integer, Integer> depthMap = new HashMap<Integer, Integer>();
			int localdepth = depth;
			boolean showTitle = true;
			BookDbAdapter linkBookDbAdapter;
			try {
				linkBookDbAdapter = dbWrangler.getBookDbAdapterByUrl(linkUrl);
				if (linkBookDbAdapter != null) {
					Integer sectionId = getLinkSectionId(linkBookDbAdapter);
					if (sectionId != null) {
						Cursor cursor = linkBookDbAdapter
								.getFullSectionAdapter().fetchFullSection(
										sectionId.toString());
						try {
							boolean has_next = cursor.moveToFirst();
							while (has_next) {
								String type = FullSectionAdapter.SectionUtils
										.getType(cursor);
								Integer secId = FullSectionAdapter.SectionUtils
										.getSectionId(cursor);
								Integer parentId = FullSectionAdapter.SectionUtils
										.getParentId(cursor);
								HtmlRenderer renderer = HtmlRenderFarm
										.getRenderer(type, dbWrangler,
												linkBookDbAdapter);
								localdepth = HtmlRenderFarm.getDepth(depthMap,
										secId, parentId, depth);
								sb.append(renderer.render(cursor, linkUrl,
										localdepth, top, showTitle, isTablet));
								has_next = cursor.moveToNext();
								showTitle = false;
							}
						} finally {
							cursor.close();
						}
					}
				}
			} catch (BookNotFoundException bnfe) {
				Log.e(TAG, "Book not found: " + bnfe.getMessage());
				ErrorReporter e = ErrorReporter.getInstance();
				ErrorReporter.getInstance().putCustomData("FailedURI", linkUrl);
				ErrorReporter.getInstance().handleException(bnfe);
				e.handleException(null);
			}
		}
		return sb.toString();
	}

	private Integer getLinkSectionId(BookDbAdapter linkBookDbAdapter) {
		Cursor cursor = linkBookDbAdapter.getSectionAdapter()
				.fetchSectionByUrl(linkUrl);
		try {
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				return SectionAdapter.SectionUtils.getSectionId(cursor);
			}
		} finally {
			cursor.close();
		}
		return null;
	}
}
