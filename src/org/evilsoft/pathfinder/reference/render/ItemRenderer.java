package org.evilsoft.pathfinder.reference.render;

import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.book.ItemAdapter;

import android.annotation.SuppressLint;
import android.database.Cursor;

public class ItemRenderer extends StatBlockRenderer {
	private BookDbAdapter bookDbAdapter;

	public ItemRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	@Override
	public String renderTitle() {
		return renderStatBlockTitle(name, newUri, top);
	}

	@Override
	public String renderDetails() {
		StringBuffer sb = new StringBuffer();
		sb.append(renderItemDetails());
		return sb.toString();
	}

	public String renderItemDetails() {
		Cursor cursor = bookDbAdapter.getItemAdapter()
				.getItemDetails(sectionId);
		try {
			StringBuffer sb = new StringBuffer();
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				sb.append(addField("Aura",
						ItemAdapter.ItemUtils.getAura(cursor), false));
				sb.append(addField("CL", ItemAdapter.ItemUtils.getCl(cursor)));
				sb.append(addField("Slot",
						ItemAdapter.ItemUtils.getSlot(cursor), false));
				sb.append(addField("Price",
						ItemAdapter.ItemUtils.getPrice(cursor), false));
				sb.append(addField("Weight",
						ItemAdapter.ItemUtils.getWeight(cursor)));
				// TODO: Construction and Description reversed due to child
				// rendering
				sb.append(renderItemMisc());
				sb.append(renderStatBlockBreaker("Description"));
				// this.suppressNextTitle = true;
			}
			return sb.toString();
		} finally {
			cursor.close();
		}
	}

	@SuppressLint("DefaultLocale")
	public String renderItemMisc() {
		Cursor cursor = bookDbAdapter.getItemAdapter().getItemMisc(sectionId);
		try {
			StringBuffer sb = new StringBuffer();
			StringBuffer titleSb = new StringBuffer();
			boolean has_next = cursor.moveToFirst();
			String lastSection = "";
			while (has_next) {
				String currentSection = ItemAdapter.ItemMiscUtils
						.getSubsection(cursor);
				if (currentSection.toLowerCase().equals(name.toLowerCase())) {
					titleSb.append(addField(
							ItemAdapter.ItemMiscUtils.getField(cursor),
							ItemAdapter.ItemMiscUtils.getValue(cursor), false));
				} else {
					if (!lastSection.equals(currentSection)) {
						sb.append(renderStatBlockBreaker(currentSection));
						lastSection = currentSection;
					}
					sb.append(addField(
							ItemAdapter.ItemMiscUtils.getField(cursor),
							ItemAdapter.ItemMiscUtils.getValue(cursor), false));
				}
				has_next = cursor.moveToNext();
			}
			return titleSb.toString() + sb.toString();
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
