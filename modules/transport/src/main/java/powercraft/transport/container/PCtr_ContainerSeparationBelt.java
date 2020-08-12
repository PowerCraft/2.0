package powercraft.transport.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.transport.tile.PCtr_TileEntitySeparationBelt;

public class PCtr_ContainerSeparationBelt extends PC_GresBaseWithInventory<PCtr_TileEntitySeparationBelt> {

	PCtr_TileEntitySeparationBelt te;

	public PCtr_ContainerSeparationBelt(EntityPlayer player, TileEntity te, Object[] o) {
		super(player, (PCtr_TileEntitySeparationBelt) te, o);
	}
}
