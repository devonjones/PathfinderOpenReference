package org.evilsoft.pathfinder.reference.render;

import org.evilsoft.pathfinder.reference.db.psrd.SkillAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;

import android.database.Cursor;

public class SkillRenderer extends Renderer {
	private SkillAdapter skillAdapter;
	private PsrdDbAdapter dbAdapter;

	public SkillRenderer(PsrdDbAdapter dbAdapter) {
		this.dbAdapter = dbAdapter;
	}

	private SkillAdapter getSkillAdapter() {
		if (this.skillAdapter == null) {
			this.skillAdapter = new SkillAdapter(this.dbAdapter);
		}
		return this.skillAdapter;
	}

	@Override
	public String renderTitle() {
		return renderTitle(name, newUri, 0, top);
	}

	@Override
	public String renderDetails() {
		StringBuffer sb = new StringBuffer();
		sb.append(renderSkillDetails(sectionId));
		sb.append("<B>Source: </B>");
		sb.append(source);
		sb.append("<BR>");
		return sb.toString();
	}

	public String renderSkillDetails(String sectionId) {
		Cursor curs = this.getSkillAdapter().fetchSkillAttr(sectionId);
		StringBuffer sb = new StringBuffer();
		boolean has_next = curs.moveToFirst();
		if (has_next) {
			sb.append("<H2>(");
			sb.append(curs.getString(0));
			boolean armorCheckPenalty = (curs.getInt(1) != 0);
			if (armorCheckPenalty) {
				sb.append("; Armor Check Penalty");
			}
			boolean trainedOnly = (curs.getInt(2) != 0);
			if (trainedOnly) {
				sb.append("; Trained Only");
			}
			sb.append(")</H2>\n");
		}
		return sb.toString();
	}
}
