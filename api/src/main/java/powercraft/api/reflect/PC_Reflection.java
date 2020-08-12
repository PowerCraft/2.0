package powercraft.api.reflect;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.EnumMap;

import javax.xml.transform.Result;

import cpw.mods.fml.relauncher.ReflectionHelper;
import powercraft.api.utils.PC_Utils;
import powercraft.launcher.PC_Logger;

public final class PC_Reflection {

	private static boolean thrownSecurityExceptionBefore;

	private static interface PC_ICallerGetter {

		public Class<?> getCallerClass(int num);

	}

	private static class PC_CallerGetterSecMan extends SecurityManager implements PC_ICallerGetter {

		PC_CallerGetterSecMan() {

		}

		@SuppressWarnings("boxing")
		@Override
		public Class<?> getCallerClass(int num) {
			Class<?>[] classes = getClassContext();
			if (classes.length > 3 + num) {
				return classes[num + 3];
			}
			// PC_Logger.severe("Class %s has no %s callers, only %s" +
			// classes[classes.length-1] + num + classes.length - 2);
			return null;
		}

	}

	private static class PC_CallerGetterFallback implements PC_ICallerGetter {

		PC_CallerGetterFallback() {

		}

		@SuppressWarnings("boxing")
		@Override
		public Class<?> getCallerClass(int num) {
			StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
			if (stackTraceElements.length > 3 + num) {
				try {
					return Class.forName(stackTraceElements[3 + num].getClassName());
				} catch (SecurityException se) {
					onSecurityException(se);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			PC_Logger.severe("Class %s has no %s callers, only %s");
			return null;
		}

	}

	private static final PC_ICallerGetter callerGetter;

	static {
		PC_ICallerGetter cGetter;
		try {
			cGetter = new PC_CallerGetterSecMan();
		} catch (Throwable e) {
			cGetter = new PC_CallerGetterFallback();
		}
		callerGetter = cGetter;
	}

	public static Class<?> getCallerClass() {
		return callerGetter.getCallerClass(0);
	}

	public static Class<?> getCallerClass(int num) {
		return callerGetter.getCallerClass(num);
	}

	@SuppressWarnings("boxing")
	public static Field findNearestBestField(Class<?> clasz, int index, Class<?> type) {
		Field fields[] = clasz.getDeclaredFields();
		Field f;
		int i = index;
		if (i >= 0 && i < fields.length) {
			f = fields[i];
			if (type.isAssignableFrom(f.getType())) {
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
				if (type.isAssignableFrom(f.getType())) {
					PC_Logger.warning("Field in %s which should be at index %s not found, now using index %s");
					return f;
				}
				max++;
			}
			if (min >= 0) {
				f = fields[min];
				if (type.isAssignableFrom(f.getType())) {
					PC_Logger.warning("Field in %s which should be at index %s not found, now using index %s");
					return f;
				}
				min--;
			}
		}
		PC_Logger.severe("Field in %s which should be at index %s not found");
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getValue(Class<?> clasz, Object object, int index, Class<T> type) {
		try {
			Field field = findNearestBestField(clasz, index, type);
			setAccessible(field);
			return (T) field.get(object);
		} catch (SecurityException se) {
			onSecurityException(se);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getValue(PC_ReflectionField<?, T> f, Object object) {
		try {
			Field field = f.getField();
			setAccessible(field);
			return (T) field.get(object);
		} catch (SecurityException se) {
			onSecurityException(se);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void setValue(Class<?> clasz, Object object, int index, Class<?> type, Object value) {

		try {
			Field field = findNearestBestField(clasz, index, type);
			setAccessible(field);
			field.set(object, value);
		} catch (SecurityException se) {
			onSecurityException(se);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setValue(PC_ReflectionField<?, ?> f, Object object, Object value) {

		try {
			Field field = f.getField();
			setAccessible(field);
			field.set(object, value);
		} catch (SecurityException se) {
			onSecurityException(se);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setValueAndFinals(Class<?> clasz, Object object, int index, Class<?> type, Object value) {

		try {
			Field field = findNearestBestField(clasz, index, type);
			setAccessible(field);
			Field field_modifiers = Field.class.getDeclaredField("modifiers");
			field_modifiers.setAccessible(true);
			int modifier = field_modifiers.getInt(field);

			if ((modifier & Modifier.FINAL) != 0) {
				field_modifiers.setInt(field, modifier & ~Modifier.FINAL);
			}

			field.set(object, value);

			if ((modifier & Modifier.FINAL) != 0) {
				field_modifiers.setInt(field, modifier);
			}

		} catch (SecurityException se) {
			onSecurityException(se);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setValueAndFinals(PC_ReflectionField<?, ?> f, Object object, Object value) {
		try {
			Field field = f.getField();
			setAccessible(field);
			Field field_modifiers = Field.class.getDeclaredField("modifiers");
			field_modifiers.setAccessible(true);
			int modifier = field_modifiers.getInt(field);

			if ((modifier & Modifier.FINAL) != 0) {
				field_modifiers.setInt(field, modifier & ~Modifier.FINAL);
			}

			field.set(object, value);

			if ((modifier & Modifier.FINAL) != 0) {
				field_modifiers.setInt(field, modifier);
			}

		} catch (SecurityException se) {
			onSecurityException(se);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Method AccessibleObject_setAccessible0;

	public static void setAccessible(AccessibleObject ao) {
		if (AccessibleObject_setAccessible0 != null) {
			try {
				AccessibleObject_setAccessible0.invoke(null, ao, Boolean.TRUE);
			} catch (Exception e) {
				/**/}
		}
		try {
			ao.setAccessible(true);
		} catch (Exception e) {
			try {
				ReflectionHelper.setPrivateValue(AccessibleObject.class, ao, Boolean.TRUE, "override");
			} catch (Exception ee) {
				if (AccessibleObject_setAccessible0 == null) {
					try {
						AccessibleObject_setAccessible0 = ReflectionHelper.findMethod(AccessibleObject.class, ao,
								new String[] { "setAccessible0" }, AccessibleObject.class, boolean.class);
						AccessibleObject_setAccessible0.invoke(null, ao, Boolean.TRUE);
					} catch (Exception eee) {
						/**/}
				}
			}
		}
	}

	public static Field[] getDeclaredFields(Class<?> c) {
		try {
			return c.getDeclaredFields();
		} catch (SecurityException se) {
			onSecurityException(se);
			try {
				return c.getFields();
			} catch (SecurityException se2) {
				return new Field[0];
			}
		}
	}

	public static <T> T newInstance(Class<T> c) {
		try {
			return c.newInstance();
		} catch (SecurityException e) {
			onSecurityException(e);
		} catch (InstantiationException e) {
			PC_Logger.severe("Error while constructing %s");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			PC_Logger.severe("Cannot access constructor %s");
		}
		return null;
	}

	public static <T> T newInstanceNoHandling(Class<T> c, Class<?>[] types, Object... values)
			throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		try {
			Constructor<T> cc = c.getConstructor(types);
			setAccessible(cc);
			return cc.newInstance(values);
		} catch (SecurityException e) {
			onSecurityException(e);
		}
		return null;
	}

	public static <T> T newInstance(Class<T> c, Class<?>[] types, Object... values) {
		try {
			Constructor<T> cc = c.getConstructor(types);
			setAccessible(cc);
			return cc.newInstance(values);
		} catch (SecurityException e) {
			onSecurityException(e);
		} catch (InstantiationException e) {
			PC_Logger.severe("Error while constructing %s");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			PC_Logger.severe("Cannot access constructor %s");
		} catch (NoSuchMethodException e) {
			PC_Logger.severe("Cannot find constructor %s");
		} catch (Exception e) {
			PC_Logger.severe("Error in constructor %s");
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unused")
	static void onSecurityException(SecurityException e) {
		if (!thrownSecurityExceptionBefore) {
			PC_Logger.warning("PowerCraft has no permission for reflection");
			thrownSecurityExceptionBefore = true;
		}
	}

	private PC_Reflection() {
	}

}