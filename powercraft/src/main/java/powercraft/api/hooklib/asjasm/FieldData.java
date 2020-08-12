package powercraft.api.hooklib.asjasm;

public class FieldData {

	public final int access;
	public final String name, desc;

	public FieldData(int access, String name, String desc) {
		this.access = access;
		this.name = name;
		this.desc = desc;
	}

	public String toString() {
		return access + " " + desc + " " + name;
	}
}
