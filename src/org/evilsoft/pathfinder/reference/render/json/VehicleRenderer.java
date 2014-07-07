package org.evilsoft.pathfinder.reference.render.json;

import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.book.VehicleAdapter;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

public class VehicleRenderer extends JsonRenderer {
	private BookDbAdapter bookDbAdapter;

	public VehicleRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	public JSONObject render(JSONObject section, Integer sectionId)
			throws JSONException {
		Cursor cursor = bookDbAdapter.getVehicleAdapter().getVehicleDetails(
				sectionId);
		try {
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				addField(section, "ac",
						VehicleAdapter.VehicleUtils.getAc(cursor));
				addField(section, "acceleration",
						VehicleAdapter.VehicleUtils.getAcceleration(cursor));
				addField(section, "base_save",
						VehicleAdapter.VehicleUtils.getBaseSave(cursor));
				addField(section, "cmb",
						VehicleAdapter.VehicleUtils.getCmb(cursor));
				addField(section, "cmd",
						VehicleAdapter.VehicleUtils.getCmd(cursor));
				addField(section, "cost",
						VehicleAdapter.VehicleUtils.getCost(cursor));
				addField(section, "crew",
						VehicleAdapter.VehicleUtils.getCrew(cursor));
				addField(section, "deck",
						VehicleAdapter.VehicleUtils.getDeck(cursor));
				addField(section, "decks",
						VehicleAdapter.VehicleUtils.getDecks(cursor));
				addField(section, "driving_check",
						VehicleAdapter.VehicleUtils.getDrivingCheck(cursor));
				addField(section, "driving_device",
						VehicleAdapter.VehicleUtils.getDrivingDevice(cursor));
				addField(section, "driving_space",
						VehicleAdapter.VehicleUtils.getDrivingSpace(cursor));
				addField(section, "forward_facing",
						VehicleAdapter.VehicleUtils.getForwardFacing(cursor));
				addField(section, "hardness",
						VehicleAdapter.VehicleUtils.getHardness(cursor));
				addField(section, "hp",
						VehicleAdapter.VehicleUtils.getHp(cursor));
				addField(section, "maximum_speed",
						VehicleAdapter.VehicleUtils.getMaximumSpeed(cursor));
				addField(section, "passengers",
						VehicleAdapter.VehicleUtils.getPassengers(cursor));
				addField(section, "propulsion",
						VehicleAdapter.VehicleUtils.getPropulsion(cursor));
				addField(section, "ramming_damage",
						VehicleAdapter.VehicleUtils.getRammingDamage(cursor));
				addField(section, "size",
						VehicleAdapter.VehicleUtils.getSize(cursor));
				addField(section, "squares",
						VehicleAdapter.VehicleUtils.getSquares(cursor));
				addField(section, "vehicle_type",
						VehicleAdapter.VehicleUtils.getVehicleType(cursor));
				addField(section, "weapons",
						VehicleAdapter.VehicleUtils.getWeapons(cursor));
			}
		} finally {
			cursor.close();
		}
		return section;
	}
}
