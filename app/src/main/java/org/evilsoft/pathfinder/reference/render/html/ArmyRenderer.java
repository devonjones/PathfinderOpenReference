package org.evilsoft.pathfinder.reference.render.html;

import org.evilsoft.pathfinder.reference.db.book.ArmyAdapter;
import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;

import android.database.Cursor;

public class ArmyRenderer extends StatBlockRenderer {
	private BookDbAdapter bookDbAdapter;

	public ArmyRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	@Override
	public String renderTitle() {
		Cursor cursor = bookDbAdapter.getArmyAdapter()
				.getArmyDetails(sectionId);
		try {
			boolean has_next = cursor.moveToFirst();
			String title = name;
			if (has_next) {
				String xp = ArmyAdapter.ArmyUtils.getXp(cursor);
				if (xp != null) {
					title = title + " (XP " + xp + ")";
				}
			}
			return renderStatBlockTitle(title, newUri, top);
		} finally {
			cursor.close();
		}
	}

	@Override
	public String renderDetails() {
		Cursor cursor = bookDbAdapter.getArmyAdapter()
				.getArmyDetails(sectionId);
		try {
			StringBuilder sb = new StringBuilder();
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				String xp = ArmyAdapter.ArmyUtils.getXp(cursor);
				if (top) {
					sb.append("<b>XP ");
					sb.append(xp);
					sb.append("</b><br>\n");
				}
				sb.append(ArmyAdapter.ArmyUtils.getAlignment(cursor));
				sb.append(" ");
				sb.append(ArmyAdapter.ArmyUtils.getSize(cursor));
				sb.append(" army of ");
				sb.append(ArmyAdapter.ArmyUtils.getCreatureType(cursor));
				sb.append("<br>\n");
				sb.append(addField("hp", ArmyAdapter.ArmyUtils.getHp(cursor),
						false));
				sb.append(addField("ACR", ArmyAdapter.ArmyUtils.getAcr(cursor)));
				sb.append(addField("DV", ArmyAdapter.ArmyUtils.getDv(cursor),
						false));
				sb.append(addField("OM", ArmyAdapter.ArmyUtils.getOm(cursor)));
				sb.append(addField("Tactics",
						ArmyAdapter.ArmyUtils.getTactics(cursor)));
				sb.append(addField("Resources",
						ArmyAdapter.ArmyUtils.getResources(cursor)));
				sb.append(addField("Special",
						ArmyAdapter.ArmyUtils.getSpecial(cursor)));
				sb.append(addField("Speed",
						ArmyAdapter.ArmyUtils.getSpeed(cursor), false));
				sb.append(addField("Consumption",
						ArmyAdapter.ArmyUtils.getConsumption(cursor)));
				sb.append(addField("Note",
						ArmyAdapter.ArmyUtils.getNote(cursor)));
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
