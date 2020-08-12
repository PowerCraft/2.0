package powercraft.api.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class PC_SlotGhost extends PC_Slot {

	public PC_SlotGhost(IInventory inv, int slot) {
		super(inv, slot);
	}

	@Override
	public boolean isItemValid(ItemStack itemStack) {
		itemStack = itemStack.copy();
		itemStack.stackSize = 1;
		if (super.isItemValid(itemStack)) {
			putStack(itemStack);
		}
		return false;
	}

	@Override
	public ItemStack decrStackSize(int par1) {
		putStack(null);
		return null;
	}

}
