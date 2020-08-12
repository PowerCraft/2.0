package powercraft.launcher.loader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.objectweb.asm.ClassReader;

import powercraft.launcher.PC_LauncherUtils;
import powercraft.launcher.PC_Version;
import powercraft.launcher.asm.PC_ClassVisitor;

public class PC_ModuleDiscovery {

	private HashMap<String, PC_ModuleClassInfo> unresolvedClients = new HashMap<String, PC_ModuleClassInfo>();
	private TreeMap<String, PC_ModuleObject> modules = new TreeMap<String, PC_ModuleObject>();
	private HashMap<String, PC_Version> moduleVersion = new HashMap<String, PC_Version>();
	private PC_ModuleObject api;
	private List<PC_ModuleObject> startList[] = new List[] { new ArrayList<PC_ModuleObject>(),
			new ArrayList<PC_ModuleObject>(), new ArrayList<PC_ModuleObject>() };
	private ClassLoader moduleLoader = PC_ModuleDiscovery.class.getClassLoader();
	private boolean addFile;
	private PC_ClassVisitor classVisitor = new PC_ClassVisitor(this);
	private File loadFile;
	private File startFile;

	public void add(PC_ModuleClassInfo classInfo) {
		classInfo.file = loadFile;
		classInfo.startFile = startFile;
		if (classInfo.annotationVisitor != null) {
			String moduleName = classInfo.annotationVisitor.getModuleName();
			String moduleDependencies = classInfo.annotationVisitor.getDependencies();
			PC_Version moduleVersion = classInfo.annotationVisitor.getVersion();
			PC_ModuleObject moduleObject;
			if (modules.containsKey(moduleName)) {
				moduleObject = modules.get(moduleName);
			} else {
				modules.put(moduleName, moduleObject = new PC_ModuleObject(moduleName));
			}
			if (moduleObject.getVersion(moduleVersion) == null) {
				PC_ModuleVersion moduleV = new PC_ModuleVersion(moduleVersion, moduleDependencies, classInfo);
				if (unresolvedClients.containsKey(classInfo.className)) {
					moduleV.setClient(unresolvedClients.get(classInfo.className));
					unresolvedClients.remove(classInfo.className);
				}
				moduleObject.addModule(moduleV);
				this.moduleVersion.put(moduleName, moduleVersion);
			}
		} else if (classInfo.isClient) {
			PC_ModuleVersion version = getModuleVersionForClass(classInfo.superName);
			if (version == null) {
				unresolvedClients.put(classInfo.superName, classInfo);
			} else {
				version.setClient(classInfo);
				moduleVersion.remove(version.getModule().getModuleName());
			}
		}
	}

	public PC_ModuleVersion getModuleVersionForClass(String className) {
		for (Entry<String, PC_ModuleObject> module : modules.entrySet()) {
			if (moduleVersion.containsKey(module.getKey())) {
				PC_ModuleVersion version = module.getValue().getVersion(moduleVersion.get(module.getKey()));
				if (version.getClassName().equals(className)) {
					return version;
				}
			}
		}
		return null;
	}

	public TreeMap<String, PC_ModuleObject> getModules() {
		return new TreeMap<String, PC_ModuleObject>(modules);
	}

	private void sortModules() {
		for (Entry<String, PC_ModuleObject> e : modules.entrySet()) {
			PC_ModuleObject module = e.getValue();
			if (module.getModuleName().equals("Api")) {
				api = module;
				startList[0].add(api);
			} else {
				String dependencies = module.getStandartVersion().getDependencies();
				String[] dependenciesList = dependencies.split(":", 2);
				dependenciesList[0] = dependenciesList[0].trim();
				if (dependenciesList.length > 1 && dependenciesList[1] != null) {
					dependenciesList[1] = dependenciesList[1].trim();
				}
				int addList = 1;
				if (dependenciesList[0].equals("required-before") || dependenciesList[0].equals("before")) {
					if (dependenciesList[1].equals("*")) {
						addList = 0;
					} else {
						String[] modulesList = dependenciesList[1].split(",");
						for (String modulesName : modulesList) {
							PC_ModuleObject depModule = modules.get(modulesName.trim());
							if (depModule != null) {
								depModule.addModuleLoadBevore(module);
							}
						}
					}
				} else if (dependenciesList[0].equals("required-after") || dependenciesList[0].equals("after")) {
					if (dependenciesList[1].equals("*")) {
						addList = 2;
					} else {
						String[] modulesList = dependenciesList[1].split(",");
						for (String modulesName : modulesList) {
							PC_ModuleObject depModule = modules.get(modulesName.trim());
							if (depModule != null) {
								module.addModuleLoadBevore(depModule);
							}
						}
					}
				} else if (dependenciesList[0].equals("")) {

				}
				startList[addList].add(module);
			}
		}
	}

	public void setModules(TreeMap<String, PC_ModuleObject> modList) {
		modules = modList;
	}

	public void search(Object... o) {
		for (int i = 0; i < o.length; i++) {
			if (o[i] instanceof File) {
				File file = (File) o[i];
				boolean addFile = false;
				if (i + 1 < o.length && o[i + 1] instanceof Boolean) {
					addFile = (Boolean) o[i + 1];
					i++;
				}
				if (file.exists()) {
					this.addFile = addFile;
					startFile = file;
					unresolvedClients.clear();
					moduleVersion.clear();
					searchDir(file);
				}
			}
		}
	}

	private void addFileToClassLoader(File file) {
		if (addFile) {
			if (moduleLoader instanceof URLClassLoader) {
				try {
					Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
					addURL.setAccessible(true);
					addURL.invoke(moduleLoader, file.toURI().toURL());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void searchDir(File file) {
		if (file.isDirectory()) {
			addFileToClassLoader(file);
			for (File f : file.listFiles()) {
				searchDir(f);
			}
		} else if (file.isFile()) {
			if (file.getName().endsWith(".class")) {
				loadFile = file;
				try {
					loadClass(new FileInputStream(file));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			} else if (file.getName().endsWith(".jar") || file.getName().endsWith(".zip")) {
				searchZip(file);
			}
		}
	}

	private void searchZip(File file) {
		try {
			addFileToClassLoader(file);
			ZipFile zip = new ZipFile(file);
			loadFile = file;
			for (ZipEntry ze : Collections.list(zip.entries())) {
				if (!ze.isDirectory()) {
					if (ze.getName().endsWith(".class")) {
						loadClass(zip.getInputStream(ze));
					}
				}
			}
			zip.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadClass(InputStream is) {
		byte[] b = new byte[1024];
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			int length = 0;
			while ((length = is.read(b)) != -1) {
				buffer.write(b, 0, length);
			}
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		b = buffer.toByteArray();
		ClassReader cr = new ClassReader(b);
		cr.accept(classVisitor, 0);
	}

	public void loadModules() {
		sortModules();
		api.load();
		for (int i = 0; i < 3; i++) {
			for (PC_ModuleObject module : startList[i]) {
				module.load();
			}
		}
		for (Entry<String, PC_ModuleObject> module : modules.entrySet()) {
			module.getValue().resolveInstances(modules);
		}
	}

	public PC_ModuleObject getAPI() {
		return api;
	}

}
