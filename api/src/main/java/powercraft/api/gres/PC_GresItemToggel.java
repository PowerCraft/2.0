package powercraft.api.gres;

import java.util.List;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import powercraft.api.inventory.PC_SlotNoPickup;
import powercraft.api.utils.PC_Struct2;
import powercraft.api.utils.PC_VecI;

public class PC_GresItemToggel extends PC_GresInventory {

	private List<PC_Struct2<ItemStack, List<String>>>[][] toogleList;
	private int index[][];
	/** Flag that is currently under pressed mouse */
	protected Slot selectedSlot;

	public PC_GresItemToggel(int width, int height) {
		super(width, height);
		toogleList = new List[width][height];
		index = new int[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				slots[i][j] = new PC_SlotNoPickup().setBackgroundStack(null);
			}
		}
	}

	@Override
	public boolean mouseClick(PC_VecI mousePos, int key) {
		Slot slot = getSlotUnderMouse(mousePos);
		if (selectedSlot == slot && slot != null && key == -1) {
			selectedSlot = null;
			PC_VecI p = getSlotCoord(slot);
			if (toogleList[p.x][p.y].size() > 0) {
				index[p.x][p.y]++;
				index[p.x][p.y] %= toogleList[p.x][p.y].size();
				((PC_SlotNoPickup) slots[p.x][p.y]).setBackgroundStack(toogleList[p.x][p.y].get(index[p.x][p.y]).a);
			}
			return true;
		}
		if (key == -1) {
			selectedSlot = null;
		} else {
			selectedSlot = slot;
		}
		return key != -1;
	}

	@Override
	public void mouseMove(PC_VecI mousePos) {
		isMouseOver = true;
		Slot slot = getSlotUnderMouse(mousePos);
		if (slot == null) {
			selectedSlot = null;
		} else {
			if (selectedSlot != slot) {
				selectedSlot = slot;
			}
		}
	}

	private PC_VecI getSlotCoord(Slot slot) {
		if (slot == null)
			return null;
		for (int i = 0; i < gridWidth; i++) {
			for (int j = 0; j < gridHeight; j++) {
				if (slots[i][j] == slot)
					return new PC_VecI(i, j);
			}
		}
		return null;
	}

	@Override
	public List<String> getTooltip(PC_VecI mousePos) {
		Slot slot = getSlotUnderMouse(mousePos);
		if (slot != null) {
			PC_VecI p = getSlotCoord(slot);
			return toogleList[p.x][p.y].get(index[p.x][p.y]).b;
		}
		return super.getTooltip(mousePos);
	}

	public void setItemList(int i, int j, List<PC_Struct2<ItemStack, List<String>>> l) {
		toogleList[i][j] = l;
		index[i][j] = 0;
		if (l != null && l.size() > 0) {
			((PC_SlotNoPickup) slots[i][j]).setBackgroundStack(l.get(0).a);
		} else {
			((PC_SlotNoPickup) slots[i][j]).setBackgroundStack(null);
		}
	}

}
