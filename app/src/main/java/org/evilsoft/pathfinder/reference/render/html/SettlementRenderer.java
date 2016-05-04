package org.evilsoft.pathfinder.reference.render.html;

import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.book.SettlementAdapter;

import android.database.Cursor;

public class SettlementRenderer extends StatBlockRenderer {
	private BookDbAdapter bookDbAdapter;

	public SettlementRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	@Override
	public String renderTitle() {
		return renderStatBlockTitle(name, newUri, top);
	}

	@Override
	public String renderDetails() {
		Cursor cursor = bookDbAdapter.getSettlementAdapter().getSettlementDetails(sectionId);
		try {
			StringBuilder sb = new StringBuilder();
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				String align = SettlementAdapter.SettlementUtils.getAlignment(cursor);
				if (align != null) {
					sb.append(align);
					sb.append(" ");
				}
				String size = SettlementAdapter.SettlementUtils.getSize(cursor);
				if (size != null) {
					sb.append(size);
					sb.append(" ");
				}
				String type = SettlementAdapter.SettlementUtils.getSettlementType(cursor);
				if (type != null) {
					sb.append(type);
				}
				sb.append("<br>\n");
				sb.append(addField("Corruption", SettlementAdapter.SettlementUtils.getCorruption(cursor), false));
				sb.append(addField("Crime", SettlementAdapter.SettlementUtils.getCrime(cursor), false));
				sb.append(addField("Economy", SettlementAdapter.SettlementUtils.getEconomy(cursor), false));
				sb.append(addField("Law", SettlementAdapter.SettlementUtils.getLaw(cursor), false));
				sb.append(addField("Lore", SettlementAdapter.SettlementUtils.getLore(cursor), false));
				sb.append(addField("Society", SettlementAdapter.SettlementUtils.getSociety(cursor)));
				sb.append(addField("Qualities", SettlementAdapter.SettlementUtils.getQualities(cursor)));
				sb.append(addField("Danger", SettlementAdapter.SettlementUtils.getDanger(cursor), false));
				sb.append(addField("Disadvantages", SettlementAdapter.SettlementUtils.getDisadvantages(cursor)));
				// TODO: Marketplace and Demographics reversed due to child
				// rendering
				sb.append(renderStatBlockBreaker("Marketplace"));
				sb.append(addField("Base Value", SettlementAdapter.SettlementUtils.getBaseValue(cursor), false));
				sb.append(addField("Purchase Limit", SettlementAdapter.SettlementUtils.getPurchaseLimit(cursor), false));
				sb.append(addField("Spellcasting", SettlementAdapter.SettlementUtils.getSpellcasting(cursor)));
				sb.append(addField("Minor Items", SettlementAdapter.SettlementUtils.getMinorItems(cursor), false));
				sb.append(addField("Medium Items", SettlementAdapter.SettlementUtils.getMediumItems(cursor), false));
				sb.append(addField("Major Items", SettlementAdapter.SettlementUtils.getMajorItems(cursor)));
				sb.append(renderStatBlockBreaker("Demographics"));
				sb.append(addField("Government", SettlementAdapter.SettlementUtils.getGovernment(cursor)));
				sb.append(addField("Population", SettlementAdapter.SettlementUtils.getPopulation(cursor)));
				this.suppressNextTitle = true;
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
