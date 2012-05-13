package org.evilsoft.pathfinder.reference.render;

import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;

import android.database.Cursor;

public class SettlementRenderer extends StatBlockRenderer {
	private PsrdDbAdapter dbAdapter;

	public SettlementRenderer(PsrdDbAdapter dbAdapter) {
		this.dbAdapter = dbAdapter;
	}

	@Override
	public String renderTitle() {
		return renderStatBlockTitle(name, newUri, top);
	}
	
	@Override
	public String renderDetails() {
		StringBuffer sb = new StringBuffer();
		Cursor curs = dbAdapter.getSettlementDetails(sectionId);
		//0: alignment, 1: settlement_type, 2: size, 3: corruption, 4: crime, 5: economy, 6: law,
		//7: lore, 8: society, 9: qualities, 10: danger, 11: disadvantages, 12: government,
		//13: population, 14: base_value, 15: purchase_limit, 16: spellcasting,
		//17: minor_items, 18: medium_items, 19: major_items
		boolean has_next = curs.moveToFirst();
		if (has_next) {
			String align = curs.getString(0);
			if (align != null) {
				sb.append(align);
				sb.append(" ");
			}
			String size = curs.getString(2);
			if (size != null) {
				sb.append(size);
				sb.append(" ");
			}
			String type = curs.getString(1);
			if (type != null) {
				sb.append(type);
			}
			sb.append("<br>\n");
			sb.append(addField("Corruption", curs.getString(3), false));
			sb.append(addField("Crime", curs.getString(4), false));
			sb.append(addField("Economy", curs.getString(5), false));
			sb.append(addField("Law", curs.getString(6), false));
			sb.append(addField("Lore", curs.getString(7), false));
			sb.append(addField("Society", curs.getString(8)));
			sb.append(addField("Qualities", curs.getString(9)));
			sb.append(addField("Danger", curs.getString(10), false));
			sb.append(addField("Disadvantages", curs.getString(11)));
			//TODO: Marketplace and Demographics reversed due to child rendering
			sb.append(renderStatBlockBreaker("Marketplace"));
			sb.append(addField("Base Value", curs.getString(14), false));
			sb.append(addField("Purchase Limit", curs.getString(15), false));
			sb.append(addField("Spellcasting", curs.getString(16)));
			sb.append(addField("Minor Items", curs.getString(17), false));
			sb.append(addField("Medium Items", curs.getString(18), false));
			sb.append(addField("Major Items", curs.getString(19)));
			sb.append(renderStatBlockBreaker("Demographics"));
			sb.append(addField("Government", curs.getString(12)));
			sb.append(addField("Population", curs.getString(13)));
			this.suppressNextTitle = true;
		}
		return sb.toString();
	}
}
