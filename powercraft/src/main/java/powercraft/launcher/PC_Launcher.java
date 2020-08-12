package powercraft.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.util.TreeMap;

import powercraft.launcher.loader.PC_ModuleDiscovery;
import powercraft.launcher.loader.PC_ModuleObject;
import powercraft.launcher.manager.PC_ModuleManager;

public class PC_Launcher {

	private static PC_Property config;
	private static boolean autoUpdate;
	private static boolean openAlwaysUpdateScreen;
	private static boolean developerVersion;
	private static PC_ModuleDiscovery modules;

	public static void loadConfig() {
		File f = new File(PC_LauncherUtils.getMCDirectory(), "config/PowerCraft.cfg");
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

		autoUpdate = config.getBoolean("updater.autoUpdate", true, "Should PowerCraft look for updates");
		developerVersion = config.getBoolean("updater.showDeveloperVersions", false, "Show developer Versions");

		saveConfig();

	}

	public static void saveConfig() {
		File f = PC_LauncherUtils.createFile(PC_LauncherUtils.getMCDirectory(), "config");
		f = new File(f, "PowerCraft.cfg");
		try {
			OutputStream os = new FileOutputStream(f);
			config.save(os);
		} catch (FileNotFoundException e) {
			PC_Logger.severe("Can't find File " + f);
		}
	}

	public static void loadModules() {
		modules = PC_LauncherUtils.searchModules(true);
		if(PC_LauncherUtils.isPreStart)
			return;
		
		PC_ModuleManager.onStart = true;
		PC_ModuleManager.lookForUpdates();
		if (modules.getModules().isEmpty()) {
			PC_ModuleManager.install("api", false);
			PC_ModuleManager.install("core", false);
		}
		modules = PC_LauncherUtils.searchModules(true);
		if (!modules.getModules().containsKey("Api")) {
			PC_ModuleManager.install("api", false);
		}
		if (!modules.getModules().containsKey("Core")) {
			PC_ModuleManager.install("core", false);
		}
		(modules = PC_LauncherUtils.searchModules(true)).loadModules();
		PC_LauncherUtils.resourceCheck(modules.getModules());
	}

	public static void preInit() {
		try {
			PC_Logger.init(PC_LauncherUtils.getPowerCraftFile());
			PC_Logger.enterSection("Loading");

			loadConfig();

			loadModules();

			PC_Logger.exitSection();

			getAPI().preInit();
		} catch (Throwable e) {
			PC_Logger.throwing("PC_Launcher", "preInit", e);
			e.printStackTrace();
		}
	}

	public static void init() {
		try {
			getAPI().initProperties(config);
			getAPI().init();
		} catch (Throwable e) {
			PC_Logger.throwing("PC_Launcher", "init", e);
			e.printStackTrace();
		}

	}

	public static void postInit() {
		try {
			getAPI().postInit();
		} catch (Throwable e) {
			PC_Logger.throwing("PC_Launcher", "postInit", e);
			e.printStackTrace();
		}

	}

	public static Object callapiMethod(String name, Class<?>[] classes, Object[] objects) {
		return getAPI().callMethod(name, classes, objects);
	}

	public static Object callapiMethod(Class<? extends Annotation> annontation, Object[] objects) {
		return getAPI().callMethod(annontation, objects);
	}

	public static TreeMap<String, PC_ModuleObject> getModules() {
		TreeMap<String, PC_ModuleObject> hm = modules.getModules();
		hm.remove("Api");
		return hm;
	}

	public static void removeModule(String name) {
		TreeMap<String, PC_ModuleObject> hm = modules.getModules();
		hm.remove(name);
		modules.setModules(hm);
	}

	public static void addModule(String name, PC_ModuleObject module) {
		TreeMap<String, PC_ModuleObject> hm = modules.getModules();
		hm.put(name, module);
		modules.setModules(hm);
	}

	public static PC_ModuleObject getAPI() {
		return modules.getAPI();
	}

	public static PC_Property getConfig() {
		return config;
	}

	public static boolean openAlwaysUpdateScreen() {
		return openAlwaysUpdateScreen;
	}

	public static boolean isDeveloperVersion() {
		return developerVersion;
	}

}
