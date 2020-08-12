package powercraft.logic.type;

public class PClo_RepeaterType {
	public static final int TOTAL_REPEATER_COUNT = 6;

	@SuppressWarnings("javadoc")
	public static final int CROSSING = 0, SPLITTER_I = 1, REPEATER_STRAIGHT = 2, REPEATER_CORNER = 3,
			REPEATER_STRAIGHT_I = 4, REPEATER_CORNER_I = 5;

	public static String[] names = new String[TOTAL_REPEATER_COUNT];

	static {
		names[CROSSING] = "crossing";
		names[SPLITTER_I] = "splitter";
		names[REPEATER_STRAIGHT] = "repeaterStraight";
		names[REPEATER_CORNER] = "repeaterCorner";
		names[REPEATER_STRAIGHT_I] = "repeaterStraightInstant";
		names[REPEATER_CORNER_I] = "repeaterCornerInstant";
	}

	public static String[] getTextures() {
		String[] textures = new String[19];
		int n = 0;
		textures[n++] = "bottomplate";
		textures[n++] = "sideplate";
		textures[n++] = "crossing0";
		textures[n++] = "crossing1";
		textures[n++] = "crossing2";
		textures[n++] = "crossing3";
		textures[n++] = "splitter_3";
		textures[n++] = "splitter_2_l";
		textures[n++] = "splitter_2_s";
		textures[n++] = "splitter_2_r";
		textures[n++] = "repeaterStraight_on";
		textures[n++] = "repeaterStraight_off";
		textures[n++] = "repeaterCorner_on_l";
		textures[n++] = "repeaterCorner_on_r";
		textures[n++] = "repeaterCorner_off_l";
		textures[n++] = "repeaterCorner_off_r";
		textures[n++] = "repeaterStraightInstant";
		textures[n++] = "repeaterCornerInstant_l";
		textures[n++] = "repeaterCornerInstant_r";
		return textures;
	}

	public static int getTextureIndex(int type, boolean on) {
		switch (type) {
		case CROSSING:
			return 2;
		case SPLITTER_I:
			return 6;
		case REPEATER_STRAIGHT:
			return 10 + (on ? 0 : 1);
		case REPEATER_CORNER:
			return 12 + (on ? 0 : 2);
		case REPEATER_STRAIGHT_I:
			return 16;
		case REPEATER_CORNER_I:
			return 17;
		}
		return 0;
	}

	public static boolean[] canBeOn = new boolean[TOTAL_REPEATER_COUNT];

	static {
		canBeOn[CROSSING] = false;
		canBeOn[SPLITTER_I] = false;
		canBeOn[REPEATER_STRAIGHT] = true;
		canBeOn[REPEATER_CORNER] = true;
		canBeOn[REPEATER_STRAIGHT_I] = false;
		canBeOn[REPEATER_CORNER_I] = false;
	}

	public static int change(int repeaterType, int type) {
		type++;

		switch (repeaterType) {
		case CROSSING:
		case SPLITTER_I:
			if (type > 3) {
				type -= 4;
			}

			break;

		case REPEATER_CORNER:
		case REPEATER_CORNER_I:
			if (type > 1) {
				type -= 2;
			}

			break;

		default:
			type = 0;
			break;
		}

		return type;
	}
}
