package powercraft.core.craftingtool;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import powercraft.api.inventory.PC_Slot;
import powercraft.api.registry.PC_KeyRegistry;
import powercraft.api.registry.PC_RecipeRegistry;
import powercraft.api.utils.PC_GlobalVariables;

public class PCco_SlotDirectCrafting extends PC_Slot {

	private static int MAX_RECURSION = 50;
	private EntityPlayer thePlayer;
	private ItemStack product;
	private boolean available = false;

	public PCco_SlotDirectCrafting(EntityPlayer entityplayer, ItemStack product, int index) {
		super(null, index);
		thePlayer = entityplayer;
		this.product = product;
		updateAvailable();
	}

	@Override
	public ItemStack getBackgroundStack() {
		return product;
	}

	@Override
	public PC_Slot setBackgroundStack(ItemStack stack) {
		product = stack.copy();
		return this;
	}

	@Override
	public boolean renderTooltipWhenEmpty() {
		return true;
	}

	@Override
	public boolean renderGrayWhenEmpty() {
		return true;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return false;
	}

	@Override
	public ItemStack decrStackSize(int i) {

		if (product != null) {
			ItemStack output = product.copy();

			if (thePlayer.capabilities.isCreativeMode
					|| PC_GlobalVariables.config.getBoolean("cheats.survivalCheating")) {
				if (PC_KeyRegistry.isPlacingReversed(thePlayer)) {
					output.stackSize = output.getMaxStackSize();
				}
				available = true;
			} else {
				ItemStack[] is = getPlayerInventory();
				int stackSize = craft(output, is, new ArrayList<ItemStack>(), 0);
				if (stackSize > 0) {
					output.stackSize = stackSize;
					setPlayerInventory(is);
					updateAvailable();
				} else {
					available = false;
					return null;
				}
			}

			return output;
		}

		return null;
	}

	@Override
	public ItemStack getStack() {
		if (product != null && available) {
			return product.copy();
		}

		return null;
	}

	@Override
	public void putStack(ItemStack itemstack) {
	}

	public void setProduct(ItemStack itemstack) {
		product = itemstack;
	}

	@Override
	public void onSlotChanged() {
	}

	@Override
	public int getSlotStackLimit() {
		if (product == null) {
			return 64;
		}

		return product.getMaxStackSize();
	}

	private ItemStack[] getPlayerInventory() {
		ItemStack[] inv = new ItemStack[thePlayer.inventory.getSizeInventory()];
		for (int i = 0; i < thePlayer.inventory.getSizeInventory(); i++) {
			inv[i] = thePlayer.inventory.getStackInSlot(i);
			if (inv[i] != null)
				inv[i] = inv[i].copy();
		}
		return inv;
	}

	private void setPlayerInventory(ItemStack[] inv) {
		for (int i = 0; i < thePlayer.inventory.getSizeInventory(); i++) {
			thePlayer.inventory.setInventorySlotContents(i, inv[i]);
		}
	}

	private ItemStack[] setTo(ItemStack[] inv, ItemStack[] inv1) {
		if (inv == null)
			inv = new ItemStack[inv1.length];
		for (int i = 0; i < inv.length; i++) {
			inv[i] = inv1[i];
		}
		return inv;
	}

	private int testItem(ItemStack stack, ItemStack[] is) {
		stack = stack.copy();
		for (int i = 0; i < is.length; i++) {
			if (stack.equals(is[i])) {
				if (stack.stackSize > is[i].stackSize) {
					stack.stackSize = (stack.stackSize - is[i].stackSize);
				} else {
					return 0;
				}
			}
		}
		return stack.stackSize;
	}

	private int testItem(List<ItemStack> l, ItemStack[] is, List<ItemStack> not, int rec) {
		int i = 0;
		for (ItemStack stack : l) {
			if (testItem(stack, is) == 0)
				return i;
			i++;
		}
		for (ItemStack stack : l) {
			int need = testItem(stack, is);
			ItemStack[] isc = setTo(null, is);
			int size = 0;
			List<ItemStack> notc = new ArrayList<ItemStack>(not);
			while (size < need) {
				int nSize = craft(stack, isc, notc, rec);
				if (nSize == 0) {
					size = 0;
					break;
				}
				size += craft(stack, isc, notc, rec);
			}
			if (size > 0) {
				stack = stack.copy();
				stack.stackSize = size;
				if (storeTo(stack, isc)) {
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

	private boolean storeTo(ItemStack get, ItemStack[] is) {
		for (int i = 0; i < is.length; i++) {
			if (get.equals(is[i])) {
				int canPut = Math.min(thePlayer.inventory.getInventoryStackLimit(), is[i].getMaxStackSize())
						- is[i].stackSize;
				if (get.stackSize > canPut) {
					get.stackSize = (get.stackSize - canPut);
					is[i].stackSize += canPut;
				} else {
					is[i].stackSize += get.stackSize;
					return true;
				}
			}
		}
		return false;
	}

	private void takeOut(ItemStack itemStack, ItemStack[] is) {
		for (int i = 0; i < is.length; i++) {
			if (itemStack.equals(is[i])) {
				if (itemStack.stackSize > is[i].stackSize) {
					itemStack.stackSize = (itemStack.stackSize - is[i].stackSize);
					is[i] = null;
				} else {
					is[i].stackSize -= itemStack.stackSize;
					if (is[i].stackSize == 0)
						is[i] = null;
					return;
				}
			}
		}
	}

	private int craft(ItemStack craft, ItemStack[] is, List<ItemStack> notc, int rec) {
		List<IRecipe> recipes = PC_RecipeRegistry.getRecipesForProduct(craft);
		if (rec > MAX_RECURSION)
			return 0;
		if (notc.contains(craft))
			return 0;
		notc.add(craft);
		rec++;
		for (IRecipe recipe : recipes) {
			ItemStack[] isc = setTo(null, is);
			List<ItemStack>[][] inp = PC_RecipeRegistry.getExpectedInput(recipe, -1, -1);
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
				ret = testItem(l, isc, notc, rec);
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

	private boolean isAvailable() {
		if (thePlayer.capabilities.isCreativeMode || PC_GlobalVariables.config.getBoolean("cheats.survivalCheating"))
			return true;
		return craft(product, getPlayerInventory(), new ArrayList<ItemStack>(), 0) > 0;
	}

	public void updateAvailable() {
		available = isAvailable();
	}

}
