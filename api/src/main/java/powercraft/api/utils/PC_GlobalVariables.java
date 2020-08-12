package powercraft.api.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import powercraft.launcher.PC_Logger;
import powercraft.launcher.PC_Property;

public class PC_GlobalVariables {

	public static List<String> splashes = new ArrayList<String>();
	public static boolean hackSplashes = true;
	public static int indexRecentlyHit = 45;
	public static int indexItemSthiftedIndex = 168;
	public static List<TileEntity> tileEntity = new ArrayList<TileEntity>();
	public static HashMap<String, Object> consts = new HashMap<String, Object>();
	public static HashMap<String, Object> oldConsts = null;
	public static boolean soundEnabled;

	public static PC_Property config;

	public static void loadConfig() {
		File f = new File(PC_Utils.getMCDirectory(), "config/PowerCraft.cfg");
		if (f.exists()) {
			try {
				InputStream is = new FileInputStream(f);
				config = PC_Property.loadFromFile(is);
			} catch (FileNotFoundException e) {
				PC_Logger.severe("Can't find File " + f);
			}
		}
		if (config == null) {
			config = new PC_Property(null);
		}

		hackSplashes = config.getBoolean("hacks.splash", true);
		config.getBoolean("cheats.survivalCheating", false);

		if (PC_Utils.isClient())
			soundEnabled = config.getBoolean("sound.enabled", true);

	}

	public static void saveConfig() {
		File f = new File(PC_Utils.getMCDirectory(), "config/PowerCraft.cfg");
		if (config != null) {
			try {
				OutputStream os = new FileOutputStream(f);
				config.save(os);
			} catch (FileNotFoundException e) {
				PC_Logger.severe("Can't find File " + f);
			}
		}
	}

}
