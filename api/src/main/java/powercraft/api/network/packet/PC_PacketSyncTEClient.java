package powercraft.api.network.packet;

import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import powercraft.api.network.PC_AbstractMessage.AbstractServerMessage;
import powercraft.api.network.PC_IPacketHandler;
import powercraft.api.utils.PC_Serializer;
import powercraft.api.utils.PC_VecI;

public class PC_PacketSyncTEClient extends AbstractServerMessage<PC_PacketSyncTEClient> {

	NBTTagCompound nbt = new NBTTagCompound();

	public PC_PacketSyncTEClient() {
	}

	public PC_PacketSyncTEClient(Object... o) {
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
		nbt = buffer.readNBTTagCompoundFromBuffer();
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeNBTTagCompoundToBuffer(nbt);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isClient()) {
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
			PC_VecI pos = (PC_VecI) o[1];
			if (player.worldObj.getTileEntity(pos.x, pos.y, pos.z) instanceof PC_IPacketHandler) {
				PC_IPacketHandler te = (PC_IPacketHandler) player.worldObj.getTileEntity(pos.x, pos.y, pos.z);
				te.handleIncomingPacket(player, o);
			} else if (player.worldObj.getBlock(pos.x, pos.y, pos.z) instanceof PC_IPacketHandler)
				((PC_IPacketHandler) player.worldObj.getBlock(pos.x, pos.y, pos.z)).handleIncomingPacket(player, o);
		}
	}
}
