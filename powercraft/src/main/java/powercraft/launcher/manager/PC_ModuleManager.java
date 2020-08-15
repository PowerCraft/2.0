package powercraft.launcher.manager;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import powercraft.launcher.PC_Launcher;
import powercraft.launcher.PC_LauncherUtils;
import powercraft.launcher.PC_Property;
import powercraft.launcher.PC_Version;
import powercraft.launcher.loader.PC_ModuleDiscovery;
import powercraft.launcher.loader.PC_ModuleObject;
import powercraft.launcher.loader.PC_ModuleVersion;
import powercraft.launcher.manager.PC_UpdateXMLFile.XMLInfoTag;
import powercraft.launcher.manager.PC_UpdateXMLFile.XMLModuleTag;
import powercraft.launcher.manager.PC_UpdateXMLFile.XMLPackTag;
import powercraft.launcher.manager.PC_UpdateXMLFile.XMLVersionTag;

public class PC_ModuleManager {

	private static PC_ThreadCheckUpdates updateChecker;

	public static List<ModuleUpdateInfo> moduleList;
	public static List<XMLPackTag> packList;
	public static List<ModuleUpdateInfo> moduleListToUpdate;
	public static XMLInfoTag updateInfo;
	public static File downloadTarget;
	public static boolean newLauncher;
	public static boolean onStart;
	public static int updatedCount = 0;
	public static ArrayList<String> errors = new ArrayList<String>();

	public static void lookForUpdates() {
		PC_ModuleManager.startUpdateInfoDownload();
		File moduleFiles = PC_LauncherUtils.getPowerCraftModuleFile();
		TreeMap<String, PC_ModuleObject> modules = PC_LauncherUtils.searchModules(false).getModules();
		PC_ModuleManager.moduleInfos(modules);
	}

	public static void startUpdateInfoDownload() {
		updateChecker = new PC_ThreadCheckUpdates();
	}

	public static void moduleInfos(TreeMap<String, PC_ModuleObject> modules) {
		updateInfo = updateChecker.getUpdateInfo();
		if (updateInfo != null && updateInfo.getPowerCraftVersion().compareTo(new PC_Version(PC_LauncherUtils.getPowerCraftVersion())) > 0) {
			newLauncher = true;
		}
		packList = new ArrayList<XMLPackTag>();
		moduleList = new ArrayList<ModuleUpdateInfo>();
		if(updateInfo != null) {
			for(XMLPackTag pack : updateInfo.getPacks()) {
				int count = 0;
				for(XMLModuleTag xml : pack.getModules()) {
					for(PC_ModuleObject m : PC_Launcher.getModules().values()) {
						if(xml.getName().equalsIgnoreCase(m.getModuleName()))
							count++;
					}
				}
				if(count != pack.getModules().size())
					packList.add(pack);
			}
			for (XMLModuleTag xmlModule : updateInfo.getModules()) {
				ModuleUpdateInfo mui = new ModuleUpdateInfo();
				mui.xmlModule = xmlModule;
				mui.newVersion = mui.xmlModule.getNewestVersion();
				mui.module = modules.get(xmlModule.getName());
				List<PC_Version> versionList = new ArrayList<PC_Version>();
				for (XMLVersionTag v : xmlModule.getVersions()) {
					if (!versionList.contains(v.getVersion()))
						versionList.add(v.getVersion());
				}
				if (mui.module == null) {
					String ignoreVersion = PC_Launcher.getConfig().getString("updater.ignore." + mui.xmlModule.getName());
				} else {
					for (PC_ModuleVersion v : mui.module.getVersions()) {
						if (!versionList.contains(v.getVersion()))
							versionList.add(v.getVersion());
					}
					mui.oldVersion = mui.module.getStandartVersion().getVersion();
					if (mui.newVersion.getVersion().compareTo(mui.oldVersion) > 0) {
						String ignoreVersion = PC_Launcher.getConfig()
								.getString("updater.ignore." + mui.xmlModule.getName());
					}
				}
				PC_Version[] versionArray = versionList.toArray(new PC_Version[0]);
				Arrays.sort(versionArray);
				mui.versions = new PC_Version[versionArray.length];
				for (int i = 0; i < versionArray.length; i++) {
					mui.versions[i] = versionArray[versionArray.length - i - 1];
				}
				moduleList.add(mui);
			}
		}
		if (!onStart) {
			boolean requestDownloadTarget = false;
			if (PC_Launcher.getConfig().getString("updater.source").equals("")) {
				downloadTarget = new File(System.getProperty("user.home"));
				requestDownloadTarget = true;
			} else {
				downloadTarget = new File(PC_Launcher.getConfig().getString("updater.source"));
			}
			PC_Launcher.saveConfig();
			PC_LauncherUtils.openUpdateGui(requestDownloadTarget);
		}
	}

	public static class ModuleUpdateInfo {
		public PC_Version[] versions;
		public PC_Version oldVersion;
		public XMLVersionTag newVersion;
		public PC_ModuleObject module;
		public XMLModuleTag xmlModule;
	}

