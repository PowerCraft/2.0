package powercraft.launcher.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import powercraft.api.recipes.PC_IRecipe;
import powercraft.launcher.PC_LauncherUtils;
import powercraft.launcher.PC_Logger;
import powercraft.launcher.PC_Property;
import powercraft.launcher.PC_Version;

public class PC_ModuleObject {

	private String moduleName;

	private List<PC_ModuleVersion> moduleVersions = new ArrayList<PC_ModuleVersion>();

	private PC_Version usingVersion;
	private Class<?> moduleClass;
	private Object module;
	private boolean isLoaded = false;
	private List<PC_ModuleObject> after = new ArrayList<PC_ModuleObject>();
	private PC_Property config;
	private boolean errored = false;
	public boolean enabled = true;

	public PC_ModuleObject(String moduleName) {
		this.moduleName = moduleName;
		String version = getConfig().getString("loader.usingVersion");
		enabled = getConfig().getBoolean("loader.enabled", true);
		if (moduleName.equalsIgnoreCase("core") || moduleName.equalsIgnoreCase("api"))
			enabled = true;
		if (!version.equals("")) {
			usingVersion = new PC_Version(version);
		}
	}

	public String getModuleName() {
		return moduleName;
	}

	public void addModule(PC_ModuleVersion module) {
		module.setModule(this);
		moduleVersions.add(module);
	}

	public void removeModule(PC_ModuleVersion module) {
		moduleVersions.remove(module);
	}

	public void addModuleLoadBevore(PC_ModuleObject bevore) {
		after.add(bevore);
	}

	public List<PC_ModuleVersion> getVersions() {
		return new ArrayList<PC_ModuleVersion>(moduleVersions);
	}

	public PC_ModuleVersion getVersion(PC_Version version) {
		for (PC_ModuleVersion moduleVersion : moduleVersions) {
			if (moduleVersion.getVersion().compareTo(version) == 0) {
				return moduleVersion;
			}
		}
		return null;
	}

	public PC_ModuleVersion getNewest() {
		PC_ModuleVersion newest = moduleVersions.get(0);
		for (PC_ModuleVersion module : moduleVersions) {
			if (module.getVersion().compareTo(newest.getVersion()) > 0) {
				newest = module;
			}
		}
		return newest;
	}

	public PC_ModuleVersion getStandartVersion() {
		PC_ModuleVersion version;
		if (usingVersion != null) {
			version = getVersion(usingVersion);
			if (version != null) {
				return version;
			}
		}
		version = getNewest();
		usingVersion = version.getModuleVersion();
		getConfig().setString("loader.usingVersion", usingVersion.toString());
		getConfig().setBoolean("loader.enabled", enabled);
		saveConfig();
		return version;
	}

