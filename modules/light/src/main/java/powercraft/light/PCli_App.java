package powercraft.light;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import powercraft.api.annotation.PC_FieldObject;
import powercraft.api.block.PC_Block;
import powercraft.api.item.PC_Item;
import powercraft.api.recipes.PC_IRecipe;
import powercraft.api.registry.PC_BlockRegistry;
import powercraft.api.registry.PC_ItemRegistry;
import powercraft.api.utils.PC_GlobalVariables;
import powercraft.launcher.loader.PC_Module;
import powercraft.launcher.loader.PC_Module.PC_InitPackets;
import powercraft.launcher.loader.PC_Module.PC_InitRecipes;
import powercraft.launcher.loader.PC_Module.PC_Instance;
import powercraft.launcher.loader.PC_ModuleObject;
import powercraft.light.block.PCli_BlockLaser;
import powercraft.light.block.PCli_BlockLaserSensor;
import powercraft.light.block.PCli_BlockLight;
import powercraft.light.block.PCli_BlockLightningConductor;
import powercraft.light.block.PCli_BlockMirror;
import powercraft.light.block.PCli_BlockPrism;
import powercraft.light.item.PCli_ItemLaserComposition;
import powercraft.light.packet.PCli_PacketLaser;

@PC_Module(name = "Light", version = "@Version@")
public class PCli_App {

	@PC_FieldObject(clazz = PCli_BlockLight.class)
	public static PC_Block light;
	@PC_FieldObject(clazz = PCli_BlockLightningConductor.class)
	public static PC_Block lightningConductor;
	@PC_FieldObject(clazz = PCli_BlockLaser.class)
	public static PC_Block laser;
	@PC_FieldObject(clazz = PCli_BlockMirror.class)
	public static PC_Block mirror;
	@PC_FieldObject(clazz = PCli_BlockPrism.class)
	public static PC_Block prism;
	@PC_FieldObject(clazz = PCli_BlockLaserSensor.class)
	public static PC_Block laserSensor;
	@PC_FieldObject(clazz = PCli_ItemLaserComposition.class)
	public static PC_Item laserComposition;
	@PC_Instance
	public static PC_ModuleObject instance;

	@PC_InitRecipes
	public void initRecipes() {
		// MEDIUM RECIPES
		if (PC_GlobalVariables.mediumRecipes) {
			GameRegistry.addRecipe(new ItemStack(light), new Object[] { "XXX", "XGX", "IRI", 'I', Items.iron_ingot, 'X',
					Blocks.glass_pane, 'R', Items.redstone, 'G', Blocks.glowstone });

			GameRegistry.addRecipe(new ItemStack(lightningConductor),
					new Object[] { " X ", "IXI", "XXX", 'X', Blocks.iron_block, 'I', Items.iron_ingot });

			GameRegistry.addRecipe(new ItemStack(laser), new Object[] { " ID", " P ", "SRS", 'S', Blocks.stone, 'P',
					Blocks.planks, 'D', Items.diamond, 'R', Items.redstone, 'I', Blocks.iron_block });

			GameRegistry.addRecipe(new ItemStack(laserSensor, 1),
					new Object[] { "GGG", "GCG", "SRS", 'G', Blocks.glass, 'S', Blocks.stone, 'C',
							PC_ItemRegistry.getPCItemByName("PCco_ItemSensorCore"), 'R', Items.redstone });

			GameRegistry.addRecipe(new ItemStack(mirror, 1, 0), new Object[] { "GGG", "GIG", "N N", 'G',
					Blocks.glass_pane, 'I', Blocks.iron_block, 'N', Items.iron_ingot });

			GameRegistry.addRecipe(new ItemStack(prism, 1),
					new Object[] { "IGI", "GGG", "IGI", 'G', Blocks.glass, 'I', Items.iron_ingot });

			List<ItemStack> l = laserComposition.getItemStacks(new ArrayList<ItemStack>());

			GameRegistry.addRecipe(l.get(0),
					new Object[] { "IXI", "XPX", "IDI", 'X', Blocks.glass, 'P',
							new ItemStack(PC_BlockRegistry.getPCBlockByName("PCco_BlockPowerCrystal"), 1, 3), 'I',
							Items.iron_ingot, 'D', Items.diamond });
			GameRegistry.addRecipe(l.get(1),
					new Object[] { "IXI", "XPX", "IDI", 'X', Blocks.glass, 'P',
							new ItemStack(PC_BlockRegistry.getPCBlockByName("PCco_BlockPowerCrystal"), 1, 1), 'I',
							Items.iron_ingot, 'D', Items.diamond });
			GameRegistry.addRecipe(l.get(2),
					new Object[] { "IXI", "XPX", "IDI", 'X', Blocks.glass, 'P',
							new ItemStack(PC_BlockRegistry.getPCBlockByName("PCco_BlockPowerCrystal"), 1, 2), 'I',
							Items.iron_ingot, 'D', Items.diamond });
			// LEGACY RECIPES
		} else if (PC_GlobalVariables.legacyRecipes) {
			GameRegistry.addShapelessRecipe(new ItemStack(light), new Object[] { Items.redstone, Blocks.glowstone });
			GameRegistry.addRecipe(new ItemStack(lightningConductor),
					new Object[] { " X ", " X ", "XXX", 'X', Blocks.iron_block });

			GameRegistry.addRecipe(new ItemStack(laser), new Object[] { " PD", " S ", "SSS", 'S', Blocks.cobblestone,
					'P', Blocks.planks, 'D', Items.diamond });

			GameRegistry.addRecipe(new ItemStack(laserSensor, 1),
					new Object[] { "L", "R", 'L', laser, 'R', Items.redstone });

			GameRegistry.addRecipe(new ItemStack(mirror, 2, 0),
					new Object[] { "GI", " I", 'G', Blocks.glass_pane, 'I', Items.iron_ingot });

			GameRegistry.addRecipe(new ItemStack(prism, 1), new Object[] { "GG", "GG", 'G', Blocks.glass });

			List<ItemStack> l = laserComposition.getItemStacks(new ArrayList<ItemStack>());

			GameRegistry.addRecipe(l.get(0), new Object[] { "GGG", "GPG", "GGG", 'G', Blocks.glass, 'P',
					new ItemStack(PC_BlockRegistry.getPCBlockByName("PCco_BlockPowerCrystal"), 1, 3) });
			GameRegistry.addRecipe(l.get(1), new Object[] { "GGG", "GPG", "GGG", 'G', Blocks.glass, 'P',
					new ItemStack(PC_BlockRegistry.getPCBlockByName("PCco_BlockPowerCrystal"), 1, 1) });
			GameRegistry.addRecipe(l.get(2), new Object[] { "GGG", "GPG", "GGG", 'G', Blocks.glass, 'P',
					new ItemStack(PC_BlockRegistry.getPCBlockByName("PCco_BlockPowerCrystal"), 1, 2) });
		}
	}

	@PC_InitPackets
	public List<Class> initPackets(List<Class> packets) {
		packets.add(PCli_PacketLaser.class);
		return packets;
	}

}
