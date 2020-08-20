package powercraft.transport;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import powercraft.api.annotation.PC_FieldObject;
import powercraft.api.block.PC_Block;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.item.PC_ItemArmor;
import powercraft.api.recipes.PC_IRecipe;
import powercraft.api.utils.PC_GlobalVariables;
import powercraft.api.utils.PC_Struct2;
import powercraft.launcher.loader.PC_Module;
import powercraft.launcher.loader.PC_Module.PC_InitRecipes;
import powercraft.launcher.loader.PC_Module.PC_RegisterContainers;
import powercraft.transport.block.PCtr_BlockBeltBreak;
import powercraft.transport.block.PCtr_BlockBeltDetector;
import powercraft.transport.block.PCtr_BlockBeltEjector;
import powercraft.transport.block.PCtr_BlockBeltNormal;
import powercraft.transport.block.PCtr_BlockBeltRedirector;
import powercraft.transport.block.PCtr_BlockBeltSeparator;
import powercraft.transport.block.PCtr_BlockBeltSpeedy;
import powercraft.transport.block.PCtr_BlockElevator;
import powercraft.transport.block.PCtr_BlockSplitter;
import powercraft.transport.container.PCtr_ContainerSeparationBelt;
import powercraft.transport.container.PCtr_ContainerSplitter;
import powercraft.transport.item.PCtr_ItemArmorStickyBoots;

@PC_Module(name = "Transport", version = "@Version@")
public class PCtr_App {

	@PC_FieldObject(clazz = PCtr_BlockBeltNormal.class)
	public static PC_Block conveyorBelt;
	@PC_FieldObject(clazz = PCtr_BlockBeltSpeedy.class)
	public static PC_Block speedyBelt;
	@PC_FieldObject(clazz = PCtr_BlockBeltDetector.class)
	public static PC_Block detectionBelt;
	@PC_FieldObject(clazz = PCtr_BlockBeltBreak.class)
	public static PC_Block breakBelt;
	@PC_FieldObject(clazz = PCtr_BlockBeltRedirector.class)
	public static PC_Block redirectionBelt;
	@PC_FieldObject(clazz = PCtr_BlockBeltSeparator.class)
	public static PC_Block separationBelt;
	@PC_FieldObject(clazz = PCtr_BlockBeltEjector.class)
	public static PC_Block ejectionBelt;
	@PC_FieldObject(clazz = PCtr_BlockElevator.class)
	public static PC_Block elevator;
	@PC_FieldObject(clazz = PCtr_BlockSplitter.class)
	public static PC_Block splitter;
	@PC_FieldObject(clazz = PCtr_ItemArmorStickyBoots.class)
	public static PC_ItemArmor slimeboots;

