package powercraft.api.registry;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.gres.PC_IGresClient;

public final class PC_GresRegistry {

	private static HashMap<String, Class<? extends PC_IGresClient>> guis = new HashMap<String, Class<? extends PC_IGresClient>>();
	private static HashMap<String, Class<? extends PC_GresBaseWithInventory>> containers = new HashMap<String, Class<? extends PC_GresBaseWithInventory>>();

	public static void registerGresGui(String name, Class<? extends PC_IGresClient> gui) {
		guis.put(name, gui);
	}

	public static void registerGresContainer(String name, Class<? extends PC_GresBaseWithInventory> container) {
		containers.put(name, container);
	}

	public static void openGres(String name, EntityPlayer player, TileEntity te, Object... o) {
		PC_RegistryServer.getInstance().openGres(name, player, te, o);
	}

	public static Class<? extends PC_IGresClient> getGui(String name) {
		if (guis.containsKey(name)) {
			return guis.get(name);
		}
		return null;
	}

	public static Class<? extends PC_GresBaseWithInventory> getContainer(String name) {
		if (containers.containsKey(name)) {
			return containers.get(name);
		}
		return null;
	}

}
