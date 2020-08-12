package powercraft.api.gres;

import net.minecraft.inventory.Slot;
import powercraft.api.utils.PC_VecI;

/**
 * @author MightyPork
 * @copy (c) 2012
 */
public class PC_GresInventoryPlayer extends PC_GresLayoutV {

	private boolean showLabel = true;

	/** align of the label on top */
	protected PC_GresAlign labelAlign = PC_GresAlign.LEFT;

	/** the upper inv block */
	protected PC_GresInventory inv1;

	/** the lower inv block */
	protected PC_GresInventory inv2;

	/**
	 * @param labelVisible show inventory label.
	 */
	public PC_GresInventoryPlayer(boolean labelVisible) {
		showLabel = labelVisible;
		setAlignH(labelAlign);
		setAlignV(PC_GresAlign.TOP);
		canAddWidget = false;
	}

	public void hideSlots() {
		PC_GresBaseWithInventory containerManager = gui.getContainer();
		if (containerManager == null)
			return;

		Slot[][] slots = containerManager.inventoryPlayerUpper;

		for (int x = 0; x < slots.length; x++) {
			for (int y = 0; y < slots[0].length; y++) {
				if (slots[x][y] != null) {
					slots[x][y].xDisplayPosition = -3000;
				}
			}
		}

		slots = containerManager.inventoryPlayerLower;

		for (int x = 0; x < slots.length; x++) {
			for (int y = 0; y < slots[0].length; y++) {
				if (slots[x][y] != null) {
					slots[x][y].xDisplayPosition = -3000;
				}
			}
		}
	}

	@Override
	public void addedToWidget() {
		if (gui == null)
			return;
		PC_GresBaseWithInventory containerManager = gui.getContainer();
		if (containerManager == null) {
			return;
		}

		canAddWidget = true;
		PC_GresWidget label = new PC_GresLabel("container.inventory").setWidgetMargin(2).setColor(textColorDisabled,
				0x404040);
		label.enable(false);
		if (showLabel) {
			add(label);
		}

		inv1 = new PC_GresInventory(9, 3);
		inv1.slots = containerManager.inventoryPlayerUpper;
		add(inv1.setWidgetMargin(4));

		inv2 = new PC_GresInventory(9, 1);
		inv2.slots = containerManager.inventoryPlayerLower;
		add(inv2.setWidgetMargin(4));
		canAddWidget = false;
		super.addedToWidget();
	}

	@Override
	public MouseOver mouseOver(PC_VecI mousePos) {
		return MouseOver.CHILD;
	}

	@Override
	public boolean mouseClick(PC_VecI mousePos, int key) {
		return inv1.mouseClick(mousePos, key) || inv2.mouseClick(mousePos, key);
	}

	protected void visibleChanged(boolean show) {
		if (inv1 != null) {
			inv1.visibleChanged(show);
			inv2.visibleChanged(show);
		}
	}

}
