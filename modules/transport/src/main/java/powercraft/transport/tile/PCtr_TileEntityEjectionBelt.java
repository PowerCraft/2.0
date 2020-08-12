package powercraft.transport.tile;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import powercraft.api.network.PC_IPacketHandler;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_VecI;
import powercraft.api.utils.PC_WorldData;
import powercraft.transport.PCtr_BeltHelper;
import powercraft.transport.block.PCtr_BlockBeltEjector;

public class PCtr_TileEntityEjectionBelt extends PC_TileEntity implements PC_IPacketHandler {
	public static Random rand = new Random();

	public int actionType = 0;
	public int numStacksEjected = 1;
	public int numItemsEjected = 1;
	public int itemSelectMode = 0;

	public boolean isActive = false;

	public int getActionType() {
		return actionType;
	}

	public void setActionType(int actionType) {
		if (this.actionType != actionType) {
			this.actionType = actionType;
		}
	}

	public int getNumStacksEjected() {
		return numStacksEjected;
	}

	public void setNumStacksEjected(int numStacksEjected) {
		if (this.numStacksEjected != numStacksEjected) {
			this.numStacksEjected = numStacksEjected;
		}
	}

	public int getNumItemsEjected() {
		return numItemsEjected;
	}

	public void setNumItemsEjected(int numItemsEjected) {
		if (this.numItemsEjected != numItemsEjected) {
			this.numItemsEjected = numItemsEjected;
		}
	}

	public int getItemSelectMode() {
		return itemSelectMode;
	}

	public void setItemSelectMode(int itemSelectMode) {
		if (this.itemSelectMode != itemSelectMode) {
			this.itemSelectMode = itemSelectMode;
			notifyChanges("itemSelectMode");
		}
	}

	@Override
	public final boolean canUpdate() {
		return true;
	}

	@Override
	public final void updateEntity() {
		if (!worldObj.isRemote) {
			PC_VecI pos = new PC_VecI(xCoord, yCoord, zCoord);
			PCtr_BlockBeltEjector block = (PCtr_BlockBeltEjector) worldObj.getBlock(xCoord, yCoord, zCoord);
			if (block.isPowered) {
				if (!isActive) {
					if (!PCtr_BeltHelper.dispenseStackFromNearbyMinecart(worldObj, pos)) {
						PCtr_BeltHelper.tryToDispenseItem(worldObj, pos);
					}
					isActive = true;
				}
			} else {
				isActive = false;
			}
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			markDirty();
			PC_WorldData data = PC_WorldData.forWorld(worldObj);
			NBTTagCompound tag = data.getData();
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("actionType", this.actionType);
		tag.setInteger("itemSelectMode", this.itemSelectMode);
		tag.setInteger("numItemsEjected", this.numItemsEjected);
		tag.setInteger("numStacksEjected", this.numStacksEjected);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.actionType = tag.getInteger("actionType");
		this.itemSelectMode = tag.getInteger("itemSelectMode");
		this.numItemsEjected = tag.getInteger("numItemsEjected");
		this.numStacksEjected = tag.getInteger("numStacksEjected");
	}

	@Override
	public boolean handleIncomingPacket(EntityPlayer player, Object[] o) {
		if ((Integer) o[0] == 1) {
			setActionType((Integer) o[2]);
			setItemSelectMode((Integer) o[3]);
			setNumStacksEjected((Integer) o[4]);
			setNumItemsEjected((Integer) o[5]);
		}
		return true;
	}

}
