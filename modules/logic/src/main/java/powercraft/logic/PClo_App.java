package powercraft.logic;

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
import powercraft.api.recipes.PC_ShapedRecipes;
import powercraft.api.registry.PC_BlockRegistry;
import powercraft.api.utils.PC_GlobalVariables;
import powercraft.api.utils.PC_Struct2;
import powercraft.launcher.loader.PC_Module;
import powercraft.launcher.loader.PC_Module.PC_InitRecipes;
import powercraft.launcher.loader.PC_Module.PC_Instance;
import powercraft.launcher.loader.PC_Module.PC_RegisterContainers;
import powercraft.launcher.loader.PC_ModuleObject;
import powercraft.logic.block.PClo_BlockDelayer;
import powercraft.logic.block.PClo_BlockFlipFlop;
import powercraft.logic.block.PClo_BlockGate;
import powercraft.logic.block.PClo_BlockPulsar;
import powercraft.logic.block.PClo_BlockRepeater;
import powercraft.logic.block.PClo_BlockSpecial;
import powercraft.logic.block.PClo_BlockWire;
import powercraft.logic.container.PClo_ContainerSpecial;
import powercraft.logic.item.PClo_ItemPulsarCore;
import powercraft.logic.type.PClo_DelayerType;
import powercraft.logic.type.PClo_FlipFlopType;
import powercraft.logic.type.PClo_GateType;
import powercraft.logic.type.PClo_RepeaterType;
import powercraft.logic.type.PClo_SpecialType;

@PC_Module(name = "Logic", version = "@Version@")
public class PClo_App {

	@PC_FieldObject(clazz = PClo_BlockPulsar.class)
	public static PC_Block pulsar;
	@PC_FieldObject(clazz = PClo_BlockGate.class)
	public static PC_Block gate;
	@PC_FieldObject(clazz = PClo_BlockFlipFlop.class)
	public static PC_Block flipFlop;
	@PC_FieldObject(clazz = PClo_BlockDelayer.class)
	public static PC_Block delayer;
	@PC_FieldObject(clazz = PClo_BlockSpecial.class)
	public static PC_Block special;
	@PC_FieldObject(clazz = PClo_BlockRepeater.class)
	public static PC_Block repeater;
	@PC_FieldObject(clazz = PClo_BlockWire.class)
	public static PC_Block wire;
	@PC_FieldObject(clazz = PClo_ItemPulsarCore.class)
	public static PC_Item pulsarCore;
	@PC_Instance
	public static PC_ModuleObject instance;

