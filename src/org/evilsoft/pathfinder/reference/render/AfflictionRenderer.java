package org.evilsoft.pathfinder.reference.render;

import org.evilsoft.pathfinder.reference.db.book.AfflictionAdapter;
import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;

import android.database.Cursor;

public class AfflictionRenderer extends StatBlockRenderer {
	private BookDbAdapter bookDbAdapter;

	public AfflictionRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	@Override
	public String renderTitle() {
		return renderStatBlockTitle(name, newUri, top);
	}

	@Override
	public String renderDetails() {
		Cursor cursor = bookDbAdapter.getAfflictionAdapter().getAfflictionDetails(sectionId);
		// 0:contracted, 1:save, 2:onset, 3:frequency, 4:effect,
		// 5:initial_effect, 6:secondary_effect, 7:cure
		try {
			StringBuffer sb = new StringBuffer();
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				String contracted = AfflictionAdapter.AfflictionUtils.getContracted(cursor);
				if (contracted != null) {
					contracted = subtype + ", " + contracted;
				} else {
					contracted = subtype;
				}
				sb.append(addField("Type", contracted, false));
				sb.append(addField("Save", AfflictionAdapter.AfflictionUtils.getSave(cursor)));
				sb.append(addField("Onset", AfflictionAdapter.AfflictionUtils.getOnset(cursor), false));
				sb.append(addField("Frequency", AfflictionAdapter.AfflictionUtils.getFrequency(cursor)));
				sb.append(addField("Effect", AfflictionAdapter.AfflictionUtils.getEffect(cursor)));
				sb.append(addField("Initial Effect", AfflictionAdapter.AfflictionUtils.getInitialEffect(cursor)));
				sb.append(addField("Secondary Effect", AfflictionAdapter.AfflictionUtils.getSecondaryEffect(cursor)));
				sb.append(addField("Cure", AfflictionAdapter.AfflictionUtils.getCure(cursor)));
				sb.append(addField("Cost", AfflictionAdapter.AfflictionUtils.getCost(cursor)));
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
