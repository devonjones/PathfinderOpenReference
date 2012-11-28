package org.evilsoft.pathfinder.reference.list;

public class FeatListItem extends BaseListItem {
	private String description;
	private String prereqs;
	private String featTypes;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFeatTypes() {
		return featTypes;
	}

	public void setFeatTypes(String featTypes) {
		this.featTypes = featTypes;
	}

	public String getPrereqs() {
		return prereqs;
	}

	public void setPrereqs(String prereqs) {
		this.prereqs = prereqs;
	}
}
