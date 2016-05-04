package org.evilsoft.pathfinder.reference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.DbWrangler;
import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.book.FullSectionAdapter;
import org.evilsoft.pathfinder.reference.render.html.AbilityRenderer;
import org.evilsoft.pathfinder.reference.render.html.AfflictionRenderer;
import org.evilsoft.pathfinder.reference.render.html.AnimalCompanionRenderer;
import org.evilsoft.pathfinder.reference.render.html.ArmyRenderer;
import org.evilsoft.pathfinder.reference.render.html.ClassRenderer;
import org.evilsoft.pathfinder.reference.render.html.CreatureRenderer;
import org.evilsoft.pathfinder.reference.render.html.EmbedRenderer;
import org.evilsoft.pathfinder.reference.render.html.FeatRenderer;
import org.evilsoft.pathfinder.reference.render.html.HauntRenderer;
import org.evilsoft.pathfinder.reference.render.html.HtmlRenderer;
import org.evilsoft.pathfinder.reference.render.html.ItemRenderer;
import org.evilsoft.pathfinder.reference.render.html.KingdomResourceRenderer;
import org.evilsoft.pathfinder.reference.render.html.LinkRenderer;
import org.evilsoft.pathfinder.reference.render.html.MythicSpellRenderer;
import org.evilsoft.pathfinder.reference.render.html.RaceRenderer;
import org.evilsoft.pathfinder.reference.render.html.ResourceRenderer;
import org.evilsoft.pathfinder.reference.render.html.SectionRenderer;
import org.evilsoft.pathfinder.reference.render.html.SettlementRenderer;
import org.evilsoft.pathfinder.reference.render.html.SkillRenderer;
import org.evilsoft.pathfinder.reference.render.html.SpellRenderer;
import org.evilsoft.pathfinder.reference.render.html.TableRenderer;
import org.evilsoft.pathfinder.reference.render.html.TalentRenderer;
import org.evilsoft.pathfinder.reference.render.html.TrapRenderer;
import org.evilsoft.pathfinder.reference.render.html.VehicleRenderer;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.util.Log;
import android.widget.TextView;

public class HtmlRenderFarm {
	private DbWrangler dbWrangler;
	private BookDbAdapter bookDbAdapter;
	private TextView title;
	private List<HtmlRenderer> renderPath;
	private boolean isTablet;
	private boolean showToc;
	private Integer noRender = null;
	private static final String TAG = "HtmlRenderFarm";

	public HtmlRenderFarm(DbWrangler dbWrangler, BookDbAdapter bookDbAdapter,
			TextView title, boolean isTablet, boolean showToc) {
		this.dbWrangler = dbWrangler;
		this.bookDbAdapter = bookDbAdapter;
		this.title = title;
		this.isTablet = isTablet;
		this.showToc = showToc;
	}

	public HtmlRenderFarm(DbWrangler dbWrangler, BookDbAdapter bookDbAdapter,
			boolean isTablet, boolean showToc) {
		this.dbWrangler = dbWrangler;
		this.bookDbAdapter = bookDbAdapter;
		this.title = null;
		this.isTablet = isTablet;
		this.showToc = showToc;
	}

	public static HtmlRenderer getRenderer(String type, DbWrangler dbWrangler,
			BookDbAdapter bookDbAdapter) {
		if (type.equals("ability")) {
			return new AbilityRenderer(bookDbAdapter);
		} else if (type.equals("affliction")) {
			return new AfflictionRenderer(bookDbAdapter);
		} else if (type.equals("animal_companion")) {
			return new AnimalCompanionRenderer(bookDbAdapter);
		} else if (type.equals("army")) {
			return new ArmyRenderer(bookDbAdapter);
		} else if (type.equals("class")) {
			return new ClassRenderer(bookDbAdapter);
		} else if (type.equals("creature")) {
			return new CreatureRenderer(bookDbAdapter);
		} else if (type.equals("feat")) {
			return new FeatRenderer(bookDbAdapter);
		} else if (type.equals("haunt")) {
			return new HauntRenderer(bookDbAdapter);
		} else if (type.equals("item")) {
			return new ItemRenderer(bookDbAdapter);
		} else if (type.equals("kingdom_resource")) {
			return new KingdomResourceRenderer(bookDbAdapter);
		} else if (type.equals("link")) {
			return new LinkRenderer(dbWrangler, bookDbAdapter);
		} else if (type.equals("mythic_spell")) {
			return new MythicSpellRenderer(dbWrangler, bookDbAdapter);
		} else if (type.equals("embed")) {
			return new EmbedRenderer(dbWrangler, bookDbAdapter);
		} else if (type.equals("race")) {
			return new RaceRenderer();
		} else if (type.equals("resource")) {
			return new ResourceRenderer(bookDbAdapter);
		} else if (type.equals("settlement")) {
			return new SettlementRenderer(bookDbAdapter);
		} else if (type.equals("skill")) {
			return new SkillRenderer(bookDbAdapter);
		} else if (type.equals("spell")) {
			return new SpellRenderer(dbWrangler, bookDbAdapter);
		} else if (type.equals("table")) {
			return new TableRenderer();
		} else if (type.equals("talent")) {
			return new TalentRenderer(bookDbAdapter);
		} else if (type.equals("trap")) {
			return new TrapRenderer(bookDbAdapter);
		} else if (type.equals("vehicle")) {
			return new VehicleRenderer(bookDbAdapter);
		} else {
			return new SectionRenderer();
		}
	}

