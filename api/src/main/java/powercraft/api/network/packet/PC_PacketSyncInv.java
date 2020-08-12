package powercraft.api.network.packet;

import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import powercraft.api.inventory.PC_IInventory;
import powercraft.api.inventory.PC_InventoryUtils;
import powercraft.api.network.PC_AbstractMessage.AbstractServerMessage;
import powercraft.api.tileentity.PC_TileEntityWithInventory;
import powercraft.api.utils.PC_Serializer;
import powercraft.api.utils.PC_VecI;

public class PC_PacketSyncInv extends AbstractServerMessage<PC_PacketSyncInv> {

	NBTTagCompound inv = new NBTTagCompound();
	NBTTagCompound nbt = new NBTTagCompound();

	public PC_PacketSyncInv() {
	}

	public PC_PacketSyncInv(IInventory invent, Object... o) {
		if (invent != null)
			PC_InventoryUtils.saveInventoryToNBT(inv, "teInv", invent);
		PC_Serializer s = new PC_Serializer();
		byte[] b = null;
		if (o != null) {
			try {
				b = s.serialize(o);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (b != null)
				nbt.setByteArray("bytesObject", b);
		}
	}

	@Override
	protected void read(PacketBuffer buffer) throws IOException {
		inv = buffer.readNBTTagCompoundFromBuffer();
		nbt = buffer.readNBTTagCompoundFromBuffer();
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeNBTTagCompoundToBuffer(inv);
		buffer.writeNBTTagCompoundToBuffer(nbt);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side == Side.SERVER) {
			PC_Serializer s = new PC_Serializer();
			Object[] o = null;
			if (nbt != null) {
				try {
					try {
						o = (Object[]) s.deserialize(nbt.getByteArray("bytesObject"));
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			PC_VecI pos = (PC_VecI) o[0];
			PC_IInventory te = (PC_IInventory) player.worldObj.getTileEntity(pos.x, pos.y, pos.z);
			if (te != null)
				PC_InventoryUtils.loadInventoryFromNBT(inv, "teInv", te);
		}
	}

}
