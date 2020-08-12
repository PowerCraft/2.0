package powercraft.net;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import powercraft.api.annotation.PC_FieldObject;
import powercraft.api.block.PC_Block;
import powercraft.api.interfaces.PC_IDataHandler;
import powercraft.api.item.PC_Item;
import powercraft.api.recipes.PC_IRecipe;
import powercraft.api.registry.PC_ItemRegistry;
import powercraft.api.utils.PC_Struct2;
import powercraft.launcher.loader.PC_Module;
import powercraft.launcher.loader.PC_Module.PC_InitDataHandlers;
import powercraft.launcher.loader.PC_Module.PC_InitRecipes;
import powercraft.launcher.loader.PC_Module.PC_Instance;
import powercraft.launcher.loader.PC_ModuleObject;
import powercraft.net.block.PCnt_BlockRadio;
import powercraft.net.block.PCnt_BlockSensor;
import powercraft.net.item.PCnt_ItemRadioRemote;

@PC_Module(name = "Net", version = "@Version@")
public class PCnt_App {

	@PC_FieldObject(clazz = PCnt_BlockSensor.class)
	public static PC_Block sensor;
	@PC_FieldObject(clazz = PCnt_BlockRadio.class)
	public static PC_Block radio;
	@PC_FieldObject(clazz = PCnt_ItemRadioRemote.class)
	public static PC_Item portableTx;
	@PC_FieldObject(clazz = PCnt_RadioManager.class)
	public static PCnt_RadioManager radioManager;
	@PC_Instance
	public static PC_ModuleObject instance;

	@PC_InitRecipes
	public List<PC_IRecipe> initRecipes(List<PC_IRecipe> recipes) {
		if (PC_ItemRegistry.getPCItemByName("PCco_ItemSensorCore") != null) {
			GameRegistry.addRecipe(new ItemStack(sensor, 1, 1), new Object[] { "C", "I", "S", 'I', Items.iron_ingot,
					'C', PC_ItemRegistry.getPCItemByName("PCco_ItemSensorCore"), 'S', Blocks.stone });
			GameRegistry.addRecipe(new ItemStack(sensor, 1, 0), new Object[] { "C", "I", "W", 'I', Items.iron_ingot,
					'C', PC_ItemRegistry.getPCItemByName("PCco_ItemSensorCore"), 'W', Blocks.planks });

			GameRegistry.addRecipe(new ItemStack(sensor, 1, 2), new Object[] { "C", "I", "O", 'I', Items.iron_ingot,
					'C', PC_ItemRegistry.getPCItemByName("PCco_ItemSensorCore"), 'O', Blocks.obsidian });
		} else {
			GameRegistry.addRecipe(new ItemStack(sensor, 1, 1),
					new Object[] { "C", "I", "S", 'I', Items.iron_ingot, 'C', Items.gold_ingot, 'S', Blocks.stone });
			GameRegistry.addRecipe(new ItemStack(sensor, 1, 0),
					new Object[] { "C", "I", "W", 'I', Items.iron_ingot, 'C', Items.gold_ingot, 'W', Blocks.planks });
			GameRegistry.addRecipe(new ItemStack(sensor, 1, 2),
					new Object[] { "C", "I", "O", 'I', Items.iron_ingot, 'C', Items.gold_ingot, 'O', Blocks.obsidian });
		}
		GameRegistry.addRecipe(new ItemStack(radio, 1, 0),
				new Object[] { " I ", "RIR", "SSS", 'I', Items.gold_ingot, 'R', Items.redstone, 'S', Blocks.stone });
		GameRegistry.addRecipe(new ItemStack(radio, 1, 1),
				new Object[] { " I ", "RIR", "SSS", 'I', Items.iron_ingot, 'R', Items.redstone, 'S', Blocks.stone });
		GameRegistry.addRecipe(new ItemStack(portableTx),
				new Object[] { "T", "B", 'B', Blocks.stone_button, 'T', radio });
		return recipes;
	}

	@PC_InitDataHandlers
	public List<PC_Struct2<String, PC_IDataHandler>> initDataHandlers(
			List<PC_Struct2<String, PC_IDataHandler>> dataHandlers) {
		dataHandlers.add(new PC_Struct2<String, PC_IDataHandler>("Radio", radioManager));
		return dataHandlers;
	}

}
