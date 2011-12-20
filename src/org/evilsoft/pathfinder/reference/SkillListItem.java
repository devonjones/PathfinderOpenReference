package org.evilsoft.pathfinder.reference;

public class SkillListItem {
	private int section_id;
	private String name;
	private String description;
	private String attribute;
	private boolean armorCheckPenalty;
	private boolean trainedOnly;

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

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public boolean isArmorCheckPenalty() {
		return armorCheckPenalty;
	}

	public void setArmorCheckPenalty(boolean armorCheckPenalty) {
		this.armorCheckPenalty = armorCheckPenalty;
	}

	public boolean isTrainedOnly() {
		return trainedOnly;
	}

	public void setTrainedOnly(boolean trainedOnly) {
		this.trainedOnly = trainedOnly;
	}

	public String getQualities() {
		return SkillListItem.buildQualitiesDisplay(this.armorCheckPenalty, this.trainedOnly);
	}

	public String toString() {
		return name;
	}

	public static String buildQualitiesDisplay(boolean armorCheckPenalty, boolean trainedOnly) {
		StringBuffer sb = new StringBuffer();
		String spacer = " (";
		String end = "";
		if (armorCheckPenalty) {
			sb.append(spacer);
			sb.append("Armor Check Penalty");
			spacer = "; ";
			end = ")";
		}
		if (trainedOnly) {
			sb.append(spacer);
			sb.append("Trained Only");
			end = ")";
		}
		sb.append(end);
		return sb.toString();
	}

	public static String shortDescription(String desc) {
		String[] parts = desc.split("\\.");
		return parts[0] + ".";
	}
}
