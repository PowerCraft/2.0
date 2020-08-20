package powercraft.deco;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import powercraft.api.annotation.PC_FieldObject;
import powercraft.api.block.PC_Block;
import powercraft.api.recipes.PC_IRecipe;
import powercraft.api.utils.PC_GlobalVariables;
import powercraft.deco.block.PCde_BlockChimney;
import powercraft.deco.block.PCde_BlockIronFrame;
import powercraft.deco.block.PCde_BlockPlatform;
import powercraft.deco.block.PCde_BlockRedstoneStorage;
import powercraft.deco.block.PCde_BlockStairs;
import powercraft.launcher.loader.PC_Module;
import powercraft.launcher.loader.PC_Module.PC_InitRecipes;
import powercraft.launcher.loader.PC_Module.PC_Instance;
import powercraft.launcher.loader.PC_ModuleObject;

@PC_Module(name = "Deco", version = "@Version@")
public class PCde_App {

	@PC_FieldObject(clazz = PCde_BlockRedstoneStorage.class)
	public static PC_Block redstoneStorage;
	@PC_FieldObject(clazz = PCde_BlockIronFrame.class)
	public static PC_Block ironFrame;
	@PC_FieldObject(clazz = PCde_BlockChimney.class)
	public static PC_Block chimney;
	@PC_FieldObject(clazz = PCde_BlockPlatform.class)
	public static PC_Block platform;
	@PC_FieldObject(clazz = PCde_BlockStairs.class)
	public static PC_Block stairs;
	@PC_Instance
	public static PC_ModuleObject instance;

	@PC_InitRecipes
	public void initRecipes() {
		//LEGACY counts
		int chimneysCount = 6;
		int frameCount = 32;
		int platformCount = 15;
		int stairsCount = 15;
		
		if(PC_GlobalVariables.mediumRecipes) {
			chimneysCount = 3;
			frameCount = 8;
			platformCount = 5;
			stairsCount = 5;
		}
		
		GameRegistry.addRecipe(new ItemStack(ironFrame, frameCount, 0),
				new Object[] { "XXX", "X X", "XXX", 'X', Items.iron_ingot });
		GameRegistry.addRecipe(new ItemStack(platform, platformCount), new Object[] { "X  ", "X  ", "XXX", 'X', Items.iron_ingot });
		GameRegistry.addRecipe(new ItemStack(stairs, stairsCount), new Object[] { "X  ", "XX  ", " XX", 'X', Items.iron_ingot });

		// Chimney's
		GameRegistry.addRecipe(new ItemStack(chimney, chimneysCount, 0),
				new Object[] { "X X", "X X", "X X", 'X', Blocks.cobblestone });
		GameRegistry.addRecipe(new ItemStack(chimney, chimneysCount, 1),
				new Object[] { "X X", "X X", "X X", 'X', Blocks.brick_block });
		GameRegistry.addRecipe(new ItemStack(chimney, chimneysCount, 2),
				new Object[] { "X X", "X X", "X X", 'X', new ItemStack(Blocks.stonebrick, 1, 0) });
		GameRegistry.addRecipe(new ItemStack(chimney, chimneysCount, 3),
				new Object[] { "X X", "X X", "X X", 'X', new ItemStack(Blocks.stonebrick, 1, 2) });
		GameRegistry.addRecipe(new ItemStack(chimney, chimneysCount, 4),
				new Object[] { "X X", "X X", "X X", 'X', new ItemStack(Blocks.stonebrick, 1, 3) });
		GameRegistry.addRecipe(new ItemStack(chimney, chimneysCount, 5),
				new Object[] { "X X", "X X", "X X", 'X', new ItemStack(Blocks.sandstone, 1, 0) });
		GameRegistry.addRecipe(new ItemStack(chimney, chimneysCount, 6),
				new Object[] { "X X", "X X", "X X", 'X', new ItemStack(Blocks.sandstone, 1, 1) });
		GameRegistry.addRecipe(new ItemStack(chimney, chimneysCount, 7),
				new Object[] { "X X", "X X", "X X", 'X', new ItemStack(Blocks.sandstone, 1, 2) });
		GameRegistry.addRecipe(new ItemStack(chimney, chimneysCount, 8),
				new Object[] { "X X", "X X", "X X", 'X', new ItemStack(Blocks.nether_brick, 1, 0) });
		GameRegistry.addRecipe(new ItemStack(chimney, chimneysCount, 9),
				new Object[] { "X X", "X X", "X X", 'X', new ItemStack(Blocks.quartz_block, 1, 0) });
		GameRegistry.addRecipe(new ItemStack(chimney, chimneysCount, 10),
				new Object[] { "X X", "X X", "X X", 'X', new ItemStack(Blocks.quartz_block, 1, 1) });
		GameRegistry.addRecipe(new ItemStack(chimney, chimneysCount, 11),
				new Object[] { "X X", "X X", "X X", 'X', new ItemStack(Blocks.quartz_block, 1, 2) });
		GameRegistry.addRecipe(new ItemStack(chimney, chimneysCount, 12),
				new Object[] { "X X", "X X", "X X", 'X', new ItemStack(Blocks.mossy_cobblestone, 1, 0) });
		GameRegistry.addRecipe(new ItemStack(chimney, chimneysCount, 13),
				new Object[] { "X X", "X X", "X X", 'X', new ItemStack(Blocks.stonebrick, 1, 1) });
		GameRegistry.addRecipe(new ItemStack(chimney, chimneysCount, 14),
				new Object[] { "X X", "X X", "X X", 'X', new ItemStack(Blocks.clay, 1, 0) });
		GameRegistry.addRecipe(new ItemStack(chimney, chimneysCount, 15),
				new Object[] { "X X", "X X", "X X", 'X', new ItemStack(Blocks.iron_block, 1, 0) });
	}

}
