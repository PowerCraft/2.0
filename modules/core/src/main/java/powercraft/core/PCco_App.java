package powercraft.core;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import powercraft.api.annotation.PC_FieldObject;
import powercraft.api.block.PC_Block;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.item.PC_Item;
import powercraft.api.recipes.PC_IRecipe;
import powercraft.api.utils.PC_GlobalVariables;
import powercraft.api.utils.PC_Struct2;
import powercraft.core.block.PCco_BlockBlockSaver;
import powercraft.core.block.PCco_BlockPowerCrystal;
import powercraft.core.container.PCco_ContainerCraftingTool;
import powercraft.core.item.PCco_ItemActivator;
import powercraft.core.item.PCco_ItemAnnihilationCore;
import powercraft.core.item.PCco_ItemCraftingCore;
import powercraft.core.item.PCco_ItemCraftingTool;
import powercraft.core.item.PCco_ItemFormationCore;
import powercraft.core.item.PCco_ItemOreSniffer;
import powercraft.core.item.PCco_ItemPowerDust;
import powercraft.core.item.PCco_ItemSensorCore;
import powercraft.launcher.PC_Property;
import powercraft.launcher.loader.PC_Module;
import powercraft.launcher.loader.PC_Module.PC_InitPackets;
import powercraft.launcher.loader.PC_Module.PC_InitProperties;
import powercraft.launcher.loader.PC_Module.PC_InitRecipes;
import powercraft.launcher.loader.PC_Module.PC_RegisterContainers;

@PC_Module(name = "Core", version = "@Version@")
public class PCco_App {

	@PC_FieldObject(clazz = PCco_BlockPowerCrystal.class)
	public static PC_Block powerCrystal;
	@PC_FieldObject(clazz = PCco_BlockBlockSaver.class)
	public static PC_Block blockSaver;
	@PC_FieldObject(clazz = PCco_ItemPowerDust.class)
	public static PC_Item powerDust;
	@PC_FieldObject(clazz = PCco_ItemActivator.class)
	public static PC_Item activator;
	@PC_FieldObject(clazz = PCco_ItemCraftingTool.class)
	public static PC_Item craftingTool;
	@PC_FieldObject(clazz = PCco_ItemOreSniffer.class)
	public static PC_Item oreSniffer;
	@PC_FieldObject(clazz = PCco_ItemCraftingCore.class)
	public static PC_Item craftingCore;
	@PC_FieldObject(clazz = PCco_ItemFormationCore.class)
	public static PC_Item formationCore;
	@PC_FieldObject(clazz = PCco_ItemAnnihilationCore.class)
	public static PC_Item annihilationCore;
	@PC_FieldObject(clazz = PCco_ItemSensorCore.class)
	public static PC_Item sensorCore;
	@PC_FieldObject(clazz = PCco_MobSpawnerSetter.class)
	public static PCco_MobSpawnerSetter spawnerSetter;

	@PC_InitProperties
	public void initProperties(PC_Property config) {
		PC_GlobalVariables.consts.put("recipes.recyclation",
				config.getBoolean("recipes.recyclation", true, "Add new recypes allowing easy material recyclation"));
		PC_GlobalVariables.consts.put("recipes.spawner",
				config.getBoolean("recipes.spawner", true, "Make spawners craftable of iron and mossy cobble"));
	}

