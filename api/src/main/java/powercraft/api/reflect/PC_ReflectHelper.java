package powercraft.api.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import powercraft.launcher.PC_Logger;

public final class PC_ReflectHelper {

	public static Class<?> getWrapper(Class<?> c) {
		if (c == null)
			return null;
		if (c.isPrimitive()) {
			if (c == boolean.class) {
				c = Boolean.class;
			} else if (c == byte.class) {
				c = Byte.class;
			} else if (c == char.class) {
				c = Character.class;
			} else if (c == short.class) {
				c = Short.class;
			} else if (c == int.class) {
				c = Integer.class;
			} else if (c == long.class) {
				c = Long.class;
			} else if (c == float.class) {
				c = Float.class;
			} else if (c == double.class) {
				c = Double.class;
			}
		}
		return c;
	}

	private static boolean isBetter(Class<?>[] params1, boolean varArg1, Class<?>[] params2, boolean varArg2,
			Class<?>[] paramsExpect) {
		if (varArg1) {
			if (params1.length < paramsExpect.length - 1) {
				return false;
			}
			if (params2 != null) {
				if (!varArg2) {
					return false;
				}
				if (params2.length > params1.length)
					return false;
			}
		} else {
			if (params1.length != paramsExpect.length) {
				return false;
			}
		}
		for (int i = 0; i < params1.length; i++) {
			params1[i] = getWrapper(params1[i]);
		}
		if (params2 != null) {
			for (int i = 0; i < params2.length; i++) {
				params2[i] = getWrapper(params2[i]);
			}
		}
		for (int i = 0; i < paramsExpect.length; i++) {
			paramsExpect[i] = getWrapper(paramsExpect[i]);
		}
		for (int i = 0; i < paramsExpect.length; i++) {
			Class<?> expect = paramsExpect[i];
			Class<?> pc1 = params1[i];
			Class<?> pc2 = null;
			if (params2 != null) {
				pc2 = params2[i];
			}
			if (expect != null) {
				if (!pc1.isAssignableFrom(expect)) {
					return false;
				}
			}
		}
		return true;
	}

	public static <T> Constructor<T> findBestConstructor(Class<T> c, Class<?>... p) {
		Constructor<?>[] constructors = c.getConstructors();
		Constructor<T> bestConstructor = null;
		Class<?>[] bestParams = null;
		boolean isBestVarArg = false;
		for (int i = 0; i < constructors.length; i++) {
			Class<?>[] params = constructors[i].getParameterTypes();
			boolean varArg = constructors[i].isVarArgs();
			if (isBetter(params, varArg, bestParams, isBestVarArg, p)) {
				bestConstructor = (Constructor<T>) constructors[i];
				bestParams = params;
				isBestVarArg = varArg;
			}
		}
		return bestConstructor;
	}

