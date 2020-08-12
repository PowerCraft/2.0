package powercraft.mobile;

import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import powercraft.api.inventory.PC_InventoryUtils;
import powercraft.api.network.PC_AbstractMessage.AbstractServerMessage;
import powercraft.api.utils.PC_Serializer;

public class PCmo_PacketMinerServer extends AbstractServerMessage<PCmo_PacketMinerServer> {

	NBTTagCompound nbt = new NBTTagCompound();
	NBTTagCompound inv = new NBTTagCompound();

	public PCmo_PacketMinerServer() {
	}

	public PCmo_PacketMinerServer(Object... o) {
		if ((Integer) o[0] == 1 || (Integer) o[0] == 2) {
			inv = (NBTTagCompound) o[2];
			Object[] o2 = new Object[] { o[0], o[1] };
			o = o2;
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
		nbt = buffer.readNBTTagCompoundFromBuffer();
		inv = buffer.readNBTTagCompoundFromBuffer();
	}

	@Override
	protected void write(PacketBuffer buffer) throws IOException {
		buffer.writeNBTTagCompoundToBuffer(nbt);
		buffer.writeNBTTagCompoundToBuffer(inv);
	}

	@Override
	public void process(EntityPlayer player, Side side) {
		if (side.isServer()) {
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
			PCmo_EntityMiner miner = (PCmo_EntityMiner) player.worldObj.getEntityByID((Integer) o[1]);
			if ((Integer) o[0] == 1) {
				// cargo inventory
				PCmo_EntityMiner.MinerCargoInventory cargo = (PCmo_EntityMiner.MinerCargoInventory) miner
						.getInventory();
				PCmo_EntityMiner.MinerCargoInventory cargoS;
				cargo = (PCmo_EntityMiner.MinerCargoInventory) miner.getInventory();
				cargoS = cargo;
				PC_InventoryUtils.loadInventoryFromNBT(inv, "cargo", cargoS);
				for (int i = 0; i < cargoS.getSizeInventory(); i++)
					cargo.setInventorySlotContents(i, cargoS.getStackInSlot(i));
				if (miner.getInGui())
					miner.setInGui(false);
			}
			if ((Integer) o[0] == 3) {
				PCmo_MinerManager manager = new PCmo_MinerManager();
				manager.handleIncomingPacket(player, o);
			}
		}
	}
}
