package org.evilsoft.pathfinder.reference.render.json;

import org.evilsoft.pathfinder.reference.db.DbWrangler;
import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.book.SectionAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class SectionRenderer {
	private DbWrangler dbWrangler;
	private BookDbAdapter bookDbAdapter;

	public SectionRenderer(DbWrangler dbWrangler, BookDbAdapter bookDbAdapter) {
		this.dbWrangler = dbWrangler;
		this.bookDbAdapter = bookDbAdapter;
	}

	public JSONObject render(Cursor cursor) throws JSONException {
		JSONObject section = new JSONObject();
		section.put("type", SectionAdapter.SectionUtils.getType(cursor));
		section.put("subtype", SectionAdapter.SectionUtils.getSubtype(cursor));
		section.put("name", SectionAdapter.SectionUtils.getName(cursor));
		section.put("abbrev", SectionAdapter.SectionUtils.getAbbrev(cursor));
		section.put("source", SectionAdapter.SectionUtils.getSource(cursor));
		section.put("description",
				SectionAdapter.SectionUtils.getDescription(cursor));
		section.put("text", SectionAdapter.SectionUtils.getBody(cursor));
		section.put("url", SectionAdapter.SectionUtils.getUrl(cursor));
		section.put("image", SectionAdapter.SectionUtils.getImage(cursor));
		section.put("alt", SectionAdapter.SectionUtils.getAlt(cursor));
		addTypeFields(SectionAdapter.SectionUtils.getType(cursor),
				SectionAdapter.SectionUtils.getSubtype(cursor), section,
				SectionAdapter.SectionUtils.getSectionId(cursor));
		addChildren(section, SectionAdapter.SectionUtils.getSectionId(cursor));
		return section;
	}

	public void addTypeFields(String type, String subtype, JSONObject section,
			Integer sectionId) throws JSONException {
		JsonRenderer renderer = getRenderer(type, dbWrangler, bookDbAdapter);
		renderer.render(section, sectionId);
	}

	public void addChildren(JSONObject section, Integer sectionId)
			throws JSONException {
		Cursor cursor = bookDbAdapter.getSectionAdapter()
				.fetchSectionByParentId(sectionId);
		try {
			boolean has_next = cursor.moveToFirst();
			JSONArray sections = new JSONArray();
			while (has_next) {
				JSONObject newSection = render(cursor);
				sections.put(newSection);
				has_next = cursor.moveToNext();
			}
			if (sections.length() > 0) {
				section.put("sections", sections);
			}
		} finally {
			cursor.close();
		}
	}

	public static JsonRenderer getRenderer(String type, DbWrangler dbWrangler,
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
			return new LinkRenderer(bookDbAdapter);
		} else if (type.equals("mythic_spell")) {
			return new MythicSpellRenderer(bookDbAdapter);
		} else if (type.equals("resource")) {
			return new ResourceRenderer(bookDbAdapter);
		} else if (type.equals("settlement")) {
			return new SettlementRenderer(bookDbAdapter);
		} else if (type.equals("skill")) {
			return new SkillRenderer(bookDbAdapter);
		} else if (type.equals("spell")) {
			return new SpellRenderer(bookDbAdapter);
		} else if (type.equals("talent")) {
			return new TalentRenderer(bookDbAdapter);
		} else if (type.equals("trap")) {
			return new TrapRenderer(bookDbAdapter);
		} else if (type.equals("vehicle")) {
			return new VehicleRenderer(bookDbAdapter);
		}
		return new NoOpRenderer();
	}
}
