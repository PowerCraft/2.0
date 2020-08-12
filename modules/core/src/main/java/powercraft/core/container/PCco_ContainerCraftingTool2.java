package powercraft.core.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.world.World;

public class PCco_ContainerCraftingTool2 extends ContainerWorkbench {

	public PCco_ContainerCraftingTool2(InventoryPlayer p_i1808_1_, World p_i1808_2_, int p_i1808_3_, int p_i1808_4_,
			int p_i1808_5_) {
		super(p_i1808_1_, p_i1808_2_, p_i1808_5_, p_i1808_5_, p_i1808_5_);
	}

	@Override
	public boolean canInteractWith(EntityPlayer p_75145_1_) {
		return true;
	}

}
