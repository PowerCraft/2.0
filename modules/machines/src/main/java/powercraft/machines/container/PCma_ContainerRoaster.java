package powercraft.machines.container;

import net.minecraft.entity.player.EntityPlayer;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.machines.tile.PCma_TileEntityRoaster;

public class PCma_ContainerRoaster extends PC_GresBaseWithInventory<PCma_TileEntityRoaster> {
	public PCma_ContainerRoaster(EntityPlayer player, PC_TileEntity te, Object[] o) {
		super(player, (PCma_TileEntityRoaster) te, o);
	}

	@Override
	protected boolean canShiftTransfer() {
		return true;
	}

}
