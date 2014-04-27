package org.evilsoft.pathfinder.reference.render.html;

import java.util.HashMap;

import org.acra.ErrorReporter;
import org.evilsoft.pathfinder.reference.HtmlRenderFarm;
import org.evilsoft.pathfinder.reference.db.BookNotFoundException;
import org.evilsoft.pathfinder.reference.db.DbWrangler;
import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.book.FullSectionAdapter;
import org.evilsoft.pathfinder.reference.db.book.MythicSpellDetailAdapter;
import org.evilsoft.pathfinder.reference.db.index.IndexGroupAdapter;

import android.database.Cursor;
import android.util.Log;

public class MythicSpellRenderer extends HtmlRenderer {
	public static final String TAG = "MythicSpellRenderer";
	private BookDbAdapter bookDbAdapter;
	private DbWrangler dbWrangler;
	private boolean exists = false;
	private String spellName;
	private Integer spellId;
	private String spellUrl;

	public MythicSpellRenderer(DbWrangler dbWrangler,
			BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
		this.dbWrangler = dbWrangler;
	}

	@Override
	public void localSetValues() {
		Cursor lcurs = bookDbAdapter.getMythicSpellDetailAdapter()
				.fetchMythicSpellDetails(sectionId);
		try {
			if (lcurs.moveToFirst()) {
				spellName = MythicSpellDetailAdapter.MythicSpellDetailUtils
						.getSpellSource(lcurs);
				Cursor ccurs = dbWrangler.getIndexDbAdapter()
						.getIndexGroupAdapter()
						.fetchByTypeAndName(spellName, "spell", null);
				try {
					if (ccurs.moveToFirst()) {
						spellId = IndexGroupAdapter.IndexGroupUtils
								.getSectionId(ccurs);
						spellUrl = IndexGroupAdapter.IndexGroupUtils
								.getUrl(ccurs);
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
		return "";
	}

	@Override
	public String renderDescription() {
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
		HashMap<Integer, Integer> depthMap = new HashMap<Integer, Integer>();
		int localdepth = depth;
		boolean showTitle = true;
		if (spellId != null) {
			BookDbAdapter mythicSpellDbAdapter;
			try {
				mythicSpellDbAdapter = dbWrangler
						.getBookDbAdapterByUrl(spellUrl);
				Cursor cursor = mythicSpellDbAdapter.getFullSectionAdapter()
						.fetchFullSection(spellId.toString());
				try {
					boolean has_next = cursor.moveToFirst();
					while (has_next) {
						String type = FullSectionAdapter.SectionUtils
								.getType(cursor);
						Integer secId = FullSectionAdapter.SectionUtils
								.getSectionId(cursor);
						Integer parentId = FullSectionAdapter.SectionUtils
								.getParentId(cursor);
						HtmlRenderer renderer = HtmlRenderFarm.getRenderer(
								type, dbWrangler, mythicSpellDbAdapter);
						localdepth = HtmlRenderFarm.getDepth(depthMap, secId,
								parentId, depth);
						sb.append(renderer.render(cursor, spellUrl, localdepth,
								top, showTitle, isTablet));
						has_next = cursor.moveToNext();
						showTitle = false;
					}
				} finally {
					cursor.close();
				}
			} catch (BookNotFoundException bnfe) {
				Log.e(TAG, "Book not found: " + bnfe.getMessage());
				ErrorReporter e = ErrorReporter.getInstance();
				ErrorReporter.getInstance()
						.putCustomData("FailedURI", spellUrl);
				ErrorReporter.getInstance().handleException(bnfe);
				e.handleException(null);
			}
		}
		return sb.toString();
	}
}
