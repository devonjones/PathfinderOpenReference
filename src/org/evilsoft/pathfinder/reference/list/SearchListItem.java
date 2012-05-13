package org.evilsoft.pathfinder.reference.list;

public class SearchListItem {
	private int section_id;
	private String name;
	private String sectionType;
	private String sectionSubtype;
	private String parent;

	public int getSectionId() {
		return section_id;
	}

	public void setSectionId(int section_id) {
		this.section_id = section_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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
