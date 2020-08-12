package powercraft.machines.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.inventory.PC_Slot;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.machines.gui.PCma_SlotAutomaticWorkbenchInventory;
import powercraft.machines.gui.PCma_SlotAutomaticWorkbenchResult;
import powercraft.machines.tile.PCma_TileEntityAutomaticWorkbench;

public class PCma_ContainerAutomaticWorkbench extends PC_GresBaseWithInventory<PCma_TileEntityAutomaticWorkbench> {
	private IInventory craftResult;

	public PCma_ContainerAutomaticWorkbench(EntityPlayer player, PC_TileEntity te, Object[] o) {
		super(player, (PCma_TileEntityAutomaticWorkbench) te, o);
	}

	@Override
	protected void init(Object[] o) {
		craftResult = new InventoryCraftResult();
		craftResult.setInventorySlotContents(0, tileEntity.getRecipeProduct());
	}

	@Override
	public void onCraftMatrixChanged(IInventory iinventory) {
		craftResult.setInventorySlotContents(0, tileEntity.getRecipeProduct());
	}

	@Override
	public PC_Slot[] getAllSlots() {
		int cnt = 0;
		invSlots = new PC_Slot[19];

		invSlots[0] = new PCma_SlotAutomaticWorkbenchResult(tileEntity, craftResult, this, 0);

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				invSlots[cnt + 1] = new PCma_SlotAutomaticWorkbenchInventory(tileEntity, this, false, cnt);
				cnt++;
			}
		}

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				invSlots[cnt + 1] = new PCma_SlotAutomaticWorkbenchInventory(tileEntity, this, true, cnt);
				cnt++;
			}
		}

		return invSlots;
	}

	@Override
	protected boolean canShiftTransfer() {
		return true;
	}

}