	public static <T> T create(Class<T> c, Object... o) {

		Class classes[] = new Class[o.length];
		for (int i = 0; i < o.length; i++) {
			if (o[i] == null) {
				classes[i] = null;
			} else {
				classes[i] = o[i].getClass();
			}
		}

		Constructor<T> constructor = findBestConstructor(c, classes);

		if (constructor == null) {
			return null;
		}

		try {
			constructor.setAccessible(true);
			return constructor.newInstance(o);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public static Field findNearesBestField(Class<?> c, int i, Class<?> expect) {
		Field fields[] = c.getDeclaredFields();
		Field f;
		if (i >= 0 && i < fields.length) {
			f = fields[i];
			if (expect.isAssignableFrom(f.getType())) {
				return f;
			}
		} else {
			if (i < 0)
				i = 0;
			if (i >= fields.length) {
				i = fields.length - 1;
			}
		}
		int min = i - 1, max = i + 1;
		while (min >= 0 || max < fields.length) {
			if (max < fields.length) {
				f = fields[max];
				if (expect.isAssignableFrom(f.getType())) {
					PC_Logger.warning(
							"Field in " + c + " which should be at index " + i + " not found, now using index " + max);
					return f;
				}
				max++;
			}
			if (min >= 0) {
				f = fields[min];
				if (expect.isAssignableFrom(f.getType())) {
					PC_Logger.warning(
							"Field in " + c + " which should be at index " + i + " not found, now using index " + min);
					return f;
				}
				min--;
			}
		}
		PC_Logger.warning("Field in " + c + " which should be at index " + i + " not found");
		return null;
	}

	public static <T> T getValue(Class<?> c, Object o, int i, Class<T> expect) {
		try {
			Field f;
			if (expect == null) {
				f = c.getDeclaredFields()[i];
			} else {
				f = findNearesBestField(c, i, expect);
			}
			f.setAccessible(true);
			return (T) f.get(o);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean setValue(Class<?> c, Object o, int i, Object v) {
		return setValue(c, o, i, v, v != null ? v.getClass() : Object.class);
	}

	public static boolean setValue(Class<?> c, Object o, int i, Object v, Class<?> expect) {
		try {
			Field f;
			if (expect == null) {
				f = c.getDeclaredFields()[i];
			} else {
				f = findNearesBestField(c, i, expect);
			}
			f.setAccessible(true);
			Field field_modifiers = Field.class.getDeclaredField("modifiers");
			field_modifiers.setAccessible(true);
			int modifier = field_modifiers.getInt(f);

			if ((modifier & Modifier.FINAL) != 0) {
				field_modifiers.setInt(f, modifier & ~Modifier.FINAL);
			}

			f.set(o, v);

			if ((modifier & Modifier.FINAL) != 0) {
				field_modifiers.setInt(f, modifier);
			}

			return true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void setFieldsWithAnnotationTo(Class<?> c, Object obj, Class<? extends Annotation> annotationClass,
			Object value) {
		Field fa[] = c.getDeclaredFields();

		for (Field f : fa) {
			if (f.isAnnotationPresent(annotationClass)) {
				f.setAccessible(true);
				try {
					f.set(obj, value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (c != Object.class) {
			setFieldsWithAnnotationTo(c.getSuperclass(), obj, annotationClass, value);
		}

	}

	public static List<Object> getFieldsWithAnnotation(Class<?> c, Object obj,
			Class<? extends Annotation> annotationClass) {
		List<Object> l = new ArrayList<Object>();
		Field fa[] = c.getDeclaredFields();

		for (Field f : fa) {
			if (f.isAnnotationPresent(annotationClass)) {
				f.setAccessible(true);
				try {
					l.add(f.get(obj));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (c != Object.class) {
			l.addAll(getFieldsWithAnnotation(c.getSuperclass(), obj, annotationClass));
		}

		return l;
	}

	public static <T extends Annotation> T getAnnotation(Class<?> c, Class<T> annotationClass) {
		if (c.isAnnotationPresent(annotationClass)) {
			return c.getAnnotation(annotationClass);
		}
		return null;
	}

	public static <T extends Annotation> T getAnnotation(Field f, Class<T> annotationClass) {
		if (f.isAnnotationPresent(annotationClass)) {
			return f.getAnnotation(annotationClass);
		}
		return null;
	}

	public static <T extends Annotation> T getAnnotation(Method m, Class<T> annotationClass) {
		if (m.isAnnotationPresent(annotationClass)) {
			return m.getAnnotation(annotationClass);
		}
		return null;
	}

	public static <T extends Annotation> T getAnnotation(Constructor<?> c, Class<T> annotationClass) {
		if (c.isAnnotationPresent(annotationClass)) {
			return c.getAnnotation(annotationClass);
		}
		return null;
	}

	public static <T extends Annotation> List<Field> getAllFieldsWithAnnotation(Class<?> c, Class<T> annotationClass) {
		return getAllFieldsWithAnnotation(c, null, annotationClass, null);
	}

	public static <T extends Annotation> List<Field> getAllFieldsWithAnnotation(Class<?> c, Object obj,
			Class<T> annotationClass, PC_IFieldAnnotationIterator<T> iterator) {
		Field fa[] = c.getDeclaredFields();
		List<Field> fields = new ArrayList<Field>();
		for (int i = 0; i < fa.length; i++) {
			if (fa[i].isAnnotationPresent(annotationClass)) {
				if (iterator != null) {
					if (iterator.onFieldWithAnnotation(
							new PC_FieldWithAnnotation<T>(c, obj, i, fa[i].getAnnotation(annotationClass)))) {
						fields.add(fa[i]);
					}
				} else {
					fields.add(fa[i]);
				}
			}
		}
		if (c != Object.class) {
			fields.addAll(getAllFieldsWithAnnotation(c.getSuperclass(), obj, annotationClass, iterator));
		}
		return fields;
	}

	public static <T extends Annotation> List<Method> getAllMethodsWithAnnotation(Class<?> c,
			Class<T> annotationClass) {
		return getAllMethodsWithAnnotation(c, null, annotationClass, null);
	}

	public static <T extends Annotation> List<Method> getAllMethodsWithAnnotation(Class<?> c, Object obj,
			Class<T> annotationClass, PC_IMethodAnnotationIterator<T> iterator) {
		Method ma[] = c.getDeclaredMethods();
		List<Method> methods = new ArrayList<Method>();
		for (int i = 0; i < ma.length; i++) {
			if (ma[i].isAnnotationPresent(annotationClass)) {
				if (iterator != null) {
					if (iterator.onMethodWithAnnotation(
							new PC_MethodWithAnnotation<T>(c, obj, i, ma[i].getAnnotation(annotationClass)))) {
						methods.add(ma[i]);
					}
				} else {
					methods.add(ma[i]);
				}
			}
		}
		if (c != Object.class) {
			methods.addAll(getAllMethodsWithAnnotation(c.getSuperclass(), obj, annotationClass, iterator));
		}
		return methods;
	}

}
