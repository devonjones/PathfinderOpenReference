package org.evilsoft.pathfinder.reference.list;

public class SkillListItem extends BaseListItem {
	private String description;
	private String attribute;
	private boolean armorCheckPenalty;
	private boolean trainedOnly;

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

	public static String buildQualitiesDisplay(boolean armorCheckPenalty, boolean trainedOnly) {
		StringBuilder sb = new StringBuilder();
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
