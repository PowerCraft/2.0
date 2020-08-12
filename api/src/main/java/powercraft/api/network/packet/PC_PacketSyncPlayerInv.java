package powercraft.api.network.packet;

import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import powercraft.api.inventory.PC_InventoryUtils;
import powercraft.api.network.PC_AbstractMessage.AbstractServerMessage;

public class PC_PacketSyncPlayerInv extends AbstractServerMessage<PC_PacketSyncPlayerInv> {

	NBTTagCompound nbt = new NBTTagCompound();

	public PC_PacketSyncPlayerInv() {
	}

	public PC_PacketSyncPlayerInv(IInventory inv) {
		PC_InventoryUtils.saveInventoryToNBT(nbt, "playerInv", inv);
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		nbt = buffer.readNBTTagCompoundFromBuffer();
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeNBTTagCompoundToBuffer(nbt);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side == Side.SERVER) {
			IInventory inv = player.inventory;
			PC_InventoryUtils.loadInventoryFromNBT(nbt, "playerInv", inv);
			for (int i = 0; i < inv.getSizeInventory(); i++) {
				player.inventory.setInventorySlotContents(i, inv.getStackInSlot(i));
			}
		}
	}

}
