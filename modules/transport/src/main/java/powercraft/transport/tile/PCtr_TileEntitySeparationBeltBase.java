package powercraft.transport.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import powercraft.api.inventory.PC_IInventory;
import powercraft.api.inventory.PC_InventoryUtils;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.network.packet.PC_PacketSyncInv;
import powercraft.api.network.packet.PC_PacketSyncPlayerInv;
import powercraft.api.utils.PC_VecI;

public abstract class PCtr_TileEntitySeparationBeltBase extends PCtr_TileEntityRedirectionBeltBase
		implements PC_IInventory {

	public ItemStack separatorContents[];

	@Override
	public void syncInventory(int side, EntityPlayer player, int slot) {
		if (side == 0) {
			if (worldObj.isRemote) {
				PC_PacketHandler
						.sendToServer(new PC_PacketSyncInv(this, new Object[] { new PC_VecI(xCoord, yCoord, zCoord) }));
				PC_PacketHandler.sendToServer(new PC_PacketSyncPlayerInv(player.inventory));
			}
		}
	}

	@Override
	public int getSizeInventory() {
		return separatorContents.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return separatorContents[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (separatorContents[i] != null) {
			if (separatorContents[i].stackSize <= j) {
				ItemStack itemstack = separatorContents[i];
				separatorContents[i] = null;
				this.markDirty();
				return itemstack;
			}

			ItemStack itemstack1 = separatorContents[i].splitStack(j);

			if (separatorContents[i].stackSize == 0) {
				separatorContents[i] = null;
			}

			this.markDirty();
			return itemstack1;
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		separatorContents[i] = itemstack;

		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}

		this.markDirty();
	}

	@Override
	public String getInventoryName() {
		return "Item Separator";
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		PC_InventoryUtils.loadInventoryFromNBT(nbttagcompound, "Items", this);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		PC_InventoryUtils.saveInventoryToNBT(nbttagcompound, "Items", this);
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int par1) {
		if (separatorContents[par1] != null) {
			ItemStack itemstack = separatorContents[par1];
			separatorContents[par1] = null;
			return itemstack;
		} else {
			return null;
		}
	}

	@Override
	public boolean canPlayerInsertStackTo(int slot, ItemStack stack) {
		return true;
	}

	@Override
	public boolean canDispenseStackFrom(int slot) {
		return false;
	}

	@Override
	public boolean canDropStackFrom(int slot) {
		return true;
	}

	@Override
	public int getSlotStackLimit(int slotIndex) {
		return getInventoryStackLimit();
	}

	@Override
	public boolean canPlayerTakeStack(int slotIndex, EntityPlayer entityPlayer) {
		return true;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return PC_InventoryUtils.makeIndexList(0, 0);
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
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		return false;
	}

}