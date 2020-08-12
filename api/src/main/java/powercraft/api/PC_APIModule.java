package powercraft.api;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import powercraft.api.annotation.PC_FieldObject;
import powercraft.api.block.PC_Block;
import powercraft.api.building.PC_CropHarvesting;
import powercraft.api.building.PC_ISpecialHarvesting;
import powercraft.api.building.PC_TreeHarvesting;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.interfaces.PC_IDataHandler;
import powercraft.api.interfaces.PC_IMSG;
import powercraft.api.item.PC_Item;
import powercraft.api.item.PC_ItemArmor;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.recipes.PC_IRecipe;
import powercraft.api.reflect.PC_FieldWithAnnotation;
import powercraft.api.reflect.PC_IFieldAnnotationIterator;
import powercraft.api.reflect.PC_ReflectHelper;
import powercraft.api.registry.PC_BlockRegistry;
import powercraft.api.registry.PC_BuildingRegistry;
import powercraft.api.registry.PC_ChunkForcerRegistry;
import powercraft.api.registry.PC_DataHandlerRegistry;
import powercraft.api.registry.PC_EntityRegistry;
import powercraft.api.registry.PC_GresRegistry;
import powercraft.api.registry.PC_ItemRegistry;
import powercraft.api.registry.PC_KeyRegistry;
import powercraft.api.registry.PC_MSGRegistry;
import powercraft.api.registry.PC_ModuleRegistry;
import powercraft.api.registry.PC_RecipeRegistry;
import powercraft.api.renderer.PC_Renderer;
import powercraft.api.utils.PC_GlobalVariables;
import powercraft.api.utils.PC_Struct2;
import powercraft.api.utils.PC_Utils;
import powercraft.launcher.PC_EventHandlerRegistry;
import powercraft.launcher.PC_Logger;
import powercraft.launcher.PC_Property;
import powercraft.launcher.loader.PC_Module;
import powercraft.launcher.loader.PC_Module.PC_Init;
import powercraft.launcher.loader.PC_Module.PC_InitProperties;
import powercraft.launcher.loader.PC_Module.PC_Instance;
import powercraft.launcher.loader.PC_Module.PC_PostInit;
import powercraft.launcher.loader.PC_Module.PC_PreInit;
import powercraft.launcher.loader.PC_Module.PC_RegisterEventHandlers;
import powercraft.launcher.loader.PC_ModuleObject;

@PC_Module(name = "Api", version = "@Version@")
public class PC_APIModule {

	protected PC_PacketHandler packetHandler;

	public static PC_CreativeTab creativeTab;

	@PC_Instance
	private static PC_ModuleObject instance;

	@PC_PreInit
	public void preInit() {
		initVars();
		PC_Logger.enterSection("PreInit");
		PC_GlobalVariables.loadConfig();
		PC_Logger.enterSection("Register Hacks");
		PC_Logger.exitSection();
		PC_Logger.enterSection("Module PreInit");
		List<PC_ModuleObject> modules = PC_ModuleRegistry.getModuleList();
		for (PC_ModuleObject module : modules) {
			if (module.getModuleName() != "Api")
				module.preInit();
		}
		PC_Logger.exitSection();
		PC_Logger.enterSection("Module Property Init");
		for (PC_ModuleObject module : modules) {
			module.initProperties(module.getConfig());
		}
		PC_KeyRegistry.setReverseKey(PC_GlobalVariables.config);
		PC_Logger.exitSection();
		clientPreInit(modules);
		PC_Logger.exitSection();
		PC_Logger.enterSection("Module Event Handlers PreInit");
		modules.add(instance);
		for (PC_ModuleObject module : modules) {
			if (module.enabled) {
				List<String> l = module.registerEventHandlers(new ArrayList<String>());
				if (l != null) {
					for (String c : l) {
						PC_EventHandlerRegistry.registry(c);
					}
				}
			}
		}
		modules.remove(instance);
		PC_Logger.exitSection();
	}

	protected void initVars() {
		PC_Utils.create();
	}

	protected void clientPreInit(List<PC_ModuleObject> modules) {
	}

	@PC_Init
	public void init() {
		PC_Logger.enterSection("Init");
		GameRegistry.registerWorldGenerator(new PC_WorldGenerator(), 0);
		GameRegistry.registerFuelHandler(new PC_FuelHandler());
		PC_BuildingRegistry.register(new PC_CropHarvesting());
		PC_BuildingRegistry.register(new PC_TreeHarvesting());
		List<PC_ModuleObject> modules = PC_ModuleRegistry.getModuleList();
		creativeTab = new PC_CreativeTab();
		PC_Logger.enterSection("Module Init");
		for (PC_ModuleObject module : modules) {
			if (module.getModuleName() != "Api")
				if (module.enabled)
					module.init();
		}
		PC_Logger.exitSection();
		PC_Logger.enterSection("Module Field Init");
		for (PC_ModuleObject module : modules) {
			if (module.enabled)
				PC_ReflectHelper.getAllFieldsWithAnnotation(module.getModuleClass(), module, PC_FieldObject.class,
						getModuleFieldInit(module));
		}
		PC_Logger.exitSection();
		PC_Logger.enterSection("Module Entity Init");
		for (PC_ModuleObject module : modules) {
			if (module.enabled) {
				List<PC_Struct2<Class<? extends Entity>, Integer>> l = module
						.initEntities(new ArrayList<PC_Struct2<Class<? extends Entity>, Integer>>());
				if (l != null) {
					for (PC_Struct2<Class<? extends Entity>, Integer> entity : l) {
						PC_EntityRegistry.register(module, entity.a, entity.b);
					}
				}
			}
		}
		PC_Logger.exitSection();
		PC_Logger.enterSection("Module Container Init");
		for (PC_ModuleObject module : modules) {
			if (module.enabled) {
				List<PC_Struct2<String, Class<PC_GresBaseWithInventory>>> l = module
						.registerContainers(new ArrayList<PC_Struct2<String, Class<PC_GresBaseWithInventory>>>());
				if (l != null) {
					for (PC_Struct2<String, Class<PC_GresBaseWithInventory>> g : l) {
						PC_GresRegistry.registerGresContainer(g.a, g.b);
					}
				}
			}
		}
		PC_Logger.exitSection();
		clientInit(modules);
		PC_Logger.exitSection();
	}

