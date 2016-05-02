package org.evilsoft.pathfinder.reference.render.html;

import android.database.Cursor;

import org.evilsoft.pathfinder.reference.db.book.TalentAdapter;
import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;

public class TalentRenderer extends StatBlockRenderer {
	private BookDbAdapter bookDbAdapter;

	public TalentRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	@Override
	public String renderTitle() {
		return renderStatBlockTitle(name, newUri, top);
	}

	@Override
	public String renderDetails() {
		Cursor cursor = bookDbAdapter.getTalentAdapter().getTalentDetails(sectionId);
		try {
			StringBuilder sb = new StringBuilder();
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				sb.append(addField("Element", TalentAdapter.TalentUtils.getElement(cursor), false));
				sb.append(addField("Type", TalentAdapter.TalentUtils.getTalentType(cursor), false));
				sb.append(addField("Level", TalentAdapter.TalentUtils.getLevel(cursor), false));
				sb.append(addField("Burn", TalentAdapter.TalentUtils.getBurn(cursor)));
				sb.append(addField("Prerequisites", TalentAdapter.TalentUtils.getPrerequisite(cursor)));
				sb.append(addField("Blast Type", TalentAdapter.TalentUtils.getBlastType(cursor), false));
				sb.append(addField("Damage", TalentAdapter.TalentUtils.getDamage(cursor)));
				sb.append(addField("Associated Blasts", TalentAdapter.TalentUtils.getAssociatedBlasts(cursor)));
				sb.append(addField("Saving Throw", TalentAdapter.TalentUtils.getSavingThrow(cursor), false));
				sb.append(addField("Spell Resistance", TalentAdapter.TalentUtils.getSpellResistance(cursor)));
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
