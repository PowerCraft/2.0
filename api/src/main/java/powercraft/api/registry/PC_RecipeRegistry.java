package powercraft.api.registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import powercraft.api.item.PC_ItemStack;
import powercraft.api.recipes.PC_3DRecipe;
import powercraft.api.recipes.PC_I3DRecipeHandler;
import powercraft.api.recipes.PC_IRecipe;
import powercraft.api.recipes.PC_IRecipeInfo;
import powercraft.api.recipes.PC_SmeltRecipe;
import powercraft.api.reflect.PC_ReflectHelper;
import powercraft.api.utils.PC_VecI;

public class PC_RecipeRegistry {

	private static List<PC_SmeltRecipe> smeltings = new ArrayList<PC_SmeltRecipe>();
	private static List<PC_3DRecipe> recipes3d = new ArrayList<PC_3DRecipe>();

	public static void register(PC_IRecipe recipe) {
		if (recipe instanceof IRecipe) {
			GameRegistry.addRecipe((IRecipe) recipe);
		} else if (recipe instanceof PC_3DRecipe) {
			add3DRecipe((PC_3DRecipe) recipe);
		} else if (recipe instanceof PC_SmeltRecipe) {
			addSmeltingRecipes((PC_SmeltRecipe) recipe);
		}
	}

	public static void add3DRecipe(PC_3DRecipe recipe) {
		recipes3d.add(recipe);
	}

	public static void add3DRecipe(PC_I3DRecipeHandler obj, Object... o) {
		recipes3d.add(new PC_3DRecipe(obj, o));
	}

	public static void addSmeltingRecipes(PC_SmeltRecipe smeltRecipe) {
		smeltings.add(smeltRecipe);
		ItemStack isInput = smeltRecipe.getInput();
		ItemStack isOutput = smeltRecipe.getResult();
		GameRegistry.addSmelting(isInput.getItem(), isOutput, smeltRecipe.getExperience());
	}

	public static List<IRecipe> getRecipesForProduct(ItemStack prod) {
		List<IRecipe> recipes = new ArrayList<IRecipe>(CraftingManager.getInstance().getRecipeList());
		List<IRecipe> ret = new ArrayList<IRecipe>();

		for (IRecipe recipe : recipes) {
			try {
				if (recipe.getRecipeOutput().isItemEqual(prod)
						|| (recipe.getRecipeOutput().getItem() == prod.getItem() && prod.getItemDamage() == -1)) {
					if (recipe instanceof ShapedOreRecipe || recipe instanceof ShapelessOreRecipe) {
						// ret.add(resolveForgeRecipe(recipe));
					} else {
						ret.add(recipe);
					}
				}
			} catch (NullPointerException npe) {
				continue;
			}
		}

		return ret;
	}

	public static boolean isFuel(ItemStack itemstack) {
		if (itemstack == null) {
			return false;
		}
		return getFuelValue(itemstack) > 0;
	}

	public static boolean isSmeltable(ItemStack itemstack) {
		if (itemstack == null || FurnaceRecipes.smelting().getSmeltingResult(itemstack) == null) {
			return false;
		}

		return true;
	}

	public static List<ItemStack> getFeedstock(ItemStack itemstack) {
		List<ItemStack> l = new ArrayList<ItemStack>();
		if (itemstack != null) {
			Map<Integer, ItemStack> map = FurnaceRecipes.smelting().getSmeltingList();
			for (Entry<Integer, ItemStack> e : map.entrySet()) {
				if (e.getValue().isItemEqual(itemstack)) {
					l.add(new ItemStack(e.getValue().getItem(), 1, 0));
				}
			}
			Map<List<Integer>, ItemStack> map2 = PC_ReflectHelper.getValue(FurnaceRecipes.class,
					FurnaceRecipes.smelting(), 3, Map.class);
			for (Entry<List<Integer>, ItemStack> e : map2.entrySet()) {
				if (e.getValue().isItemEqual(itemstack)) {
					l.add(new ItemStack(e.getValue().getItem(), 1, e.getKey().get(1)));
				}
			}
		}
		return l;
	}

