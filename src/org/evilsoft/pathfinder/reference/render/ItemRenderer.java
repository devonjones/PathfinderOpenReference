package org.evilsoft.pathfinder.reference.render;

import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.book.ItemAdapter;

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
		sb.append(renderItemMisc());
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
				sb.append(renderStatBlockBreaker("Construction"));
				sb.append(addField("Requirements",
						ItemAdapter.ItemUtils.getRequirements(cursor), false));
				sb.append(addField("Skill",
						ItemAdapter.ItemUtils.getSkill(cursor), false));
				sb.append(addField("CR Increase",
						ItemAdapter.ItemUtils.getCrIncrease(cursor), false));
				sb.append(addField("Cost",
						ItemAdapter.ItemUtils.getCost(cursor)));
				sb.append(renderStatBlockBreaker("Description"));
				this.suppressNextTitle = true;
			}
			return sb.toString();
		} finally {
			cursor.close();
		}
	}

	public String renderItemMisc() {
		Cursor cursor = bookDbAdapter.getItemAdapter().getItemMisc(sectionId);
		try {
			StringBuffer sb = new StringBuffer();
			boolean has_next = cursor.moveToFirst();
			String lastSection = "";
			if (has_next) {
				String currentSection = ItemAdapter.ItemMiscUtils
						.getSubsection(cursor);
				if (!lastSection.equals(currentSection)) {
					sb.append(renderStatBlockBreaker("currentSection"));
					lastSection = currentSection;
				}
				sb.append(addField(ItemAdapter.ItemMiscUtils.getField(cursor),
						ItemAdapter.ItemMiscUtils.getValue(cursor), false));
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
