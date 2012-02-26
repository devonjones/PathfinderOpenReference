package org.evilsoft.pathfinder.reference.db.psrd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PsrdDbAdapter {
	private Context context;
	public SQLiteDatabase database;
	private PsrdDbHelper dbHelper;
	private static final String TAG = "PsrdDbAdapter";

	public PsrdDbAdapter(Context context) {
		this.context = context;
	}

	public PsrdDbAdapter open() throws SQLException {
		dbHelper = new PsrdDbHelper(context);
		database = dbHelper.openDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	public Cursor fetchSectionByType(String sectionType) {
		return fetchSectionByType(sectionType, null);
	}

	public String[] toStringArray(List<String> input) {
		String[] retarr = new String[input.size()];
		for (int i = 0; i < input.size(); i++) {
			retarr[i] = input.get(i);
		}
		return retarr;
	}

	public Cursor fetchSectionByType(String sectionType, String sectionSubType) {
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT section_id, name");
		sb.append(" FROM sections");
		String where = " WHERE";
		if (sectionType != null) {
			sb.append(where);
			sb.append(" type = ?");
			where = "  AND";
			args.add(sectionType);
		}
		if (sectionSubType != null) {
			sb.append(where);
			sb.append(" subtype = ?");
			args.add(sectionSubType);
		}
		sb.append(" ORDER BY name");
		String sql = sb.toString();
		return database.rawQuery(sql, toStringArray(args));
	}

	public ArrayList<HashMap<String, String>> getPath(String sectionId) {
		ArrayList<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();
		Cursor curs = fetchSection(sectionId);
		Log.e(TAG, sectionId);
		boolean has_rows = curs.moveToFirst();
		Log.e(TAG, ((Boolean) has_rows).toString());
		if (has_rows) {
			String parentId = curs.getString(1);
			HashMap<String, String> element = new HashMap<String, String>();
			element.put("id", curs.getString(0));
			element.put("name", curs.getString(2));
			path.add(element);
			while (parentId != null) {
				Cursor curs2 = fetchSection(parentId);
				curs2.moveToFirst();
				parentId = curs2.getString(1);
				element = new HashMap<String, String>();
				element.put("id", curs2.getString(0));
				element.put("name", curs2.getString(2));
				path.add(element);
			}
		}
		return path;
	}

	public Cursor fetchSection(String sectionId) {
		List<String> args = new ArrayList<String>();
		args.add(sectionId);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT section_id, parent_id, name");
		sb.append(" FROM sections");
		sb.append(" WHERE section_id = ?");
		sb.append(" LIMIT 1");
		String sql = sb.toString();
		return database.rawQuery(sql, toStringArray(args));
	}

	public Cursor fetchSectionByParentId(String parentId) {
		List<String> args = new ArrayList<String>();
		args.add(parentId);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT section_id, name, type, subtype");
		sb.append(" FROM sections");
		sb.append(" WHERE parent_id = ?");
		sb.append(" ORDER BY name");
		String sql = sb.toString();
		return database.rawQuery(sql, toStringArray(args));
	}

	public Cursor fetchSectionByParentIdAndName(String parentId, String name) {
		List<String> args = new ArrayList<String>();
		args.add(parentId);
		args.add(name);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT section_id, name, type, subtype");
		sb.append(" FROM sections");
		sb.append(" WHERE parent_id = ?");
		sb.append("  AND name = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, toStringArray(args));
	}

	public Cursor fetchFullSection(String sectionId) {
		List<String> args = new ArrayList<String>();
		args.add(sectionId);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT node.section_id, node.lft, node.rgt, node.parent_id, node.type,");
		sb.append("  node.subtype, node.name, node.abbrev, node.source, node.description, node.body");
		sb.append(" FROM sections AS node, sections AS parent");
		sb.append(" WHERE node.lft BETWEEN parent.lft AND parent.rgt");
		sb.append("  AND parent.section_id = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, toStringArray(args));
	}

	public Cursor getAbilityTypes(String sectionId) {
		List<String> args = new ArrayList<String>();
		args.add(sectionId);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ability_type");
		sb.append(" FROM ability_types");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, toStringArray(args));
	}

	public Cursor getAfflictionDetails(String sectionId) {
		List<String> args = new ArrayList<String>();
		args.add(sectionId);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT contracted, save, onset, frequency, effect, initial_effect, ");
		sb.append("  secondary_effect,cure");
		sb.append(" FROM affliction_details");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, toStringArray(args));
	}

	public Cursor getAnimalCompanionDetails(String sectionId) {
		List<String> args = new ArrayList<String>();
		args.add(sectionId);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ac, attack, ability_scores, special_qualities, special_attacks, ");
		sb.append("  size, speed, level");
		sb.append(" FROM animal_companion_details");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, toStringArray(args));
	}

	public Cursor getItemDetails(String sectionId) {
		List<String> args = new ArrayList<String>();
		args.add(sectionId);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT aura, slot, cl, price, weight, requirements, skill, ");
		sb.append("  cr_increase, cost");
		sb.append(" FROM item_details");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, toStringArray(args));
	}

	public Cursor getSettlementDetails(String sectionId) {
		List<String> args = new ArrayList<String>();
		args.add(sectionId);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT alignment, settlement_type, size, corruption, crime, economy, law, ");
		sb.append("  lore, society, qualities, danger, disadvantages, government, ");
		sb.append("  population, base_value, purchase_limit, spellcasting, ");
		sb.append("  minor_items, medium_items, major_items");
		sb.append(" FROM settlement_details");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, toStringArray(args));
	}

	public Cursor getTrapDetails(String sectionId) {
		List<String> args = new ArrayList<String>();
		args.add(sectionId);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT cr, trap_type, perception, disable_device, duration, effect, trigger, reset");
		sb.append(" FROM trap_details");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, toStringArray(args));
	}

	public Cursor getVehicleDetails(String sectionId) {
		List<String> args = new ArrayList<String>();
		args.add(sectionId);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT size, vehicle_type, squares, cost, ac, hardness, hp, base_save, ");
		sb.append("  maximum_speed, acceleration, cmb, cmd, ramming_damage, propulsion, ");
		sb.append("  driving_check, forward_facing, driving_device, driving_space, decks, ");
		sb.append("  deck, weapons, crew, passengers");
		sb.append(" FROM vehicle_details");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, toStringArray(args));
	}

	public Cursor autocomplete(String constraint) {
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT section_id as _id,");
		sb.append("  search_name AS " + SearchManager.SUGGEST_COLUMN_TEXT_1 + ",");
		sb.append("  search_name AS " + SearchManager.SUGGEST_COLUMN_QUERY);
		sb.append(" FROM section_index");
		if(constraint != null) {
			sb.append(" WHERE search_name like ?");
			args.add('%' + constraint + '%');
		}
		sb.append(" GROUP BY search_name");
		sb.append(" ORDER BY search_name");
		String sql = sb.toString();
		return database.rawQuery(sql, toStringArray(args));
	}
	
	public Integer countSearchArticles(String constraint) {
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT count(*)");
		sb.append(" FROM section_index");
		if(constraint != null) {
			sb.append(" WHERE search_name like ?");
			args.add('%' + constraint + '%');
		}
		String sql = sb.toString();
		Cursor c = database.rawQuery(sql, toStringArray(args));
		c.moveToFirst();
		return c.getInt(0);
	}

	public Cursor getSingleSearchArticle(String constraint) {
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT s.*");
		sb.append(" FROM sections s");
		sb.append("  INNER JOIN section_index i");
		sb.append("   ON i.section_id = s.section_id");
		sb.append(" WHERE i.search_name like ?");
		sb.append(" LIMIT 1");
		args.add('%' + constraint + '%');
		String sql = sb.toString();
		return database.rawQuery(sql, toStringArray(args));
	}
	
	public Cursor search(String constraint) {
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT s.section_id, s.name, s.type, s.subtype, p.name");
		sb.append(" FROM sections s");
		sb.append("  INNER JOIN sections p");
		sb.append("   ON s.parent_id = p.section_id");
		sb.append("  INNER JOIN section_index i");
		sb.append("   ON i.section_id = s.section_id");
		if(constraint != null) {
			sb.append(" WHERE i.search_name like ?");
			args.add('%' + constraint + '%');
		}
		sb.append(" ORDER BY i.search_name, s.section_id");
		String sql = sb.toString();
		return database.rawQuery(sql, toStringArray(args));
	}
}
