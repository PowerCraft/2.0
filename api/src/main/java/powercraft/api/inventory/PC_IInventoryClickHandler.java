package powercraft.api.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface PC_IInventoryClickHandler {

	public ItemStack slotClick(int slot, int mouseKey, int par3, EntityPlayer entityPlayer);

}
