package org.evilsoft.pathfinder.reference.list;

public class NpcListItem extends BaseListItem {
	private String description;
	private String superRace;
	private String size;
	private String alignment;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSuperRace() {
		return superRace;
	}

	public void setSuperRace(String creatureType) {
		this.superRace = creatureType;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getAlignment() {
		return alignment;
	}

	public void setAlignment(String alignment) {
		this.alignment = alignment;
	}
}
