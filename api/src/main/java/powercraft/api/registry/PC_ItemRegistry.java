package powercraft.api.registry;

import java.util.TreeMap;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import powercraft.api.annotation.PC_Config;
import powercraft.api.item.PC_Item;
import powercraft.api.item.PC_ItemArmor;
import powercraft.api.reflect.PC_FieldWithAnnotation;
import powercraft.api.reflect.PC_IFieldAnnotationIterator;
import powercraft.api.reflect.PC_ReflectHelper;
import powercraft.launcher.PC_Property;
import powercraft.launcher.loader.PC_ModuleObject;

public final class PC_ItemRegistry {

	private static TreeMap<String, PC_Item> items = new TreeMap<String, PC_Item>();
	private static TreeMap<String, PC_ItemArmor> itemArmors = new TreeMap<String, PC_ItemArmor>();

	public static <T extends Item> T register(PC_ModuleObject module, Class<T> c) {
		if (PC_Item.class.isAssignableFrom(c)) {
			return (T) registerItem(module, (Class<? extends PC_Item>) c);
		} else if (PC_ItemArmor.class.isAssignableFrom(c)) {
			return (T) registerItemArmor(module, (Class<? extends PC_ItemArmor>) c);
		} else {
			throw new IllegalArgumentException("Expect class of PC_Item or PC_ItemArmor");
		}
	}

	public static PC_Item registerItem(PC_ModuleObject module, Class<? extends PC_Item> itemClass) {
		final PC_Property config = module.getConfig().getProperty(itemClass.getSimpleName(), null, null);
		try {
			if (!config.getBoolean("enabled", true)) {
				return null;
			}

			PC_Item item = itemClass.getConstructor(int.class).newInstance(2);
			items.put(itemClass.getSimpleName(), item);
			item.setUnlocalizedName(itemClass.getSimpleName());
			item.setModule(module);

			PC_ReflectHelper.getAllFieldsWithAnnotation(itemClass, item, PC_Config.class,
					new PC_IFieldAnnotationIterator<PC_Config>() {

						@Override
						public boolean onFieldWithAnnotation(PC_FieldWithAnnotation<PC_Config> fieldWithAnnotation) {
							Class<?> c = fieldWithAnnotation.getFieldClass();
							String name = fieldWithAnnotation.getAnnotation().name();
							if (name.equals("")) {
								name = fieldWithAnnotation.getFieldName();
							}
							String[] comment = fieldWithAnnotation.getAnnotation().comment();
							if (c == String.class) {
								String data = (String) fieldWithAnnotation.getValue();
								data = config.getString(name, data, comment);
								fieldWithAnnotation.setValue(data);
							} else if (c == Integer.class || c == int.class) {
								int data = (Integer) fieldWithAnnotation.getValue();
								data = config.getInt(name, data, comment);
								fieldWithAnnotation.setValue(data);
							} else if (c == Float.class || c == float.class) {
								float data = (Float) fieldWithAnnotation.getValue();
								data = config.getFloat(name, data, comment);
								fieldWithAnnotation.setValue(data);
							} else if (c == Boolean.class || c == boolean.class) {
								boolean data = (Boolean) fieldWithAnnotation.getValue();
								data = config.getBoolean(name, data, comment);
								fieldWithAnnotation.setValue(data);
							}
							return false;
						}
					});
			GameRegistry.registerItem(item, itemClass.getSimpleName());
			return item;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static PC_ItemArmor registerItemArmor(PC_ModuleObject module, Class<? extends PC_ItemArmor> itemArmorClass) {
		final PC_Property config = module.getConfig().getProperty(itemArmorClass.getSimpleName(), null, null);
		try {
			if (!config.getBoolean("enabled", true)) {
				return null;
			}
			PC_ItemArmor itemArmor = itemArmorClass.getConstructor(int.class).newInstance(3);
			itemArmors.put(itemArmorClass.getSimpleName(), itemArmor);
			itemArmor.setUnlocalizedName(itemArmorClass.getSimpleName());
			itemArmor.setModule(module);

			PC_ReflectHelper.getAllFieldsWithAnnotation(itemArmorClass, itemArmor, PC_Config.class,
					new PC_IFieldAnnotationIterator<PC_Config>() {

						@Override
						public boolean onFieldWithAnnotation(PC_FieldWithAnnotation<PC_Config> fieldWithAnnotation) {
							Class<?> c = fieldWithAnnotation.getFieldClass();
							String name = fieldWithAnnotation.getAnnotation().name();
							if (name.equals("")) {
								name = fieldWithAnnotation.getFieldName();
							}
							String[] comment = fieldWithAnnotation.getAnnotation().comment();
							if (c == String.class) {
								String data = (String) fieldWithAnnotation.getValue();
								data = config.getString(name, data, comment);
								fieldWithAnnotation.setValue(data);
							} else if (c == Integer.class || c == int.class) {
								int data = (Integer) fieldWithAnnotation.getValue();
								data = config.getInt(name, data, comment);
								fieldWithAnnotation.setValue(data);
							} else if (c == Float.class || c == float.class) {
								float data = (Float) fieldWithAnnotation.getValue();
								data = config.getFloat(name, data, comment);
								fieldWithAnnotation.setValue(data);
							} else if (c == Boolean.class || c == boolean.class) {
								boolean data = (Boolean) fieldWithAnnotation.getValue();
								data = config.getBoolean(name, data, comment);
								fieldWithAnnotation.setValue(data);
							}
							return false;
						}
					});
			GameRegistry.registerItem(itemArmor, itemArmorClass.getSimpleName());
			return itemArmor;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static PC_Item getPCItemByName(String name) {
		if (items.containsKey(name)) {
			return items.get(name);
		}
		return null;
	}

	public static TreeMap<String, PC_Item> getPCItems() {
		return new TreeMap<String, PC_Item>(items);
	}

	public static PC_ItemArmor getPCItemArmorByName(String name) {
		if (itemArmors.containsKey(name)) {
			return itemArmors.get(name);
		}
		return null;
	}

	public static TreeMap<String, PC_ItemArmor> getPCItemArmors() {
		return new TreeMap<String, PC_ItemArmor>(itemArmors);
	}

}
