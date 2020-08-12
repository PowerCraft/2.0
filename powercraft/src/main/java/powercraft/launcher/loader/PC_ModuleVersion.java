package powercraft.launcher.loader;

import java.io.File;

import powercraft.launcher.PC_Version;

public class PC_ModuleVersion {

	private PC_ModuleObject module;
	private PC_Version moduleVersion;
	private String dependencies;

	private PC_ModuleClassInfo common;
	private PC_ModuleClassInfo client;

	public PC_ModuleVersion(PC_Version moduleVersion, String dependencies, PC_ModuleClassInfo moduleClassInfo) {
		this.moduleVersion = moduleVersion;
		this.dependencies = dependencies;
		common = moduleClassInfo;
	}

	public PC_ModuleObject getModule() {
		return module;
	}

	public void setModule(PC_ModuleObject module) {
		this.module = module;
	}

	public PC_Version getVersion() {
		return moduleVersion;
	}

	public String getClassName() {
		if (client != null)
			return client.className;
		return common.className;
	}

	public String getSuperName() {
		if (client != null)
			return client.superName;
		return common.superName;
	}

	public String[] getInterfaces() {
		if (client != null)
			return client.interfaces;
		return common.interfaces;
	}

	public PC_Version getModuleVersion() {
		return moduleVersion;
	}

	public String getDependencies() {
		return dependencies;
	}

	public File getFile() {
		if (client != null)
			return client.file;
		return common.file;
	}

	public File getStartFile() {
		if (client != null)
			return client.startFile;
		return common.startFile;
	}

	public void setClient(PC_ModuleClassInfo client) {
		this.client = client;
	}

	public PC_ModuleClassInfo getCommon() {
		return common;
	}

	public PC_ModuleClassInfo getClient() {
		return client;
	}

}