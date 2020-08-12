package powercraft.launcher.loader;

import java.io.File;

import powercraft.launcher.asm.PC_AnnotationVisitor;

public class PC_ModuleClassInfo {

	public String className;
	public String superName;
	public String[] interfaces;
	public PC_AnnotationVisitor annotationVisitor;
	public boolean isClient;
	public File file;
	public File startFile;

}
