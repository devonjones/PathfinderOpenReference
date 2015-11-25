package org.evilsoft.pathfinder.reference.list;

public class SearchListItem extends BaseListItem {
	private String sectionType;
	private String sectionSubtype;
	private String parent;

	public String getSectionType() {
		return sectionType;
	}

	public void setSectionType(String sectionType) {
		this.sectionType = sectionType;
	}

	public String getSectionSubtype() {
		return sectionSubtype;
	}

	public void setSectionSubtype(String sectionSubtype) {
		this.sectionSubtype = sectionSubtype;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}
}
