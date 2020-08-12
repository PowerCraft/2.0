package powercraft.api;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringTranslate;
import powercraft.api.registry.PC_RegistryClient;
import powercraft.api.registry.PC_RegistryServer;
import powercraft.api.utils.PC_Utils;
import powercraft.launcher.PC_Launcher;
import powercraft.launcher.loader.PC_ModuleObject;

public class PC_Lang {

	public PC_Lang() {
		load();
	}

	public static void load() {
		InputStream inputstream;
		for (PC_ModuleObject m : PC_Launcher.getModules().values()) {
			try {
				inputstream = new FileInputStream(
						PC_Utils.getPowerCraftFile() + "/lang/en_US/" + m.getModuleName() + ".lang");
				StringTranslate.inject(inputstream); // hardcoded en_US for missing lang files and missing translates
			} catch (FileNotFoundException e) {
			}
			try {
				inputstream = new FileInputStream(
						PC_Utils.getPowerCraftFile() + "/lang/" + getUsedLang() + "/" + m.getModuleName() + ".lang");
				StringTranslate.inject(inputstream);
			} catch (FileNotFoundException e) {
			}

		}
	}

	public static String tr(String key) {
		return StatCollector.translateToLocal(key);
	}

	public static String tr(String key, Object... obj) {
		return StatCollector.translateToLocalFormatted(key, obj);
	}

	public static String getUsedLang() {
		return PC_RegistryClient.getUsedLng();
	}

}
