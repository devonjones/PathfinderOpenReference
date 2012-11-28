package org.evilsoft.pathfinder.reference.db.book;

import java.util.ArrayList;
import java.util.List;

import org.evilsoft.pathfinder.reference.db.BaseDbHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class VehicleAdapter {
	public SQLiteDatabase database;
	public String dbName;

	public VehicleAdapter(SQLiteDatabase database, String dbName) {
		this.database = database;
		this.dbName = dbName;
	}

	public Cursor getVehicleDetails(Integer sectionId) {
		List<String> args = new ArrayList<String>();
		args.add(sectionId.toString());
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT size, vehicle_type, squares, cost, ac, hardness, hp, base_save,");
		sb.append("  maximum_speed, acceleration, cmb, cmd, ramming_damage, propulsion,");
		sb.append("  driving_check, forward_facing, driving_device, driving_space, decks,");
		sb.append("  deck, weapons, crew, passengers");
		sb.append(" FROM vehicle_details");
		sb.append(" WHERE section_id = ?");
		String sql = sb.toString();
		return database.rawQuery(sql, BaseDbHelper.toStringArray(args));
	}

	public static class VehicleUtils {
		public static String getSize(Cursor cursor) {
			return cursor.getString(0);
		}
		public static String getVehicleType(Cursor cursor) {
			return cursor.getString(1);
		}
		public static String getSquares(Cursor cursor) {
			return cursor.getString(2);
		}
		public static String getCost(Cursor cursor) {
			return cursor.getString(3);
		}
		public static String getAc(Cursor cursor) {
			return cursor.getString(4);
		}
		public static String getHardness(Cursor cursor) {
			return cursor.getString(5);
		}
		public static String getHp(Cursor cursor) {
			return cursor.getString(6);
		}
		public static String getBaseSave(Cursor cursor) {
			return cursor.getString(7);
		}
		public static String getMaximumSpeed(Cursor cursor) {
			return cursor.getString(8);
		}
		public static String getAcceleration(Cursor cursor) {
			return cursor.getString(9);
		}
		public static String getCmb(Cursor cursor) {
			return cursor.getString(10);
		}
		public static String getCmd(Cursor cursor) {
			return cursor.getString(11);
		}
		public static String getRammingDamage(Cursor cursor) {
			return cursor.getString(12);
		}
		public static String getPropulsion(Cursor cursor) {
			return cursor.getString(13);
		}
		public static String getDrivingCheck(Cursor cursor) {
			return cursor.getString(14);
		}
		public static String getForwardFacing(Cursor cursor) {
			return cursor.getString(15);
		}
		public static String getDrivingDevice(Cursor cursor) {
			return cursor.getString(16);
		}
		public static String getDrivingSpace(Cursor cursor) {
			return cursor.getString(17);
		}
		public static String getDecks(Cursor cursor) {
			return cursor.getString(18);
		}
		public static String getDeck(Cursor cursor) {
			return cursor.getString(19);
		}
		public static String getWeapons(Cursor cursor) {
			return cursor.getString(20);
		}
		public static String getCrew(Cursor cursor) {
			return cursor.getString(21);
		}
		public static String getPassengers(Cursor cursor) {
			return cursor.getString(22);
		}
	}
}
