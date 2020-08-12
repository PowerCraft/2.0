package powercraft.core.craftingtool;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import powercraft.api.inventory.PC_Slot;

public class PCco_SlotTrash extends PC_Slot {

	public PCco_SlotTrash() {
		super(null, 0);
	}

	@Override
	public void onSlotChange(ItemStack par1ItemStack, ItemStack par2ItemStack) {
	}

	@Override
	protected void onCrafting(ItemStack itemstack, int i) {
	}

	@Override
	protected void onCrafting(ItemStack itemstack) {
	}

	@Override
	public void onPickupFromSlot(EntityPlayer player, ItemStack par1ItemStack) {
	}

	@Override
	public boolean isItemValid(ItemStack par1ItemStack) {
		return true;
	}

	@Override
	public ItemStack getStack() {
		return null;
	}

	@Override
	public boolean getHasStack() {
		return false;
	}

	@Override
	public void putStack(ItemStack par1ItemStack) {
	}

	@Override
	public void onSlotChanged() {
	}

	@Override
	public int getSlotStackLimit() {
		return 128;
	}

	@Override
	public IIcon getBackgroundIconIndex() {
		return null;
	}

	@Override
	public ItemStack decrStackSize(int par1) {
		return null;
	}

}
