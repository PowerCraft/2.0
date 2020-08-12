package powercraft.api.registry;

import powercraft.api.utils.PC_GlobalVariables;
import powercraft.api.utils.PC_Utils;

public final class PC_SoundRegistry {

	public static boolean isSoundEnabled() {
		if (PC_Utils.isServer()) {
			return false;
		}
		return PC_GlobalVariables.soundEnabled;
	}

	public static void playSound(double x, double y, double z, String sound, float soundVolume, float pitch) {
		if (isSoundEnabled()) {
			PC_RegistryServer.getInstance().playSound(x, y, z, sound, soundVolume, pitch);
		}
	}

}