	public String render(String sectionId, String inUrl) {
		return render(sectionId, inUrl, true);
	}

	public String render(String sectionId, String inUrl,
			boolean wrapHeaderFooter) {
		Cursor cursor = this.bookDbAdapter.getFullSectionAdapter()
				.fetchFullSection(sectionId);
		try {
			renderPath = new ArrayList<HtmlRenderer>();
			return renderSection(cursor, inUrl, wrapHeaderFooter);
		} finally {
			cursor.close();
		}
	}

	public String renderSection(Cursor cursor, String inUrl,
			boolean wrapHeaderFooter) {
		HashMap<Integer, Integer> depthMap = new HashMap<Integer, Integer>();
		HashMap<Integer, String> titleMap = new HashMap<Integer, String>();
		int depth = 0;
		StringBuilder sb = new StringBuilder();
		boolean has_next = cursor.moveToFirst();
		try {
			boolean top = true;
			String topTitle = FullSectionAdapter.SectionUtils.getName(cursor);
			if (wrapHeaderFooter) {
				sb.append(renderHeader());
			}
			sb.append("<body>");
			if (this.title != null) {
				this.title.setText(topTitle);
			} else {
				sb.append("<H1>");
				sb.append(topTitle);
				sb.append("</H1>");
			}
			while (has_next) {
				int sectionId = FullSectionAdapter.SectionUtils
						.getSectionId(cursor);
				int parentId = FullSectionAdapter.SectionUtils
						.getParentId(cursor);
				String name = FullSectionAdapter.SectionUtils.getName(cursor);
				depth = getDepth(depthMap, sectionId, parentId, depth);
				if (noRender != null && depth <= noRender) {
					noRender = null;
				}
				if (noRender == null) {
					titleMap.put(sectionId, name);
					sb.append(renderSectionText(cursor, depth, top));
				}
				has_next = cursor.moveToNext();
				top = false;
			}
		} catch (CursorIndexOutOfBoundsException cioobe) {
			Log.i(TAG, "FailedURI " + inUrl);
		}
		sb.append("</body>");
		if (wrapHeaderFooter) {
			sb.append(renderFooter());
		}
		return sb.toString();
	}

	public static int getDepth(HashMap<Integer, Integer> depthMap,
			int section_id, int parent_id, int depth) {
		if (depthMap.containsKey(parent_id)) {
			depth = depthMap.get(parent_id) + 1;
			depthMap.put(section_id, depth);
		} else {
			depthMap.put(section_id, depth);
		}
		return depth;
	}

	public String renderSectionText(Cursor cursor, int depth, boolean top) {
		String type = FullSectionAdapter.SectionUtils.getType(cursor);
		String url = FullSectionAdapter.SectionUtils.getUrl(cursor);
		HtmlRenderer renderer = getRenderer(type, dbWrangler, bookDbAdapter);
		String text = renderer.render(cursor, url, depth, top, suppressTitle(),
				isTablet);
		if (noRender == null && !renderer.renderBelow()) {
			noRender = depth;
		}
		renderPath.add(renderer);
		return text;
	}

	public boolean suppressTitle() {
		if (renderPath.size() == 0) {
			return true;
		}
		HtmlRenderer prev = renderPath.get(renderPath.size() - 1);
		return prev.suppressNextTitle == true;
	}

	public String renderFooter() {
		StringBuilder sb = new StringBuilder();
		sb.append("<script type=\"text/javascript\" src=\"file:///android_asset/application.min.js\"></script>");
		if (showToc) {
			if (isTablet) {
				sb.append("<script type=\"text/javascript\">window.psrd_toc.side();</script>");
			} else {
				sb.append("<script type=\"text/javascript\">window.psrd_toc.full();</script>");
			}
		} else {
			sb.append("<script type=\"text/javascript\">window.psrd_toc.hide();</script>");
		}
		return sb.toString();
	}

	public String renderHeader() {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<head>");
		sb.append("<meta name=\"viewport\" content=\"width=device-width; initial-scale=1; maximum-sale=1; minimum-scale=1; user-scalable=n;\" />");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
		sb.append("<link media=\"screen, print\" rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/display.css\" />");
		sb.append("<link media=\"screen, print\" rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/application.min.css\" />");
		sb.append("</head>");
		return sb.toString();
	}

	public String readFile(InputStream is) {
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		try {
			for (int readnum; (readnum = is.read(buffer)) != -1;) {
				// is.read(buffer);
				bo.write(buffer, 0, readnum);
			}
			bo.close();
			is.close();
		} catch (IOException e) {

		}
		return bo.toString();
	}
}
