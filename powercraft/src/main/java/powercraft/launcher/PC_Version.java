package powercraft.launcher;

public class PC_Version implements Comparable<PC_Version> {

	public VersionsNode[] versionNodes;

	public PC_Version(String s) {
		if (s.equalsIgnoreCase("@Version@"))
			s = "0.0.1Dev";
		String sl[] = s.split("\\.");
		versionNodes = new VersionsNode[sl.length];
		for (int i = 0; i < sl.length; i++) {
			versionNodes[i] = new VersionsNode(sl[i]);
		}
	}

	@Override
	public String toString() {
		String text = versionNodes[0].toString();
		for (int i = 1; i < versionNodes.length; i++) {
			text += "." + versionNodes[i];
		}
		return text;
	}

	@Override
	public int compareTo(PC_Version other) {
		for (int i = 0; i < versionNodes.length; i++) {
			if (other.versionNodes.length <= i) {
				return 1;
			}
			int diff = versionNodes[i].compareTo(other.versionNodes[i]);
			if (diff != 0)
				return diff;
		}
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PC_Version) {
			return compareTo((PC_Version) obj) == 0;
		} else if (obj instanceof String) {
			return compareTo(new PC_Version((String) obj)) == 0;
		}
		return false;
	}

	public static class VersionsNode implements Comparable<VersionsNode> {

		public int number;
		public String text;

		public VersionsNode(String s) {
			s = s.trim();
			int i = 0;
			while (s.length() > i && isDigit(s.charAt(i)))
				i++;
			number = Integer.parseInt(s.substring(0, i));
			text = s.substring(i);
		}

		private boolean isDigit(char c) {
			return c >= '0' && c <= '9';
		}

		@Override
		public String toString() {
			return number + text;
		}

		@Override
		public int compareTo(VersionsNode other) {
			int diff = number - other.number;
			if (diff != 0)
				return diff;
			return text.compareToIgnoreCase(other.text);
		}

	}

}
