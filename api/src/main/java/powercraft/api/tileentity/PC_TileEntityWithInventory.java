package powercraft.api.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import powercraft.api.inventory.PC_IInventory;
import powercraft.api.inventory.PC_InventoryUtils;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.network.packet.PC_PacketSyncInv;
import powercraft.api.network.packet.PC_PacketSyncPlayerInv;
import powercraft.api.utils.PC_VecI;

public class PC_TileEntityWithInventory extends PC_TileEntity implements PC_IInventory {

	protected String inventoryTitle;
	protected int slotsCount;
	protected ItemStack inventoryContents[];

	public PC_TileEntityWithInventory(String title, int size) {
		inventoryTitle = title;
		slotsCount = size;
		inventoryContents = new ItemStack[slotsCount];
	}

	@Override
	public int getSizeInventory() {
		return slotsCount;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventoryContents[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (this.inventoryContents[i] != null) {
			ItemStack itemstack;

			if (this.inventoryContents[i].stackSize <= j) {
				itemstack = this.inventoryContents[i];
				this.inventoryContents[i] = null;
				this.markDirty();
				return itemstack;
			} else {
				itemstack = this.inventoryContents[i].splitStack(j);

				if (this.inventoryContents[i].stackSize == 0) {
					this.inventoryContents[i] = null;
				}

				this.markDirty();
				return itemstack;
			}
		} else {
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		if (this.inventoryContents[i] != null) {
			ItemStack itemstack = this.inventoryContents[i];
			this.inventoryContents[i] = null;
			return itemstack;
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		this.inventoryContents[i] = itemstack;

		if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit()) {
			itemstack.stackSize = this.getInventoryStackLimit();
		}

		markDirty();

	}

	@Override
	public String getInventoryName() {
		return inventoryTitle;
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
		return true;
	}

	@Override
	public boolean canPlayerTakeStack(int i, EntityPlayer entityPlayer) {
		return true;
	}

	@Override
	public boolean canDispenseStackFrom(int i) {
		return true;
	}

	@Override
	public boolean canDropStackFrom(int i) {
		return true;
	}

	@Override
	public int getSlotStackLimit(int i) {
		return getInventoryStackLimit();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
		// PC_InventoryUtils.loadInventoryFromNBT(nbtTagCompound, "Items", this);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
		// PC_InventoryUtils.saveInventoryToNBT(nbtTagCompound, "Items", this);
	}

	@Override
	public void invalidate() {
		super.invalidate();
		PC_InventoryUtils.dropInventoryContents(this, worldObj, getCoord());
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return PC_InventoryUtils.makeIndexList(0, getSizeInventory());
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int side) {
		return canDispenseStackFrom(i);
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		return true;
	}

	@Override
	public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_, int p_102007_3_) {
		return true;
	}

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

}
