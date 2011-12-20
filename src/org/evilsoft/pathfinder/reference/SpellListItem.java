package org.evilsoft.pathfinder.reference;

public class SpellListItem {
	private int section_id;
	private String name;
	private String description;
	private String school;
	private String subschool;
	private int level;

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

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getSubschool() {
		return subschool;
	}

	public void setSubschool(String subschool) {
		this.subschool = subschool;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String toString() {
		return name;
	}

	public static String buildSchoolLine(String school, String subschool) {
		StringBuffer sb = new StringBuffer();
		sb.append(school);
		if (subschool != null) {
			sb.append(" (");
			sb.append(subschool);
			sb.append(")");
		}
		return sb.toString();
	}

	public static String shortDescription(String desc) {
		if (desc != null) {
			String[] parts = desc.split("\\.");
			return parts[0] + ".";
		}
		return "";
	}
}
