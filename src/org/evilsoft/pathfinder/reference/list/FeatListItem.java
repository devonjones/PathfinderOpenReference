package org.evilsoft.pathfinder.reference.list;

public class FeatListItem {
	private int section_id;
	private String name;
	private String description;
	private String prereqs;
	private String featTypes;

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

	public String getFeatTypes() {
		return featTypes;
	}

	public void setFeatTypes(String featTypes) {
		this.featTypes = featTypes;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPrereqs() {
		return prereqs;
	}

	public void setPrereqs(String prereqs) {
		this.prereqs = prereqs;
	}

	public String toString() {
		return name;
	}
}
