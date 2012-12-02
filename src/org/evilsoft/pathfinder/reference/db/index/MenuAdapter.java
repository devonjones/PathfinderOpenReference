package org.evilsoft.pathfinder.reference.db.index;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.MenuItem;
import org.evilsoft.pathfinder.reference.db.BaseDbHelper;
import org.evilsoft.pathfinder.reference.preference.FilterPreferenceManager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MenuAdapter {
	public SQLiteDatabase database;

	public MenuAdapter(SQLiteDatabase database) {
		this.database = database;
	}

	public Cursor fetchMenu() {
		List<String> args = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT menu_id, parent_menu_id, name, NULL AS parent_name, type, subtype, url, db, grouping, priority");
		sb.append(" FROM menu");
		sb.append(" WHERE parent_menu_id IS NULL");
		sb.append(FilterPreferenceManager.getSourceFilter(args, "AND"));
		sb.append(" ORDER BY priority, name");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public Cursor fetchMenu(String parentMenuId) {
		List<String> args = new ArrayList<String>();
		args.add(parentMenuId);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT m.menu_id, m.parent_menu_id, m.name, p.name AS parent_name,");
		sb.append("  m.type, m.subtype, m.url, m.db, m.grouping, m.priority");
		sb.append(" FROM menu m");
		sb.append("  INNER JOIN menu p");
		sb.append("   ON m.parent_menu_id = p.menu_id");
		sb.append(" WHERE m.parent_menu_id = ?");
		sb.append(" ORDER BY m.priority, m.name");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}
	
	public static class MenuUtils {
		public static MenuItem genMenuItem(Cursor cursor) {
			MenuItem item = new MenuItem();
			item.setId(getMenuId(cursor));
			item.setName(getName(cursor));
			StringBuffer sb = new StringBuffer();
			sb.append("pfsrd://Menu/");
			String parentName = getParentName(cursor);
			if(parentName == null) {
				parentName = item.getName();
			}
			sb.append(parentName);
			sb.append("/");
			String type = getType(cursor);
			if(type == null) {
				type = "*";
			}
			sb.append(type);
			String subtype = getSubtype(cursor);
			if(subtype != null) {
				sb.append("/");
				sb.append(subtype);
			}
			item.setUrl(sb.toString());
			return item;
		}

		public static Integer getMenuId(Cursor cursor) {
			return cursor.getInt(0);
		}

		public static Integer getParentMenuId(Cursor cursor) {
			return cursor.getInt(1);
		}

		public static String getName(Cursor cursor) {
			return cursor.getString(2);
		}

		public static String getParentName(Cursor cursor) {
			return cursor.getString(3);
		}

		public static String getType(Cursor cursor) {
			return cursor.getString(4);
		}

		public static String getSubtype(Cursor cursor) {
			return cursor.getString(5);
		}

		public static String getListUrl(Cursor cursor) {
			return cursor.getString(6);
		}

		public static String getDb(Cursor cursor) {
			return cursor.getString(7);
		}

		public static String getGrouping(Cursor cursor) {
			return cursor.getString(8);
		}

		public static Integer getPriority(Cursor cursor) {
			return cursor.getInt(9);
		}
	}
}
