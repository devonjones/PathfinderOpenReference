package org.evilsoft.pathfinder.reference.render.html;

import org.evilsoft.pathfinder.reference.db.book.BookDbAdapter;
import org.evilsoft.pathfinder.reference.db.book.ResourceAdapter;

import android.database.Cursor;

public class ResourceRenderer extends StatBlockRenderer {
	private BookDbAdapter bookDbAdapter;

	public ResourceRenderer(BookDbAdapter bookDbAdapter) {
		this.bookDbAdapter = bookDbAdapter;
	}

	@Override
	public String renderTitle() {
		return renderStatBlockTitle(name, newUri, top);
	}

	@Override
	public String renderDetails() {
		if (this.subtype.equals("manager")) {
			return renderManager();
		} else if (this.subtype.equals("building")) {
			return renderBuilding();
		} else if (this.subtype.equals("organization")) {
			return renderOrganiation();
		} else {
			return renderResource();
		}
	}

	public String renderManager() {
		Cursor cursor = bookDbAdapter.getResourceAdapter().getResourceDetails(
				sectionId);
		try {
			StringBuffer sb = new StringBuffer();
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				sb.append(addField("Wage",
						ResourceAdapter.ResourceUtils.getWage(cursor)));
				sb.append(addField("Skills",
						ResourceAdapter.ResourceUtils.getSkills(cursor)));
			}
			return sb.toString();
		} finally {
			cursor.close();
		}
	}

	public String renderBuilding() {
		Cursor cursor = bookDbAdapter.getResourceAdapter().getResourceDetails(
				sectionId);
		try {
			StringBuffer sb = new StringBuffer();
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				sb.append(addField("Create",
						ResourceAdapter.ResourceUtils.getCreate(cursor)));
				sb.append(addField("Rooms",
						ResourceAdapter.ResourceUtils.getRooms(cursor)));
			}
			return sb.toString();
		} finally {
			cursor.close();
		}
	}

	public String renderOrganiation() {
		Cursor cursor = bookDbAdapter.getResourceAdapter().getResourceDetails(
				sectionId);
		try {
			StringBuffer sb = new StringBuffer();
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				sb.append(addField("Create",
						ResourceAdapter.ResourceUtils.getCreate(cursor)));
				sb.append(addField("Teams",
						ResourceAdapter.ResourceUtils.getTeams(cursor)));
			}
			return sb.toString();
		} finally {
			cursor.close();
		}
	}

	public String renderResource() {
		Cursor cursor = bookDbAdapter.getResourceAdapter().getResourceDetails(
				sectionId);
		try {
			StringBuffer sb = new StringBuffer();
			boolean has_next = cursor.moveToFirst();
			if (has_next) {
				sb.append(addField("Earnings",
						ResourceAdapter.ResourceUtils.getEarnings(cursor)));
				sb.append(addField("Benefit",
						ResourceAdapter.ResourceUtils.getBenefit(cursor)));
				sb.append(addField("Create",
						ResourceAdapter.ResourceUtils.getCreate(cursor), false));
				sb.append(addField("Time",
						ResourceAdapter.ResourceUtils.getTime(cursor)));
				sb.append(addField("Size",
						ResourceAdapter.ResourceUtils.getSize(cursor)));
				sb.append(addField("Upgrades To",
						ResourceAdapter.ResourceUtils.getUpgradeTo(cursor),
						false));
				sb.append(addField("Upgrades From",
						ResourceAdapter.ResourceUtils.getUpgradeFrom(cursor)));
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