	protected ModuleFieldInit getModuleFieldInit(PC_ModuleObject module) {
		return new ModuleFieldInit(module);
	}

	protected void clientInit(List<PC_ModuleObject> modules) {
		new PC_Renderer(true);
		new PC_Renderer(false);
	}

	@PC_PostInit
	public void postInit() {
		PC_Logger.enterSection("PostInit");
		PC_Logger.enterSection("Module Recipes Init");
		List<PC_ModuleObject> modules = PC_ModuleRegistry.getModuleList();
		for (PC_ModuleObject module : modules) {
			if (module.enabled) {
				List<PC_IRecipe> l = module.initRecipes(new ArrayList<PC_IRecipe>());
				if (l != null) {
					for (PC_IRecipe recipe : l) {
						PC_RecipeRegistry.register(recipe);
					}
				}
			}
		}
		PC_Logger.exitSection();
		PC_Logger.enterSection("Module Data Handlers Init");
		PC_DataHandlerRegistry.regsterDataHandler("chunckUpdateForcer", PC_ChunkForcerRegistry.getInstance());
		for (PC_ModuleObject module : modules) {
			if (module.enabled) {
				List<PC_Struct2<String, PC_IDataHandler>> l = module
						.initDataHandlers(new ArrayList<PC_Struct2<String, PC_IDataHandler>>());
				if (l != null) {
					for (PC_Struct2<String, PC_IDataHandler> dataHandler : l) {
						PC_DataHandlerRegistry.regsterDataHandler(dataHandler.a, dataHandler.b);
					}
				}
			}
		}
		PC_Logger.exitSection();
		PC_Logger.enterSection("Module Packet Handlers Init");
		for (PC_ModuleObject module : modules) {
			if (module.enabled) {
				List<Class> l = module.initPackets(new ArrayList<Class>());
				if (l != null) {
					for (Class c : l) {
						PC_PacketHandler.registry(c);
					}
				}
			}
		}
		PC_Logger.exitSection();
		PC_Logger.enterSection("Module PostInit");
		for (PC_ModuleObject module : modules) {
			if (module.getModuleName() != "Api")
				if (module.enabled)
					module.postInit();
		}
		PC_Logger.exitSection();
		clientPostInit(modules);
		PC_Logger.enterSection("Module Config Saving");
		for (PC_ModuleObject module : modules) {
			module.saveConfig();
		}
		PC_GlobalVariables.saveConfig();
		PC_Logger.exitSection();
		PC_Logger.exitSection();
	}

	protected void clientPostInit(List<PC_ModuleObject> modules) {

	}

	@PC_InitProperties
	public void initProperties(PC_Property config) {

	}

	@PC_RegisterEventHandlers
	public List<String> registerEventHandlers(List<String> eventHandlers) {
		eventHandlers.add("powercraft.api.renderer.PC_BBRenderer");
		eventHandlers.add("powercraft.api.PC_ResourceReloader");
		return eventHandlers;
	}

	public static PC_APIModule getInstance() {
		return (PC_APIModule) instance.getModule();
	}

	protected static class ModuleFieldInit implements PC_IFieldAnnotationIterator<PC_FieldObject> {

		protected PC_ModuleObject module;

		public ModuleFieldInit(PC_ModuleObject module) {
			this.module = module;
		}

		@Override
		public boolean onFieldWithAnnotation(PC_FieldWithAnnotation<PC_FieldObject> fieldWithAnnotation) {
			Class<?> clazz = fieldWithAnnotation.getAnnotation().clazz();
			Object o;
			if (PC_Block.class.isAssignableFrom(clazz)) {
				o = PC_BlockRegistry.register(module, (Class<? extends PC_Block>) clazz);
			} else if (PC_Item.class.isAssignableFrom(clazz) || PC_ItemArmor.class.isAssignableFrom(clazz)) {
				o = PC_ItemRegistry.register(module, (Class<? extends Item>) clazz);
			} else {
				o = PC_ReflectHelper.create(clazz);
			}
			registerObject(o);
			fieldWithAnnotation.setValue(o);
			return false;
		}

		protected void registerObject(Object object) {
			if (object instanceof PC_IMSG) {
				PC_MSGRegistry.registerMSGObject((PC_IMSG) object);
			}
			if (object instanceof PC_ISpecialHarvesting) {
				PC_BuildingRegistry.register((PC_ISpecialHarvesting) object);
			}
			if (object instanceof PC_IDataHandler) {
				PC_DataHandlerRegistry.regsterDataHandler(((PC_IDataHandler) object).getName(),
						(PC_IDataHandler) object);
			}
		}

	}

}
