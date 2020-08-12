package powercraft.machines.gui;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import powercraft.api.inventory.PC_Slot;

public class PCma_SlotAutomaticWorkbenchInventory extends PC_Slot {
	private Container parentContainer;
	private boolean inrecipe = false;

	public PCma_SlotAutomaticWorkbenchInventory(IInventory iinventory, Container parent, boolean inRecipe, int i) {
		super(iinventory, i);
		this.parentContainer = parent;
		this.inrecipe = inRecipe;
	}

	@Override
	public void onSlotChanged() {
		inventory.markDirty();

		if (inrecipe) {
			parentContainer.onCraftMatrixChanged(super.inventory);
		}
	}
}