	public static void openURL(String url) {
		try {
			Desktop.getDesktop().browse(new URI(url));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	 private static boolean match(String a, String b) { 
		 a=a.toLowerCase();
		 b=b.toLowerCase(); 
		 for(int i = 0; i < a.length(); i++) { 
			 for(int j = 0; j < b.length(); j++) {
				 if(a.charAt(i) == b.charAt(j)) {
					 int count = 0; 
					 for(int k = 0; k < b.length(); k++) {
						 if(i+k < a.length()) 
							 if(a.charAt(i + k) == b.charAt(k)) count++; 
						 }
					 if(count == b.length()) 
						 return true;
					 }
				 }
			 }
		 return false;
	 }
	 

	private static void deleteResource(String module) {
		File dir = new File("./PowerCraft/lang/");
		if(dir.exists()) {
			for (File f : dir.listFiles()) {
				if (f.isDirectory()) {
					for (File lang : f.listFiles()) {
						if (lang.getName().equalsIgnoreCase(module + ".lang")) {
							lang.delete();
						}
					}
				}
			}
		}
		/*
		 * dir = new File("./PowerCraft/Assets/"); TODO: maybe it deleted FileReader
		 * reader = null; try { reader = new FileReader(dir+"/"+module+".json"); } catch
		 * (IOException e) { PC_Logger.warning("Error read assets info file"); } Object
		 * obj; if(reader == null) { PC_Logger.warning("Module "
		 * +module+" maybe was deleted earlier!"); return; } obj = new
		 * JsonParser().parse(reader); JsonObject jo = (JsonObject) obj; for(JsonElement
		 * file : jo.get("files").getAsJsonArray()) { File res = new File(dir +
		 * "/powercraft/textures/" + file.getAsString()); res.delete(); } File info =
		 * new File(dir+"/"+module+".json"); info.delete();
		 */
	}

	public static void delete(String module) {//TODO: Maybe move it on another thread?
		File dir = new File("./PowerCraft/Modules/");
		for (File f : dir.listFiles()) {
			if (f.isDirectory() || f.getName() == ".DS_Store")
				continue;
			if (f.getName().contains(module.toLowerCase())) {
				f.delete();
			}
		}
		PC_Launcher.removeModule(module);
		deleteResource(module);
	}

	public static void install(Object xml, boolean multiThread) {
		if (xml instanceof String) {
			install((String) xml, multiThread);
		}
		if (xml instanceof XMLPackTag) {
			install((XMLPackTag) xml, multiThread);
		}
		if (xml instanceof ModuleUpdateInfo) {
			install((ModuleUpdateInfo) xml, multiThread);
		}
	}

	public static void install(XMLPackTag pack, boolean multiThread) {
		for (XMLModuleTag tag : pack.getModules()) {
			if (tag != null) {
				for (ModuleUpdateInfo mui : moduleList) {
					if (tag.getName().equalsIgnoreCase(mui.xmlModule.getName()))
						install(mui, multiThread);
				}
			}
		}
	}

	public static void install(String moduleName, boolean multiThread) {
		for (ModuleUpdateInfo mui : moduleList) {
			if (mui.xmlModule.getName().equalsIgnoreCase(moduleName))
				install(mui, multiThread);
		}
	}

	public static void install(ModuleUpdateInfo module, boolean multiThread) {
		if (multiThread) {
			new PC_ThreadModuleDownloader(module.newVersion.getDownloadLink(), module.xmlModule.getName(),
					module.newVersion.getVersion().toString());
			new PC_ThreadLangDownloader(module.xmlModule.getName());
		} else {
			new PC_ThreadModuleDownloader(module.newVersion.getDownloadLink(), module.xmlModule.getName(),
					module.newVersion.getVersion().toString(), false);
			new PC_ThreadLangDownloader(module.xmlModule.getName());
		}
	}

	public static void activateModule(PC_ModuleObject module, boolean activate) {
		module.getConfig().setBoolean("loader.enabled", activate);
		module.saveConfig();
	}

	public static void ignoreUpdates() {
		PC_Property config = PC_Launcher.getConfig();
		for (ModuleUpdateInfo mui : moduleList) {
			config.setString("updater.ignore." + mui.xmlModule.getName(), mui.newVersion.getVersion().toString());
		}
		PC_Launcher.saveConfig();
	}

	public static void checkForUpdate() {
		ArrayList<PC_ModuleObject> modules = new ArrayList<PC_ModuleObject>();
		moduleListToUpdate = new ArrayList<ModuleUpdateInfo>();
		modules.addAll(PC_Launcher.getModules().values());
		modules.add(PC_Launcher.getAPI());
		for (ModuleUpdateInfo mui : moduleList) {
			for (PC_ModuleObject m : modules) {
				if(m != null && mui != null)
					if (m.getModuleName().equalsIgnoreCase(mui.xmlModule.getName()))
						if (mui.newVersion.getVersion().compareTo(m.getStandartVersion().getVersion()) > 0)
							moduleListToUpdate.add(mui);
			}
		}
	}

	public static void updateAll() {
		for (ModuleUpdateInfo mui : moduleListToUpdate) {
			delete(mui.xmlModule.getName());
			install(mui, true);
		}
	}

}
