package org.evilsoft.pathfinder.reference.render;

import org.evilsoft.pathfinder.reference.db.book.AnimalCompanionAdapter;
import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;

import android.database.Cursor;

public class AnimalCompanionRenderer extends StatBlockRenderer {
	private BookDbAdapter bookDbAdapter;

	public AnimalCompanionRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	@Override
	public String renderTitle() {
		Cursor cursor = bookDbAdapter.getAnimalCompanionAdapter().getAnimalCompanionDetails(sectionId);
		try {
			StringBuffer sb = new StringBuffer();
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				String level = AnimalCompanionAdapter.AnimalCompanionUtils.getLevel(cursor);
				if (level != null) {
					sb.append(renderStatBlockBreaker(level));
				} else {
					sb.append(renderStatBlockTitle(name, newUri, top));
					sb.append(renderStatBlockBreaker("Starting Statistics"));
				}
			}
			return sb.toString();
		} finally {
			cursor.close();
		}
	}

	@Override
	public String renderDetails() {
		Cursor cursor = bookDbAdapter.getAnimalCompanionAdapter().getAnimalCompanionDetails(sectionId);
		try {
			StringBuffer sb = new StringBuffer();
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				sb.append(addField("Size", AnimalCompanionAdapter.AnimalCompanionUtils.getSize(cursor), false));
				sb.append(addField("Speed", AnimalCompanionAdapter.AnimalCompanionUtils.getSpeed(cursor)));
				sb.append(addField("AC", AnimalCompanionAdapter.AnimalCompanionUtils.getAc(cursor)));
				sb.append(addField("Attack", AnimalCompanionAdapter.AnimalCompanionUtils.getAttack(cursor)));
				sb.append(addField("Ability Scores", AnimalCompanionAdapter.AnimalCompanionUtils.getAbilityScores(cursor)));
				sb.append(addField("Special Qualities", AnimalCompanionAdapter.AnimalCompanionUtils.getSpecialQualities(cursor)));
				sb.append(addField("Special Attacks", AnimalCompanionAdapter.AnimalCompanionUtils.getSpecialAttacks(cursor)));
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
