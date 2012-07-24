package org.evilsoft.pathfinder.reference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.acra.ErrorReporter;
import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;
import org.evilsoft.pathfinder.reference.render.AbilityRenderer;
import org.evilsoft.pathfinder.reference.render.AfflictionRenderer;
import org.evilsoft.pathfinder.reference.render.AnimalCompanionRenderer;
import org.evilsoft.pathfinder.reference.render.ClassRenderer;
import org.evilsoft.pathfinder.reference.render.FeatRenderer;
import org.evilsoft.pathfinder.reference.render.ItemRenderer;
import org.evilsoft.pathfinder.reference.render.LinkRenderer;
import org.evilsoft.pathfinder.reference.render.MonsterRenderer;
import org.evilsoft.pathfinder.reference.render.RaceRenderer;
import org.evilsoft.pathfinder.reference.render.Renderer;
import org.evilsoft.pathfinder.reference.render.SectionRenderer;
import org.evilsoft.pathfinder.reference.render.SettlementRenderer;
import org.evilsoft.pathfinder.reference.render.SkillRenderer;
import org.evilsoft.pathfinder.reference.render.SpellRenderer;
import org.evilsoft.pathfinder.reference.render.TableRenderer;
import org.evilsoft.pathfinder.reference.render.TrapRenderer;
import org.evilsoft.pathfinder.reference.render.VehicleRenderer;

import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.util.Log;
import android.widget.TextView;

public class RenderFarm {
	private static final String TAG = "SectionRenderer";
	private PsrdDbAdapter dbAdapter;
	private AssetManager assets;
	private static String css;
	private TextView title;
	private List<Renderer> renderPath;
	private boolean isTablet;

	public RenderFarm(PsrdDbAdapter dbAdapter, AssetManager assets,
			TextView title, boolean isTablet) {
		this.dbAdapter = dbAdapter;
		this.assets = assets;
		this.title = title;
		this.isTablet = isTablet;
	}

	public static Renderer getRenderer(String type, PsrdDbAdapter dbAdapter) {
		if (type.equals("ability")) {
			return new AbilityRenderer(dbAdapter);
		} else if (type.equals("affliction")) {
			return new AfflictionRenderer(dbAdapter);
		} else if (type.equals("animal_companion")) {
			return new AnimalCompanionRenderer(dbAdapter);
		} else if (type.equals("class")) {
			return new ClassRenderer(dbAdapter);
		} else if (type.equals("creature")) {
			return new MonsterRenderer(dbAdapter);
		} else if (type.equals("feat")) {
			return new FeatRenderer(dbAdapter);
		} else if (type.equals("item")) {
			return new ItemRenderer(dbAdapter);
		} else if (type.equals("link")) {
			return new LinkRenderer(dbAdapter);
		} else if (type.equals("race")) {
			return new RaceRenderer(dbAdapter);
		} else if (type.equals("settlement")) {
			return new SettlementRenderer(dbAdapter);
		} else if (type.equals("skill")) {
			return new SkillRenderer(dbAdapter);
		} else if (type.equals("spell")) {
			return new SpellRenderer(dbAdapter);
		} else if (type.equals("table")) {
			return new TableRenderer(dbAdapter);
		} else if (type.equals("trap")) {
			return new TrapRenderer(dbAdapter);
		} else if (type.equals("vehicle")) {
			return new VehicleRenderer(dbAdapter);
		} else {
			return new SectionRenderer(dbAdapter);
		}
	}

	public String render(String sectionId, String inUrl) {
		Cursor curs = this.dbAdapter.fetchFullSection(sectionId);
		try {
			renderPath = new ArrayList<Renderer>();
			return renderSection(curs, inUrl);
		} finally {
			curs.close();
		}
	}

	public String renderSection(Cursor curs, String inUrl) {
		HashMap<Integer, Integer> depthMap = new HashMap<Integer, Integer>();
		HashMap<Integer, String> titleMap = new HashMap<Integer, String>();
		int depth = 0;
		StringBuffer sb = new StringBuffer();
		boolean has_next = curs.moveToFirst();
		try {
			boolean top = true;
			String topTitle = curs.getString(6);
			// 0:section_id, 1:lft, 2:rgt, 3:parent_id, 4:type, 5:subtype,
			// 6:name, 7:abbrev,
			// 8:source, 9:description, 10:body
			// 11:image, 12:alt, 13:create_index, 14:url

			sb.append(renderCss());
			this.title.setText(topTitle);
			while (has_next) {
				int sectionId = curs.getInt(0);
				int parentId = curs.getInt(3);
				String name = curs.getString(6);
				String abbrev = curs.getString(7);
				depth = getDepth(depthMap, sectionId, parentId, depth);
				titleMap.put(sectionId, name);
				String title = name;
				if (abbrev != null && !abbrev.equals("")) {
					title += " (" + abbrev + ")";
				}
				if (titleMap.containsKey(parentId)) {
					title = titleMap.get(parentId);
				}
				sb.append(renderSectionText(curs, title, depth, top));
				has_next = curs.moveToNext();
				top = false;
			}
		} catch (CursorIndexOutOfBoundsException cioobe) {
			ErrorReporter.getInstance().putCustomData("FailedURI", inUrl);
			ErrorReporter.getInstance().handleException(cioobe);
		}
		return sb.toString();
	}

	public int getDepth(HashMap<Integer, Integer> depthMap, int section_id,
			int parent_id, int depth) {
		if (depthMap.containsKey(parent_id)) {
			depth = depthMap.get(parent_id) + 1;
			depthMap.put(section_id, depth);
		} else {
			depthMap.put(section_id, depth);
		}
		return depth;
	}

	public String renderSectionText(Cursor curs, String title, int depth,
			boolean top) {
		String type = curs.getString(4);
		String uri = curs.getString(14);
		Renderer renderer = getRenderer(type, dbAdapter);
		String text = renderer
				.render(curs, uri, depth, top, suppressTitle(), isTablet);
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

	public String renderCss() {
		StringBuffer sb = new StringBuffer();
		sb.append("<head><style type='text/css'>");
		if (css == null) {
			try {
				InputStream in = assets.open("display.css");
				css = readFile(in);
			} catch (IOException e) {
				Log.e(TAG, "Failed to loaded display.css");
			}
		}
		sb.append("\n");
		sb.append(css);
		sb.append("</style></head>");
		sb.append("\n");
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
