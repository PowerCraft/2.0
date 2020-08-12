package powercraft.light.packet;

import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import powercraft.api.network.PC_AbstractMessage.AbstractClientMessage;
import powercraft.api.utils.PC_Serializer;
import powercraft.light.tile.PCli_TileEntityLaser;

public class PCli_PacketLaser extends AbstractClientMessage<PCli_PacketLaser> {

	NBTTagCompound nbt = new NBTTagCompound();

	public PCli_PacketLaser() {
	}

	public PCli_PacketLaser(Object... o) {// TODO: maybe move in main TE packet?
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
		if (side == Side.CLIENT) {
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
			PCli_TileEntityLaser te = (PCli_TileEntityLaser) player.worldObj.getTileEntity((Integer) o[0],
					(Integer) o[1], (Integer) o[2]);
			te.setPowered((Boolean) o[3]);
		}
	}
}
