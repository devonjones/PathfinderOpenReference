package org.evilsoft.pathfinder.reference.list;

public class MonsterListItem {
	private int section_id;
	private String name;
	private String description;
	private String creatureType;
	private String creatureSubtype;
	private String cr;
	private String xp;
	private String size;
	private String alignment;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreatureType() {
		return creatureType;
	}

	public void setCreatureType(String creatureType) {
		this.creatureType = creatureType;
	}

	public String getCreatureSubtype() {
		return creatureSubtype;
	}

	public void setCreatureSubtype(String creatureSubtype) {
		this.creatureSubtype = creatureSubtype;
	}

	public String getCr() {
		return cr;
	}

	public void setCr(String cr) {
		this.cr = cr;
	}

	public String getXp() {
		return xp;
	}

	public void setXp(String xp) {
		this.xp = xp;
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

	public String toString() {
		return name;
	}
}
