package powercraft.launcher.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import powercraft.launcher.loader.PC_ModuleClassInfo;
import powercraft.launcher.loader.PC_ModuleDiscovery;

public class PC_ClassVisitor extends ClassVisitor {

	public PC_ModuleClassInfo classInfo;
	private PC_ModuleDiscovery discovery;

	public PC_ClassVisitor(PC_ModuleDiscovery discovery) {
		super(Opcodes.ASM4);
		this.discovery = discovery;
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		classInfo = new PC_ModuleClassInfo();
		classInfo.className = name;
		classInfo.superName = superName;
		classInfo.interfaces = interfaces;
		classInfo.annotationVisitor = null;
		classInfo.isClient = false;
	}

	@Override
	public void visitSource(String source, String debug) {
	}

	@Override
	public void visitOuterClass(String owner, String name, String desc) {
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		if (desc.equals("Lpowercraft/launcher/loader/PC_Module;")) {
			classInfo.annotationVisitor = new PC_AnnotationVisitor();
			return classInfo.annotationVisitor;
		} else if (desc.equals("Lpowercraft/launcher/loader/PC_ClientModule;")) {
			classInfo.isClient = true;
		}
		return null;
	}

	@Override
	public void visitAttribute(Attribute attr) {
	}

	@Override
	public void visitInnerClass(String name, String outerName, String innerName, int access) {
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		return null;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		return null;
	}

	@Override
	public void visitEnd() {
		if (discovery != null) {
			if (classInfo.isClient || classInfo.annotationVisitor != null) {
				discovery.add(classInfo);
			}
		}
	}

}
