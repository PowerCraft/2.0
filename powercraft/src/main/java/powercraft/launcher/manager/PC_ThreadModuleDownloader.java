package powercraft.launcher.manager;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import powercraft.launcher.PC_Launcher;
import powercraft.launcher.PC_LauncherUtils;
import powercraft.launcher.PC_Logger;
import powercraft.launcher.loader.PC_ModuleDiscovery;
import powercraft.launcher.loader.PC_ModuleObject;
import powercraft.launcher.manager.PC_ModuleManager.ModuleUpdateInfo;

public class PC_ThreadModuleDownloader extends Thread {

	private String url;
	private String name;
	private String version;

	public PC_ThreadModuleDownloader(String url, String name, String version) {
		this.url = url;
		this.name = name;
		this.version = version;
		start();
	}

	public PC_ThreadModuleDownloader(String url, String name, String version, boolean noThread) {
		this.url = url;
		this.name = name;
		this.version = version;
		boolean error = false;
		int depth = 0;
		do {
			try {
				download(url, name, version);
				PC_ModuleDiscovery md = PC_LauncherUtils.searchModules(true);
				PC_Launcher.addModule(name, md.getModules().get(name));
			} catch (IOException e) {
				if (depth <= 5)
					error = true;
				else {
					PC_Logger.warning("Error while downloading module " + name);
					PC_ModuleManager.errors.add("Error downloading module " + name);
				}
				depth++;
			}
		} while (error);
		PC_ModuleManager.updatedCount++;
	}

	public static void download(String urlStr, String name, String version) throws IOException {
		URL url = new URL(urlStr);
		BufferedInputStream bis = new BufferedInputStream(url.openStream());
		name = name.toLowerCase();
		FileOutputStream fis = new FileOutputStream(
				PC_LauncherUtils.getPowerCraftModuleFile() + "/" + name + "-" + version + ".jar");
		byte[] buffer = new byte[1024];
		int count = 0;
		while ((count = bis.read(buffer, 0, 1024)) != -1)
			fis.write(buffer, 0, count);
		fis.close();
		bis.close();
	}

	@Override
	public void run() {
		boolean error = false;
		int depth = 0;
		do {
			error = false;
			try {
				download(url, name, version);
				PC_ModuleDiscovery md = PC_LauncherUtils.searchModules(true);
				PC_Launcher.addModule(name, md.getModules().get(name));
			} catch (IOException e) {
				if (depth <= 5)
					error = true;
				else {
					PC_Logger.warning("Error while downloading module " + name);
					PC_ModuleManager.errors.add("Error downloading module " + name);
				}
				depth++;
			}
		} while (error);
		PC_ModuleManager.updatedCount++;
	}

}