	@PC_InitRecipes
	public void initRecipes() {
		GameRegistry.addRecipe(new ItemStack(pulsar, 1, 0),
				new Object[] { " ro", "rcr", "or ", 'c', pulsarCore, 'r', Items.redstone, 'o', Blocks.obsidian });
		if (PC_BlockRegistry.getPCBlockByName("PCco_BlockPowerCrystal") != null) {
			GameRegistry.addRecipe(new ItemStack(pulsarCore, 1),
					new Object[] { "LTL", "ROG", "LTL", 'L',
							new ItemStack(this.repeater, 1, PClo_RepeaterType.REPEATER_CORNER), 'T', Items.redstone,
							'R', new ItemStack(PC_BlockRegistry.getPCBlockByName("PCco_BlockPowerCrystal"), 1, 1), 'G',
							new ItemStack(PC_BlockRegistry.getPCBlockByName("PCco_BlockPowerCrystal"), 1, 2), 'O',
							Blocks.obsidian });
		} else {
			GameRegistry.addRecipe(new ItemStack(pulsarCore, 1),
					new Object[] { "LTL", "ROR", "LTL", 'L',
							new ItemStack(this.repeater, 1, PClo_RepeaterType.REPEATER_CORNER), 'T', Items.redstone,
							'R', Items.gold_ingot, 'O', Blocks.obsidian });
		}
		GameRegistry.addRecipe(new ItemStack(gate, 1, PClo_GateType.NOT),
				new Object[] { "rst", 'r', Items.redstone, 's', Blocks.stone, 't', Blocks.redstone_torch });
		GameRegistry.addRecipe(new ItemStack(gate, 1, PClo_GateType.AND),
				new Object[] { " r ", "sss", "rrr", 'r', Items.redstone, 's', Blocks.stone });
		GameRegistry.addRecipe(new ItemStack(gate, 1, PClo_GateType.OR),
				new Object[] { " r ", "rsr", " r ", 'r', Items.redstone, 's', Blocks.stone });
		GameRegistry.addRecipe(new ItemStack(gate, 1, PClo_GateType.XOR),
				new Object[] { "r", "x", 'r', Items.redstone, 'x', new ItemStack(gate, 1, PClo_GateType.OR) });
		GameRegistry.addRecipe(new ItemStack(gate, 1, PClo_GateType.NAND), new Object[] { "n", "a", 'n',
				new ItemStack(gate, 1, PClo_GateType.NOT), 'a', new ItemStack(gate, 1, PClo_GateType.AND) });
		GameRegistry.addRecipe(new ItemStack(gate, 1, PClo_GateType.NOR), new Object[] { "n", "o", 'n',
				new ItemStack(gate, 1, PClo_GateType.NOT), 'o', new ItemStack(gate, 1, PClo_GateType.OR) });
		GameRegistry.addRecipe(new ItemStack(gate, 1, PClo_GateType.XNOR), new Object[] { "n", "x", 'n',
				new ItemStack(gate, 1, PClo_GateType.NOT), 'x', new ItemStack(gate, 1, PClo_GateType.XOR) });
		GameRegistry.addRecipe(new ItemStack(flipFlop, 1, PClo_FlipFlopType.D),
				new Object[] { " S ", "RSR", " S ", 'S', Blocks.stone, 'R', Items.redstone });
		GameRegistry.addRecipe(new ItemStack(flipFlop, 1, PClo_FlipFlopType.RS),
				new Object[] { " R ", "SLS", "R R", 'R', Items.redstone, 'S', Blocks.stone, 'L', Blocks.lever });
		GameRegistry.addRecipe(new ItemStack(flipFlop, 1, PClo_FlipFlopType.T),
				new Object[] { "RSR", 'R', Items.redstone, 'S', Blocks.stone });
		GameRegistry.addRecipe(new ItemStack(flipFlop, 1, PClo_FlipFlopType.RANDOM),
				new Object[] { "R", "T", 'R', Items.redstone, 'T', new ItemStack(flipFlop, 1, PClo_FlipFlopType.T) });
		// recipes.add(new PC_ShapedRecipes(new ItemStack(delayer, 1,
		// PClo_DelayerType.FIFO), "DDD", "SSS", 'D',
		// Items.repeater, 'S', Blocks.stone));
		GameRegistry.addRecipe(new ItemStack(delayer, 1, PClo_DelayerType.HOLD),
				new Object[] { "DD", "SS", 'D', Items.repeater, 'S', Blocks.stone });
		GameRegistry.addRecipe(new ItemStack(special, 1, PClo_SpecialType.DAY),
				new Object[] { "G", "P", 'G', Items.glowstone_dust, 'P', Blocks.wooden_pressure_plate });
		GameRegistry.addRecipe(new ItemStack(special, 1, PClo_SpecialType.DAY),
				new Object[] { "G", "P", 'G', Items.glowstone_dust, 'P', Blocks.stone_pressure_plate });
		GameRegistry.addRecipe(new ItemStack(special, 1, PClo_SpecialType.NIGHT), new Object[] { "N", "G", 'N',
				new ItemStack(gate, 1, PClo_GateType.NOT), 'G', new ItemStack(special, 1, PClo_SpecialType.DAY) });
		GameRegistry.addRecipe(new ItemStack(special, 1, PClo_SpecialType.RAIN),
				new Object[] { "L", "P", 'L', new ItemStack(Items.dye, 1, 4), 'P', Blocks.wooden_pressure_plate });
		GameRegistry.addRecipe(new ItemStack(special, 1, PClo_SpecialType.RAIN),
				new Object[] { "L", "P", 'L', new ItemStack(Items.dye, 1, 4), 'P', Blocks.stone_pressure_plate });
		GameRegistry.addRecipe(new ItemStack(special, 1, PClo_SpecialType.CHEST_EMPTY),
				new Object[] { "C", "P", 'C', Blocks.chest, 'P', Blocks.wooden_pressure_plate });
		GameRegistry.addRecipe(new ItemStack(special, 1, PClo_SpecialType.CHEST_EMPTY),
				new Object[] { "C", "P", 'C', Blocks.chest, 'P', Blocks.stone_pressure_plate });
		GameRegistry.addRecipe(new ItemStack(special, 1, PClo_SpecialType.CHEST_FULL),
				new Object[] { "I", "G", 'I', new ItemStack(gate, 1, PClo_GateType.NOT), 'G',
						new ItemStack(special, 1, PClo_SpecialType.CHEST_EMPTY) });
		GameRegistry.addRecipe(new ItemStack(special, 1, PClo_SpecialType.SPECIAL),
				new Object[] { " I", "RS", 'R', Items.redstone, 'S', Blocks.stone, 'I', Items.iron_ingot });
		GameRegistry.addRecipe(new ItemStack(repeater, 1, PClo_RepeaterType.REPEATER_STRAIGHT),
				new Object[] { "R", "R", "R", 'R', Items.redstone });
		GameRegistry.addRecipe(new ItemStack(repeater, 1, PClo_RepeaterType.REPEATER_CORNER),
				new Object[] { "RR", " R", 'R', Items.redstone });
		GameRegistry.addRecipe(new ItemStack(repeater, 1, PClo_RepeaterType.REPEATER_STRAIGHT_I), new Object[] { "R",
				"S", 'R', Items.redstone, 'S', new ItemStack(repeater, 1, PClo_RepeaterType.REPEATER_STRAIGHT) });
		GameRegistry.addRecipe(new ItemStack(repeater, 1, PClo_RepeaterType.REPEATER_CORNER_I), new Object[] { "R", "S",
				'R', Items.redstone, 'S', new ItemStack(repeater, 1, PClo_RepeaterType.REPEATER_CORNER) });
		GameRegistry.addRecipe(new ItemStack(repeater, 1, PClo_RepeaterType.CROSSING),
				new Object[] { " r ", "rrr", " r ", 'r', Items.redstone });
		GameRegistry.addRecipe(new ItemStack(repeater, 1, PClo_RepeaterType.SPLITTER_I),
				new Object[] { "SrS", "rrr", "SrS", 'r', Items.redstone, 'S', Blocks.stone });

		// MEDIUM RECIPES
		if (PC_GlobalVariables.mediumRecipes) {
			GameRegistry.addRecipe(new ItemStack(wire, 1),
					new Object[] { "SSS", "RRR", "SSS", 'R', Items.redstone, 'S', Blocks.stone_slab });

			// LEGACY RECIPES
		} else if (PC_GlobalVariables.legacyRecipes) {
			GameRegistry.addRecipe(new ItemStack(wire, 3),
					new Object[] { "SSS", "RRR", "SSS", 'R', Items.redstone, 'S', Blocks.stone_slab });
		}

	}

	@PC_RegisterContainers
	public List<PC_Struct2<String, Class<? extends PC_GresBaseWithInventory>>> registerContainers(
			List<PC_Struct2<String, Class<? extends PC_GresBaseWithInventory>>> guis) {
		guis.add(new PC_Struct2<String, Class<? extends PC_GresBaseWithInventory>>("Special",
				PClo_ContainerSpecial.class));
		return guis;
	}
}
