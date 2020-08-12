package powercraft.storage;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import powercraft.api.annotation.PC_FieldObject;
import powercraft.api.block.PC_Block;
import powercraft.api.item.PC_Item;
import powercraft.api.recipes.PC_3DRecipe;
import powercraft.api.recipes.PC_I3DRecipeHandler;
import powercraft.api.recipes.PC_IRecipe;
import powercraft.api.registry.PC_ItemRegistry;
import powercraft.api.registry.PC_ModuleRegistry;
import powercraft.launcher.loader.PC_Module;
import powercraft.launcher.loader.PC_Module.PC_InitRecipes;

@PC_Module(name = "Storage", version = "@Version@")
public class PCs_App {

	@PC_FieldObject(clazz = PCs_BlockBigChest.class)
	public static PC_Block bigChest;
	@PC_FieldObject(clazz = PCs_ItemCompressor.class)
	public static PC_Item compressor;

	@PC_InitRecipes
	public List<PC_IRecipe> initRecipes(List<PC_IRecipe> recipes) {
		recipes.add(new PC_3DRecipe((PC_I3DRecipeHandler) bigChest, new String[] { "g  g", "    ", "    ", "g  g" },
				new String[] { "f  f", "    ", "    ", "f  f" }, new String[] { "f  f", "    ", "    ", "f  f" },
				new String[] { "g  g", "    ", "    ", "g  g" }, 'g', Blocks.glass, 'f', Blocks.fence, ' ', null));
		if (PC_ModuleRegistry.getModule("Net") != null) {
			GameRegistry.addRecipe(new ItemStack(compressor, 1, PCs_ItemCompressor.NORMAL), " r ", "lcl", " l ", 'c',
					Blocks.chest, 'l', Blocks.lever, 'r', PC_ItemRegistry.getPCItemByName("PCnt_ItemRadioRemote"));
			GameRegistry.addRecipe(new ItemStack(compressor, 1, PCs_ItemCompressor.ENDERACCESS), " r ", "lel", " l ",
					'e', Blocks.ender_chest, 'l', Blocks.lever, 'r',
					PC_ItemRegistry.getPCItemByName("PCnt_ItemRadioRemote"));
		} else {
			GameRegistry.addRecipe(new ItemStack(compressor, 1, PCs_ItemCompressor.NORMAL), " l ", "lcl", " l ", 'c',
					Blocks.chest, 'l', Blocks.lever);
			GameRegistry.addRecipe(new ItemStack(compressor, 1, PCs_ItemCompressor.ENDERACCESS), " l ", "lel", " l ",
					'e', Blocks.ender_chest, 'l', Blocks.lever);
		}
		return recipes;
	}

}