	@PC_InitRecipes
	public void initRecipes() {
		//MEDIUM RECIPES
		if (PC_GlobalVariables.mediumRecipes) {
			GameRegistry.addRecipe(new ItemStack(conveyorBelt, 4), new Object[] { "XXX", "YRY", "   ", 'X',
					Items.leather, 'Y', Items.iron_ingot, 'R', Items.redstone });
			
			GameRegistry.addRecipe(new ItemStack(conveyorBelt, 1),
					new Object[] { "XXX", "YRY", "   ", 'X', Items.paper, 'Y', Items.iron_ingot, 'R', Items.redstone });
			
			GameRegistry.addRecipe(new ItemStack(speedyBelt, 4), new Object[] { "XXX", "YRY", "   ", 'X', Items.leather,
					'Y', Items.gold_ingot, 'R', Items.redstone });
			
			GameRegistry.addRecipe(new ItemStack(speedyBelt, 1),
					new Object[] { "XXX", "YRY", "   ", 'X', Items.paper, 'Y', Items.gold_ingot, 'R', Items.redstone });
			
			GameRegistry.addRecipe(new ItemStack(ejectionBelt, 1),
					new Object[] { "XXX", " Y ", " Z ", 'X', Items.bow, 'Y', conveyorBelt, 'Z', Items.redstone });
			
			GameRegistry.addRecipe(new ItemStack(detectionBelt, 1), new Object[] { "XXX", " Y ", " Z ", 'X',
					Blocks.stone_pressure_plate, 'Y', conveyorBelt, 'Z', Items.redstone });
			
			GameRegistry.addRecipe(new ItemStack(detectionBelt, 1), new Object[] { "XXX", " Y ", " Z ", 'X',
					Blocks.wooden_pressure_plate, 'Y', conveyorBelt, 'Z', Items.redstone });
			
			GameRegistry.addRecipe(new ItemStack(separationBelt, 1),
					new Object[] { " Y ", "RXR", " Z ", 'R', redirectionBelt, 'X', Items.diamond, 'Y', conveyorBelt, 'Z', Items.redstone });
			
			GameRegistry.addRecipe(new ItemStack(breakBelt, 1),
					new Object[] { "XXX", " Y ", " Z ", 'X', Blocks.iron_bars, 'Y', conveyorBelt, 'Z', Items.redstone });
			
			GameRegistry.addRecipe(new ItemStack(redirectionBelt, 1),
					new Object[] { "YXY", " Y ", "   ", 'X', conveyorBelt, 'Y', Items.redstone });
			
			GameRegistry.addRecipe(new ItemStack(elevator, 3, 0),
					new Object[] { "XGX", "X X", "XGX", 'X', conveyorBelt, 'G', Items.gold_ingot });
			
			GameRegistry.addRecipe(new ItemStack(elevator, 3, 1), new Object[] { "XGX", "XRX", "XGX", 'X', conveyorBelt,
					'G', Items.gold_ingot, 'R', Items.redstone });
			
			GameRegistry.addRecipe(new ItemStack(splitter, 1, 0),
					new Object[] { "U U", "XSX", "D D", 'U', new ItemStack(elevator, 1, 0), 'X', conveyorBelt, 'S',
							separationBelt, 'D', new ItemStack(elevator, 1, 1) });
			
			GameRegistry.addRecipe(new ItemStack(slimeboots, 1, 0),
					new Object[] { " B ", "SSS", " B ", 'B', Items.iron_boots, 'S', Items.slime_ball });
		//LEGACY RECIPES
		}else if(PC_GlobalVariables.legacyRecipes) {
			GameRegistry.addRecipe(new ItemStack(conveyorBelt, 16), new Object[] { "XXX", "YRY", "   ", 'X',
					Items.leather, 'Y', Items.iron_ingot, 'R', Items.redstone });
			
			GameRegistry.addRecipe(new ItemStack(conveyorBelt, 4),
					new Object[] { "XXX", "YRY", "   ", 'X', Items.paper, 'Y', Items.iron_ingot, 'R', Items.redstone });
			
			GameRegistry.addRecipe(new ItemStack(speedyBelt, 16), new Object[] { "XXX", "YRY", "   ", 'X', Items.leather,
					'Y', Items.gold_ingot, 'R', Items.redstone });
			
			GameRegistry.addRecipe(new ItemStack(speedyBelt, 4),
					new Object[] { "XXX", "YRY", "   ", 'X', Items.paper, 'Y', Items.gold_ingot, 'R', Items.redstone });
			
			GameRegistry.addRecipe(new ItemStack(ejectionBelt, 1),
					new Object[] { "X", "Y", "Z", 'X', Items.bow, 'Y', conveyorBelt, 'Z', Items.redstone });
			
			GameRegistry.addRecipe(new ItemStack(detectionBelt, 1), new Object[] { "X", "Y", "Z", 'X',
					Blocks.stone_pressure_plate, 'Y', conveyorBelt, 'Z', Items.redstone });
			
			GameRegistry.addRecipe(new ItemStack(detectionBelt, 1), new Object[] { "X", "Y", "Z", 'X',
					Blocks.wooden_pressure_plate, 'Y', conveyorBelt, 'Z', Items.redstone });
			
			GameRegistry.addRecipe(new ItemStack(separationBelt, 1),
					new Object[] { "X", "Y", "Z", 'R', redirectionBelt, 'X', Items.diamond, 'Y', conveyorBelt, 'Z', Items.redstone });
			
			GameRegistry.addRecipe(new ItemStack(breakBelt, 1),
					new Object[] { "X", "Y", "Z", 'X', Items.iron_ingot, 'Y', conveyorBelt, 'Z', Items.redstone });
			
			GameRegistry.addRecipe(new ItemStack(redirectionBelt, 1),
					new Object[] { "X", "Y", " ", 'X', conveyorBelt, 'Y', Items.redstone });
			
			GameRegistry.addRecipe(new ItemStack(elevator, 6, 0),
					new Object[] { "XGX", "X X", "XGX", 'X', conveyorBelt, 'G', Items.gold_ingot });
			
			GameRegistry.addRecipe(new ItemStack(elevator, 6, 1), new Object[] { "XGX", "XRX", "XGX", 'X', conveyorBelt,
					'G', Items.gold_ingot, 'R', Items.redstone });
			
			GameRegistry.addRecipe(new ItemStack(splitter, 1, 0),
					new Object[] { " U ", "XSX", " D ", 'U', new ItemStack(elevator, 1, 0), 'X', conveyorBelt, 'S',
							separationBelt, 'D', new ItemStack(elevator, 1, 1) });
			
			GameRegistry.addRecipe(new ItemStack(slimeboots, 1, 0),
					new Object[] { "B", "S", "B", 'B', Items.iron_boots, 'S', Items.slime_ball });
		
		}
	}

	@PC_RegisterContainers
	public List<PC_Struct2<String, Class<? extends PC_GresBaseWithInventory>>> registerContainers(
			List<PC_Struct2<String, Class<? extends PC_GresBaseWithInventory>>> guis) {
		guis.add(new PC_Struct2<String, Class<? extends PC_GresBaseWithInventory>>("SeperationBelt",
				PCtr_ContainerSeparationBelt.class));
		guis.add(new PC_Struct2<String, Class<? extends PC_GresBaseWithInventory>>("Splitter",
				PCtr_ContainerSplitter.class));
		return guis;
	}
}
