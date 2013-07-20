package org.evilsoft.pathfinder.reference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.acra.ErrorReporter;
import org.evilsoft.pathfinder.reference.db.DbWrangler;
import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.book.FullSectionAdapter;
import org.evilsoft.pathfinder.reference.render.AbilityRenderer;
import org.evilsoft.pathfinder.reference.render.AfflictionRenderer;
import org.evilsoft.pathfinder.reference.render.AnimalCompanionRenderer;
import org.evilsoft.pathfinder.reference.render.ArmyRenderer;
import org.evilsoft.pathfinder.reference.render.ClassRenderer;
import org.evilsoft.pathfinder.reference.render.CreatureRenderer;
import org.evilsoft.pathfinder.reference.render.EmbedRenderer;
import org.evilsoft.pathfinder.reference.render.FeatRenderer;
import org.evilsoft.pathfinder.reference.render.HauntRenderer;
import org.evilsoft.pathfinder.reference.render.ItemRenderer;
import org.evilsoft.pathfinder.reference.render.KingdomResourceRenderer;
import org.evilsoft.pathfinder.reference.render.LinkRenderer;
import org.evilsoft.pathfinder.reference.render.RaceRenderer;
import org.evilsoft.pathfinder.reference.render.Renderer;
import org.evilsoft.pathfinder.reference.render.ResourceRenderer;
import org.evilsoft.pathfinder.reference.render.SectionRenderer;
import org.evilsoft.pathfinder.reference.render.SettlementRenderer;
import org.evilsoft.pathfinder.reference.render.SkillRenderer;
import org.evilsoft.pathfinder.reference.render.SpellRenderer;
import org.evilsoft.pathfinder.reference.render.TableRenderer;
import org.evilsoft.pathfinder.reference.render.TrapRenderer;
import org.evilsoft.pathfinder.reference.render.VehicleRenderer;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.widget.TextView;

public class RenderFarm {
	private DbWrangler dbWrangler;
	private BookDbAdapter bookDbAdapter;
	private TextView title;
	private List<Renderer> renderPath;
	private boolean isTablet;
	private boolean showToc;

	public RenderFarm(DbWrangler dbWrangler, BookDbAdapter bookDbAdapter,
			TextView title, boolean isTablet, boolean showToc) {
		this.dbWrangler = dbWrangler;
		this.bookDbAdapter = bookDbAdapter;
		this.title = title;
		this.isTablet = isTablet;
		this.showToc = showToc;
	}

	public static Renderer getRenderer(String type, DbWrangler dbWrangler,
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
			return new SpellRenderer(bookDbAdapter);
		} else if (type.equals("table")) {
			return new TableRenderer();
		} else if (type.equals("trap")) {
			return new TrapRenderer(bookDbAdapter);
		} else if (type.equals("vehicle")) {
			return new VehicleRenderer(bookDbAdapter);
		} else {
			return new SectionRenderer();
		}
	}

	public String render(String sectionId, String inUrl) {
		Cursor cursor = this.bookDbAdapter.getFullSectionAdapter()
				.fetchFullSection(sectionId);
		try {
			renderPath = new ArrayList<Renderer>();
			return renderSection(cursor, inUrl);
		} finally {
			cursor.close();
		}
	}

	public String renderSection(Cursor cursor, String inUrl) {
		HashMap<Integer, Integer> depthMap = new HashMap<Integer, Integer>();
		HashMap<Integer, String> titleMap = new HashMap<Integer, String>();
		int depth = 0;
		StringBuffer sb = new StringBuffer();
		boolean has_next = cursor.moveToFirst();
		try {
			boolean top = true;
			String topTitle = FullSectionAdapter.SectionUtils.getName(cursor);
			sb.append(renderHeader());
			sb.append("<body>");
			this.title.setText(topTitle);
			while (has_next) {
				int sectionId = FullSectionAdapter.SectionUtils
						.getSectionId(cursor);
				int parentId = FullSectionAdapter.SectionUtils
						.getParentId(cursor);
				String name = FullSectionAdapter.SectionUtils.getName(cursor);
				String abbrev = FullSectionAdapter.SectionUtils
						.getAbbrev(cursor);
				depth = getDepth(depthMap, sectionId, parentId, depth);
				titleMap.put(sectionId, name);
				String title = name;
				if (abbrev != null && !abbrev.equals("")) {
					title += " (" + abbrev + ")";
				}
				if (titleMap.containsKey(parentId)) {
					title = titleMap.get(parentId);
				}
				sb.append(renderSectionText(cursor, title, depth, top));
				has_next = cursor.moveToNext();
				top = false;
			}
		} catch (CursorIndexOutOfBoundsException cioobe) {
			ErrorReporter.getInstance().putCustomData("FailedURI", inUrl);
			ErrorReporter.getInstance().handleException(cioobe);
		}
		sb.append("</body>");
		sb.append(renderFooter());
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

	public String renderSectionText(Cursor cursor, String title, int depth,
			boolean top) {
		String type = FullSectionAdapter.SectionUtils.getType(cursor);
		String url = FullSectionAdapter.SectionUtils.getUrl(cursor);
		Renderer renderer = getRenderer(type, dbWrangler, bookDbAdapter);
		String text = renderer.render(cursor, url, depth, top, suppressTitle(),
				isTablet);
		renderPath.add(renderer);
		return text;
	}

	public boolean suppressTitle() {
		if (renderPath.size() == 0) {
			return true;
		}
		Renderer prev = renderPath.get(renderPath.size() - 1);
		return prev.suppressNextTitle == true;
	}

	public String renderFooter() {
		StringBuffer sb = new StringBuffer();
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
		StringBuffer sb = new StringBuffer();
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