	public static List<ItemStack>[][] getExpectedInput(IRecipe recipe, int width, int hight) {
		List<ItemStack>[][] list;
		if (recipe instanceof PC_IRecipeInfo) {
			PC_IRecipeInfo ri = (PC_IRecipeInfo) recipe;
			PC_VecI size = ri.getSize();
			if (size != null) {
				if (width == -1)
					width = size.x;
				if (hight == -1)
					hight = size.y;
				if (size.x > width || size.y > hight)
					return null;
			} else {
				int rsize = recipe.getRecipeSize();
				if (width == -1)
					width = rsize;
				if (hight == -1)
					hight = 1;
				if (hight * width < rsize || rsize == 0)
					return null;
				size = new PC_VecI(width, hight);
			}
			list = new List[width][hight];
			int i = 0;
			for (int y = 0; y < size.y; y++) {
				for (int x = 0; x < size.x; x++) {
					if (i < ri.getPCRecipeSize()) {
						list[x][y] = ri.getExpectedInputFor(i);
					}
					i++;
				}
			}
		} else if (recipe instanceof ShapedRecipes) {
			int sizeX = PC_ReflectHelper.getValue(ShapedRecipes.class, recipe, 0, int.class);
			int sizeY = PC_ReflectHelper.getValue(ShapedRecipes.class, recipe, 1, int.class);
			ItemStack[] stacks = PC_ReflectHelper.getValue(ShapedRecipes.class, recipe, 2, ItemStack[].class);
			if (width == -1)
				width = sizeX;
			if (hight == -1)
				hight = sizeY;
			if (sizeX > width || sizeY > hight)
				return null;
			list = new List[width][hight];
			int i = 0;
			for (int y = 0; y < sizeY; y++) {
				for (int x = 0; x < sizeX; x++) {
					if (i < stacks.length) {
						if (stacks[i] != null) {
							list[x][y] = new ArrayList<ItemStack>();
							list[x][y].add(stacks[i]);
						}
					}
					i++;
				}
			}
		} else if (recipe instanceof ShapelessRecipes) {
			List<ItemStack> stacks = PC_ReflectHelper.getValue(ShapelessRecipes.class, recipe, 1, List.class);
			if (width == -1)
				width = stacks.size();
			if (hight == -1)
				hight = 1;
			if (hight * width < stacks.size())
				return null;
			list = new List[width][hight];
			int i = 0;
			for (int y = 0; y < hight; y++) {
				for (int x = 0; x < width; x++) {
					if (i < stacks.size()) {
						list[x][y] = new ArrayList<ItemStack>();
						list[x][y].add(stacks.get(i));
					}
					i++;
				}
			}
		} else {
			return null;
		}
		return list;
	}

	public static int getFuelValue(ItemStack itemstack) {
		return TileEntityFurnace.getItemBurnTime(itemstack);
	}

	public static ItemStack getSmeltingResult(ItemStack item) {
		return FurnaceRecipes.smelting().getSmeltingResult(item);
	}

	public static boolean searchRecipe3DAndDo(EntityPlayer entityplayer, World world, PC_VecI pos) {
		for (PC_3DRecipe recipe : recipes3d) {
			if (recipe.isStruct(entityplayer, world, pos)) {
				return true;
			}
		}
		return false;
	}

	public static void unloadSmeltRecipes() {
		FurnaceRecipes smlt = FurnaceRecipes.smelting();
		for (PC_SmeltRecipe smelting : smeltings) {
			ItemStack is = smelting.getInput();
			if (is != null) {
				smlt.getSmeltingList().remove(Integer.valueOf(is.getItem().toString()));

				Map map = PC_ReflectHelper.getValue(FurnaceRecipes.class, smlt, 3, HashMap.class);
				map.remove(
						Arrays.asList(Integer.valueOf(is.getItem().toString()), Integer.valueOf(is.getItemDamage())));

				map = PC_ReflectHelper.getValue(FurnaceRecipes.class, smlt, 2, Map.class);
				map.remove(Integer.valueOf(is.getItem().toString()));

				map = PC_ReflectHelper.getValue(FurnaceRecipes.class, smlt, 4, HashMap.class);
				map.remove(
						Arrays.asList(Integer.valueOf(is.getItem().toString()), Integer.valueOf(is.getItemDamage())));
			}
		}
	}

	public static void loadSmeltRecipes() {
		for (PC_SmeltRecipe smelting : smeltings) {
			ItemStack isInput = smelting.getInput();
			ItemStack isOutput = smelting.getResult();
			GameRegistry.addSmelting(isInput.getItem(), isOutput, smelting.getExperience());
		}
	}

}
