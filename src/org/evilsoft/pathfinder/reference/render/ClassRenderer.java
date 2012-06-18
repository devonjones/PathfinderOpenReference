package org.evilsoft.pathfinder.reference.render;

import org.evilsoft.pathfinder.reference.db.psrd.ClassAdapter;
import org.evilsoft.pathfinder.reference.db.psrd.PsrdDbAdapter;

import android.database.Cursor;

public class ClassRenderer extends Renderer {
	private ClassAdapter classAdapter;
	private PsrdDbAdapter dbAdapter;

	public ClassRenderer(PsrdDbAdapter dbAdapter) {
		this.dbAdapter = dbAdapter;
	}

	private ClassAdapter getClassAdapter() {
		if (this.classAdapter == null) {
			this.classAdapter = new ClassAdapter(this.dbAdapter);
		}
		return this.classAdapter;
	}

	@Override
	public String renderTitle() {
		return renderTitle(name, newUri, depth, top);
	}

	@Override
	public String renderFooter() {
		StringBuffer sb = new StringBuffer();
		Cursor curs = this.getClassAdapter().fetchClassDetails(sectionId);
		try {
			sb.append("<B>Source: </B>");
			sb.append(source);
			sb.append("<BR>");
			boolean has_next = curs.moveToFirst();
			if (has_next) {
				String align = curs.getString(0);
				if (align != null) {
					sb.append("<B>Alignment: </B>");
					sb.append(align);
					sb.append("<BR>\n");
				}
				String hd = curs.getString(1);
				if (hd != null) {
					sb.append("<B>Hit Die: </B>");
					sb.append(hd);
					sb.append("<BR>\n");
				}
			}
		} finally {
			curs.close();
		}
		return sb.toString();
	}

	@Override
	public String renderHeader() {
		return "";
	}

	@Override
	public String renderDetails() {
		return "";
	}
}
