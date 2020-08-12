package powercraft.core.craftingtool;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import powercraft.api.inventory.PC_InventoryUtils;
import powercraft.api.utils.PC_Utils;

public final class PCco_CraftingToolCrafter {

	private static int MAX_RECURSION = 10;

	private PCco_CraftingToolCrafter() {
	}

	public static boolean tryToCraft(ItemStack product, EntityPlayer thePlayer) {
		if (thePlayer.capabilities.isCreativeMode) {
			return true;
		}
		return craft(product, getPlayerInventory(thePlayer), new ArrayList<ItemStack>(), 0, thePlayer) > 0;
	}

	public static ItemStack[] getPlayerInventory(EntityPlayer thePlayer) {
		ItemStack[] inv = new ItemStack[thePlayer.inventory.getSizeInventory()];
		for (int i = 0; i < thePlayer.inventory.getSizeInventory(); i++) {
			inv[i] = thePlayer.inventory.getStackInSlot(i);
			if (inv[i] != null)
				inv[i] = inv[i].copy();
		}
		return inv;
	}

	public static void setPlayerInventory(ItemStack[] inv, EntityPlayer thePlayer) {
		for (int i = 0; i < thePlayer.inventory.getSizeInventory(); i++) {
			thePlayer.inventory.setInventorySlotContents(i, inv[i]);
		}
	}

	private static ItemStack[] setTo(ItemStack[] inv, ItemStack[] inv1) {
		ItemStack[] invv = inv == null ? new ItemStack[inv1.length] : inv;
		for (int i = 0; i < invv.length; i++) {
			invv[i] = inv1[i];
		}
		return invv;
	}

	private static int testItem(ItemStack get, ItemStack[] is) {
		int size = get.stackSize;
		for (int i = 0; i < is.length; i++) {
			if (PC_InventoryUtils.itemStacksEqual(get, is[i])) {
				if (size > is[i].stackSize) {
					size -= is[i].stackSize;
				} else {
					return 0;
				}
			}
		}
		return size;
	}

	private static int testItem(List<ItemStack> get, ItemStack[] is, List<ItemStack> not, int rec,
			EntityPlayer thePlayer) {
		int i = 0;
		for (ItemStack stack : get) {
			if (testItem(stack, is) == 0)
				return i;
			i++;
		}
		i = 0;
		for (ItemStack stack : get) {
			int need = testItem(stack, is);
			ItemStack[] isc = setTo(null, is);
			int size = 0;
			List<ItemStack> notc = new ArrayList<ItemStack>(not);
			while (size < need) {
				int nSize = craft(stack, isc, notc, rec, thePlayer);
				if (nSize == 0) {
					size = 0;
					break;
				}
				size += nSize;
			}
			if (size > 0) {
				stack = stack.copy();
				stack.stackSize = size;
				if (storeTo(stack, isc, thePlayer)) {
					setTo(is, isc);
					not.clear();
					not.addAll(notc);
					return i;
				}
			}
			i++;
		}
		return -1;
	}

	private static boolean storeTo(ItemStack get, ItemStack[] is, EntityPlayer thePlayer) {
		for (int i = 0; i < is.length; i++) {
			if (PC_InventoryUtils.itemStacksEqual(get, is[i])) {
				int canPut = Math.min(thePlayer.inventory.getInventoryStackLimit(), is[i].getMaxStackSize())
						- is[i].stackSize;
				if (get.stackSize > canPut) {
					get.stackSize -= canPut;
					is[i].stackSize += canPut;
				} else {
					is[i].stackSize += get.stackSize;
					return true;
				}
			}
		}
		for (int i = 0; i < is.length; i++) {
			if (is[i] == null) {
				int canPut = Math.min(thePlayer.inventory.getInventoryStackLimit(), get.getMaxStackSize());
				if (get.stackSize > canPut) {
					is[i] = get.copy();
					is[i].stackSize = canPut;
					get.stackSize -= canPut;
				} else {
					is[i] = get.copy();
					return true;
				}
			}
		}
		return false;
	}

	private static void takeOut(ItemStack get, ItemStack[] is) {
		for (int i = 0; i < is.length; i++) {
			if (PC_InventoryUtils.itemStacksEqual(get, is[i])) {
				if (get.stackSize > is[i].stackSize) {
					get.stackSize -= is[i].stackSize;
					is[i] = null;
				} else {
					is[i].stackSize -= get.stackSize;
					if (is[i].stackSize == 0)
						is[i] = null;
					return;
				}
			}
		}
	}

	public static int craft(ItemStack craft, ItemStack[] is, List<ItemStack> not, int rec, EntityPlayer thePlayer) {
		if (thePlayer.capabilities.isCreativeMode) {
			return 1;
		}

		if (rec > MAX_RECURSION)
			return 0;
		if (not.contains(craft))
			return 0;
		not.add(craft);

		List<IRecipe> recipes = PC_Utils.getRecipesForProduct(craft);
		int r = rec + 1;
		for (IRecipe recipe : recipes) {
			ItemStack[] isc = setTo(null, is);
			List<ItemStack>[][] inp = PC_Utils.getExpectedInput(recipe, -1, -1);
			List<List<ItemStack>> input = new ArrayList<List<ItemStack>>();
			if (inp == null)
				continue;
			for (int x = 0; x < inp.length; x++) {
				for (int y = 0; y < inp[x].length; y++) {
					if (inp[x][y] != null) {
						input.add(inp[x][y]);
					}
				}
			}

			int ret;
			boolean con = false;
			for (List<ItemStack> l : input) {
				ret = testItem(l, isc, not, r, thePlayer);
				if (ret < 0) {
					con = true;
					break;
				}
				takeOut(l.get(ret), isc);
			}
			if (con)
				continue;

			setTo(is, isc);
			return recipe.getRecipeOutput().stackSize;
		}
		return 0;
	}
}