	public void load() {
		if (!isLoaded) {
			isLoaded = true;
			for (PC_ModuleObject module : after) {
				module.load();
			}
			try {
				PC_ModuleVersion moduleVersion = getStandartVersion();
				PC_ModuleClassInfo classInfo = moduleVersion.getCommon();
				if (PC_LauncherUtils.isClient()) {
					if (moduleVersion.getClient() != null)
						classInfo = moduleVersion.getClient();
				}
				moduleClass = PC_ModuleLoader.load(classInfo.className.replace('/', '.'), classInfo.file);
				module = moduleClass.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Class<?> getModuleClass() {
		return moduleClass;
	}

	public Object getModule() {
		return module;
	}

	public Object callMethod(String name, Class<?>[] classes, Object[] objects) {
		if (errored)
			return null;
		Class<?> c = moduleClass;

		while (c != null) {

			Method m = null;
			try {
				m = c.getMethod(name, classes);
				m.setAccessible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (m != null) {
				try {
					return m.invoke(module, objects);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			c = c.getSuperclass();
		}

		return null;

	}

	public Object callMethod(Class<? extends Annotation> annontation, Object[] objects) {
		if (errored)
			return null;
		Class<?> c = moduleClass;

		while (c != null) {

			Method ma[] = c.getDeclaredMethods();
			for (Method m : ma) {
				if (m.isAnnotationPresent(annontation)) {
					try {
						m.setAccessible(true);
						return m.invoke(module, objects);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}
			}

			c = c.getSuperclass();
		}
		return null;
	}

	public void loadConfig() {
		File f = new File(PC_LauncherUtils.getMCDirectory(), "/PowerCraft/Configs/" + moduleName + ".cfg");
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
	}

	public PC_Property getConfig() {
		if (config == null)
			loadConfig();
		return config;
	}

	public void setConfig(PC_Property config) {
		this.config = config;
	}

	public void saveConfig() {
		File f = PC_LauncherUtils.createFile(PC_LauncherUtils.getMCDirectory(), "/PowerCraft/Configs/");
		f = new File(f, moduleName + ".cfg");
		try {
			OutputStream os = new FileOutputStream(f);
			config.save(os);
		} catch (FileNotFoundException e) {
			PC_Logger.severe("Can't find File " + f);
		}
	}

	public void preInit() {
		try {
			callMethod(PC_Module.PC_PreInit.class, new Object[] {});
		} catch (Throwable e) {
			errored();
			PC_Logger.throwing("PC_ModuleObject", "preInit", e);
		}
	}

	public void init() {
		try {
			callMethod(PC_Module.PC_Init.class, new Object[] {});
		} catch (Throwable e) {
			errored();
			PC_Logger.throwing("PC_ModuleObject", "init", e);
		}
	}

	public void postInit() {
		try {
			callMethod(PC_Module.PC_PostInit.class, new Object[] {});
		} catch (Throwable e) {
			errored();
			PC_Logger.throwing("PC_ModuleObject", "postInit", e);
		}
	}

	public void initProperties(PC_Property config) {
		try {
			callMethod(PC_Module.PC_InitProperties.class, new Object[] { config });
		} catch (Throwable e) {
			errored();
			PC_Logger.throwing("PC_ModuleObject", "initProperties", e);
		}
	}

	public List initEntities(List entities) {
		try {
			return (List) callMethod(PC_Module.PC_InitEntities.class, new Object[] { entities });
		} catch (Throwable e) {
			errored();
			PC_Logger.throwing("PC_ModuleObject", "initEntities", e);
		}
		return new ArrayList();
	}

	public void initRecipes() {
		try {
			callMethod(PC_Module.PC_InitRecipes.class, new Object[] {});
		} catch (Throwable e) {
			errored();
			PC_Logger.throwing("PC_ModuleObject", "initRecipes", e);
		}
	}
	
	public List init3DRecipes(List recipes) {
		try {
			return (List)callMethod(PC_Module.PC_Init3DRecipes.class, new Object[] {recipes});
		} catch (Throwable e) {
			errored();
			PC_Logger.throwing("PC_ModuleObject", "initRecipes", e);
		}
		return new ArrayList<PC_IRecipe>();
	}

	public List initDataHandlers(List dataHandlers) {
		try {
			return (List) callMethod(PC_Module.PC_InitDataHandlers.class, new Object[] { dataHandlers });
		} catch (Throwable e) {
			errored();
			PC_Logger.throwing("PC_ModuleObject", "initDataHandlers", e);
		}
		return new ArrayList();
	}

	public List initPackets(List packets) {
		try {
			return (List) callMethod(PC_Module.PC_InitPackets.class, new Object[] { packets });
		} catch (Throwable e) {
			errored();
			PC_Logger.throwing("PC_ModuleObject", "initPacketHandlers", e);
		}
		return new ArrayList();
	}

	public List registerEventHandlers(List eventHandlers) {
		try {
			return (List) callMethod(PC_Module.PC_RegisterEventHandlers.class, new Object[] { eventHandlers });
		} catch (Throwable e) {
			errored();
			PC_Logger.throwing("PC_ModuleObject", "registerEventHandlers", e);
		}
		return new ArrayList();
	}

	public List registerContainers(List containers) {
		try {
			return (List) callMethod(PC_Module.PC_RegisterContainers.class, new Object[] { containers });
		} catch (Throwable e) {
			errored();
			PC_Logger.throwing("PC_ModuleObject", "registerContainers", e);
		}
		return new ArrayList();
	}

	public List initLanguage(List arrayList) {
		try {
			return (List) callMethod(PC_ClientModule.PC_InitLanguage.class, new Object[] { arrayList });
		} catch (Throwable e) {
			errored();
			PC_Logger.throwing("PC_ModuleObject", "initLanguage", e);
		}
		return new ArrayList();
	}

	public List loadTextureFiles(List arrayList) {
		try {
			return (List) callMethod(PC_ClientModule.PC_LoadTextureFiles.class, new Object[] { arrayList });
		} catch (Throwable e) {
			errored();
			PC_Logger.throwing("PC_ModuleObject", "loadTextureFiles", e);
		}
		return new ArrayList();
	}

	public List addSplashes(List arrayList) {
		try {
			return (List) callMethod(PC_ClientModule.PC_AddSplashes.class, new Object[] { arrayList });
		} catch (Throwable e) {
			errored();
			PC_Logger.throwing("PC_ModuleObject", "addSplashes", e);
		}
		return new ArrayList();
	}

	public List registerEntityRender(List arrayList) {
		try {
			return (List) callMethod(PC_ClientModule.PC_RegisterEntityRender.class, new Object[] { arrayList });
		} catch (Throwable e) {
			errored();
			PC_Logger.throwing("PC_ModuleObject", "registerEntityRender", e);
		}
		return new ArrayList();
	}

	public List registerGuis(List guis) {
		try {
			return (List) callMethod(PC_ClientModule.PC_RegisterGuis.class, new Object[] { guis });
		} catch (Throwable e) {
			errored();
			PC_Logger.throwing("PC_ModuleObject", "registerGuis", e);
		}
		return new ArrayList();
	}

	public void resolveInstances(TreeMap<String, PC_ModuleObject> modules) {
		if (errored)
			return;
		Class<?> c = moduleClass;
		while (c != null) {
			Field fa[] = c.getDeclaredFields();
			for (Field f : fa) {
				if (f.isAnnotationPresent(PC_Module.PC_Instance.class)) {
					String modulename = f.getAnnotation(PC_Module.PC_Instance.class).module();
					PC_ModuleObject module;
					if (modulename.equals("")) {
						module = this;
					} else {
						module = modules.get(modulename);
					}
					if (module != null) {
						f.setAccessible(true);
						try {
							f.set(this.module, module);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}

			c = c.getSuperclass();
		}
	}

	public void errored() {
		errored = true;
	}

}
