package powercraft.launcher;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.TreeMap;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import powercraft.launcher.loader.PC_ModuleDiscovery;
import powercraft.launcher.loader.PC_ModuleObject;
import powercraft.launcher.manager.PC_ThreadLangDownloader;

public class PC_LauncherUtils {

	protected static PC_LauncherUtils instance;
	public static boolean isPreStart = false;

	public PC_LauncherUtils() {
		instance = this;
	}

	public static MinecraftServer mcs() {
		return MinecraftServer.getServer();
	}

	public static boolean isClient() {
		return instance.pIsClient();
	}

	public static File createFile(File pfile, String name) {
		File file = new File(pfile, name);
		if (!file.exists())
			file.mkdirs();
		return file;
	}

	public static File getMCDirectory() {
		if(isPreStart) return new File("./");
		return instance.pGetMCDirectory();
	}

	public static File getPowerCraftFile() {
		return createFile(getMCDirectory(), "PowerCraft");
	}

	public static File getPowerCraftModuleFile() {
		return createFile(getPowerCraftFile(), "Modules");
	}

	public static mod_PowerCraft getMod() {
		return mod_PowerCraft.getInstance();
	}

	public static String getPowerCraftVersion() {
		return mod_PowerCraft.getInstance().getVersion();
	}

	public static String getPowerCraftName() {
		return mod_PowerCraft.getInstance().getName();
	}

	public static void addAuthor(String name) {
		mod_PowerCraft.getInstance().getModMetadata().authorList.add(name);
	}

	public static void addCredit(String name) {
		mod_PowerCraft.getInstance().getModMetadata().credits += ", " + name;
	}

	public static boolean isDeveloperVersion() {
		return PC_Launcher.isDeveloperVersion();
	}

	public static void openUpdateGui(boolean requestDownloadTarget) {
		instance.pOpenUpdateGui(requestDownloadTarget);
	}

	public static PC_ModuleDiscovery searchModules(boolean addAny) {
		File modules = PC_LauncherUtils.getPowerCraftModuleFile();
		File res = null;
		try {
			URL url = mod_PowerCraft.class.getResource("/");
			if (url != null) {
				res = new File(url.toURI());
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		PC_ModuleDiscovery moduleDiscovery = new PC_ModuleDiscovery();
		moduleDiscovery.search(modules, addAny, res, false);
		return moduleDiscovery;
	}

	public static void lookForUpdates() {
		instance.pLookForUpdates();
	}

	public void pLookForUpdates() {

	}

	public void pOpenUpdateGui(boolean requestDownloadTarget) {

	}

	protected boolean pIsClient() {
		return false;
	}

	protected File pGetMCDirectory() {
		return mcs().getFile("");
	}

	// ResourceChecker TODO: make own progress bar on start or remove it
	public static void resourceCheck(TreeMap<String, PC_ModuleObject> modules) {
		cpw.mods.fml.common.ProgressManager.ProgressBar bar = cpw.mods.fml.common.ProgressManager
				.push("Check PowerCraft resources", 1, false);
		File dirL = new File(PC_LauncherUtils.getPowerCraftFile() + "/lang/");
		ArrayList<String> moduleUpdateLang = new ArrayList<String>();
		if (!dirL.exists()) {
			for (PC_ModuleObject m : modules.values()) {
				moduleUpdateLang.add(m.getModuleName());
			}
			moduleUpdateLang.remove("Api");
			if (isAvailable(
					"https://raw.githubusercontent.com/PowerCraft/Maven/master/1.7.10/lang/en_US/" + "Core.lang")) {
				new PC_ThreadLangDownloader(moduleUpdateLang);
				bar.step("Lang");
			} else {
				bar.step("Network error, lang skiped");
			}
		} else {
			for (PC_ModuleObject m : modules.values()) {
				File langDefault = new File(dirL + "/en_US/" + m.getModuleName() + ".lang");
				File langCurrent = new File(dirL + "/"
						+ Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode() + "/"
						+ m.getModuleName() + ".lang");
				if (!langDefault.exists() || !langCurrent.exists())
					moduleUpdateLang.add(m.getModuleName());
			}
			moduleUpdateLang.remove("Api");
			if (isAvailable(
					"https://raw.githubusercontent.com/PowerCraft/Maven/master/1.7.10/lang/en_US/" + "Core.lang")) {
				new PC_ThreadLangDownloader(moduleUpdateLang);
				bar.step("Lang");
			} else {
				bar.step("Network error, lang skiped");
			}
		}
		cpw.mods.fml.common.ProgressManager.pop(bar);
	}

	private static boolean isAvailable(String ur) {
		try {
			final URL url = new URL(ur);
			final URLConnection conn = url.openConnection();
			conn.connect();
			conn.getInputStream().close();
			return true;
		} catch (MalformedURLException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}
}
