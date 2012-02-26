package org.evilsoft.pathfinder.reference.render;

import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;

import android.database.Cursor;

public class AnimalCompanionRenderer extends StatBlockRenderer {
	private PsrdDbAdapter dbAdapter;

	public AnimalCompanionRenderer(PsrdDbAdapter dbAdapter) {
		this.dbAdapter = dbAdapter;
	}

	@Override
	public String renderTitle() {
		StringBuffer sb = new StringBuffer();
		Cursor curs = dbAdapter.getAnimalCompanionDetails(sectionId);
		// 0:ac, 1:attack, 2:ability_scores, 3:special_qualities,
		// 4:special_attacks, 5:size, 6:speed, 7:level
		boolean has_next = curs.moveToFirst();
		if (has_next) {
			String level = curs.getString(7);
			if (level != null) {
				sb.append(renderStatBlockBreaker(level));
			} else {
				sb.append(renderStatBlockTitle(name, newUri, top));
				sb.append(renderStatBlockBreaker("Starting Statistics"));
			}
		}
		return sb.toString();
	}

	@Override
	public String renderDetails() {
		StringBuffer sb = new StringBuffer();
		Cursor curs = dbAdapter.getAnimalCompanionDetails(sectionId);
		// 0:ac, 1:attack, 2:ability_scores, 3:special_qualities,
		// 4:special_attacks, 5:size, 6:speed, 7:level
		boolean has_next = curs.moveToFirst();
		if (has_next) {
			sb.append(addField("Size", curs.getString(5), false));
			sb.append(addField("Speed", curs.getString(6)));
			sb.append(addField("AC", curs.getString(0)));
			sb.append(addField("Attack", curs.getString(1)));
			sb.append(addField("Ability Scores", curs.getString(2)));
			sb.append(addField("Special Qualities", curs.getString(3)));
			sb.append(addField("Special Attacks", curs.getString(4)));
		}
		return sb.toString();
	}
}
