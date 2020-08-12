package powercraft.logic.type;

public class PClo_DelayerType {
	public static final int TOTAL_DELAYER_COUNT = 2;

	@SuppressWarnings("javadoc")
	public static final int FIFO = 0, HOLD = 1;

	public static String[] names = new String[TOTAL_DELAYER_COUNT];

	static {
		names[FIFO] = "buffer";
		names[HOLD] = "slowRepeater";
	}

	public static String[] getTextures() {
		String[] textures = new String[2 + 2 * TOTAL_DELAYER_COUNT];
		textures[0] = "bottomplate";
		textures[1] = "sideplate";
		for (int i = 0; i < TOTAL_DELAYER_COUNT; i++) {
			textures[i + 2] = names[i] + "_on";
		}
		for (int i = 0; i < TOTAL_DELAYER_COUNT; i++) {
			textures[i + 2 + TOTAL_DELAYER_COUNT] = names[i] + "_off";
		}
		return textures;
	}

}
