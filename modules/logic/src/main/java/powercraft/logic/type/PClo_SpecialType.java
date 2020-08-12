package powercraft.logic.type;

public class PClo_SpecialType {
	public static final int TOTAL_SPECIAL_COUNT = 6;

	@SuppressWarnings("javadoc")
	public static final int DAY = 0, NIGHT = 1, RAIN = 2, CHEST_EMPTY = 3, CHEST_FULL = 4, SPECIAL = 5;

	public static String[] names = new String[TOTAL_SPECIAL_COUNT];

	static {
		names[DAY] = "day";
		names[NIGHT] = "night";
		names[RAIN] = "rain";
		names[CHEST_EMPTY] = "chestEmpty";
		names[CHEST_FULL] = "chestFull";
		names[SPECIAL] = "special";
	}

	public static String[] getTextures() {
		String[] textures = new String[4 + 2 * TOTAL_SPECIAL_COUNT];
		textures[0] = "bottomplate";
		textures[1] = "sideplate";
		textures[2] = "special_side";
		textures[3] = "special_side_2";
		for (int i = 0; i < TOTAL_SPECIAL_COUNT; i++) {
			textures[i + 4] = names[i] + "_on";
		}
		for (int i = 0; i < TOTAL_SPECIAL_COUNT; i++) {
			textures[i + 4 + TOTAL_SPECIAL_COUNT] = names[i] + "_off";
		}
		return textures;
	}
}
