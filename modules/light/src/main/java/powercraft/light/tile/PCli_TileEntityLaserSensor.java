package powercraft.light.tile;

import powercraft.api.annotation.PC_ClientServerSync;
import powercraft.api.tileentity.PC_ITileEntityAABB;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_Direction;
import powercraft.api.utils.PC_Utils;

public class PCli_TileEntityLaserSensor extends PC_TileEntity implements PC_ITileEntityAABB {

	@PC_ClientServerSync(clientChangeAble = false)
	private boolean active = false;
	private int coolDown = 2;

	public void hitByBeam() {
		coolDown = 2;
		if (!active) {
			active = true;
			notifyChanges("active");
			PC_Utils.hugeUpdate(worldObj, xCoord, yCoord, zCoord);
		}
	}

	public boolean isActive() {
		return active;
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void updateEntity() {
		if (coolDown > 0) {
			if (--coolDown == 0) {
				active = false;
				notifyChanges("active");
				PC_Utils.hugeUpdate(worldObj, xCoord, yCoord, zCoord);
			}
		}
	}

	@Override
	public int getProvidingStrongRedstonePowerValue(PC_Direction dir) {
		return active ? 15 : 0;
	}

}
