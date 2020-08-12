package powercraft.api.registry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldInfo;
import powercraft.api.interfaces.PC_IDataHandler;

public final class PC_DataHandlerRegistry {

	private static HashMap<String, PC_IDataHandler> dataHandlers = new HashMap<String, PC_IDataHandler>();

	public static void regsterDataHandler(String name, PC_IDataHandler dataHandler) {
		if (!dataHandlers.containsKey(name))
			dataHandlers.put(name, dataHandler);
	}

	public static void savePowerCraftData(WorldInfo worldInfo, File worldDirectory) {
		worldDirectory = new File(worldDirectory, "powercraft");
		if (!worldDirectory.exists())
			worldDirectory.mkdirs();
		for (Entry<String, PC_IDataHandler> dataHandler : dataHandlers.entrySet()) {
			if (dataHandler.getValue().needSave()) {
				// NBTTagCompound nbttag =
				// dataHandler.getValue().save(dataHandler.getKey());TODO:Check it
				try {
					File file = new File(worldDirectory, dataHandler.getKey() + ".dat");
					// CompressedStreamTools.writeCompressed(nbttag, new FileOutputStream(file));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void loadPowerCraftData(WorldInfo worldInfo, File worldDirectory) {
		worldDirectory = new File(worldDirectory, "powercraft");
		for (PC_IDataHandler dh : dataHandlers.values()) {
			dh.reset();
		}
		if (worldDirectory.exists()) {
			File files[] = worldDirectory.listFiles();
			for (File file : files) {
				String name = file.getName();
				if (name.endsWith(".dat")) {
					name = name.substring(0, name.length() - 4);
					if (dataHandlers.containsKey(name)) {
						try {
							dataHandlers.get(name)
									.load(CompressedStreamTools.readCompressed(new FileInputStream(file)));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		PC_MSGRegistry.callAllMSG(PC_MSGRegistry.MSG_LOAD_WORLD, worldInfo, worldDirectory);
	}

}
