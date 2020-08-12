package powercraft.api.network.packet;

import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import powercraft.api.network.PC_AbstractMessage.AbstractClientMessage;
import powercraft.api.registry.PC_GresRegistry;
import powercraft.api.utils.PC_Serializer;

public class PC_PacketOpenGres extends AbstractClientMessage<PC_PacketOpenGres> {

	String name;
	int x, y, z, id;
	NBTTagCompound nbt = new NBTTagCompound();

	public PC_PacketOpenGres() {
	}

	public PC_PacketOpenGres(String name, TileEntity te, Object... o) {
		this.name = name;
		if (te != null) {
			this.x = te.xCoord;
			this.y = te.yCoord;
			this.z = te.zCoord;
		} else {
			this.x = 999999999;
		}
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
		name = buffer.readStringFromBuffer(255);
		x = buffer.readInt();
		y = buffer.readInt();
		z = buffer.readInt();
		nbt = buffer.readNBTTagCompoundFromBuffer();
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeStringToBuffer(name);
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);
		buffer.writeNBTTagCompoundToBuffer(nbt);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		TileEntity te;
		if (x != 999999999) {
			te = player.getEntityWorld().getTileEntity(x, y, z);
		} else {
			te = null;
		}
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
		PC_GresRegistry.openGres(name, player, te, o);// new Object[] {id, x, y, z});
	}

}
