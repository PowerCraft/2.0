package powercraft.api.hooklib.asjasm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class ASJASM implements IClassTransformer {

	public static HashMap<String, ArrayList<FieldData>> fieldsMap = new HashMap<String, ArrayList<FieldData>>();

	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (!fieldsMap.containsKey(transformedName))
			return basicClass;
		ClassReader cr = new ClassReader(basicClass);
		ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		for (FieldData fd : fieldsMap.get(transformedName))
			cw.visitField(fd.access, fd.name, fd.desc, null, null).visitEnd();
		cr.accept(cw, 0);
		return cw.toByteArray();
	}

	public static void registerFieldHookContainer(String className) {
		try {
			transform(IOUtils
					.toByteArray(ASJASM.class.getResourceAsStream('/' + className.replace('.', '/') + ".class")));
		} catch (IOException e) {
			System.out.println("[ASJASM] <ERROR> Can not parse hooks container " + className);
			e.printStackTrace();
		}
	}

	private static void transform(byte[] basicClass) {
		ClassReader cr = new ClassReader(basicClass);
		ClassNode cn = new ClassNode();
		cr.accept(cn, 0);
		for (FieldNode fn : cn.fields) {
			boolean flag = false;
			String targetClassName = "";
			if (fn.visibleAnnotations != null && !fn.visibleAnnotations.isEmpty()) {
				for (AnnotationNode an : fn.visibleAnnotations) {
					if (an.desc.equals(Type.getDescriptor(HookField.class))) {
						flag = true;
						if (an.values != null && !an.values.isEmpty()) {
							for (int i = 0; i < an.values.size(); i += 2) {
								if (an.values.get(i).equals("targetClassName")) {
									targetClassName = an.values.get(i + 1).toString();
									break;
								}
							}
						}
					}
					if (flag && targetClassName != "")
						break;
				}
			}

			if (!flag || targetClassName == "")
				continue;

			if (!fieldsMap.containsKey(targetClassName))
				fieldsMap.put(targetClassName, new ArrayList<FieldData>());
			fieldsMap.get(targetClassName).add(new FieldData(fn.access, fn.name, fn.desc));
		}
	}
}
