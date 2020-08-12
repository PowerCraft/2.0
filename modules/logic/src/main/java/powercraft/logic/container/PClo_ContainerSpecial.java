package powercraft.logic.container;

import net.minecraft.entity.player.EntityPlayer;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.logic.tile.PClo_TileEntitySpecial;

public class PClo_ContainerSpecial extends PC_GresBaseWithInventory<PClo_TileEntitySpecial> {
	public PClo_ContainerSpecial(EntityPlayer player, PC_TileEntity te, Object[] o) {
		super(player, (PClo_TileEntitySpecial) te, o);
	}

	@Override
	protected boolean canShiftTransfer() {
		return true;
	}

}
