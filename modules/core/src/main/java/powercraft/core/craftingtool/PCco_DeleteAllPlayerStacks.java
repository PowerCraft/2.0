package powercraft.core.craftingtool;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import powercraft.api.inventory.PC_InventoryUtils;
import powercraft.api.registry.PC_ItemRegistry;

public class PCco_DeleteAllPlayerStacks {
	public boolean handleIncomingPacket(EntityPlayer player, Object[] o) {

		final Item craftingTool = PC_ItemRegistry.getPCItemByName("PCco_ItemCraftingTool");

		if ("Delete".equals(o[0])) {
			IInventory inv = player.inventory;

			for (int i = 0; i < inv.getSizeInventory() - 4; i++) {
				ItemStack stack = inv.getStackInSlot(i);

				if (stack != null) {
					if (stack.getItem() != craftingTool) {
						inv.decrStackSize(i, inv.getStackInSlot(i).stackSize);
					}
				}
			}
		} else {
			InventoryPlayer inv = player.inventory;
			List<ItemStack> stacks = new ArrayList<ItemStack>();

			for (int i = 0; i < inv.getSizeInventory() - 4; i++) {
				ItemStack stack = inv.getStackInSlot(i);

				if (stack != null) {
					inv.setInventorySlotContents(i, null);
					stacks.add(stack);
				}
			}

			if (stacks.size() == 0) {
				return false;
			}

			PC_InventoryUtils.groupStacks(stacks);
			List<ItemStack> sorted = new ArrayList<ItemStack>();

			while (stacks.size() > 0) {
				ItemStack lowest = null;
				int indexLowest = -1;

				for (int i = 0; i < stacks.size(); i++) {
					ItemStack checked = stacks.get(i);

					if (checked == null) {
						indexLowest = i;
						break;
					}

					if (lowest == null || checked.getItem() == craftingTool && lowest.getItem() != craftingTool) {
						lowest = checked;
						indexLowest = i;
					}
				}

				if (lowest != null) {
					sorted.add(stacks.remove(indexLowest));
				}
			}

			for (ItemStack stack : sorted) {
				inv.addItemStackToInventory(stack);
			}
		}

		return false;
	}
}
