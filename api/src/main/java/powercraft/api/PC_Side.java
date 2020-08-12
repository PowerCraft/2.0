package powercraft.api;

import cpw.mods.fml.relauncher.Side;

public enum PC_Side {
	SERVER(Side.SERVER), CLIENT(Side.CLIENT);

	public final Side side;

	PC_Side(Side side) {
		this.side = side;
	}

	public static PC_Side from(Side side) {
		switch (side) {
		case CLIENT:
			return CLIENT;
		case SERVER:
			return SERVER;
		default:
			return null;
		}
	}
}
