package powercraft.api.registry;

import java.util.HashMap;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import powercraft.launcher.PC_Property;

public final class PC_KeyRegistry {

	protected static HashMap<String, KeyBinding> keys = new HashMap<String, KeyBinding>();
	protected static int keyReverse;

	public static boolean isPlacingReversed(EntityPlayer player) {
		return isKeyPressed(player, "keyReverse");
	}

	public static boolean isKeyPressed(EntityPlayer player, String key) {
		return keys.get(key).isPressed();
	}

	public static void watchForKey(String name, int key) {
		PC_RegistryServer.getInstance().watchForKey(name, key);
	}

	public static int watchForKey(PC_Property config, String name, int key, String... info) {
		KeyBinding reg = new KeyBinding(name, key, getLabel());
		ClientRegistry.registerKeyBinding(reg);
		key = config.getInt("key." + name, key, info);
		keys.put(name, reg);
		watchForKey(name, key);
		return key;
	}

	public static void setReverseKey(PC_Property config) {
		keyReverse = watchForKey(config, "keyReverse", 29, "Key for rotate placing");
	}

	public static String getLabel() {
		return "PowerCraft";
	}

}
