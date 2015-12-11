package org.boundless.cf.servicebroker.cfutils;

public class CFResourceEntity {

	private String id, name;
	private CFEntityType type;

	public CFEntityType getType() {
		return type;
	}

	public void setType(CFEntityType type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "CFResourceEntity [type=" + type + ", id=" + id + ", name="
				+ name + "]";
	}
	
}
