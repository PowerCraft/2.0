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
	public List<PC_IRecipe> initRecipes(List<PC_IRecipe> recipes) {
		GameRegistry.addShapelessRecipe(new ItemStack(light), new Object[] { Items.redstone, Blocks.glowstone });
		GameRegistry.addRecipe(new ItemStack(lightningConductor),
				new Object[] { " X ", " X ", "XXX", 'X', Blocks.iron_block });

		GameRegistry.addRecipe(new ItemStack(laser), new Object[] { "RWD", " S ", "SSS", 'S', Blocks.stone, 'W',
				new ItemStack(Blocks.planks, 1), 'D', Items.diamond, 'R', Items.redstone });
		GameRegistry.addRecipe(new ItemStack(laser), new Object[] { "RWD", " S ", "SSS", 'S', Blocks.cobblestone, 'W',
				new ItemStack(Blocks.planks, 1), 'D', Items.diamond, 'R', Items.redstone });

		if (PC_ItemRegistry.getPCItemByName("PCco_ItemSensorCore") != null) {
			GameRegistry.addRecipe(new ItemStack(laserSensor, 1),
					new Object[] { "L", "C", "R", 'C', PC_ItemRegistry.getPCItemByName("PCco_ItemSensorCore"), 'L',
							new ItemStack(laser, 1), 'R', Items.redstone });
		} else {
			GameRegistry.addRecipe(new ItemStack(laserSensor, 1), new Object[] { "L", "C", "R", 'C', Items.diamond, 'L',
					new ItemStack(laser, 1), 'R', Items.redstone });
		}
		GameRegistry.addRecipe(new ItemStack(mirror, 2, 0),
				new Object[] { "GI", " I", 'G', Blocks.glass_pane, 'I', Items.iron_ingot });

		GameRegistry.addRecipe(new ItemStack(prism, 1), new Object[] { "GG", "GG", 'G', Blocks.glass });

		List<ItemStack> l = laserComposition.getItemStacks(new ArrayList<ItemStack>());

		if (PC_BlockRegistry.getPCBlockByName("PCco_BlockPowerCrystal") != null) {
			GameRegistry.addRecipe(l.get(0), new Object[] { "XXX", "XPX", "XXX", 'X', Blocks.glass, 'P',
					new ItemStack(PC_BlockRegistry.getPCBlockByName("PCco_BlockPowerCrystal"), 1, 3) });

			GameRegistry.addRecipe(l.get(1), new Object[] { "XXX", "XPX", "XXX", 'X', Blocks.glass, 'P',
					new ItemStack(PC_BlockRegistry.getPCBlockByName("PCco_BlockPowerCrystal"), 1, 1) });

			GameRegistry.addRecipe(l.get(2), new Object[] { "XXX", "XPX", "XXX", 'X', Blocks.glass, 'P',
					new ItemStack(PC_BlockRegistry.getPCBlockByName("PCco_BlockPowerCrystal"), 1, 2) });
		} else {
			GameRegistry.addRecipe(l.get(0), new Object[] { // TODO: change meta for glass blocks
					"XXX", "XPX", "XXX", 'X', new ItemStack(Blocks.glass, 1, 2), 'P', Items.diamond });

			GameRegistry.addRecipe(l.get(1),
					new Object[] { "XXX", "XPX", "XXX", 'X', new ItemStack(Blocks.glass, 1, 3), 'P', Items.diamond });

			GameRegistry.addRecipe(l.get(2),
					new Object[] { "XXX", "XPX", "XXX", 'X', new ItemStack(Blocks.glass, 1, 4), 'P', Items.diamond });
		}
		return recipes;
	}

	@PC_InitPackets
	public List<Class> initPackets(List<Class> packets) {
		packets.add(PCli_PacketLaser.class);
		return packets;
	}

}
