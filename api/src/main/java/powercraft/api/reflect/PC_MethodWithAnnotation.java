package powercraft.api.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class PC_MethodWithAnnotation<T extends Annotation> {

	private Class c;
	private Object obj;
	private int index;
	private T annotation;

	public PC_MethodWithAnnotation(Class<?> c, Object obj, int index, T annotation) {
		this.c = c;
		this.obj = obj;
		this.index = index;
		this.annotation = annotation;
	}

	public T getAnnotation() {
		return annotation;
	}

	public Class<?>[] getMethodParams() {
		return c.getDeclaredMethods()[index].getParameterTypes();
	}

	public String getMethodName() {
		return c.getDeclaredMethods()[index].getName();
	}

	public Method getMethod() {
		return c.getDeclaredMethods()[index];
	}

}