	@PC_InitRecipes
	public void initRecipes() {
		// MEDIUM RECIPES
		if (PC_GlobalVariables.mediumRecipes) {
			// cores
			GameRegistry.addRecipe(new ItemStack(sensorCore),
					new Object[] { "RGE", "ICI", "EGR", 'I', Items.iron_ingot, 'R', Items.redstone, 'G',
							Items.gold_ingot, 'E', Items.ender_pearl, 'C', new ItemStack(powerCrystal, 1, 6) });

			GameRegistry.addRecipe(new ItemStack(annihilationCore),
					new Object[] { "ILI", "GPG", "RRR", 'I', Items.iron_ingot, 'L', Blocks.glass_pane, 'G',
							Items.gold_ingot, 'R', Items.redstone, 'P', Items.diamond_pickaxe });

			GameRegistry.addRecipe(new ItemStack(formationCore),
					new Object[] { "ILI", "GDG", "RRR", 'I', Items.iron_ingot, 'L', Blocks.glass_pane, 'G',
							Items.gold_ingot, 'R', Items.redstone, 'D', Items.diamond });

			GameRegistry.addRecipe(new ItemStack(craftingCore, 1),
					new Object[] { "#R#", "EXE", "#R#", 'X', new ItemStack(powerCrystal, 1, 1), 'E', Items.ender_pearl,
							'#', Blocks.crafting_table, 'R', Items.redstone });

			// other
			GameRegistry.addRecipe(new ItemStack(craftingTool, 1), new Object[] { "RI ", "GXG", " IR", 'X',
					craftingCore, 'G', Items.glowstone_dust, 'I', Items.iron_ingot, 'R', Items.redstone });

			GameRegistry.addShapelessRecipe(new ItemStack(powerDust, 4), new Object[] { powerCrystal });// TODO: where
																										// it using??

			GameRegistry.addRecipe(new ItemStack(activator, 1), new Object[] { "IXI", " R ", " I ", 'X', powerCrystal,
					'I', Items.iron_ingot, 'R', Items.redstone });

			GameRegistry.addRecipe(new ItemStack(oreSniffer, 1), new Object[] { " RD", "GCG", "DR ", 'G',
					Items.gold_ingot, 'D', Items.diamond, 'C', powerCrystal, 'R', Items.redstone });

			if ((Boolean) PC_GlobalVariables.consts.get("recipes.spawner")) {
				GameRegistry.addRecipe(new ItemStack(Blocks.mob_spawner, 1), new Object[] { "#B#", "BCB", "#B#", '#',
						Blocks.iron_bars, 'B', Blocks.mossy_cobblestone, 'C', formationCore });
			}

			// LEGACY RECIPES
		} else if (PC_GlobalVariables.legacyRecipes) {
			GameRegistry.addRecipe(new ItemStack(craftingTool, 1),
					new Object[] { " R ", "RIR", " R ", 'I', Blocks.iron_block, 'R', Items.redstone });

			GameRegistry.addShapelessRecipe(new ItemStack(powerDust, 4), new Object[] { powerCrystal });// TODO: where
																										// it using??

			GameRegistry.addRecipe(new ItemStack(activator, 1),
					new Object[] { "X", "I", 'X', powerCrystal, 'I', Items.iron_ingot });

			GameRegistry.addRecipe(new ItemStack(oreSniffer, 1),
					new Object[] { " G ", "GCG", " G ", 'G', Items.gold_ingot, 'C', powerCrystal });

			if ((Boolean) PC_GlobalVariables.consts.get("recipes.spawner")) {
				GameRegistry.addRecipe(new ItemStack(Blocks.mob_spawner, 1),
						new Object[] { "#B#", "B B", "#B#", '#', Items.iron_ingot, 'B', Blocks.mossy_cobblestone });
			}
		}

		// REVERSE RECIPES
		if ((Boolean) PC_GlobalVariables.consts.get("recipes.recyclation")) {
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.sand, 4), new Object[] { Blocks.sandstone });
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.planks, 6), new Object[] { Items.wooden_door });
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.planks, 8), new Object[] { Blocks.chest });
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.planks, 4), new Object[] { Blocks.crafting_table });
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.planks, 2),
					new Object[] { Blocks.wooden_pressure_plate });
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.stone, 2),
					new Object[] { Blocks.stone_pressure_plate });
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.stone, 1), new Object[] { Blocks.stone_button });
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.planks, 1), new Object[] { Blocks.wooden_button });
			GameRegistry.addShapelessRecipe(new ItemStack(Items.stick, 3), new Object[] { Blocks.fence });
			GameRegistry.addShapelessRecipe(new ItemStack(Items.stick, 7), new Object[] { Blocks.ladder });
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.planks, 6), new Object[] { Items.sign });
			GameRegistry.addShapelessRecipe(new ItemStack(Items.iron_ingot, 6), new Object[] { Items.iron_door });
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.cobblestone, 8), new Object[] { Blocks.furnace });
			GameRegistry.addShapelessRecipe(new ItemStack(Items.iron_ingot, 5), new Object[] { Items.minecart });
			GameRegistry.addShapelessRecipe(new ItemStack(Items.iron_ingot, 3), new Object[] { Items.bucket });
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.planks, 5), new Object[] { Items.boat });
			GameRegistry.addShapelessRecipe(new ItemStack(Items.stick, 8), new Object[] { Blocks.fence_gate });
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.stone, 1), new Object[] { Blocks.stonebrick });
			GameRegistry.addShapelessRecipe(new ItemStack(Items.iron_ingot, 7), new Object[] { Blocks.cauldron });
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.planks, 3), new Object[] { Blocks.trapdoor });
			GameRegistry.addShapelessRecipe(new ItemStack(Blocks.planks, 1), new Object[] { Items.stick, Items.stick });
		}
	}

	@PC_RegisterContainers
	public List<PC_Struct2<String, Class<? extends PC_GresBaseWithInventory>>> registerContainers(
			List<PC_Struct2<String, Class<? extends PC_GresBaseWithInventory>>> guis) {
		guis.add(new PC_Struct2<String, Class<? extends PC_GresBaseWithInventory>>("CraftingTool",
				PCco_ContainerCraftingTool.class));
		return guis;
	}

	@PC_InitPackets
	public List<Class> initPackets(List<Class> packets) {
		packets.add(PCco_PacketMobSpawnerSetter.class);
		return packets;
	}

}
