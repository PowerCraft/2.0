package powercraft.teleport;

import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import powercraft.api.network.PC_AbstractMessage.AbstractServerMessage;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_VecI;

public class PCtp_PacketTeleporterSync extends AbstractServerMessage<PCtp_PacketTeleporterSync> {

	private int x, y, z;
	private String target;
	private PC_TileEntity te;
	private PCtp_TeleporterData td = new PCtp_TeleporterData();
	private NBTTagCompound nbt = new NBTTagCompound();

	public PCtp_PacketTeleporterSync() {
	}

	public PCtp_PacketTeleporterSync(PCtp_TeleporterData td, String target, int code) {
		if (code == 0) {
			this.target = target;
			this.td = td;
			nbt.setString("target", target);
			nbt.setBoolean("animals", td.animals);
			nbt.setInteger("defaultTargerDimension", td.defaultTargetDimension);
			nbt.setInteger("dimension", td.dimension);
			nbt.setInteger("direction", td.direction);
			nbt.setBoolean("items", td.items);
			nbt.setBoolean("lasers", td.lasers);
			nbt.setBoolean("monsters", td.monsters);
			nbt.setString("name", td.name);
			nbt.setBoolean("playerChose", td.playerChoose);
			nbt.setBoolean("players", td.players);
			if (td.pos != null) {
				nbt.setInteger("posX", td.pos.x);
				nbt.setInteger("posY", td.pos.y);
				nbt.setInteger("posZ", td.pos.z);
			} else {
				nbt.setInteger("posX", 300000001);
			}
			nbt.setBoolean("sneakTrigger", td.sneakTrigger);
			nbt.setBoolean("soundEnabled", td.soundEnabled);
		} else {
			if (code == 1) {
				nbt.setInteger("code", code);
				nbt.setString("target", target);
			}
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
		if (side.isServer()) {
			if (nbt != null) {
				if (nbt.getInteger("code") == 0) {
					target = nbt.getString("target");
					td.animals = nbt.getBoolean("animals");
					td.defaultTargetDimension = nbt.getInteger("defaultTargerDimension");
					td.dimension = nbt.getInteger("dimension");
					td.direction = nbt.getInteger("direction");
					td.items = nbt.getBoolean("items");
					td.lasers = nbt.getBoolean("lasers");
					td.monsters = nbt.getBoolean("monsters");
					td.name = nbt.getString("name");
					td.playerChoose = nbt.getBoolean("playerChose");
					td.players = nbt.getBoolean("players");
					if (nbt.getInteger("posX") != 300000001)
						td.pos = new PC_VecI(nbt.getInteger("posX"), nbt.getInteger("posY"), nbt.getInteger("posZ"));
					td.sneakTrigger = nbt.getBoolean("sneakTrigger");
					td.soundEnabled = nbt.getBoolean("soundEnabled");

					PCtp_TeleporterManager tm = new PCtp_TeleporterManager();
					if (!target.isEmpty()) {
						PCtp_TeleporterData td3 = tm.getTargetByName(target);
						td.defaultTarget = td3.pos;
					}
					tm.releaseTeleporterData(td.dimension, td.pos);
					tm.registerTeleporterData(td.dimension, td.pos, td);
				} else {
					if (nbt.getInteger("code") == 1) {
						PCtp_TeleporterManager tm = new PCtp_TeleporterManager();
						PCtp_TeleporterData td2 = tm.getTargetByName(nbt.getString("target"));
					}
				}
			}
		}
	}

}
