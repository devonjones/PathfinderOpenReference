package org.evilsoft.pathfinder.reference.render.html;

import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.book.KingdomResourceAdapter;
import org.evilsoft.pathfinder.reference.db.book.ResourceAdapter;

import android.database.Cursor;

public class KingdomResourceRenderer extends StatBlockRenderer {
	private BookDbAdapter bookDbAdapter;

	public KingdomResourceRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	@Override
	public String renderTitle() {
		Cursor cursor = bookDbAdapter.getKingdomResourceAdapter()
				.getKingdomResourceDetails(sectionId);
		try {
			boolean has_next = cursor.moveToFirst();
			String title = name;
			if (has_next) {
				String bp = KingdomResourceAdapter.KingdomResourceUtils
						.getBp(cursor);
				String append = "";
				if (bp != null) {
					append = "BP " + bp;
				}
				String lot = KingdomResourceAdapter.KingdomResourceUtils
						.getLot(cursor);
				if (lot != null) {
					if (append.length() > 0) {
						append = append + ", ";
					}
					append = append + lot;
					if (lot.equals("1")) {
						append = append + " Lot";
					} else {
						append = append + " Lots";
					}
				}
				if (append.length() > 0) {
					title = title + " (" + append + ")";
				}
			}
			return renderStatBlockTitle(title, newUri, top);
		} finally {
			cursor.close();
		}
	}

	@Override
	public String renderDetails() {
		Cursor cursor = bookDbAdapter.getKingdomResourceAdapter()
				.getKingdomResourceDetails(sectionId);
		try {
			StringBuffer sb = new StringBuffer();
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				if (top) {
					String bp = KingdomResourceAdapter.KingdomResourceUtils
							.getBp(cursor);
					String lot = KingdomResourceAdapter.KingdomResourceUtils
							.getLot(cursor);
					sb.append("<b>BP ");
					sb.append(bp);
					sb.append(", ");
					sb.append(lot);
					sb.append(" Lot");
					if (!lot.equals("1")) {
						sb.append("s");
					}
					sb.append("</b><br>\n");
				}
				sb.append(addField("Kingdom",
						KingdomResourceAdapter.KingdomResourceUtils
								.getKingdom(cursor)));
				sb.append(addField("Discount",
						KingdomResourceAdapter.KingdomResourceUtils
								.getDiscount(cursor)));
				sb.append(addField("Limit",
						KingdomResourceAdapter.KingdomResourceUtils
								.getLimit(cursor)));
				sb.append(addField("Upgrades To",
						ResourceAdapter.ResourceUtils.getUpgradeTo(cursor),
						false));
				sb.append(addField("Upgrades From",
						ResourceAdapter.ResourceUtils.getUpgradeFrom(cursor)));
				sb.append(addField("Special",
						KingdomResourceAdapter.KingdomResourceUtils
								.getSpecial(cursor)));
				sb.append(addField("Magic Items",
						KingdomResourceAdapter.KingdomResourceUtils
								.getMagicItems(cursor)));
				sb.append(addField("Settlement",
						KingdomResourceAdapter.KingdomResourceUtils
								.getSettlement(cursor)));
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
