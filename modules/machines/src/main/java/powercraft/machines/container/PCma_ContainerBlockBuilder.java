package powercraft.machines.container;

import net.minecraft.entity.player.EntityPlayer;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.machines.tile.PCma_TileEntityBlockBuilder;

public class PCma_ContainerBlockBuilder extends PC_GresBaseWithInventory<PCma_TileEntityBlockBuilder> {

	public PCma_ContainerBlockBuilder(EntityPlayer player, PC_TileEntity te, Object[] o) {
		super(player, (PCma_TileEntityBlockBuilder) te, o);
	}

	@Override
	protected boolean canShiftTransfer() {
		return true;
	}

}
