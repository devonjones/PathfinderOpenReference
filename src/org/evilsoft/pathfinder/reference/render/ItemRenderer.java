package org.evilsoft.pathfinder.reference.render;

import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;

import android.database.Cursor;

public class ItemRenderer extends StatBlockRenderer {
	private PsrdDbAdapter dbAdapter;

	public ItemRenderer(PsrdDbAdapter dbAdapter) {
		this.dbAdapter = dbAdapter;
	}

	@Override
	public String renderTitle() {
		return renderStatBlockTitle(name, newUri, top);
	}

	@Override
	public String renderDetails() {
		Cursor curs = dbAdapter.getItemDetails(sectionId);
		// 0: aura, 1: slot, 2: cl, 3: price, 4: weight, 5: requirements,
		// 6: skill, 7: cr_increase, 8: cost
		try {
			StringBuffer sb = new StringBuffer();
			boolean has_next = curs.moveToFirst();
			if (has_next) {
				sb.append(addField("Aura", curs.getString(0), false));
				sb.append(addField("CL", curs.getString(2)));
				sb.append(addField("Slot", curs.getString(1), false));
				sb.append(addField("Price", curs.getString(3), false));
				sb.append(addField("Weight", curs.getString(4)));
				// TODO: Construction and Description reversed due to child
				// rendering
				sb.append(renderStatBlockBreaker("Construction"));
				sb.append(addField("Requirements", curs.getString(5), false));
				sb.append(addField("Skill", curs.getString(6), false));
				sb.append(addField("CR Increase", curs.getString(7), false));
				sb.append(addField("Cost", curs.getString(8)));
				sb.append(renderStatBlockBreaker("Description"));
				this.suppressNextTitle = true;
			}
			return sb.toString();
		} finally {
			curs.close();
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
