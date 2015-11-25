package org.evilsoft.pathfinder.reference.render.html;

import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.book.VehicleAdapter;

import android.database.Cursor;

public class VehicleRenderer extends StatBlockRenderer {
	private BookDbAdapter bookDbAdapter;

	public VehicleRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	@Override
	public String renderTitle() {
		return renderStatBlockTitle(name, newUri, top);
	}

	@Override
	public String renderDetails() {
		Cursor cursor = bookDbAdapter.getVehicleAdapter().getVehicleDetails(sectionId);
		try {
			StringBuffer sb = new StringBuffer();
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				String size = VehicleAdapter.VehicleUtils.getSize(cursor);
				if (size != null) {
					sb.append(size);
					sb.append(" ");
				}
				String type = VehicleAdapter.VehicleUtils.getVehicleType(cursor);
				if (type != null) {
					sb.append(type);
					sb.append(" ");
				}
				sb.append("vehicle<br>\n");
				sb.append(addField("Squares", VehicleAdapter.VehicleUtils.getSquares(cursor), false));
				sb.append(addField("Cost", VehicleAdapter.VehicleUtils.getCost(cursor), false));
				sb.append(renderStatBlockBreaker("Defense"));
				sb.append(addField("AC", VehicleAdapter.VehicleUtils.getAc(cursor), false));
				sb.append(addField("Hardness", VehicleAdapter.VehicleUtils.getHardness(cursor)));
				sb.append(addField("hp", VehicleAdapter.VehicleUtils.getHp(cursor)));
				sb.append(addField("Base Save", VehicleAdapter.VehicleUtils.getBaseSave(cursor)));
				sb.append(renderStatBlockBreaker("Offense"));
				sb.append(addField("Maximum Speed", VehicleAdapter.VehicleUtils.getMaximumSpeed(cursor), false));
				sb.append(addField("Acceleration", VehicleAdapter.VehicleUtils.getAcceleration(cursor)));
				sb.append(addField("CMB", VehicleAdapter.VehicleUtils.getCmb(cursor), false));
				sb.append(addField("CMD", VehicleAdapter.VehicleUtils.getCmd(cursor)));
				sb.append(addField("Ramming Damage", VehicleAdapter.VehicleUtils.getRammingDamage(cursor)));
				sb.append(renderStatBlockBreaker("Description"));
				sb.append(addField("Propulsion", VehicleAdapter.VehicleUtils.getPropulsion(cursor)));
				sb.append(addField("Driving Check", VehicleAdapter.VehicleUtils.getDrivingCheck(cursor)));
				sb.append(addField("Forward Facing", VehicleAdapter.VehicleUtils.getForwardFacing(cursor)));
				sb.append(addField("Driving Device", VehicleAdapter.VehicleUtils.getDrivingDevice(cursor)));
				sb.append(addField("Driving Space", VehicleAdapter.VehicleUtils.getDrivingSpace(cursor)));
				sb.append(addField("Crew", VehicleAdapter.VehicleUtils.getCrew(cursor)));
				sb.append(addField("Passengers", VehicleAdapter.VehicleUtils.getPassengers(cursor)));
				sb.append(addField("Decks", VehicleAdapter.VehicleUtils.getDecks(cursor)));
				sb.append(addField("Deck", VehicleAdapter.VehicleUtils.getDeck(cursor)));
				sb.append(addField("Weapons", VehicleAdapter.VehicleUtils.getWeapons(cursor)));
			}
			return sb.toString();
		} finally {
			cursor.close();
		}
	}

	@Override
	public String renderFooter() {
		return "";
	}

	@Override
	public String renderHeader() {
		return "";
	}
}
