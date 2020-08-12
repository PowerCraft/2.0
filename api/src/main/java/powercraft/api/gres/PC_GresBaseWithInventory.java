package powercraft.api.gres;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import powercraft.api.inventory.PC_IInventorySpecialSlots;
import powercraft.api.inventory.PC_IInventoryWrapper;
import powercraft.api.inventory.PC_Slot;
import powercraft.api.tileentity.PC_TileEntity;

public class PC_GresBaseWithInventory<t extends PC_TileEntity> extends Container {
	public EntityPlayer thePlayer;

	protected static final int playerSlots = 9 * 4;

	public PC_Slot[][] inventoryPlayerUpper = new PC_Slot[9][3];

	public PC_Slot[][] inventoryPlayerLower = new PC_Slot[9][1];

	public PC_Slot[] invSlots;

	protected t tileEntity;

	public PC_GresBaseWithInventory(EntityPlayer player, t te, Object[] o) {
		thePlayer = player;

		tileEntity = te;

		if (thePlayer != null) {
			for (int i = 0; i < 9; i++) {
				inventoryPlayerLower[i][0] = new PC_Slot(player.inventory, i);
				addSlotToContainer(inventoryPlayerLower[i][0]);
			}

			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 3; j++) {
					inventoryPlayerUpper[i][j] = new PC_Slot(player.inventory, i + j * 9 + 9);
					addSlotToContainer(inventoryPlayerUpper[i][j]);
				}
			}
		}
		init(o);
		PC_Slot[] sl = getAllSlots();
		if (sl != null)
			for (PC_Slot s : sl) {
				addSlotToContainer(s);
			}
	}

	protected PC_Slot[] getAllSlots() {
		IInventory inv = null;
		if (tileEntity instanceof IInventory) {
			inv = (IInventory) tileEntity;
		} else if (tileEntity instanceof PC_IInventoryWrapper) {
			inv = ((PC_IInventoryWrapper) tileEntity).getInventory();
		} else {
			return null;
		}
		invSlots = new PC_Slot[inv.getSizeInventory()];
		for (int i = 0; i < invSlots.length; i++) {
			if (inv instanceof PC_IInventorySpecialSlots) {
				invSlots[i] = ((PC_IInventorySpecialSlots) inv).getSlot(i);
			} else {
				invSlots[i] = new PC_Slot(inv, i);
			}
		}
		return invSlots;
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

	protected boolean canShiftTransfer() {
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		if (slotIndex < playerSlots && !canShiftTransfer()) {
			return null;
		}

		ItemStack itemstack = null;
		Slot slot = (Slot) inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (slotIndex < playerSlots) {
				if (!mergeItemStack(itemstack1, playerSlots, inventorySlots.size(), false)) {
					return null;
				} else {
					slot.onPickupFromSlot(player, itemstack);
				}
			} else if (!mergeItemStack(itemstack1, 0, playerSlots, false)) {
				return null;
			} else {
				slot.onPickupFromSlot(player, itemstack);
			}

			if (itemstack1.stackSize == 0) {
				slot.putStack(null);
				slot.onSlotChanged();
			} else {
				slot.onSlotChanged();
			}
		}

		return itemstack;
	}

	private int getLimit(Slot slot, int a, boolean flag) {
		if (flag) {
			return a;
		}

		return Math.min(a, slot.getSlotStackLimit());
	}

	@Override
	protected boolean mergeItemStack(ItemStack itemstack, int i, int j, boolean flag) {
		boolean flag1 = false;
		int k = i;

		if (flag) {
			k = j - 1;
		}

		if (itemstack.isStackable()) {
			while (itemstack.stackSize > 0 && (!flag && k < j || flag && k >= i)) {
				Slot slot = (Slot) inventorySlots.get(k);
				ItemStack itemstack1 = slot.getStack();

				if (itemstack1 != null && slot.isItemValid(itemstack)
						&& (flag || itemstack1.stackSize < slot.getSlotStackLimit())
						&& itemstack1.getItem() == itemstack.getItem()
						&& (!itemstack.getHasSubtypes() || itemstack.getItemDamage() == itemstack1.getItemDamage())) {
					int i1 = itemstack1.stackSize + itemstack.stackSize;

					if (i1 <= getLimit(slot, itemstack.getMaxStackSize(), flag)) {
						itemstack.stackSize = 0;
						itemstack1.stackSize = i1;
						slot.onSlotChanged();
						flag1 = true;
					} else if (itemstack1.stackSize < getLimit(slot, itemstack.getMaxStackSize(), flag)) {
						itemstack.stackSize -= getLimit(slot, itemstack.getMaxStackSize(), flag) - itemstack1.stackSize;
						itemstack1.stackSize = getLimit(slot, itemstack.getMaxStackSize(), flag);
						slot.onSlotChanged();
						flag1 = true;
					}
				}

				if (flag) {
					k--;
				} else {
					k++;
				}
			}
		}

		if (itemstack.stackSize > 0) {
			int l;

			if (flag) {
				l = j - 1;
			} else {
				l = i;
			}

			do {
				if ((flag || l >= j) && (!flag || l < i)) {
					break;
				}

				Slot slot = (Slot) inventorySlots.get(l);
				ItemStack itemstack2 = slot.getStack();

				if (itemstack2 == null && slot.isItemValid(itemstack)) {
					ItemStack toStore = itemstack.copy();
					toStore.stackSize = getLimit(slot, toStore.stackSize, flag);
					if (toStore.stackSize > toStore.getMaxStackSize())
						toStore.stackSize = toStore.getMaxStackSize();
					itemstack.stackSize -= toStore.stackSize;
					slot.putStack(toStore);
					slot.onSlotChanged();

					if (itemstack.stackSize <= 0) {
						flag1 = true;
						itemstack.stackSize = 0;
						break;
					}
				}

				if (flag) {
					l--;
				} else {
					l++;
				}
			} while (true);
		}
		return flag1;
	}

	@Override
	public ItemStack slotClick(int par1, int par2, int par3, EntityPlayer par4EntityPlayer) {
		if ((par3 == 0 || par3 == 1) && (par2 == 0 || par2 == 1)) {
			if (par1 >= 0 && par3 != 1) {
				Slot slot = (Slot) this.inventorySlots.get(par1);

				if (slot instanceof PC_Slot) {
					if (((PC_Slot) slot).isHandlingSlotClick()) {
						return ((PC_Slot) slot).slotClick(par2, par3, par4EntityPlayer);
					}
				}
			}
		}
		return super.slotClick(par1, par2, par3, par4EntityPlayer);
	}

	protected void init(Object[] o) {
	}

}
