package powercraft.api.reflect;

import java.lang.annotation.Annotation;

public class PC_FieldWithAnnotation<T extends Annotation> {

	private Class c;
	private Object obj;
	private int index;
	private T annotation;

	public PC_FieldWithAnnotation(Class c, Object obj, int index, T annotation) {
		this.c = c;
		this.obj = obj;
		this.index = index;
		this.annotation = annotation;
	}

	public T getAnnotation() {
		return annotation;
	}

	public boolean setValue(Object value) {
		return PC_ReflectHelper.setValue(c, obj, index, value, null);
	}

	public Object getValue() {
		return PC_ReflectHelper.getValue(c, obj, index, null);
	}

	public Class<?> getFieldClass() {
		return c.getDeclaredFields()[index].getType();
	}

	public String getFieldName() {
		return c.getDeclaredFields()[index].getName();
	}

}
