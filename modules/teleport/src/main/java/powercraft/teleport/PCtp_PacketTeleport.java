package powercraft.teleport;

import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import powercraft.api.network.PC_AbstractMessage.AbstractServerMessage;

public class PCtp_PacketTeleport extends AbstractServerMessage<PCtp_PacketTeleport> {

	private NBTTagCompound nbt = new NBTTagCompound();

	public PCtp_PacketTeleport() {
	}

	public PCtp_PacketTeleport(String target) {
		nbt.setString("target", target);
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
				PCtp_TeleporterManager tm = new PCtp_TeleporterManager();
				PCtp_TeleporterData td2 = tm.getTargetByName(nbt.getString("target"));

				tm.teleportEntityToTarget(player, td2);
			}
		}
	}

}
