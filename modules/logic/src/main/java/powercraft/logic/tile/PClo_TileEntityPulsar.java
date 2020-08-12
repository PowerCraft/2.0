package powercraft.logic.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import powercraft.api.PC_Lang;
import powercraft.api.annotation.PC_ClientServerSync;
import powercraft.api.network.PC_IPacketHandler;
import powercraft.api.registry.PC_SoundRegistry;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_Utils;
import powercraft.logic.block.PClo_BlockPulsar;

public class PClo_TileEntityPulsar extends PC_TileEntity implements PC_IPacketHandler {

	private int delayTimer = 0;
	@PC_ClientServerSync
	private int delay = 10;
	@PC_ClientServerSync
	private int holdtime = 4;
	@PC_ClientServerSync
	private boolean paused = false;
	@PC_ClientServerSync
	private boolean silent = false;

	private boolean should = true;

	public void setTimes(int delay, int holdTime) {
		if (delay < 2) {
			delay = 2;
		}

		this.delay = delay;

		if (holdTime >= delay) {
			holdTime = delay - 1;
		}

		holdtime = holdTime;
		notifyChanges("delay", "holdtime");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("delayTimer", delayTimer);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		delayTimer = nbttagcompound.getInteger("delayTimer");
	}

	public void printDelay() {
		PC_Utils.chatMsg(PC_Lang.tr("pc.pulsar.clickMsg",
				new Object[] { getDelay() + "", PC_Utils.ticksToSecs(getDelay()) + "" }));
	}

	public void printDelayTime() {
		PC_Utils.chatMsg(PC_Lang.tr("pc.pulsar.clickMsgTime", new Object[] { getDelay() + "",
				PC_Utils.ticksToSecs(getDelay()) + "", (getDelay() - delayTimer) + "" }));
	}

	public boolean isActive() {
		return PC_Utils.getBID(worldObj, xCoord, yCoord, zCoord) == PClo_BlockPulsar.on;
	}

	@Override
	public void updateEntity() {
		if (isPaused() || worldObj.isRemote) {
			return;
		}

		if (delayTimer < 0 && !isActive()) {
			PC_Utils.setBlockState(worldObj, xCoord, yCoord, zCoord, true);
		}

		delayTimer++;

		if (delayTimer >= getHold() && isActive()) {
			PC_Utils.setBlockState(worldObj, xCoord, yCoord, zCoord, false);
		}

		if (delayTimer >= getDelay()) {
			delayTimer = -1;
		}

	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	public boolean isSilent() {
		return silent;
	}

	public int getDelay() {
		return delay;
	}

	public int getHold() {
		return holdtime;
	}

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		if (this.paused != paused) {
			this.paused = paused;
			notifyChanges("paused");
		}
	}

	public void setSilent(boolean silent) {
		if (this.silent != silent) {
			this.silent = silent;
			notifyChanges("silent");
		}
	}

	@Override
	protected void onCall(String key, Object[] value) {
		if (key.equals("change")) {
			if (worldObj.isRemote) {
				if (!isSilent()) {
					PC_SoundRegistry.playSound(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, "random.click", 1.0F, 1.0F);
				}
			}
		}
	}

	public boolean getShould() {
		return should;
	}

	@Override
	public boolean handleIncomingPacket(EntityPlayer player, Object[] o) {
		this.setPaused((Boolean) o[2]);
		this.setSilent((Boolean) o[3]);
		this.setTimes((Integer) o[4], (Integer) o[5]);
		return false;
	}

}
