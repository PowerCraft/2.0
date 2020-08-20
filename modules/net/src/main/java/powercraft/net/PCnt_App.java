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
import powercraft.api.registry.PC_ItemRegistry;
import powercraft.api.utils.PC_GlobalVariables;
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
	public void initRecipes() {
		// MEDIUM RECIPES
		if (PC_GlobalVariables.mediumRecipes) {
			GameRegistry.addRecipe(new ItemStack(sensor, 1, 1), new Object[] { " C ", " R ", "SSS", 'R', Items.redstone,
					'C', PC_ItemRegistry.getPCItemByName("PCco_ItemSensorCore"), 'S', Blocks.stone });

			GameRegistry.addRecipe(new ItemStack(sensor, 1, 0), new Object[] { " C ", " R ", "WWW", 'R', Items.redstone,
					'C', PC_ItemRegistry.getPCItemByName("PCco_ItemSensorCore"), 'W', Blocks.planks });

			GameRegistry.addRecipe(new ItemStack(sensor, 1, 2), new Object[] { " C ", " R ", "OOO", 'R', Items.redstone,
					'C', PC_ItemRegistry.getPCItemByName("PCco_ItemSensorCore"), 'O', Blocks.obsidian });

			GameRegistry.addRecipe(new ItemStack(radio, 1, 0), new Object[] { " G ", "RIR", "SSS", 'I',
					Items.iron_ingot, 'G', Items.gold_ingot, 'R', Items.redstone, 'S', Blocks.stone });

			GameRegistry.addRecipe(new ItemStack(radio, 1, 1), new Object[] { " I ", "RQR", "SSS", 'Q', Items.quartz,
					'I', Items.iron_ingot, 'R', Items.redstone, 'S', Blocks.stone });

			GameRegistry.addRecipe(new ItemStack(portableTx),
					new Object[] { " T ", "IBI", " I ", 'I', Items.iron_ingot, 'B', Blocks.stone_button, 'T', radio });

		// LEGACY RECIPES
		} else if (PC_GlobalVariables.legacyRecipes) {
			GameRegistry.addRecipe(new ItemStack(sensor, 1, 1),
					new Object[] { "R", "I", "S", 'R', Items.redstone, 'I', Items.iron_ingot, 'S', Blocks.stone });

			GameRegistry.addRecipe(new ItemStack(sensor, 1, 0),
					new Object[] { "R", "I", "W", 'R', Items.redstone, 'I', Items.iron_ingot, 'W', Blocks.planks });

			GameRegistry.addRecipe(new ItemStack(sensor, 1, 2),
					new Object[] { "R", "I", "O", 'R', Items.redstone, 'I', Items.iron_ingot, 'O', Blocks.obsidian });

			GameRegistry.addRecipe(new ItemStack(radio, 1, 0), new Object[] { " G ", "RGR", "SSS", 'G',
					Items.gold_ingot, 'R', Items.redstone, 'S', Blocks.stone });

			GameRegistry.addRecipe(new ItemStack(radio, 1, 1), new Object[] { " I ", "RIR", "SSS", 'I',
					Items.iron_ingot, 'R', Items.redstone, 'S', Blocks.stone });

			GameRegistry.addRecipe(new ItemStack(portableTx),
					new Object[] { "T", "B", 'I', Items.iron_ingot, 'B', Blocks.stone_button, 'T', radio });
		}
	}

	@PC_InitDataHandlers
	public List<PC_Struct2<String, PC_IDataHandler>> initDataHandlers(
			List<PC_Struct2<String, PC_IDataHandler>> dataHandlers) {
		dataHandlers.add(new PC_Struct2<String, PC_IDataHandler>("Radio", radioManager));
		return dataHandlers;
	}

}
