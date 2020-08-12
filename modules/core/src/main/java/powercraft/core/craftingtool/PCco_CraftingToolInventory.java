package powercraft.core.craftingtool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import powercraft.api.inventory.PC_IInventory;
import powercraft.api.inventory.PC_IInventoryBackground;
import powercraft.api.registry.PC_RecipeRegistry;
import powercraft.api.utils.PC_VecI;
import powercraft.core.gui.PCco_GuiCraftingTool;

public class PCco_CraftingToolInventory implements PC_IInventory, PC_IInventoryBackground {

	private List<ItemStack> items = new ArrayList<ItemStack>();
	private List<ItemStack> items2Display = Collections.synchronizedList(new ArrayList<ItemStack>());

	private PC_VecI size;
	private boolean[] can;
	private int scroll = 0;

	private PCco_GuiCraftingTool gui;

	private SearchThread search;
	private UpdateAvailabilityThread updateAvailability;

	public PCco_CraftingToolInventory(PCco_GuiCraftingTool gui, PC_VecI size) {
		this.size = size.copy();
		this.gui = gui;
		can = new boolean[size.x * size.y];
		ArrayList<Block> block_list = new ArrayList<Block>();
		ArrayList<Item> item_list = new ArrayList<Item>();
		for (int i = 1; i < 32767; i++) {

			Item item = Item.getItemById(i);

			if (item != null)
				if (PC_RecipeRegistry.getRecipesForProduct(new ItemStack(item)) != null)
					item_list.add(item);
		}

		for (int i = 0; i < item_list.size(); i++) {
			Item item = item_list.get(i);
			if (item != null) {
				if (item.getCreativeTab() != null) {
					item.getSubItems(item, item.getCreativeTab(), items);
				}
			}
		}
		items2Display.addAll(items);
		updateAvailability();
	}

	public void setSearchString(String searchString) {
		if (search != null) {
			search.stopSearch();
			search = null;
		}
		items2Display.clear();
		if (searchString == null || searchString.equals("")) {
			items2Display.addAll(items);
		} else {
			search = new SearchThread(searchString);
		}
		updateAvailability();
	}

	public void setScroll(int scroll) {
		if (this.scroll != scroll) {
			this.scroll = scroll;
			updateAvailability();
		}
	}

	public ItemStack getProductForSlot(int i) {
		i += scroll * size.x;
		synchronized (items2Display) {
			if (i < items2Display.size()) {
				return items2Display.get(i);
			}
		}
		return null;
	}

	public int getNumRows() {
		synchronized (items2Display) {
			return items2Display.size() / size.x + 1;
		}
	}

	public boolean canBeCrafted(int slot) {
		synchronized (can) {
			return can[slot];
		}
	}

	@Override
	public int getSizeInventory() {
		return size.x * size.y;
	}

	public synchronized void updateAvailability() {
		if (updateAvailability != null) {
			updateAvailability.stopUpdateAvailability();
			updateAvailability = null;
		}
		updateAvailability = new UpdateAvailabilityThread();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		if (canBeCrafted(i)) {
			ItemStack is = getProductForSlot(i);
			if (is != null) {
				is = is.copy();
				is.stackSize = 1;
				return is;
			}
		}
		return null;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
	}

	@Override
	public String getInventoryName() {
		return "CraftingToolInventory";
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean canPlayerInsertStackTo(int i, ItemStack itemstack) {
		return false;
	}

	@Override
	public boolean canPlayerTakeStack(int i, EntityPlayer entityPlayer) {
		return true;
	}

	@Override
	public boolean canDispenseStackFrom(int i) {
		return false;
	}

	@Override
	public boolean canDropStackFrom(int i) {
		return false;
	}

	@Override
	public int getSlotStackLimit(int i) {
		ItemStack is = getProductForSlot(i);
		return is == null ? 0 : is.getMaxStackSize();
	}

	@Override
	public ItemStack getBackgroundStack(int slotIndex) {
		return getProductForSlot(slotIndex);
	}

	@Override
	public boolean renderTooltipWhenEmpty(int slotIndex) {
		return true;
	}

	@Override
	public boolean renderGrayWhenEmpty(int slotIndex) {
		return true;
	}

	@Override
	public boolean useAlwaysBackground(int slotIndex) {
		return false;
	}

	private class SearchThread extends Thread {

		private String searchString;
		private Boolean stop = false;

		public SearchThread(String searchString) {
			this.searchString = searchString.toLowerCase();
			setDaemon(true);
			start();
		}

		public void stopSearch() {
			synchronized (stop) {
				stop = true;
			}
		}

		@Override
		public void run() {
			int num = 0;
			for (ItemStack itemStack : items) {
				List<String> info = (List<String>) itemStack.getTooltip(gui.thePlayer, false);
				for (String infoString : info) {
					if (infoString.toLowerCase().contains(searchString)) {
						synchronized (stop) {
							if (stop)
								return;
						}
						synchronized (items2Display) {
							items2Display.add(itemStack);
						}
						num++;
						gui.updateSrcoll();
						if (num == scroll * size.x + size.x * size.y - 1) {
							updateAvailability();
						}
						break;
					}
				}
				synchronized (stop) {
					if (stop)
						return;
				}
			}
			if (num < scroll * size.x + size.x * size.y - 1) {
				updateAvailability();
			}

		}

	}

	private class UpdateAvailabilityThread extends Thread {

		private Boolean stop = false;

		public UpdateAvailabilityThread() {
			setDaemon(true);
			start();
		}

		public void stopUpdateAvailability() {
			synchronized (stop) {
				stop = true;
			}
		}

		@Override
		public void run() {
			synchronized (can) {
				for (int i = 0; i < can.length; i++) {
					can[i] = false;
				}
			}
			for (int i = 0; i < can.length; i++) {
				ItemStack is = getProductForSlot(i);
				if (is != null) {
					boolean availabe = PCco_CraftingToolCrafter.tryToCraft(is, gui.thePlayer);
					synchronized (stop) {
						if (stop)
							return;
					}
					if (availabe) {
						synchronized (can) {
							can[i] = true;
						}
					}
				}
			}
		}

	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return null;
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return false;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return false;
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		return false;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}

	@Override
	public void syncInventory(int side, EntityPlayer player, int slot) {
	}

}
