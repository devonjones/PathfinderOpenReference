package org.evilsoft.pathfinder.reference.render;

import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;

import android.database.Cursor;

public class VehicleRenderer extends StatBlockRenderer {
	private PsrdDbAdapter dbAdapter;

	public VehicleRenderer(PsrdDbAdapter dbAdapter) {
		this.dbAdapter = dbAdapter;
	}

	@Override
	public String renderTitle() {
		Cursor curs = dbAdapter.getTrapDetails(sectionId);
		try {
			boolean has_next = curs.moveToFirst();
			String title = name;
			if (has_next) {
				String cr = curs.getString(0);
				if (cr != null) {
					title = title + " (CR " + cr + ")";
				}
			}
			return renderStatBlockTitle(title, newUri, top);
		} finally {
			curs.close();
		}
	}
	
	@Override
	public String renderDetails() {
		Cursor curs = dbAdapter.getVehicleDetails(sectionId);
		//0: size, 1: vehicle_type, 2: squares, 3: cost, 4: ac, 5: hardness, 6: hp, 7: base_save,
		//8: maximum_speed, 9: acceleration, 10: cmb, 11: cmd, 12: ramming_damage, 13: propulsion,
		//14: driving_check, 15: forward_facing, 16: driving_device, 17: driving_space, 18: decks,
		//19: deck, 20: weapons, 21: crew, 22: passengers
		try {
			StringBuffer sb = new StringBuffer();
			boolean has_next = curs.moveToFirst();
			if (has_next) {
				String size = curs.getString(0);
				if (size != null) {
					sb.append(size);
					sb.append(" ");
				}
				String type = curs.getString(1);
				if (type != null) {
					sb.append(type);
					sb.append(" ");
				}
				sb.append("vehicle<br>\n");
				sb.append(addField("Squares", curs.getString(2), false));
				sb.append(addField("Cost", curs.getString(3), false));
				sb.append(renderStatBlockBreaker("Defense"));
				sb.append(addField("AC", curs.getString(4), false));
				sb.append(addField("Hardness", curs.getString(5)));
				sb.append(addField("hp", curs.getString(6)));
				sb.append(addField("Base Save", curs.getString(7)));
				sb.append(renderStatBlockBreaker("Offense"));
				sb.append(addField("Maximum Speed", curs.getString(8), false));
				sb.append(addField("Acceleration", curs.getString(9)));
				sb.append(addField("CMB", curs.getString(10), false));
				sb.append(addField("CMD", curs.getString(11)));
				sb.append(addField("Ramming Damage", curs.getString(12)));
				sb.append(renderStatBlockBreaker("Description"));
				sb.append(addField("Propulsion", curs.getString(13)));
				sb.append(addField("Driving Check", curs.getString(14)));
				sb.append(addField("Forward Facing", curs.getString(15)));
				sb.append(addField("Driving Device", curs.getString(16)));
				sb.append(addField("Driving Space", curs.getString(17)));
				sb.append(addField("Crew", curs.getString(21)));
				sb.append(addField("Passengers", curs.getString(22)));
				sb.append(addField("Decks", curs.getString(18)));
				sb.append(addField("Deck", curs.getString(19)));
				sb.append(addField("Weapons", curs.getString(20)));
			}
			return sb.toString();
		} finally {
			curs.close();
		}
	}
}
