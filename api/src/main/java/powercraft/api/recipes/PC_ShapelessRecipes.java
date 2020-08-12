package powercraft.api.recipes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import powercraft.api.block.PC_ItemBlock;
import powercraft.api.item.PC_Item;
import powercraft.api.reflect.PC_ReflectHelper;
import powercraft.api.utils.PC_GlobalVariables;
import powercraft.api.utils.PC_VecI;

public class PC_ShapelessRecipes implements IRecipe, PC_IRecipeInfo, PC_IRecipe {

	private final ItemStack recipeOutput;
	private final List<ItemStack>[] recipeItems;
	private String op;

	public PC_ShapelessRecipes(ItemStack recipeOutput, List<ItemStack>[] recipeItems) {
		this.recipeOutput = recipeOutput;
		this.recipeItems = recipeItems;
	}

	public PC_ShapelessRecipes(ItemStack itemStack, Object... recipe) {
		this(null, itemStack, recipe);
	}

	public PC_ShapelessRecipes(String op, ItemStack itemStack, Object... o) {
		this.op = op;
		recipeOutput = itemStack;

		List<List<ItemStack>> recipeItems = new ArrayList<List<ItemStack>>();

		for (Object obj : o) {
			List<ItemStack> list = new ArrayList<ItemStack>();
			if (obj instanceof Block) {
				list.add(new ItemStack((Block) obj));
			} else if (obj instanceof Item) {
				list.add(new ItemStack((Item) obj));
				// }else if(obj instanceof ItemStack){
				// list.add(new ItemStack((ItemStack)obj));
			} else if (obj instanceof ItemStack) {
				list.add((ItemStack) obj);
			} else if (obj instanceof List) {
				list.addAll((List) obj);
			}
			recipeItems.add(list);
		}

		this.recipeItems = recipeItems.toArray(new List[0]);

	}

	@Override
	public boolean canBeCrafted() {
		if (op == null)
			return true;
		if (!PC_GlobalVariables.consts.containsKey(op))
			return true;
		Object o = PC_GlobalVariables.consts.get(op);
		if (o instanceof Boolean)
			return (Boolean) o;
		return recipeOutput != null;
	}

	public ItemStack getRecipeOutput() {
		if (!canBeCrafted())
			return null;
		return recipeOutput;
	}

	public boolean matches(InventoryCrafting inventoryCrafting, World world) {
		if (!canBeCrafted())
			return false;

		boolean[] used = new boolean[recipeItems.length];

		int craftSizeY = inventoryCrafting.getSizeInventory();
		int craftSizeX = PC_ReflectHelper.getValue(InventoryCrafting.class, inventoryCrafting, 1, int.class);
		craftSizeY = craftSizeY / craftSizeX;

		for (int y = 0; y < craftSizeY; y++) {
			for (int x = 0; x < craftSizeX; x++) {
				ItemStack get = inventoryCrafting.getStackInRowAndColumn(x, y);
				if (get != null) {
					boolean ok = false;
					for (int i = 0; i < recipeItems.length; i++) {
						if (!used[i]) {
							List<ItemStack> expect = recipeItems[i];
							for (ItemStack is : expect) {
								if (is.equals(get)) {
									ok = true;
									break;
								}
							}
							if (ok) {
								used[i] = true;
								break;
							}
						}
					}
					if (!ok)
						return false;
				}
			}
		}

		for (int i = 0; i < used.length; i++) {
			if (!used[i])
				return false;
		}

		return true;
	}

	public ItemStack getCraftingResult(InventoryCrafting par1InventoryCrafting) {
		if (!canBeCrafted())
			return null;
		ItemStack itemStack = getRecipeOutput().copy();
		if (itemStack.getItem() instanceof PC_Item) {
			((PC_Item) itemStack.getItem()).doCrafting(itemStack, par1InventoryCrafting);
		}
		if (itemStack.getItem() instanceof PC_ItemBlock) {
			((PC_ItemBlock) itemStack.getItem()).doCrafting(itemStack, par1InventoryCrafting);
		}
		return itemStack;
	}

	public int getRecipeSize() {
		if (!canBeCrafted())
			return 0;
		return recipeItems.length;
	}

	@Override
	public PC_VecI getSize() {
		return null;
	}

	@Override
	public List<ItemStack> getExpectedInputFor(int index) {
		return recipeItems[index];
	}

	@Override
	public int getPCRecipeSize() {
		return getRecipeSize();
	}

}
