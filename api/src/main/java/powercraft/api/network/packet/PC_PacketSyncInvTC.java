package powercraft.api.network.packet;

import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import powercraft.api.inventory.PC_IInventory;
import powercraft.api.inventory.PC_InventoryUtils;
import powercraft.api.network.PC_AbstractMessage;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.utils.PC_Serializer;
import powercraft.api.utils.PC_VecI;

public class PC_PacketSyncInvTC extends PC_AbstractMessage<PC_PacketSyncInvTC> {

	NBTTagCompound inv = new NBTTagCompound();
	NBTTagCompound nbt = new NBTTagCompound();

	public PC_PacketSyncInvTC() {
	}

	public PC_PacketSyncInvTC(IInventory invent, Object... o) {
		if (invent != null)
			PC_InventoryUtils.saveInventoryToNBT(inv, "teInvC", invent);
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
		if (side == Side.SERVER) {
			PC_PacketHandler.sendToAllAround(new PC_PacketSyncInvTC(te, o), player.dimension, pos.x, pos.y, pos.z, 20);
		}
		if (side == Side.CLIENT) {
			PC_InventoryUtils.loadInventoryFromNBT(inv, "teInvC", te);
		}
	}

}
