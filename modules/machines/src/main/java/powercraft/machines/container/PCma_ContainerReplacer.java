package powercraft.machines.container;

import net.minecraft.entity.player.EntityPlayer;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.machines.tile.PCma_TileEntityReplacer;

public class PCma_ContainerReplacer extends PC_GresBaseWithInventory<PCma_TileEntityReplacer> {
	public PCma_ContainerReplacer(EntityPlayer player, PC_TileEntity te, Object[] o) {
		super(player, (PCma_TileEntityReplacer) te, o);
	}
}
