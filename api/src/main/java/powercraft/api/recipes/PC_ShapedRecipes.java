package powercraft.api.recipes;

import java.util.ArrayList;
import java.util.HashMap;
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

public class PC_ShapedRecipes implements IRecipe, PC_IRecipeInfo, PC_IRecipe {

	private PC_VecI size;
	private List<ItemStack>[][] recipeItems;
	private ItemStack recipeOutput;
	private String op;

	public PC_ShapedRecipes(ItemStack recipeOutput, PC_VecI size, List<ItemStack>[][] recipeItems) {
		this.recipeOutput = recipeOutput;
		this.size = size;
		this.recipeItems = recipeItems;
	}

	public PC_ShapedRecipes(ItemStack itemStack, Object... recipe) {
		this(null, itemStack, recipe);
	}

	public PC_ShapedRecipes(String op, ItemStack itemStack, Object... o) {
		this.op = op;
		recipeOutput = itemStack;
		size = new PC_VecI();

		List<String> lines = new ArrayList<String>();
		HashMap<Character, List<ItemStack>> map = new HashMap<Character, List<ItemStack>>();

		int i = 0;
		while (o[i] instanceof String) {
			lines.add((String) o[i]);
			i++;
		}

		while (i < o.length && o[i] instanceof Character) {
			char c = (Character) o[i];
			i++;
			List<ItemStack> list = new ArrayList<ItemStack>();
			while (i < o.length) {
				if (o[i] instanceof Block) {
					list.add(new ItemStack((Block) o[i]));
				} else if (o[i] instanceof Item) {
					list.add(new ItemStack((Item) o[i]));
				} else if (o[i] instanceof ItemStack) {
					list.add((ItemStack) o[i]);
				} else if (o[i] instanceof List) {
					list.addAll((List) o[i]);
				} else {
					break;
				}
				i++;
			}
			map.put(c, list);
		}

		size.y = lines.size();
		for (String line : lines) {
			if (line.length() > size.x)
				size.x = line.length();
		}

		recipeItems = new List[size.x][size.y];

		for (int y = 0; y < size.y; y++) {
			String line = lines.get(y);
			for (int x = 0; x < line.length(); x++) {
				char c = line.charAt(x);
				recipeItems[x][y] = map.get(c);
			}
		}

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

		int craftSizeY = inventoryCrafting.getSizeInventory();
		int craftSizeX = PC_ReflectHelper.getValue(InventoryCrafting.class, inventoryCrafting, 1, int.class);
		craftSizeY = craftSizeY / craftSizeX;
		for (int y = 0; y <= craftSizeY - size.y; y++) {
			for (int x = 0; x <= craftSizeX - size.x; x++) {
				boolean otherEmpty = true;
				for (int yy = 0; yy < craftSizeY && otherEmpty; yy++) {
					for (int xx = 0; xx <= craftSizeX && otherEmpty; xx++) {
						if (inventoryCrafting.getStackInRowAndColumn(xx, yy) != null) {
							if (xx < x || xx >= x + size.x || yy < y || yy >= y + size.y)
								otherEmpty = false;
						}
					}
				}
				if (otherEmpty) {
					if (checkMatch(inventoryCrafting, x, y)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	private boolean checkMatch(InventoryCrafting inventoryCrafting, int x, int y) {
		for (int j = 0; j < size.y; j++) {
			for (int i = 0; i < size.x; i++) {
				List<ItemStack> expect = recipeItems[i][j];
				ItemStack get = inventoryCrafting.getStackInRowAndColumn(x + i, y + j);
				if (expect == null && get != null) {
					return false;
				} else if (expect == null && get == null) {
					continue;
				}
				boolean ok = false;
				for (ItemStack is : expect) {
					if (is.equals(get)) {
						ok = true;
						break;
					}
				}
				if (!ok)
					return false;
			}
		}
		return true;
	}

	public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) {
		if (!canBeCrafted())
			return null;
		ItemStack itemStack = getRecipeOutput().copy();
		if (itemStack.getItem() instanceof PC_Item) {
			((PC_Item) itemStack.getItem()).doCrafting(itemStack, inventoryCrafting);
		}
		if (itemStack.getItem() instanceof PC_ItemBlock) {
			((PC_ItemBlock) itemStack.getItem()).doCrafting(itemStack, inventoryCrafting);
		}
		return itemStack;
	}

	public int getRecipeSize() {
		if (!canBeCrafted())
			return 0;
		return size.x * size.y;
	}

	@Override
	public PC_VecI getSize() {
		if (!canBeCrafted())
			return null;
		return size.copy();
	}

	@Override
	public List<ItemStack> getExpectedInputFor(int index) {
		int y = index / size.x;
		int x = index % size.x;
		return recipeItems[x][y];
	}

	@Override
	public int getPCRecipeSize() {
		return getRecipeSize();
	}

}
