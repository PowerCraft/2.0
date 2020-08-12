package powercraft.launcher.loader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.objectweb.asm.ClassReader;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.LaunchClassLoader;
import powercraft.launcher.asm.PC_ClassVisitor;

public class PC_ModuleLoader extends ClassLoader {

	private static HashMap<String, File> packetFile = new HashMap<String, File>();
	private static HashMap<String, Class> classes = new HashMap<String, Class>();
	private static PC_ModuleLoader moduleLoader = new PC_ModuleLoader(PC_ModuleLoader.class.getClassLoader());
	private static LaunchClassLoader rcl = (LaunchClassLoader) PC_ModuleLoader.class.getClassLoader();

	public PC_ModuleLoader(ClassLoader parent) {
		super(parent);
	}

	private byte[] searchResourceInDir(File file, String resource) {
		byte[] b = null;
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				b = searchResourceInDir(f, resource);
				if (b != null)
					return b;
			}
		} else if (file.isFile()) {
			if (file.getName().endsWith(".class")) {
				try {
					b = loadClass(new FileInputStream(file), resource);
					if (b != null)
						return b;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			} else if (file.getName().endsWith(".jar") || file.getName().endsWith(".zip")) {
				b = searchResourceInZip(file, resource);
				if (b != null)
					return b;
			}
		}
		return null;
	}

	private byte[] searchResourceInZip(File file, String resource) {
		byte[] b = null;
		try {
			ZipFile zip = new ZipFile(file);
			for (ZipEntry ze : Collections.list(zip.entries())) {
				if (!ze.isDirectory()) {
					if (ze.getName().endsWith(".class")) {
						b = loadClass(zip.getInputStream(ze), resource);
						if (b != null) {
							zip.close();
							return b;
						}
					}
				}
			}
			zip.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private byte[] loadClass(InputStream is, String resource) {
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
			return null;
		}
		b = buffer.toByteArray();
		ClassReader cr = new ClassReader(b);
		PC_ClassVisitor cv = new PC_ClassVisitor(null);
		cr.accept(cv, 0);
		if (cv.classInfo.className != null && cv.classInfo.className.replace('/', '.').equals(resource))
			return b;
		return null;
	}

	@Override
	protected URL findResource(String resource) {
		for (Entry<String, File> e : packetFile.entrySet()) {
			if (resource.startsWith(e.getKey())) {
				try {
					return e.getValue().toURI().toURL();
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return super.findResource(resource);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		Class<?> c = classes.get(name);
		if (c != null)
			return c;
		c = findLoadedClass(name);
		if (c != null)
			return c;
		return super.findClass(name);
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		Class<?> c = classes.get(name);
		if (c != null)
			return c;
		c = findLoadedClass(name);
		if (c != null)
			return c;
		for (Entry<String, File> e : packetFile.entrySet()) {
			if (name.startsWith(e.getKey())) {
				byte[] b = searchResourceInDir(e.getValue(), name);
				if (b != null) {
					for (IClassTransformer trans : rcl.getTransformers()) {
						b = trans.transform(name, name, b);
					}
					c = defineClass(name, b, 0, b.length);
					classes.put(name, c);
					Field f = LaunchClassLoader.class.getDeclaredFields()[3];
					f.setAccessible(true);
					// try {
					// Map m = (Map) f.get(PC_ModuleLoader.class.getClassLoader());
					// m.put(name, c);
					// } catch (Exception e1) {
					// e1.printStackTrace();
					// }
					return c;
				}
			}
		}
		return super.loadClass(name, resolve);
	}

	public static Class<?> load(String className, File file) {

		if (file != null) {
			if (file.getName().endsWith(".class")) {
				file = file.getParentFile();
			}

			packetFile.put(className.substring(0, className.lastIndexOf('.')), file);
		}
		try {
			return moduleLoader.loadClass(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
