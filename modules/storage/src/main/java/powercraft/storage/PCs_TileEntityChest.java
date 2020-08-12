package powercraft.storage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityChest;
import powercraft.api.hooklib.asm.Hook;
import powercraft.api.hooklib.asm.Hook.ReturnValue;
import powercraft.api.hooklib.asm.ReturnCondition;

public class PCs_TileEntityChest {

	@Hook(createMethod = true, injectOnExit = true, returnCondition = ReturnCondition.ALWAYS)
	public static boolean isUseableByPlayer(TileEntityChest te, EntityPlayer p_70300_1_,
			@ReturnValue boolean returnValue) {
		if (p_70300_1_.getCurrentEquippedItem() != null && p_70300_1_.getCurrentEquippedItem().getTagCompound() != null
				&& p_70300_1_.getCurrentEquippedItem().getTagCompound().getIntArray("posChest") != null) {
			return true;
		} else {
			return returnValue;
		}
	}

}
