package org.evilsoft.pathfinder.reference.list;

public class SpellListItem extends BaseListItem {
	private String description;
	private String school;
	private String subschool;
	private String descriptor;
	private int level;
	private String classes;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSchool() {
		return school;
	}
	public String getClasses() {
		return classes;
	}

	public void setClasses(String classes) {
		this.classes = classes;
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

	public String getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(String descriptor) {
		this.descriptor = descriptor;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public static String buildSchoolLine(String school, String subschool, String descriptor) {
		StringBuffer sb = new StringBuffer();
		sb.append(school);
		if (subschool != null) {
			sb.append(" (");
			sb.append(subschool);
			sb.append(")");
		}
		if (descriptor != null) {
			sb.append(" [");
			sb.append(descriptor);
			sb.append("]");
		}
		return sb.toString();
	}

	public static String shortDescription(String desc) {
		if (desc != null) {
			return desc;
		}
		return "";
	}
}
